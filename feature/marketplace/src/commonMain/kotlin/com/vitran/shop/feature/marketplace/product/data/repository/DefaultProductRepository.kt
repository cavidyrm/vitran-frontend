package com.vitran.shop.feature.marketplace.product.data.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.data.mapper.toDomain
import com.vitran.shop.feature.marketplace.product.data.mapper.toProductSummaryPage
import com.vitran.shop.feature.marketplace.product.data.remote.PublicProductApi
import com.vitran.shop.feature.marketplace.product.domain.error.toProductResultError
import com.vitran.shop.feature.marketplace.product.domain.model.ProductDetails
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductSummary
import com.vitran.shop.feature.marketplace.product.domain.query.ProductBrowseQuery
import com.vitran.shop.feature.marketplace.product.domain.query.ProductSearchQuery
import com.vitran.shop.feature.marketplace.product.domain.repository.ProductRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultProductRepository(
    private val publicProductApi: PublicProductApi,
) : ProductRepository {

    private val detailMutex = Mutex()
    private val detailById = mutableMapOf<ProductId, ProductDetails>()

    override suspend fun getProducts(query: ProductBrowseQuery): AppResult<CursorPage<ProductSummary>> =
        when (val result = publicProductApi.getProducts(query)) {
            is AppResult.Success -> AppResult.Success(result.value.products.toProductSummaryPage())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun searchProducts(query: ProductSearchQuery): AppResult<CursorPage<ProductSummary>> =
        when (val result = publicProductApi.searchProducts(query)) {
            is AppResult.Success -> AppResult.Success(result.value.products.toProductSummaryPage())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun getProduct(id: ProductId): AppResult<ProductDetails> {
        detailMutex.withLock { detailById[id] }?.let { return AppResult.Success(it) }
        return when (val result = publicProductApi.getProductById(id)) {
            is AppResult.Success -> {
                val details = result.value.product.toDomain()
                detailMutex.withLock { detailById[id] = details }
                AppResult.Success(details)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toProductResultError())
        }
    }
}
