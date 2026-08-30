package com.vitran.shop.feature.seller.shop.domain.model

import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug

/**
 * Domain create command. [slug] null/blank means omit for server auto-generation.
 * [categoryNumericIds] are Postman `category_slugs` longs — empty until taxonomy exposes IDs.
 */
data class CreateShopCommand(
    val title: String,
    val slug: ShopSlug? = null,
    val description: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val supportTimes: String? = null,
    /** Shop business type string; Postman proves `"retailer"` only. */
    val type: String,
    val cityId: CityId,
    val categoryNumericIds: List<Long> = emptyList(),
    val whatsapp: String? = null,
    val telegram: String? = null,
    val instagram: String? = null,
    val website: String? = null,
)
