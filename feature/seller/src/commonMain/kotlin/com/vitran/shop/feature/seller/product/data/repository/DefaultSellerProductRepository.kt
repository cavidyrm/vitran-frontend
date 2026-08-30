package com.vitran.shop.feature.seller.product.data.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.seller.product.data.mapper.toDomain
import com.vitran.shop.feature.seller.product.data.mapper.toSellerSummaryPage
import com.vitran.shop.feature.seller.product.data.mapper.toSummary
import com.vitran.shop.feature.seller.product.data.remote.SellerProductApi
import com.vitran.shop.feature.seller.product.data.state.SellerProductStateStore
import com.vitran.shop.feature.seller.product.domain.error.toSellerProductAppError
import com.vitran.shop.feature.seller.product.domain.model.CreateProductCommand
import com.vitran.shop.feature.seller.product.domain.model.ProductImageId
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.model.SellerProductSummary
import com.vitran.shop.feature.seller.product.domain.model.UpdateProductCommand
import com.vitran.shop.feature.seller.product.domain.query.SellerProductListQuery
import com.vitran.shop.feature.seller.product.domain.repository.SellerProductRepository

internal class DefaultSellerProductRepository(
    private val api: SellerProductApi,
    private val stateStore: SellerProductStateStore,
) : SellerProductRepository {

    override suspend fun getProducts(query: SellerProductListQuery): AppResult<CursorPage<SellerProductSummary>> =
        when (val result = api.listMyProducts(query)) {
            is AppResult.Success -> {
                val page = result.value.products.toSellerSummaryPage()
                if (query.pagination.cursor == null) {
                    stateStore.replaceSummaries(page.items)
                } else {
                    page.items.forEach { stateStore.upsertSummary(it) }
                }
                AppResult.Success(page)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerProductAppError())
        }

    override suspend fun getProduct(productId: ProductId): AppResult<SellerProductDetails> =
        when (val result = api.getMyProduct(productId)) {
            is AppResult.Success -> {
                val details = result.value.product.toDomain(previous = stateStore.getDetails(productId))
                stateStore.putDetails(details)
                stateStore.upsertSummary(details.toSummary())
                AppResult.Success(details)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerProductAppError())
        }

    override suspend fun createProduct(command: CreateProductCommand): AppResult<SellerProductDetails> =
        when (val result = api.createProduct(command)) {
            is AppResult.Success -> {
                val details = result.value.product.toDomain(fallbackShopId = command.shopId)
                stateStore.putDetails(details)
                stateStore.upsertSummary(details.toSummary())
                AppResult.Success(details)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerProductAppError())
        }

    override suspend fun updateProduct(command: UpdateProductCommand): AppResult<SellerProductDetails> =
        when (val result = api.updateProduct(command)) {
            is AppResult.Success -> {
                val previous = stateStore.getDetails(command.productId)
                val details = result.value.product.toDomain(previous = previous)
                stateStore.putDetails(details)
                stateStore.updateSummary(details.toSummary())
                AppResult.Success(details)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerProductAppError())
        }

    override suspend fun setProductActive(
        productId: ProductId,
        active: Boolean,
    ): AppResult<SellerProductDetails> =
        when (val result = api.setProductActive(productId, active)) {
            is AppResult.Success -> {
                val previous = stateStore.getDetails(productId)
                val details = result.value.product.toDomain(previous = previous)
                stateStore.putDetails(details)
                stateStore.updateSummary(details.toSummary())
                AppResult.Success(details)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerProductAppError())
        }

    override suspend fun deleteProduct(productId: ProductId): AppResult<Unit> =
        when (val result = api.deleteProduct(productId)) {
            is AppResult.Success -> {
                stateStore.removeSummary(productId)
                stateStore.removeDetails(productId)
                AppResult.Success(Unit)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerProductAppError())
        }

    override suspend fun deleteProductImage(
        productId: ProductId,
        imageId: ProductImageId,
    ): AppResult<SellerProductDetails> =
        when (val result = api.deleteProductImage(productId, imageId)) {
            is AppResult.Success -> {
                val previous = stateStore.getDetails(productId)
                val details = result.value.product.toDomain(previous = previous)
                // Prefer response images; if response omitted meaningful merge, drop deleted id.
                val reconciled =
                    if (result.value.product.images.isEmpty() && previous != null &&
                        previous.images.any { it.id == imageId }
                    ) {
                        details.copy(images = previous.images.filterNot { it.id == imageId })
                    } else {
                        details
                    }
                stateStore.putDetails(reconciled)
                AppResult.Success(reconciled)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerProductAppError())
        }
}
