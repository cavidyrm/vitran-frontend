package com.vitran.shop.feature.engagement.review.domain.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.review.domain.model.ProductReview
import com.vitran.shop.feature.engagement.review.domain.model.SubmitReviewCommand
import com.vitran.shop.feature.engagement.review.domain.model.SubmittedProductReview
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId

interface ProductReviewRepository {
    suspend fun getReviews(
        productId: ProductId,
        pagination: CursorPagination = CursorPagination(),
    ): AppResult<CursorPage<ProductReview>>

    suspend fun submitReview(command: SubmitReviewCommand): AppResult<SubmittedProductReview>
}
