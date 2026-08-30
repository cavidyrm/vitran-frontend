package com.vitran.shop.feature.seller.subscription.domain.model

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanLimits
import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug
import kotlinx.datetime.Instant

sealed class SubscriptionStatus {
    data object Active : SubscriptionStatus()
    data class Unknown(val raw: String) : SubscriptionStatus()

    companion object {
        fun parse(raw: String): SubscriptionStatus =
            when (raw.lowercase()) {
                "active" -> Active
                else -> Unknown(raw)
            }
    }
}

/**
 * Plan projection embedded in subscription responses — may omit features/description.
 */
data class SubscriptionPlan(
    val id: PlanId,
    val slug: PlanSlug,
    val title: String,
    val priceAmount: Long?,
    val durationDays: Int?,
    val limits: PlanLimits,
)

data class ShopSubscription(
    val shopId: ShopId,
    val plan: SubscriptionPlan,
    val status: SubscriptionStatus,
    val startedAt: Instant,
    val expiresAt: Instant?,
    val daysRemaining: Int?,
)
