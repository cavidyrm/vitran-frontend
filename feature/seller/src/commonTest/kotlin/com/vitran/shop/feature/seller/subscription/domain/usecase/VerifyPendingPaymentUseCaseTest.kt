package com.vitran.shop.feature.seller.subscription.domain.usecase

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanLimits
import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug
import com.vitran.shop.feature.seller.subscription.domain.model.PaymentSession
import com.vitran.shop.feature.seller.subscription.domain.model.PaymentId
import com.vitran.shop.feature.seller.subscription.domain.model.ShopSubscription
import com.vitran.shop.feature.seller.subscription.domain.model.SubscriptionPlan
import com.vitran.shop.feature.seller.subscription.domain.model.SubscriptionStatus
import com.vitran.shop.feature.seller.subscription.domain.repository.SubscriptionRepository
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant

class VerifyPendingPaymentUseCaseTest {

    @Test
    fun planChange_confirmsWhenTargetPlanActive() = runTest {
        val baseline = sampleSub(PlanId(1), Instant.parse("2026-07-01T10:00:00Z"))
        val after = sampleSub(PlanId(2), Instant.parse("2026-08-01T10:00:00Z"))
        val uc = VerifyPendingPaymentUseCase(FakeSubRepo(after))
        val result = uc(ShopId(1), PlanId(2), baseline)
        assertIs<PaymentVerificationResult.Confirmed>(result)
    }

    @Test
    fun samePlanRenewal_confirmsWhenExpiresLater() = runTest {
        val baseline = sampleSub(PlanId(2), Instant.parse("2026-07-01T10:00:00Z"))
        val after = sampleSub(PlanId(2), Instant.parse("2026-08-01T10:00:00Z"))
        val uc = VerifyPendingPaymentUseCase(FakeSubRepo(after))
        assertIs<PaymentVerificationResult.Confirmed>(uc(ShopId(1), PlanId(2), baseline))
    }

    @Test
    fun unchanged_isNotYetConfirmed() = runTest {
        val baseline = sampleSub(PlanId(2), Instant.parse("2026-07-01T10:00:00Z"))
        val uc = VerifyPendingPaymentUseCase(FakeSubRepo(baseline))
        assertIs<PaymentVerificationResult.NotYetConfirmed>(uc(ShopId(1), PlanId(2), baseline))
    }

    @Test
    fun networkError_retainsAsVerificationError() = runTest {
        val uc =
            VerifyPendingPaymentUseCase(
                FakeSubRepo(null, AppResult.Failure(AppError.Network.Timeout())),
            )
        assertIs<PaymentVerificationResult.VerificationError>(
            uc(ShopId(1), PlanId(2), null),
        )
    }
}

private fun sampleSub(planId: PlanId, expires: Instant?) =
    ShopSubscription(
        shopId = ShopId(1),
        plan =
            SubscriptionPlan(
                id = planId,
                slug = PlanSlug.of(if (planId.value == 1L) "free" else "starter"),
                title = "P",
                priceAmount = 150000,
                durationDays = 30,
                limits = PlanLimits(50, 5, 1),
            ),
        status = SubscriptionStatus.Active,
        startedAt = Instant.parse("2026-06-01T10:00:00Z"),
        expiresAt = expires,
        daysRemaining = 12,
    )

private class FakeSubRepo(
    private val sub: ShopSubscription?,
    private val failure: AppResult<ShopSubscription>? = null,
) : SubscriptionRepository {
    override val subscriptionsByShopId: StateFlow<Map<ShopId, ShopSubscription>> =
        MutableStateFlow(emptyMap())

    override suspend fun getSubscription(shopId: ShopId, forceRefresh: Boolean) =
        failure ?: AppResult.Success(sub!!)

    override suspend fun refreshSubscription(shopId: ShopId) =
        getSubscription(shopId, true)

    override suspend fun startPlanPurchase(shopId: ShopId, planId: PlanId) =
        AppResult.Success(PaymentSession(PaymentId(1), "a", "http://localhost/p"))
}
