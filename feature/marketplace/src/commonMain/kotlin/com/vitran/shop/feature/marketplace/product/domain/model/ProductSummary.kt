package com.vitran.shop.feature.marketplace.product.domain.model

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug

data class ProductSummary(
    val id: ProductId,
    val shopId: ShopId,
    val categorySlug: CategorySlug?,
    val title: String,
    val priceAmount: Long,
    val active: Boolean,
    val confirmed: Boolean,
    val images: List<ProductImage>,
)
