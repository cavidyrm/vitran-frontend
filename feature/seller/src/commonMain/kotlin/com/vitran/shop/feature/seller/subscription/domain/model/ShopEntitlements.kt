package com.vitran.shop.feature.seller.subscription.domain.model

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.plan.domain.model.PlanCapabilities
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanLimits
import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug
import kotlinx.datetime.Instant

/**
 * Derived commercial entitlements for a shop. Not persisted as a separate source of truth.
 */
data class ShopEntitlements(
    val shopId: ShopId,
    val planId: PlanId,
    val planSlug: PlanSlug,
    val planTitle: String,
    val limits: PlanLimits,
    val capabilities: PlanCapabilities,
    val subscriptionStatus: SubscriptionStatus,
    val expiresAt: Instant?,
    val daysRemaining: Int?,
)
