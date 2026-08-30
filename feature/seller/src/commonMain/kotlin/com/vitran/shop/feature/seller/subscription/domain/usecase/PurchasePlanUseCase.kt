package com.vitran.shop.feature.seller.subscription.domain.usecase

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.repository.PlanRepository
import com.vitran.shop.feature.seller.subscription.domain.model.PaymentSession
import com.vitran.shop.feature.seller.subscription.domain.model.PurchasePlanCommand
import com.vitran.shop.feature.seller.subscription.domain.model.ShopSubscription
import com.vitran.shop.feature.seller.subscription.domain.repository.SubscriptionRepository

data class PurchasePlanResult(
    val session: PaymentSession,
    val baseline: ShopSubscription?,
    val targetPlanId: PlanId,
)

/**
 * Starts plan purchase. Never opens browsers. Never calls payment callback.
 * Does not mutate subscription. Non-idempotent — do not auto-retry.
 */
class PurchasePlanUseCase(
    private val subscriptionRepository: SubscriptionRepository,
    private val planRepository: PlanRepository,
) {
    suspend operator fun invoke(command: PurchasePlanCommand): AppResult<PurchasePlanResult> {
        // Only allow purchase of plans present in the public catalog.
        val plans =
            when (val result = planRepository.getPlans()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return AppResult.Failure(result.error)
            }
        val selected = plans.firstOrNull { it.id == command.planId }
            ?: return AppResult.Failure(AppError.NotFound(message = "Plan unavailable"))

        if (selected.slug.isFree) {
            return AppResult.Failure(
                AppError.Validation(message = "Free plan cannot be purchased"),
            )
        }

        val baseline =
            when (val sub = subscriptionRepository.getSubscription(command.shopId)) {
                is AppResult.Success -> sub.value
                is AppResult.Failure -> null // still allow purchase if subscription load fails
            }

        return when (val result = subscriptionRepository.startPlanPurchase(command.shopId, command.planId)) {
            is AppResult.Success ->
                AppResult.Success(
                    PurchasePlanResult(
                        session = result.value,
                        baseline = baseline,
                        targetPlanId = command.planId,
                    ),
                )
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }
}
