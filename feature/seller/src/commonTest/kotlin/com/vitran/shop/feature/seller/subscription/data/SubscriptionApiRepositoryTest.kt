package com.vitran.shop.feature.seller.subscription.data

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.createSellerTestClient
import com.vitran.shop.feature.seller.createSellerTestExecutor
import com.vitran.shop.feature.seller.hasAuthBearer
import com.vitran.shop.feature.seller.jsonResponse
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.subscription.data.remote.SellerSubscriptionApi
import com.vitran.shop.feature.seller.subscription.data.repository.DefaultSubscriptionRepository
import com.vitran.shop.feature.seller.subscription.data.state.SubscriptionStateStore
import com.vitran.shop.feature.seller.subscription.domain.model.SubscriptionStatus
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.async
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.yield
import kotlinx.coroutines.test.runTest

class SubscriptionApiRepositoryTest {

    @Test
    fun paidSubscription_mapsFields_andRequiresAuth() = runTest {
        val engine =
            MockEngine { request ->
                assertEquals(HttpMethod.Get, request.method)
                assertTrue(request.hasAuthBearer("OLD_ACCESS"))
                jsonResponse(HttpStatusCode.OK, paidSubscriptionBody)
            }
        val (repo, store) = createSubscriptionRepository(engine)
        val result = repo.getSubscription(ShopId(1))
        assertIs<AppResult.Success<*>>(result)
        val sub = (result as AppResult.Success).value
        assertEquals(ShopId(1), sub.shopId)
        assertEquals(PlanId(2), sub.plan.id)
        assertEquals(SubscriptionStatus.Active, sub.status)
        assertEquals(12, sub.daysRemaining)
        assertEquals(store.get(ShopId(1)), sub)
    }

    @Test
    fun freeSubscription_nullExpiryIsValid() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, freeSubscriptionBody) }
        val (repo, _) = createSubscriptionRepository(engine)
        val sub = (repo.getSubscription(ShopId(1)) as AppResult.Success).value
        assertNull(sub.expiresAt)
        assertNull(sub.daysRemaining)
        assertNull(sub.plan.priceAmount)
    }

    @Test
    fun multiShop_subscriptionsRemainDistinct() = runTest {
        val engine =
            MockEngine { request ->
                val id = request.url.encodedPath.substringAfter("/shops/").substringBefore('/')
                jsonResponse(
                    HttpStatusCode.OK,
                    if (id == "1") paidSubscriptionBody else freeSubscriptionShop2Body,
                )
            }
        val (repo, store) = createSubscriptionRepository(engine)
        repo.getSubscription(ShopId(1))
        repo.getSubscription(ShopId(2))
        assertEquals(PlanId(2), store.get(ShopId(1))?.plan?.id)
        assertEquals(PlanId(1), store.get(ShopId(2))?.plan?.id)
    }

    @Test
    fun logout_clearsSubscriptionCache() = runTest {
        val listeners = mutableListOf<SessionInvalidationListener>()
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, paidSubscriptionBody) }
        val (repo, store) = createSubscriptionRepository(engine, listeners)
        repo.getSubscription(ShopId(1))
        assertTrue(store.byShopId.value.isNotEmpty())
        listeners.forEach { it.onSessionInvalidated() }
        assertTrue(store.byShopId.value.isEmpty())
    }

    @Test
    fun purchase_sendsPlanId_preservesPaymentUrl() = runTest {
        var bodyText = ""
        val engine =
            MockEngine { request ->
                assertEquals(HttpMethod.Post, request.method)
                assertTrue(request.hasAuthBearer("OLD_ACCESS"))
                assertTrue(request.url.encodedPath.endsWith("/subscription/purchase"))
                bodyText = (request.body as TextContent).text
                jsonResponse(HttpStatusCode.Created, purchaseBody)
            }
        val (repo, _) = createSubscriptionRepository(engine)
        val result = repo.startPlanPurchase(ShopId(1), PlanId(2))
        assertIs<AppResult.Success<*>>(result)
        val session = (result as AppResult.Success).value
        assertTrue(bodyText.contains("\"plan_id\":2") || bodyText.contains("\"plan_id\": 2"))
        assertEquals(
            "http://localhost:8080/payments/callback?Authority=mock-99000&Status=OK",
            session.paymentUrl,
        )
        assertFalse(session.paymentUrl.contains("/api/v1/payments/callback"))
    }

    @Test
    fun purchase_duplicateTap_secondFailsConflict() = runTest {
        val gate = CompletableDeferred<Unit>()
        var postCount = 0
        val engine =
            MockEngine {
                postCount += 1
                gate.await()
                jsonResponse(HttpStatusCode.Created, purchaseBody)
            }
        val (repo, _) = createSubscriptionRepository(engine)
        val first = async { repo.startPlanPurchase(ShopId(1), PlanId(2)) }
        yield()
        val second = repo.startPlanPurchase(ShopId(1), PlanId(2))
        assertIs<AppResult.Failure>(second)
        gate.complete(Unit)
        assertIs<AppResult.Success<*>>(first.await())
        assertEquals(1, postCount)
    }

    private fun createSubscriptionRepository(
        engine: MockEngine,
        listeners: MutableList<SessionInvalidationListener> = mutableListOf(),
    ): Pair<DefaultSubscriptionRepository, SubscriptionStateStore> {
        val client = createSellerTestClient(engine)
        val api =
            SellerSubscriptionApi(
                client,
                ApiEnvironment(origin = "http://localhost:8080"),
                createSellerTestExecutor(),
            )
        val store = SubscriptionStateStore(listeners)
        return DefaultSubscriptionRepository(api, store) to store
    }
}

internal val paidSubscriptionBody =
    """
    {
      "success": true, "message": "ok", "code": 1,
      "data": {
        "subscription": {
          "shop_id": 1,
          "plan": {
            "id": 2, "slug": "starter", "title": "Starter",
            "price_amount": 150000, "duration_days": 30,
            "max_products": 50, "max_images": 5, "max_shops": 1,
            "active": true, "sort_order": 2
          },
          "status": "active",
          "started_at": "2026-06-01T10:00:00Z",
          "expires_at": "2026-07-01T10:00:00Z",
          "days_remaining": 12
        }
      },
      "errors": []
    }
    """.trimIndent()

internal val freeSubscriptionBody =
    """
    {
      "success": true, "message": "ok", "code": 1,
      "data": {
        "subscription": {
          "shop_id": 1,
          "plan": {
            "id": 1, "slug": "free", "title": "Free",
            "max_products": 15, "max_images": 3, "max_shops": 1
          },
          "status": "active",
          "started_at": "2026-06-01T10:00:00Z",
          "expires_at": null,
          "days_remaining": null
        }
      },
      "errors": []
    }
    """.trimIndent()

internal val freeSubscriptionShop2Body =
    """
    {
      "success": true, "message": "ok", "code": 1,
      "data": {
        "subscription": {
          "shop_id": 2,
          "plan": {
            "id": 1, "slug": "free", "title": "Free",
            "max_products": 15, "max_images": 3, "max_shops": 1
          },
          "status": "active",
          "started_at": "2026-06-01T10:00:00Z",
          "expires_at": null,
          "days_remaining": null
        }
      },
      "errors": []
    }
    """.trimIndent()

internal val purchaseBody =
    """
    {
      "success": true, "message": "ok", "code": 1,
      "data": {
        "payment": {
          "payment_id": 1,
          "authority": "mock-99000",
          "payment_url": "http://localhost:8080/payments/callback?Authority=mock-99000&Status=OK"
        }
      },
      "errors": []
    }
    """.trimIndent()
