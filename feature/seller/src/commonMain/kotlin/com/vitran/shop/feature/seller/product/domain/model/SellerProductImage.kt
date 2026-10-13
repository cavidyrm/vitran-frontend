package com.vitran.shop.feature.seller.product.domain.model

data class SellerProductImage(
    val id: ProductImageId,
    val url: String,
    val sortOrder: Int,
    val isMain: Boolean = false,
)

fun List<SellerProductImage>.coverImage(): SellerProductImage? =
    firstOrNull { it.isMain } ?: minByOrNull { it.sortOrder }
