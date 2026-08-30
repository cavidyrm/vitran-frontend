package com.vitran.shop.feature.engagement.contact.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.contact.domain.model.ContactProductResult
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId

interface ProductContactRepository {
    suspend fun contactProduct(productId: ProductId): AppResult<ContactProductResult>
}
