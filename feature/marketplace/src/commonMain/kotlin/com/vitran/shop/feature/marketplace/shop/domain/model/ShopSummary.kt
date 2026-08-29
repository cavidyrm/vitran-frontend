package com.vitran.shop.feature.marketplace.shop.domain.model

data class ShopSummary(
    val id: ShopId,
    val slug: ShopSlug,
    val title: String?,
    val active: Boolean = true,
    val confirmed: Boolean = true,
    val plan: ShopPlanSummary? = null,
)
