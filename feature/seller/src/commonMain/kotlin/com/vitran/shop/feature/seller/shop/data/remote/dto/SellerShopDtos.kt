package com.vitran.shop.feature.seller.shop.data.remote.dto

import com.vitran.shop.core.network.pagination.CursorPageDto
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SlugCheckDataDto(
    @SerialName("slug_check") val slugCheck: SlugCheckDto,
)

@Serializable
data class SlugCheckDto(
    val slug: String,
    val available: Boolean,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CreateShopRequestDto(
    val title: String,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val slug: String? = null,
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
    @SerialName("category_slugs") val categorySlugs: List<Long> = emptyList(),
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val whatsapp: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val telegram: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val instagram: String? = null,
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
    @SerialName("category_slugs")
    val categorySlugs: List<Long>? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val whatsapp: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val telegram: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val instagram: String? = null,
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
    val description: String? = null,
    val type: String,
    @SerialName("share_url") val shareUrl: String? = null,
    @SerialName("qr_code_url") val qrCodeUrl: String? = null,
    val active: Boolean,
    val confirmed: Boolean,
    @SerialName("category_slugs") val categorySlugs: List<Long> = emptyList(),
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
    val description: String? = null,
    val address: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("support_times") val supportTimes: String? = null,
    val type: String? = null,
    @SerialName("share_url") val shareUrl: String? = null,
    @SerialName("qr_code_url") val qrCodeUrl: String? = null,
    @SerialName("category_slugs") val categorySlugs: List<Long> = emptyList(),
    val whatsapp: String? = null,
    val telegram: String? = null,
    val instagram: String? = null,
    val website: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class FulfillmentOptionsDataDto(
    @SerialName("fulfillment_options") val fulfillmentOptions: List<String> = emptyList(),
)

@Serializable
data class RegenerateApiKeyDataDto(
    @SerialName("api_key") val apiKey: String,
)
