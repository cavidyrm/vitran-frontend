package com.vitran.shop.feature.seller.analytics.data

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.executor.FileDownloadExecutor
import com.vitran.shop.core.network.logging.NoOpNetworkLogger
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.analytics.data.mapper.toQueryValue
import com.vitran.shop.feature.seller.analytics.data.remote.SellerAnalyticsApi
import com.vitran.shop.feature.seller.analytics.data.repository.DefaultSellerAnalyticsRepository
import com.vitran.shop.feature.seller.analytics.data.state.SellerAnalyticsStateStore
import com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsPeriod
import com.vitran.shop.feature.seller.createSellerTestClient
import com.vitran.shop.feature.seller.hasAuthBearer
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SellerAnalyticsApiRepositoryTest {

    @Test
    fun export_usesPeriodQuery_andRequiresAuth() = runTest {
        val csv = "a,b\n1,2\n".encodeToByteArray()
        val engine =
            MockEngine { request ->
                assertEquals(HttpMethod.Get, request.method)
                assertTrue(request.hasAuthBearer("OLD_ACCESS"))
                assertTrue(request.url.encodedPath.endsWith("/seller/shops/1/analytics/export"))
                assertEquals("30d", request.url.parameters["period"])
                respond(
                    content = csv,
                    status = HttpStatusCode.OK,
                    headers =
                        headersOf(
                            HttpHeaders.ContentType to listOf("text/csv"),
                            HttpHeaders.ContentDisposition to listOf("attachment; filename=\"export.csv\""),
                        ),
                )
            }
        val (repo, _) = createAnalyticsRepository(engine)
        val result = repo.exportAnalytics(ShopId(1), AnalyticsPeriod.ThirtyDays)
        val export = assertIs<AppResult.Success<*>>(result).value as com.vitran.shop.feature.seller.analytics.domain.model.AnalyticsExport
        assertTrue(export.bytes.contentEquals(csv))
        assertEquals("export.csv", export.serverSuggestedFileName)
    }

    @Test
    fun export_sevenDays_serializesExactly() = runTest {
        val engine =
            MockEngine { request ->
                assertEquals("7d", request.url.parameters["period"])
                respond(
                    content = "x".encodeToByteArray(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "text/csv"),
                )
            }
        val (repo, _) = createAnalyticsRepository(engine)
        assertIs<AppResult.Success<*>>(repo.exportAnalytics(ShopId(1), AnalyticsPeriod.SevenDays))
        assertEquals("7d", AnalyticsPeriod.SevenDays.toQueryValue())
        assertEquals("30d", AnalyticsPeriod.ThirtyDays.toQueryValue())
    }

    @Test
    fun export_forbiddenJson_isNotReturnedAsCsv() = runTest {
        val engine =
            MockEngine {
                respond(
                    content =
                        """
                        {
                          "success": false,
                          "message": "forbidden",
                          "code": 403,
                          "data": null,
                          "errors": []
                        }
                        """.trimIndent(),
                    status = HttpStatusCode.Forbidden,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        val (repo, _) = createAnalyticsRepository(engine)
        val result = repo.exportAnalytics(ShopId(1), AnalyticsPeriod.ThirtyDays)
        val failure = assertIs<AppResult.Failure>(result)
        assertIs<AppError.Forbidden>(failure.error)
    }

    @Test
    fun logout_clearsAnalyticsStore() = runTest {
        val listeners = mutableListOf<SessionInvalidationListener>()
        val engine =
            MockEngine {
                respond(
                    content = "ok".encodeToByteArray(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "text/csv"),
                )
            }
        val (repo, store) = createAnalyticsRepository(engine, listeners)
        repo.exportAnalytics(ShopId(1), AnalyticsPeriod.SevenDays)
        assertTrue(store.hasExportAttempt(ShopId(1), AnalyticsPeriod.SevenDays))
        listeners.forEach { it.onSessionInvalidated() }
        assertFalse(store.hasExportAttempt(ShopId(1), AnalyticsPeriod.SevenDays))
    }

    private fun createAnalyticsRepository(
        engine: MockEngine,
        listeners: MutableList<SessionInvalidationListener> = mutableListOf(),
    ): Pair<DefaultSellerAnalyticsRepository, SellerAnalyticsStateStore> {
        val client = createSellerTestClient(engine)
        val api =
            SellerAnalyticsApi(
                client,
                ApiEnvironment(origin = "http://localhost:8080"),
                FileDownloadExecutor(json = createNetworkJson(), logger = NoOpNetworkLogger),
            )
        val store = SellerAnalyticsStateStore(listeners)
        return DefaultSellerAnalyticsRepository(api, store) to store
    }
}
