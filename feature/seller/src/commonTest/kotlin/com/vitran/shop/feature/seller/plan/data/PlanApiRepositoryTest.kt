package com.vitran.shop.feature.seller.plan.data

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.seller.createSellerTestClient
import com.vitran.shop.feature.seller.createSellerTestExecutor
import com.vitran.shop.feature.seller.jsonResponse
import com.vitran.shop.feature.seller.plan.data.remote.PlanApi
import com.vitran.shop.feature.seller.plan.data.repository.DefaultPlanRepository
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.RankingBoostLevel
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class PlanApiRepositoryTest {

    @Test
    fun getPlans_usesAuthModeNone_preservesOrder_mapsFreeAndPaid() = runTest {
        var requestCount = 0
        val engine =
            MockEngine { request ->
                requestCount += 1
                assertEquals(HttpMethod.Get, request.method)
                assertEquals(null, request.headers["Authorization"])
                assertTrue(request.url.encodedPath.endsWith("/plans"))
                jsonResponse(HttpStatusCode.OK, plansListBody)
            }
        val repo = createPlanRepository(engine)
        val result = repo.getPlans()
        assertIs<AppResult.Success<*>>(result)
        val plans = (result as AppResult.Success).value
        assertEquals(2, plans.size)
        assertEquals(PlanId(1), plans[0].id)
        assertEquals(0L, plans[0].priceAmount)
        assertNull(plans[0].durationDays)
        assertEquals(15, plans[0].limits.maxProducts)
        assertEquals(RankingBoostLevel.None, plans[0].capabilities.rankingBoost)

        assertEquals(PlanId(2), plans[1].id)
        assertEquals(150_000L, plans[1].priceAmount)
        assertEquals(30, plans[1].durationDays)
        assertEquals(RankingBoostLevel.Slight, plans[1].capabilities.rankingBoost)
        assertTrue(plans[1].capabilities.contactButtons)

        // Cache: second read does not hit network
        repo.getPlans()
        assertEquals(1, requestCount)

        // Explicit refresh hits network
        repo.refreshPlans()
        assertEquals(2, requestCount)
    }

    @Test
    fun getPlan_mapsDescriptionAndFeatures() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, planDetailBody) }
        val repo = createPlanRepository(engine)
        val result = repo.getPlan(PlanId(2))
        assertIs<AppResult.Success<*>>(result)
        val plan = (result as AppResult.Success).value
        assertEquals("For small shops getting serious online.", plan.description)
        assertTrue(plan.capabilities.basicAnalytics)
        assertTrue(plan.capabilities.offersDiscounts)
    }

    @Test
    fun getPlan_inactive_returnsFailure() = runTest {
        val engine =
            MockEngine {
                jsonResponse(
                    HttpStatusCode.NotFound,
                    """{"success":false,"message":"not found","code":0,"data":null,"errors":[]}""",
                )
            }
        val repo = createPlanRepository(engine)
        val result = repo.getPlan(PlanId(99))
        assertIs<AppResult.Failure>(result)
        assertIs<AppError>(result.error)
    }

    private fun createPlanRepository(engine: MockEngine): DefaultPlanRepository {
        val client = createSellerTestClient(engine)
        val api = PlanApi(client, ApiEnvironment(origin = "http://localhost:8080"), createSellerTestExecutor())
        return DefaultPlanRepository(api)
    }
}

internal val plansListBody =
    """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "plans": [
          {
            "id": 1,
            "slug": "free",
            "title": "Free",
            "price_amount": 0,
            "max_products": 15,
            "max_images": 3,
            "max_shops": 1,
            "features": { "ranking_boost": "none" },
            "active": true,
            "sort_order": 1
          },
          {
            "id": 2,
            "slug": "starter",
            "title": "Starter",
            "price_amount": 150000,
            "duration_days": 30,
            "max_products": 50,
            "max_images": 5,
            "max_shops": 1,
            "features": { "ranking_boost": "slight", "contact_buttons": true },
            "active": true,
            "sort_order": 2
          }
        ]
      },
      "errors": []
    }
    """.trimIndent()

internal val planDetailBody =
    """
    {
      "success": true,
      "message": "ok",
      "code": 1,
      "data": {
        "plan": {
          "id": 2,
          "slug": "starter",
          "title": "Starter",
          "description": "For small shops getting serious online.",
          "price_amount": 150000,
          "duration_days": 30,
          "max_products": 50,
          "max_images": 5,
          "max_shops": 1,
          "features": {
            "ranking_boost": "slight",
            "contact_buttons": true,
            "basic_analytics": true,
            "offers_discounts": true
          },
          "active": true,
          "sort_order": 2
        }
      },
      "errors": []
    }
    """.trimIndent()
