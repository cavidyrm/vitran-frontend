package com.vitran.shop.feature.admin.moderation.data

import com.vitran.shop.core.network.pagination.PageDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

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
    @SerialName("category_slugs") val categorySlugs: List<JsonElement> = emptyList(),
    @SerialName("updated_at") val updatedAt: String? = null,
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
internal data class AdminProductDetailsDto(
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
