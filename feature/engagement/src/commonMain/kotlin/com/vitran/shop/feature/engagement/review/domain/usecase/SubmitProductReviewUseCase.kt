package com.vitran.shop.feature.engagement.review.domain.usecase

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.contact.domain.model.PurchaseIntentId
import com.vitran.shop.feature.engagement.review.domain.model.Rating
import com.vitran.shop.feature.engagement.review.domain.model.SubmitReviewCommand
import com.vitran.shop.feature.engagement.review.domain.model.SubmittedProductReview
import com.vitran.shop.feature.engagement.review.domain.repository.ProductReviewRepository
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId

class SubmitProductReviewUseCase(
    private val productReviewRepository: ProductReviewRepository,
) {
    suspend operator fun invoke(
        productId: ProductId,
        ratingValue: Int,
        comment: String,
        intentId: PurchaseIntentId? = null,
    ): AppResult<SubmittedProductReview> {
        val rating = when (val parsed = Rating.create(ratingValue)) {
            is AppResult.Success -> parsed.value
            is AppResult.Failure -> return parsed
        }
        val trimmed = comment.trim()
        if (trimmed.isEmpty()) {
            return AppResult.Failure(AppError.Validation(message = "Comment is required"))
        }
        return productReviewRepository.submitReview(
            SubmitReviewCommand(
                productId = productId,
                rating = rating,
                comment = trimmed,
                intentId = intentId,
            ),
        )
    }
}
