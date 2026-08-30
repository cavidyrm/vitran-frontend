package com.vitran.shop.feature.engagement.review.data.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.review.data.mapper.toDomain
import com.vitran.shop.feature.engagement.review.data.mapper.toDomainPage
import com.vitran.shop.feature.engagement.review.data.remote.ProductReviewApi
import com.vitran.shop.feature.engagement.review.data.remote.dto.CreateReviewRequestDto
import com.vitran.shop.feature.engagement.review.domain.model.ProductReview
import com.vitran.shop.feature.engagement.review.domain.model.SubmitReviewCommand
import com.vitran.shop.feature.engagement.review.domain.model.SubmittedProductReview
import com.vitran.shop.feature.engagement.review.domain.repository.ProductReviewRepository
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId

internal class DefaultProductReviewRepository(
    private val api: ProductReviewApi,
) : ProductReviewRepository {
    override suspend fun getReviews(
        productId: ProductId,
        pagination: CursorPagination,
    ): AppResult<CursorPage<ProductReview>> =
        when (val result = api.getReviews(productId, pagination)) {
            is AppResult.Success -> AppResult.Success(result.value.toDomainPage())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun submitReview(
        command: SubmitReviewCommand,
    ): AppResult<SubmittedProductReview> {
        val request = CreateReviewRequestDto(
            rating = command.rating.value,
            comment = command.comment,
            intentId = command.intentId?.value,
        )
        return when (val result = api.createReview(command.productId, request)) {
            is AppResult.Success -> AppResult.Success(result.value.review.toDomain())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }
}
