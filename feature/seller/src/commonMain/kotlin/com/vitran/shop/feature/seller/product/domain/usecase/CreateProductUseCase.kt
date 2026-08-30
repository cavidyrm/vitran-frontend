package com.vitran.shop.feature.seller.product.domain.usecase

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.seller.product.domain.model.CreateProductCommand
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.repository.SellerProductRepository

private const val MaxProductImages = 5

class CreateProductUseCase(
    private val sellerProductRepository: SellerProductRepository,
) {
    suspend operator fun invoke(command: CreateProductCommand): AppResult<SellerProductDetails> {
        val title = command.title.trim()
        if (title.isBlank()) {
            return AppResult.Failure(AppError.Validation(message = "title required"))
        }
        if (command.priceAmount < 0) {
            return AppResult.Failure(AppError.Validation(message = "price invalid"))
        }
        if (command.images.size > MaxProductImages) {
            return AppResult.Failure(AppError.Validation(message = "images max $MaxProductImages"))
        }
        return sellerProductRepository.createProduct(
            command.copy(title = title, description = command.description.trim()),
        )
    }
}
