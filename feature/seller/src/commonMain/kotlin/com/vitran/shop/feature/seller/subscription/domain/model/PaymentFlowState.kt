package com.vitran.shop.feature.seller.subscription.domain.model

/**
 * Presentation/application payment lifecycle. Opening a payment URL ≠ Confirmed.
 */
sealed class PaymentFlowState {
    data object Idle : PaymentFlowState()

    data object Initiating : PaymentFlowState()

    data class AwaitingExternalPayment(
        val session: PaymentSession,
        val targetPlanId: com.vitran.shop.feature.seller.plan.domain.model.PlanId,
        val baseline: ShopSubscription?,
    ) : PaymentFlowState()

    data class Verifying(
        val session: PaymentSession,
        val targetPlanId: com.vitran.shop.feature.seller.plan.domain.model.PlanId,
        val baseline: ShopSubscription?,
    ) : PaymentFlowState()

    data class Confirmed(
        val subscription: ShopSubscription,
    ) : PaymentFlowState()

    data class NotYetConfirmed(
        val session: PaymentSession,
        val targetPlanId: com.vitran.shop.feature.seller.plan.domain.model.PlanId,
        val baseline: ShopSubscription?,
    ) : PaymentFlowState()

    data class Error(
        val message: String?,
        /** Retained session for launch retry when initiation succeeded. */
        val session: PaymentSession? = null,
        val targetPlanId: com.vitran.shop.feature.seller.plan.domain.model.PlanId? = null,
        val baseline: ShopSubscription? = null,
    ) : PaymentFlowState()
}
