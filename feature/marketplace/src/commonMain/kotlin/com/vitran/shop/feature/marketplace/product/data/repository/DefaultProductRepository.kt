package com.vitran.shop.feature.marketplace.product.data.repository

import com.vitran.shop.core.database.VitranDatabase
import com.vitran.shop.core.database.entity.ProductDetailEntity
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.data.mapper.toDomain
import com.vitran.shop.feature.marketplace.product.data.mapper.toProductSummaryPage
import com.vitran.shop.feature.marketplace.product.data.remote.PublicProductApi
import com.vitran.shop.feature.marketplace.product.data.remote.dto.ProductDetailsDto
import com.vitran.shop.feature.marketplace.product.domain.error.toProductResultError
import com.vitran.shop.feature.marketplace.product.domain.model.ProductDetails
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductSummary
import com.vitran.shop.feature.marketplace.product.domain.query.ProductBrowseQuery
import com.vitran.shop.feature.marketplace.product.domain.query.ProductSearchQuery
import com.vitran.shop.feature.marketplace.product.domain.repository.ProductRepository
import kotlinx.serialization.json.Json
import kotlin.time.Clock

internal class DefaultProductRepository(
    private val publicProductApi: PublicProductApi,
    private val database: VitranDatabase,
) : ProductRepository {

    private val productDetailDao get() = database.productDetailDao()

    private val json = Json { ignoreUnknownKeys = true }

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
        productDetailDao.getById(id.value)?.let { entity ->
            if (entity.unavailable) {
                return AppResult.Failure(AppError.NotFound(message = "محصول در دسترس نیست").toProductResultError())
            }
            return AppResult.Success(entity.toDomain(json))
        }
        return when (val result = publicProductApi.getProductById(id)) {
            is AppResult.Success -> {
                val dto = result.value.product
                val now = Clock.System.now().toEpochMilliseconds()
                productDetailDao.upsert(
                    ProductDetailEntity(
                        id = dto.id,
                        payloadJson = json.encodeToString(ProductDetailsDto.serializer(), dto),
                        unavailable = false,
                        fetchedAt = now,
                    ),
                )
                AppResult.Success(dto.toDomain())
            }
            is AppResult.Failure -> {
                if (result.error is AppError.NotFound) {
                    productDetailDao.markUnavailable(id.value)
                    return AppResult.Failure(result.error.toProductResultError())
                }
                val cached = productDetailDao.getById(id.value)
                if (cached != null && !cached.unavailable) {
                    AppResult.Success(cached.toDomain(json))
                } else {
                    AppResult.Failure(result.error.toProductResultError())
                }
            }
        }
    }

    override suspend fun invalidateProduct(id: ProductId) {
        productDetailDao.deleteById(id.value)
    }
}

private fun ProductDetailEntity.toDomain(json: Json): ProductDetails =
    json.decodeFromString(ProductDetailsDto.serializer(), payloadJson).toDomain()
