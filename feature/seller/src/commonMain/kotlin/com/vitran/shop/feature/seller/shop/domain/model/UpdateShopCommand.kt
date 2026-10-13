package com.vitran.shop.feature.seller.shop.domain.model

import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug

/**
 * Partial shop update. Null optional strings and lists are omitted.
 * An empty string clears a stored optional string. An empty list clears that list.
 * [clearAvatarUrl] and [clearWebhookUrl] send the explicit clear flags.
 */
data class UpdateShopCommand(
    val shopId: ShopId,
    val title: String? = null,
    val slug: ShopSlug? = null,
    val avatarUrl: String? = null,
    val description: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val supportTimes: String? = null,
    val type: String? = null,
    val cityId: CityId? = null,
    val categorySlugs: List<String>? = null,
    val seoTitle: String? = null,
    val seoDescription: String? = null,
    val noindex: Boolean? = null,
    val rules: List<ShopRule>? = null,
    val shippingMethods: List<ShopShippingMethod>? = null,
    val workingHours: List<ShopWorkingHours>? = null,
    val fulfillmentType: String? = null,
    val webhookUrl: String? = null,
    val clearAvatarUrl: Boolean = false,
    val clearWebhookUrl: Boolean = false,
    val whatsapp: String? = null,
    val bale: String? = null,
    val rubika: String? = null,
    val telegram: String? = null,
    val instagram: String? = null,
    val tiktok: String? = null,
    val website: String? = null,
)
