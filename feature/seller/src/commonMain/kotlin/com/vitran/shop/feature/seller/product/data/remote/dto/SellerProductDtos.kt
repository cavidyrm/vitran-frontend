package com.vitran.shop.feature.seller.product.data.remote.dto

import com.vitran.shop.core.network.pagination.CursorPageDto
import com.vitran.shop.feature.marketplace.common.data.serializer.FlexibleCategorySlugSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SellerProductsDataDto(
    val products: CursorPageDto<SellerProductListItemDto>,
)

@Serializable
data class SellerProductDataDto(
    val product: SellerProductDto,
)

@Serializable
data class SellerProductListItemDto(
    val id: Long,
    @SerialName("shop_id") val shopId: Long,
    val title: String,
    val active: Boolean,
    val confirmed: Boolean,
)

@Serializable
data class SellerProductDto(
    val id: Long,
    @SerialName("shop_id") val shopId: Long? = null,
    @Serializable(with = FlexibleCategorySlugSerializer::class)
    @SerialName("category_slug")
    val categorySlug: String? = null,
    val title: String? = null,
    val description: String? = null,
    val price: Long? = null,
    val active: Boolean? = null,
    val confirmed: Boolean? = null,
    val images: List<SellerProductImageDto> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class SellerProductImageDto(
    val id: Long,
    val url: String,
    @SerialName("sort_order") val sortOrder: Int = 0,
)

@Serializable
data class SetProductActiveRequestDto(
    val active: Boolean,
)
