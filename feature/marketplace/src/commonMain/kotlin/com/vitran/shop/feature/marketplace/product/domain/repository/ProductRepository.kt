package com.vitran.shop.feature.marketplace.product.domain.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.domain.model.ProductDetails
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductSummary
import com.vitran.shop.feature.marketplace.product.domain.query.ProductBrowseQuery
import com.vitran.shop.feature.marketplace.product.domain.query.ProductSearchQuery

interface ProductRepository {
    suspend fun getProducts(query: ProductBrowseQuery): AppResult<CursorPage<ProductSummary>>
    suspend fun searchProducts(query: ProductSearchQuery): AppResult<CursorPage<ProductSummary>>
    suspend fun getProduct(id: ProductId): AppResult<ProductDetails>
}
