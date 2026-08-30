package com.vitran.shop.feature.seller.plan.domain.model

/**
 * Server-driven plan limits. Values are authoritative integers from the API.
 * Do not assume `0` means unlimited unless the backend documents that.
 */
data class PlanLimits(
    val maxProducts: Int,
    val maxImages: Int,
    val maxShops: Int,
)
