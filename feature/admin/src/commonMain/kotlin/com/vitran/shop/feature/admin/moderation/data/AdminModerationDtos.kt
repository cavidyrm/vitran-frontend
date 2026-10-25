package com.vitran.shop.feature.admin.moderation.data

import com.vitran.shop.core.network.pagination.PageDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.ShopRuleDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.ShopShippingMethodDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.ShopWorkingHoursDto
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

@Serializable
internal data class AdminShopSummaryDto(
    val id: Long,
    val slug: String,
    val active: Boolean,
    val confirmed: Boolean,
    val title: String? = null,
    @SerialName("owner_id") val ownerId: Long? = null,
    val type: String? = null,
    @SerialName("share_url") val shareUrl: String? = null,
    /** May be string slugs or numeric ids (Gap 1); keep as JsonElement. */
    @OptIn(ExperimentalSerializationApi::class)
    @SerialName("category_slugs")
    @JsonNames("CategorySlugs")
    val categorySlugs: List<JsonElement> = emptyList(),
    @SerialName("updated_at") val updatedAt: String? = null,
    val description: String? = null,
    val address: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("support_times") val supportTimes: String? = null,
    @SerialName("city_id") val cityId: Long? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("seo_title") val seoTitle: String? = null,
    @SerialName("seo_description") val seoDescription: String? = null,
    val noindex: Boolean? = null,
    val rules: List<ShopRuleDto> = emptyList(),
    @SerialName("shipping_methods") val shippingMethods: List<ShopShippingMethodDto> = emptyList(),
    @SerialName("working_hours") val workingHours: List<ShopWorkingHoursDto> = emptyList(),
    @SerialName("fulfillment_type") val fulfillmentType: String? = null,
    @SerialName("webhook_url") val webhookUrl: String? = null,
    val whatsapp: String? = null,
    val telegram: String? = null,
    val instagram: String? = null,
    val website: String? = null,
    val bale: String? = null,
    val rubika: String? = null,
    val tiktok: String? = null,
)

@Serializable
internal data class AdminProductSummaryDto(
    val id: Long,
    @SerialName("shop_id") val shopId: Long,
    @SerialName("category_slug") val categorySlug: String? = null,
    val title: String,
    val price: Long? = null,
    val active: Boolean,
    val confirmed: Boolean,
    val images: List<JsonElement> = emptyList(),
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
internal data class AdminProductImageDto(
    val id: Long,
    val url: String,
    @SerialName("sort_order") val sortOrder: Int = 0,
    @SerialName("is_main") val isMain: Boolean = false,
)

@Serializable
internal data class AdminProductDetailsDto(
    val id: Long,
    @SerialName("shop_id") val shopId: Long,
    @SerialName("category_slug") val categorySlug: String? = null,
    val title: String,
    val description: String? = null,
    val price: Long? = null,
    @SerialName("compare_at_price") val compareAtPrice: Long? = null,
    val active: Boolean,
    val confirmed: Boolean,
    val slug: String? = null,
    @SerialName("seo_title") val seoTitle: String? = null,
    @SerialName("seo_description") val seoDescription: String? = null,
    val noindex: Boolean? = null,
    @SerialName("main_image") val mainImage: AdminProductImageDto? = null,
    /** Objects become images; non-objects are ignored. */
    val images: List<JsonElement> = emptyList(),
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
internal data class AdminCommentConfirmDto(
    val id: Long,
    @SerialName("shop_id") val shopId: Long,
    @SerialName("user_id") val userId: Long,
    val title: String,
    val confirmed: Boolean,
)

@Serializable
internal data class AdminShopsDataDto(val shops: PageDto<AdminShopSummaryDto>)

@Serializable
internal data class AdminProductsDataDto(val products: PageDto<AdminProductSummaryDto>)

@Serializable
internal data class AdminShopDataDto(val shop: AdminShopSummaryDto)

@Serializable
internal data class AdminProductDataDto(val product: AdminProductDetailsDto)

@Serializable
internal data class AdminCommentDataDto(val comment: AdminCommentConfirmDto)
