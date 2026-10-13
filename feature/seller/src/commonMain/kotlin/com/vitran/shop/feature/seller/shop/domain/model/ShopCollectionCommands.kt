package com.vitran.shop.feature.seller.shop.domain.model

import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

const val ShopCollectionTitleMaxLength: Int = 150
const val ShopCollectionDescriptionMaxLength: Int = 5000
const val ShopCollectionProductIdMaxCount: Int = 500

data class CreateShopCollectionCommand(
    val shopId: ShopId,
    val title: String,
    val description: String? = null,
    val productIds: List<ProductId> = emptyList(),
)

/**
 * Null [title], [description], or [productIds] means omit (keep stored value).
 * An empty [productIds] list replaces membership with none.
 */
data class UpdateShopCollectionCommand(
    val shopId: ShopId,
    val collectionId: ShopCollectionId,
    val title: String? = null,
    val description: String? = null,
    val productIds: List<ProductId>? = null,
)
