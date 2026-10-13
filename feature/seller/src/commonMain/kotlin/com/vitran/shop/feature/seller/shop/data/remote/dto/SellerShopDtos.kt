package com.vitran.shop.feature.seller.shop.data.remote.dto

import com.vitran.shop.core.network.pagination.CursorPageDto
import com.vitran.shop.feature.marketplace.common.data.serializer.FlexibleCategorySlugListSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class SlugCheckDataDto(
    @SerialName("slug_check") val slugCheck: SlugCheckDto,
)

@Serializable
data class SlugCheckDto(
    val slug: String,
    val available: Boolean,
)

@Serializable
data class TitleCheckDataDto(
    @SerialName("title_check") val titleCheck: TitleCheckDto,
)

@Serializable
data class TitleCheckDto(
    val title: String,
    val available: Boolean,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CreateShopRequestDto(
    val title: String,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val slug: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val description: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val address: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("support_times")
    val supportTimes: String? = null,
    val type: String,
    @SerialName("city_id") val cityId: Long,
    @Serializable(with = FlexibleCategorySlugListSerializer::class)
    @SerialName("category_slugs")
    val categorySlugs: List<String> = emptyList(),
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("seo_title")
    val seoTitle: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("seo_description")
    val seoDescription: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val noindex: Boolean? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val rules: List<ShopRuleDto>? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("shipping_methods")
    val shippingMethods: List<ShopShippingMethodDto>? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("working_hours")
    val workingHours: List<ShopWorkingHoursDto>? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("fulfillment_type")
    val fulfillmentType: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("webhook_url")
    val webhookUrl: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val whatsapp: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val bale: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val rubika: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val telegram: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val instagram: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val tiktok: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val website: String? = null,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class UpdateShopRequestDto(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val title: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val slug: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val description: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val address: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("support_times")
    val supportTimes: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val type: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("city_id")
    val cityId: Long? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @Serializable(with = FlexibleCategorySlugListSerializer::class)
    @SerialName("category_slugs")
    val categorySlugs: List<String>? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("seo_title")
    val seoTitle: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("seo_description")
    val seoDescription: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val noindex: Boolean? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val rules: List<ShopRuleDto>? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("shipping_methods")
    val shippingMethods: List<ShopShippingMethodDto>? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("working_hours")
    val workingHours: List<ShopWorkingHoursDto>? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("fulfillment_type")
    val fulfillmentType: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("webhook_url")
    val webhookUrl: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("clear_avatar_url")
    val clearAvatarUrl: Boolean? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("clear_webhook_url")
    val clearWebhookUrl: Boolean? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val whatsapp: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val bale: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val rubika: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val telegram: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val instagram: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val tiktok: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val website: String? = null,
)

@Serializable
data class CreateShopDataDto(
    val tokens: CreateShopTokensDto? = null,
    val shop: SellerShopCreateResponseDto,
)

@Serializable
data class CreateShopTokensDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_at") val expiresAt: String,
)

@Serializable
data class SellerShopCreateResponseDto(
    val id: Long,
    @SerialName("owner_id") val ownerId: Long,
    @SerialName("city_id") val cityId: Long,
    val title: String,
    val slug: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val description: String? = null,
    val type: String,
    @SerialName("share_url") val shareUrl: String? = null,
    @SerialName("qr_code_url") val qrCodeUrl: String? = null,
    val active: Boolean,
    val confirmed: Boolean,
    @OptIn(ExperimentalSerializationApi::class)
    @Serializable(with = FlexibleCategorySlugListSerializer::class)
    @SerialName("category_slugs")
    @JsonNames("CategorySlugs")
    val categorySlugs: List<String> = emptyList(),
    @SerialName("seo_title") val seoTitle: String? = null,
    @SerialName("seo_description") val seoDescription: String? = null,
    val noindex: Boolean? = null,
    val rules: List<ShopRuleDto> = emptyList(),
    @SerialName("shipping_methods") val shippingMethods: List<ShopShippingMethodDto> = emptyList(),
    @SerialName("working_hours") val workingHours: List<ShopWorkingHoursDto> = emptyList(),
    @SerialName("fulfillment_type") val fulfillmentType: String? = null,
    @SerialName("webhook_url") val webhookUrl: String? = null,
    val bale: String? = null,
    val rubika: String? = null,
    val tiktok: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class SellerShopsDataDto(
    val shops: CursorPageDto<SellerShopListItemDto>,
)

@Serializable
data class SellerShopListItemDto(
    val id: Long,
    val title: String,
    val active: Boolean,
    val confirmed: Boolean,
)

@Serializable
data class SellerShopDataDto(
    val shop: SellerShopDetailsDto,
)

/**
 * Union of fields seen across create / get / update response examples.
 * Unverified fields are optional.
 */
@Serializable
data class SellerShopDetailsDto(
    val id: Long,
    val slug: String,
    val active: Boolean,
    val confirmed: Boolean,
    @SerialName("owner_id") val ownerId: Long? = null,
    @SerialName("city_id") val cityId: Long? = null,
    val title: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val description: String? = null,
    val address: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("support_times") val supportTimes: String? = null,
    val type: String? = null,
    @SerialName("share_url") val shareUrl: String? = null,
    @SerialName("qr_code_url") val qrCodeUrl: String? = null,
    @OptIn(ExperimentalSerializationApi::class)
    @Serializable(with = FlexibleCategorySlugListSerializer::class)
    @SerialName("category_slugs")
    @JsonNames("CategorySlugs")
    val categorySlugs: List<String> = emptyList(),
    val whatsapp: String? = null,
    val bale: String? = null,
    val rubika: String? = null,
    val telegram: String? = null,
    val instagram: String? = null,
    val tiktok: String? = null,
    val website: String? = null,
    @SerialName("seo_title") val seoTitle: String? = null,
    @SerialName("seo_description") val seoDescription: String? = null,
    val noindex: Boolean? = null,
    val rules: List<ShopRuleDto> = emptyList(),
    @SerialName("shipping_methods") val shippingMethods: List<ShopShippingMethodDto> = emptyList(),
    @SerialName("working_hours") val workingHours: List<ShopWorkingHoursDto> = emptyList(),
    @SerialName("fulfillment_type") val fulfillmentType: String? = null,
    @SerialName("webhook_url") val webhookUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class ShopRuleDto(
    val title: String,
    val body: String,
)

@Serializable
data class ShopShippingMethodDto(
    val code: String,
    val title: String,
    val cost: Long? = null,
    @SerialName("min_days") val minDays: Int? = null,
    @SerialName("max_days") val maxDays: Int? = null,
    val active: Boolean? = null,
)

@Serializable
data class ShopWorkingHoursDto(
    val day: String,
    @SerialName("opens_at") val opensAt: String? = null,
    @SerialName("closes_at") val closesAt: String? = null,
    val closed: Boolean = false,
)

@Serializable
data class FulfillmentOptionsDataDto(
    @SerialName("fulfillment_options") val fulfillmentOptions: List<String> = emptyList(),
)

@Serializable
data class RegenerateApiKeyDataDto(
    @SerialName("api_key") val apiKey: String,
)
