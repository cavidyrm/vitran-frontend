package com.vitran.shop.feature.seller.product.domain.model

import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlinx.datetime.Instant

data class SellerProductDetails(
    val id: ProductId,
    val shopId: ShopId,
    val categorySlug: CategorySlug?,
    val title: String,
    val description: String?,
    val priceAmount: Long?,
    val active: Boolean,
    val confirmed: Boolean,
    val images: List<SellerProductImage>,
    val createdAt: Instant?,
    val updatedAt: Instant?,
    val publicationState: ProductPublicationState =
        productPublicationState(active = active, confirmed = confirmed),
)
