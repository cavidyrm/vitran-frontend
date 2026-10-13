package com.vitran.shop.feature.marketplace.product.domain.model

data class ProductImage(
    val id: Long,
    val url: String,
    val sortOrder: Int,
    val isMain: Boolean = false,
)

/** Cover is `is_main`, otherwise the lowest `sort_order`. */
fun List<ProductImage>.coverImage(): ProductImage? =
    firstOrNull { it.isMain } ?: minByOrNull { it.sortOrder }

/** Gallery order: cover first, then remaining images by `sort_order`. */
fun List<ProductImage>.withCoverFirst(): List<ProductImage> {
    val cover = coverImage() ?: return this
    return listOf(cover) + filterNot { it.id == cover.id }
}
