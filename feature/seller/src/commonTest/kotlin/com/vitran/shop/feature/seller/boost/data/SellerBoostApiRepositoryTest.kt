package com.vitran.shop.feature.seller.boost.data

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.client.createHttpClient
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.NetworkConfig
import com.vitran.shop.core.network.config.NetworkDiagnosticsConfig
import com.vitran.shop.core.network.logging.NoOpNetworkLogger
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.core.session.auth.SessionAuthCoordinator
import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.boost.data.remote.SellerBoostApi
import com.vitran.shop.feature.seller.boost.data.repository.DefaultSellerBoostRepository
import com.vitran.shop.feature.seller.boost.data.state.SellerBoostStateStore
import com.vitran.shop.feature.seller.boost.domain.model.ActiveBoosts
import com.vitran.shop.feature.seller.boost.domain.model.BoostTarget
import com.vitran.shop.feature.seller.boost.domain.model.CreateBoostCommand
import com.vitran.shop.feature.seller.boost.domain.model.CreatedBoost
import com.vitran.shop.feature.seller.createSellerTestClient
import com.vitran.shop.feature.seller.createSellerTestExecutor
import com.vitran.shop.feature.seller.hasAuthBearer
import com.vitran.shop.feature.seller.jsonResponse
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.async
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.yield
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException

class SellerBoostApiRepositoryTest {

    @Test
    fun shopTarget_omitsProductId_sendsDaysAndPricePaid() = runTest {
        var bodyText = ""
        val engine =
            MockEngine { request ->
                if (request.method == HttpMethod.Get) {
                    return@MockEngine jsonResponse(HttpStatusCode.OK, emptyBoostsBody)
                }
                assertEquals(HttpMethod.Post, request.method)
                assertTrue(request.hasAuthBearer("OLD_ACCESS"))
                assertTrue(request.url.encodedPath.endsWith("/seller/shops/1/boosts"))
                bodyText = (request.body as TextContent).text
                jsonResponse(HttpStatusCode.Created, createBoostBody)
            }
        val (repo, _) = createBoostRepository(engine)
        val result =
            repo.createBoost(
                CreateBoostCommand(
                    shopId = ShopId(1),
                    target = BoostTarget.Shop,
                    days = 7,
                    pricePaid = 12345,
                ),
            )
        val created = assertIs<AppResult.Success<CreatedBoost>>(result).value
        assertEquals(1L, created.id.value)
        assertEquals(ShopId(1), created.shopId)
        assertEquals(7, created.days)
        assertFalse(bodyText.contains("product_id"))
        assertTrue(bodyText.contains("\"days\":7") || bodyText.contains("\"days\": 7"))
        assertTrue(bodyText.contains("\"price_paid\":12345") || bodyText.contains("\"price_paid\": 12345"))
    }

    @Test
    fun productTarget_sendsProductId() = runTest {
        var bodyText = ""
        val engine =
            MockEngine { request ->
                if (request.method == HttpMethod.Get) {
                    return@MockEngine jsonResponse(HttpStatusCode.OK, emptyBoostsBody)
                }
                bodyText = (request.body as TextContent).text
                jsonResponse(HttpStatusCode.Created, createBoostBody)
            }
        val (repo, _) = createBoostRepository(engine)
        repo.createBoost(
            CreateBoostCommand(
                shopId = ShopId(1),
                target = BoostTarget.Product(ProductId(9)),
                days = 7,
                pricePaid = 12345,
            ),
        )
        assertTrue(bodyText.contains("\"product_id\":9") || bodyText.contains("\"product_id\": 9"))
    }

    @Test
    fun create_timeout_isNotAutoRetried() = runTest {
        var attempts = 0
        val client =
            createHttpClient(
                config =
                    NetworkConfig(
                        apiEnvironment = ApiEnvironment(origin = "http://localhost:8080"),
                        diagnostics = NetworkDiagnosticsConfig(enableHttpLogging = false),
                        maxRetryCount = 2,
                    ),
                json = createNetworkJson(),
                sessionAuthCoordinator =
                    object : SessionAuthCoordinator {
                        override suspend fun resolveAccessToken(authMode: AuthMode) =
                            AppResult.Success("OLD_ACCESS")

                        override suspend fun handleUnauthorizedResponse(
                            authMode: AuthMode,
                            retryOnce: suspend () -> HttpResponse,
                        ) = AppResult.Failure(AppError.Authentication.Unauthorized())
                    },
                networkLogger = NoOpNetworkLogger,
                engine =
                    MockEngine {
                        attempts += 1
                        throw IOException("timeout")
                    },
            )
        val api =
            SellerBoostApi(
                client,
                ApiEnvironment(origin = "http://localhost:8080"),
                createSellerTestExecutor(),
            )
        val repo = DefaultSellerBoostRepository(api, SellerBoostStateStore(mutableListOf()))
        val result =
            repo.createBoost(
                CreateBoostCommand(ShopId(1), BoostTarget.Shop, days = 7, pricePaid = 1),
            )
        assertIs<AppResult.Failure>(result)
        assertEquals(1, attempts)
    }

    @Test
    fun duplicateCreate_whilePending_sendsOnePost() = runTest {
        val gate = CompletableDeferred<Unit>()
        var postCount = 0
        val engine =
            MockEngine { request ->
                if (request.method == HttpMethod.Get) {
                    return@MockEngine jsonResponse(HttpStatusCode.OK, emptyBoostsBody)
                }
                postCount += 1
                gate.await()
                jsonResponse(HttpStatusCode.Created, createBoostBody)
            }
        val (repo, _) = createBoostRepository(engine)
        val command = CreateBoostCommand(ShopId(1), BoostTarget.Shop, days = 7, pricePaid = 1)
        val first = async { repo.createBoost(command) }
        yield()
        val second = repo.createBoost(command)
        assertIs<AppResult.Failure>(second)
        assertIs<AppError.Conflict>(second.error)
        gate.complete(Unit)
        assertIs<AppResult.Success<*>>(first.await())
        assertEquals(1, postCount)
    }

    @Test
    fun emptyBoosts_mapToEmpty() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, emptyBoostsBody) }
        val (repo, _) = createBoostRepository(engine)
        val result = repo.getActiveBoosts(ShopId(1))
        assertEquals(ActiveBoosts.Empty, assertIs<AppResult.Success<ActiveBoosts>>(result).value)
    }

    @Test
    fun nonEmptyBoosts_areUnmappedNotInvented() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, nonEmptyUnknownBoostsBody) }
        val (repo, _) = createBoostRepository(engine)
        val result = repo.getActiveBoosts(ShopId(1))
        val unmapped = assertIs<ActiveBoosts.Unmapped>(assertIs<AppResult.Success<ActiveBoosts>>(result).value)
        assertEquals(1, unmapped.count)
    }

    @Test
    fun logout_clearsBoostCache() = runTest {
        val listeners = mutableListOf<SessionInvalidationListener>()
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, emptyBoostsBody) }
        val (repo, store) = createBoostRepository(engine, listeners)
        repo.getActiveBoosts(ShopId(1))
        assertEquals(ActiveBoosts.Empty, store.get(ShopId(1)))
        listeners.forEach { it.onSessionInvalidated() }
        assertEquals(null, store.get(ShopId(1)))
    }

    private fun createBoostRepository(
        engine: MockEngine,
        listeners: MutableList<SessionInvalidationListener> = mutableListOf(),
    ): Pair<DefaultSellerBoostRepository, SellerBoostStateStore> {
        val client = createSellerTestClient(engine)
        val api =
            SellerBoostApi(
                client,
                ApiEnvironment(origin = "http://localhost:8080"),
                createSellerTestExecutor(),
            )
        val store = SellerBoostStateStore(listeners)
        return DefaultSellerBoostRepository(api, store) to store
    }
}

private val createBoostBody =
    """
    {
      "success": true,
      "message": "عملیات با موفقیت انجام شد",
      "code": 1,
      "data": { "boost": { "id": 1, "shop_id": 1, "days": 7 } },
      "errors": []
    }
    """.trimIndent()

private val emptyBoostsBody =
    """
    {
      "success": true,
      "message": "عملیات با موفقیت انجام شد",
      "code": 1,
      "data": { "boosts": [] },
      "errors": []
    }
    """.trimIndent()

private val nonEmptyUnknownBoostsBody =
    """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": { "boosts": [ { "id": 1 } ] },
      "errors": []
    }
    """.trimIndent()
