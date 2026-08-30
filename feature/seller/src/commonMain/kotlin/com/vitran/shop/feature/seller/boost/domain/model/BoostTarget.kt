package com.vitran.shop.feature.seller.boost.domain.model

import com.vitran.shop.feature.marketplace.product.domain.model.ProductId

sealed class BoostTarget {
    data object Shop : BoostTarget()
    data class Product(val productId: ProductId) : BoostTarget()
}
