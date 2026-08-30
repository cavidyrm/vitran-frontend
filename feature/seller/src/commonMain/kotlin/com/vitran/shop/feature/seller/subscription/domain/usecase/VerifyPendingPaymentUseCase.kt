package com.vitran.shop.feature.seller.subscription.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.subscription.domain.model.ShopSubscription
import com.vitran.shop.feature.seller.subscription.domain.repository.SubscriptionRepository

sealed class PaymentVerificationResult {
    data class Confirmed(val subscription: ShopSubscription) : PaymentVerificationResult()

    data class NotYetConfirmed(val subscription: ShopSubscription) : PaymentVerificationResult()

    /** Network/other error — pending payment state must be retained. */
    data class VerificationError(val error: com.vitran.shop.core.domain.error.AppError) :
        PaymentVerificationResult()
}

/**
 * Reconciles payment outcome via authoritative ShopSubscription refresh.
 * Does not call /payments/callback.
 */
class VerifyPendingPaymentUseCase(
    private val subscriptionRepository: SubscriptionRepository,
) {
    suspend operator fun invoke(
        shopId: ShopId,
        targetPlanId: PlanId,
        baseline: ShopSubscription?,
    ): PaymentVerificationResult {
        val subscription =
            when (val result = subscriptionRepository.refreshSubscription(shopId)) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return PaymentVerificationResult.VerificationError(result.error)
            }

        if (isConfirmed(subscription, targetPlanId, baseline)) {
            return PaymentVerificationResult.Confirmed(subscription)
        }
        return PaymentVerificationResult.NotYetConfirmed(subscription)
    }

    private fun isConfirmed(
        current: ShopSubscription,
        targetPlanId: PlanId,
        baseline: ShopSubscription?,
    ): Boolean {
        if (current.plan.id != targetPlanId) return false

        // Plan change: target plan now active.
        if (baseline == null || baseline.plan.id != targetPlanId) {
            return current.plan.id == targetPlanId
        }

        // Same-plan renewal: expiresAt must extend beyond baseline.
        val baselineExpiry = baseline.expiresAt
        val currentExpiry = current.expiresAt
        if (baselineExpiry != null && currentExpiry != null) {
            return currentExpiry > baselineExpiry
        }

        // If both null (unexpected for paid), not confirmed.
        return false
    }
}
