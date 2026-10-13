package com.vitran.shop.feature.seller.shop.domain.model

import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug

/**
 * Domain create command. [slug] null/blank means omit for server auto-generation.
 * [categorySlugs] are taxonomy slugs from `GET /categories` (string ids such as `aa-1-2-3-4`).
 */
data class CreateShopCommand(
    val title: String,
    val slug: ShopSlug? = null,
    val avatarUrl: String? = null,
    val description: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val supportTimes: String? = null,
    /** Shop business type string; Postman proves `"retailer"` only. */
    val type: String,
    val cityId: CityId,
    val categorySlugs: List<String> = emptyList(),
    val seoTitle: String? = null,
    val seoDescription: String? = null,
    val noindex: Boolean? = null,
    val rules: List<ShopRule> = emptyList(),
    val shippingMethods: List<ShopShippingMethod> = emptyList(),
    val workingHours: List<ShopWorkingHours> = emptyList(),
    val fulfillmentType: String? = null,
    val webhookUrl: String? = null,
    val whatsapp: String? = null,
    val bale: String? = null,
    val rubika: String? = null,
    val telegram: String? = null,
    val instagram: String? = null,
    val tiktok: String? = null,
    val website: String? = null,
)
