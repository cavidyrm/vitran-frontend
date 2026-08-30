package com.vitran.shop.feature.seller.product.domain.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.seller.product.domain.model.CreateProductCommand
import com.vitran.shop.feature.seller.product.domain.model.ProductImageId
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.model.SellerProductSummary
import com.vitran.shop.feature.seller.product.domain.model.UpdateProductCommand
import com.vitran.shop.feature.seller.product.domain.query.SellerProductListQuery

interface SellerProductRepository {
    suspend fun getProducts(query: SellerProductListQuery): AppResult<CursorPage<SellerProductSummary>>

    suspend fun getProduct(productId: ProductId): AppResult<SellerProductDetails>

    suspend fun createProduct(command: CreateProductCommand): AppResult<SellerProductDetails>

    suspend fun updateProduct(command: UpdateProductCommand): AppResult<SellerProductDetails>

    suspend fun setProductActive(productId: ProductId, active: Boolean): AppResult<SellerProductDetails>

    suspend fun deleteProduct(productId: ProductId): AppResult<Unit>

    suspend fun deleteProductImage(
        productId: ProductId,
        imageId: ProductImageId,
    ): AppResult<SellerProductDetails>
}
