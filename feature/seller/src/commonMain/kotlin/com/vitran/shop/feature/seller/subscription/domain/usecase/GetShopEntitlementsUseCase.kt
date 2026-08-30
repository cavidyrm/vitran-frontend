package com.vitran.shop.feature.seller.subscription.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.plan.domain.model.PlanCapabilities
import com.vitran.shop.feature.seller.plan.domain.repository.PlanRepository
import com.vitran.shop.feature.seller.subscription.domain.model.ShopEntitlements
import com.vitran.shop.feature.seller.subscription.domain.repository.SubscriptionRepository

/**
 * Composes ShopSubscription + public Plan catalog capabilities when available.
 * Subscription remains usable even if public plan detail is missing.
 */
class GetShopEntitlementsUseCase(
    private val subscriptionRepository: SubscriptionRepository,
    private val planRepository: PlanRepository,
) {
    suspend operator fun invoke(
        shopId: ShopId,
        forceRefresh: Boolean = false,
    ): AppResult<ShopEntitlements> {
        val subscription =
            when (val result = subscriptionRepository.getSubscription(shopId, forceRefresh)) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return AppResult.Failure(result.error)
            }

        val capabilities =
            when (val planResult = planRepository.getPlans(forceRefresh = false)) {
                is AppResult.Success ->
                    planResult.value
                        .firstOrNull { it.id == subscription.plan.id }
                        ?.capabilities
                        ?: PlanCapabilities()
                is AppResult.Failure -> PlanCapabilities()
            }

        return AppResult.Success(
            ShopEntitlements(
                shopId = subscription.shopId,
                planId = subscription.plan.id,
                planSlug = subscription.plan.slug,
                planTitle = subscription.plan.title,
                limits = subscription.plan.limits,
                capabilities = capabilities,
                subscriptionStatus = subscription.status,
                expiresAt = subscription.expiresAt,
                daysRemaining = subscription.daysRemaining,
            ),
        )
    }
}
