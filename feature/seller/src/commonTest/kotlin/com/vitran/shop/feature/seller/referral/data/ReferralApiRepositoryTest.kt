package com.vitran.shop.feature.seller.referral.data

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.createSellerTestClient
import com.vitran.shop.feature.seller.createSellerTestExecutor
import com.vitran.shop.feature.seller.hasAuthBearer
import com.vitran.shop.feature.seller.jsonResponse
import com.vitran.shop.feature.seller.referral.data.remote.ReferralApi
import com.vitran.shop.feature.seller.referral.data.repository.DefaultReferralRepository
import com.vitran.shop.feature.seller.referral.data.state.ReferralStateStore
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCode
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCodeValidation
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditId
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditStatus
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class ReferralApiRepositoryTest {

    @Test
    fun validateCode_validTrue() = runTest {
        val engine =
            MockEngine {
                assertEquals(null, it.headers["Authorization"])
                jsonResponse(HttpStatusCode.OK, """{"success":true,"message":"ok","code":1,"data":{"valid":true},"errors":[]}""")
            }
        val repo = createReferralRepo(engine)
        val result = repo.validateCode(ReferralCode("V2"))
        assertEquals(ReferralCodeValidation.Valid, (result as AppResult.Success).value)
    }

    @Test
    fun validateCode_validFalse_isNotHttpError() = runTest {
        val engine =
            MockEngine {
                jsonResponse(HttpStatusCode.OK, """{"success":true,"message":"ok","code":1,"data":{"valid":false},"errors":[]}""")
            }
        val repo = createReferralRepo(engine)
        val result = repo.validateCode(ReferralCode("NOPE"))
        assertEquals(ReferralCodeValidation.Invalid, (result as AppResult.Success).value)
    }

    @Test
    fun getProfile_mapsFieldsAndTimestamps() = runTest {
        val engine =
            MockEngine { request ->
                assertTrue(request.hasAuthBearer("OLD_ACCESS"))
                jsonResponse(HttpStatusCode.OK, referralProfileBody)
            }
        val repo = createReferralRepo(engine)
        val profile = (repo.getProfile() as AppResult.Success).value
        assertEquals("V2", profile.code.value)
        assertEquals("https://vitran.ir/signup?ref=V2", profile.inviteUrl)
        assertEquals(1, profile.stats.availableCredits)
        assertEquals(1, profile.successfulReferrals.size)
        assertEquals(1, profile.pendingReferrals.size)
        assertIs<ReferralCreditStatus.Available>(profile.credits.first().status)
    }

    @Test
    fun applyCredit_sendsShopId() = runTest {
        var body = ""
        val engine =
            MockEngine { request ->
                assertEquals(HttpMethod.Post, request.method)
                body = (request.body as TextContent).text
                jsonResponse(HttpStatusCode.OK, """{"success":true,"message":"ok","code":1,"data":{},"errors":[]}""")
            }
        val repo = createReferralRepo(engine)
        val result = repo.applyCredit(ReferralCreditId(1), ShopId(1))
        assertIs<AppResult.Success<*>>(result)
        assertTrue(body.contains("\"shop_id\":1") || body.contains("\"shop_id\": 1"))
    }

    @Test
    fun logout_clearsReferralProfile() = runTest {
        val listeners = mutableListOf<SessionInvalidationListener>()
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, referralProfileBody) }
        val store = ReferralStateStore(listeners)
        val client = createSellerTestClient(engine)
        val api = ReferralApi(client, ApiEnvironment(origin = "http://localhost:8080"), createSellerTestExecutor())
        val repo = DefaultReferralRepository(api, store)
        repo.getProfile()
        assertTrue(store.profile.value != null)
        listeners.forEach { it.onSessionInvalidated() }
        assertEquals(null, store.profile.value)
    }

    private fun createReferralRepo(engine: MockEngine): DefaultReferralRepository {
        val client = createSellerTestClient(engine)
        val api = ReferralApi(client, ApiEnvironment(origin = "http://localhost:8080"), createSellerTestExecutor())
        return DefaultReferralRepository(api, ReferralStateStore(mutableListOf()))
    }
}

internal val referralProfileBody =
    """
    {
      "success": true, "message": "ok", "code": 1,
      "data": {
        "referral": {
          "referral_code": "V2",
          "invite_url": "https://vitran.ir/signup?ref=V2",
          "stats": {
            "total_referrals": 2,
            "rewarded_referrals": 1,
            "pending_referrals": 1,
            "available_credits": 1
          },
          "successful_referrals": [{
            "id": 1, "referred_user_id": 5, "phone_masked": "0912***6789",
            "status": "successful",
            "signed_up_at": "2026-06-01T10:00:00Z",
            "rewarded_at": "2026-06-05T14:00:00Z"
          }],
          "pending_referrals": [{
            "id": 2, "referred_user_id": 6, "phone_masked": "0913***4321",
            "status": "pending",
            "signed_up_at": "2026-06-08T09:00:00Z"
          }],
          "credits": [{
            "id": 1, "plan_id": 2, "plan_title": "Starter", "duration_days": 30,
            "source": "referral_referrer", "status": "available",
            "created_at": "2026-06-09T14:00:00Z"
          }]
        }
      },
      "errors": []
    }
    """.trimIndent()
