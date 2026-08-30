package com.vitran.shop.feature.marketplace.product.domain.repository

import com.vitran.shop.feature.marketplace.product.domain.model.ProductId

fun interface ProductPublicCacheInvalidator {
    suspend fun invalidate(productId: ProductId)
}
