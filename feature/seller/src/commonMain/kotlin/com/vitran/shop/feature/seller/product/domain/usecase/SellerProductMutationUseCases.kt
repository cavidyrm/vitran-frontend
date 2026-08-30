package com.vitran.shop.feature.seller.product.domain.usecase

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.repository.ProductPublicCacheInvalidator
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.model.UpdateProductCommand
import com.vitran.shop.feature.seller.product.domain.repository.SellerProductRepository

class UpdateProductUseCase(
    private val sellerProductRepository: SellerProductRepository,
    private val publicCacheInvalidator: ProductPublicCacheInvalidator,
) {
    suspend operator fun invoke(command: UpdateProductCommand): AppResult<SellerProductDetails> =
        when (val result = sellerProductRepository.updateProduct(command)) {
            is AppResult.Failure -> result
            is AppResult.Success -> {
                publicCacheInvalidator.invalidate(result.value.id)
                AppResult.Success(result.value)
            }
        }
}

class SetProductActiveUseCase(
    private val sellerProductRepository: SellerProductRepository,
    private val publicCacheInvalidator: ProductPublicCacheInvalidator,
) {
    /**
     * @param localConfirmed when false, blocks publish client-side; null skips the precondition.
     */
    suspend operator fun invoke(
        productId: ProductId,
        active: Boolean,
        localConfirmed: Boolean? = null,
    ): AppResult<SellerProductDetails> {
        if (active && localConfirmed == false) {
            return AppResult.Failure(
                AppError.Validation(message = "محصول باید ابتدا تأیید شود"),
            )
        }
        return when (val result = sellerProductRepository.setProductActive(productId, active)) {
            is AppResult.Failure -> result
            is AppResult.Success -> {
                publicCacheInvalidator.invalidate(result.value.id)
                AppResult.Success(result.value)
            }
        }
    }
}

class DeleteProductUseCase(
    private val sellerProductRepository: SellerProductRepository,
    private val publicCacheInvalidator: ProductPublicCacheInvalidator,
) {
    suspend operator fun invoke(productId: ProductId): AppResult<Unit> =
        when (val result = sellerProductRepository.deleteProduct(productId)) {
            is AppResult.Failure -> result
            is AppResult.Success -> {
                publicCacheInvalidator.invalidate(productId)
                AppResult.Success(Unit)
            }
        }
}
