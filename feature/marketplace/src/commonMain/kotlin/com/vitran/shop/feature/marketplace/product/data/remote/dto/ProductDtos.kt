package com.vitran.shop.feature.marketplace.product.data.remote.dto

import com.vitran.shop.core.network.pagination.CursorPageDto
import com.vitran.shop.feature.marketplace.common.data.serializer.FlexibleCategorySlugSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsDataDto(
    val products: CursorPageDto<ProductListItemDto>,
)

@Serializable
data class ProductListItemDto(
    val id: Long,
    @SerialName("shop_id") val shopId: Long,
    @Serializable(with = FlexibleCategorySlugSerializer::class)
    @SerialName("category_slug")
    val categorySlug: String? = null,
    val title: String,
    val price: Long,
    val active: Boolean = true,
    val confirmed: Boolean = true,
    val images: List<ProductImageDto> = emptyList(),
)

@Serializable
data class ProductDataDto(
    val product: ProductDetailsDto,
)

@Serializable
data class ProductDetailsDto(
    val id: Long,
    @SerialName("shop_id") val shopId: Long,
    @Serializable(with = FlexibleCategorySlugSerializer::class)
    @SerialName("category_slug")
    val categorySlug: String? = null,
    val title: String,
    val description: String,
    val price: Long,
    val active: Boolean = true,
    val confirmed: Boolean = true,
    val images: List<ProductImageDto> = emptyList(),
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
data class ProductImageDto(
    val id: Long,
    val url: String,
    @SerialName("sort_order") val sortOrder: Int = 0,
)
