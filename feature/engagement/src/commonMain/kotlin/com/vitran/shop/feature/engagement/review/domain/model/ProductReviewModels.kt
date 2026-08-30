package com.vitran.shop.feature.engagement.review.domain.model

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.contact.domain.model.PurchaseIntentId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import kotlin.jvm.JvmInline

@JvmInline
value class ProductReviewId(val value: Long)

@JvmInline
value class Rating private constructor(val value: Int) {
    companion object {
        fun create(value: Int): AppResult<Rating> =
            if (value in 1..5) {
                AppResult.Success(Rating(value))
            } else {
                AppResult.Failure(AppError.Validation(message = "Rating must be between 1 and 5"))
            }

        fun fromVerified(value: Int): Rating {
            require(value in 1..5) { "Rating must be 1..5" }
            return Rating(value)
        }
    }
}

data class ProductReview(
    val id: ProductReviewId,
    val productId: ProductId,
    val authorUserId: Long,
    val rating: Rating,
    val comment: String,
)

data class SubmittedProductReview(
    val id: ProductReviewId,
    val productId: ProductId,
    val rating: Rating,
    val comment: String,
)

data class SubmitReviewCommand(
    val productId: ProductId,
    val rating: Rating,
    val comment: String,
    val intentId: PurchaseIntentId? = null,
)
