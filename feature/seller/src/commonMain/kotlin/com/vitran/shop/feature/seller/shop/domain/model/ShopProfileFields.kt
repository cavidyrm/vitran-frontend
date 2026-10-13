package com.vitran.shop.feature.seller.shop.domain.model

data class ShopRule(
    val title: String,
    val body: String,
)

data class ShopShippingMethod(
    val code: String,
    val title: String,
    val cost: Long? = null,
    val minDays: Int? = null,
    val maxDays: Int? = null,
    val active: Boolean? = null,
)

data class ShopWorkingHours(
    val day: String,
    val opensAt: String? = null,
    val closesAt: String? = null,
    val closed: Boolean = false,
)
