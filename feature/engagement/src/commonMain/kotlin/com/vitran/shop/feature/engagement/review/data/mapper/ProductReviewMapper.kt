package com.vitran.shop.feature.engagement.review.data.mapper

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.network.pagination.toDomain
import com.vitran.shop.feature.engagement.review.data.remote.dto.ProductReviewListItemDto
import com.vitran.shop.feature.engagement.review.data.remote.dto.ReviewsDataDto
import com.vitran.shop.feature.engagement.review.data.remote.dto.SubmittedProductReviewDto
import com.vitran.shop.feature.engagement.review.domain.model.ProductReview
import com.vitran.shop.feature.engagement.review.domain.model.ProductReviewId
import com.vitran.shop.feature.engagement.review.domain.model.Rating
import com.vitran.shop.feature.engagement.review.domain.model.SubmittedProductReview
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId

internal fun ReviewsDataDto.toDomainPage(): CursorPage<ProductReview> {
    val page = reviews.toDomain()
    return CursorPage(
        items = page.items.map { it.toDomain() },
        nextCursor = page.nextCursor,
        hasMore = page.hasMore,
    )
}

internal fun ProductReviewListItemDto.toDomain(): ProductReview =
    ProductReview(
        id = ProductReviewId(id),
        productId = ProductId(productId),
        authorUserId = userId,
        rating = Rating.fromVerified(rating),
        comment = comment,
    )

internal fun SubmittedProductReviewDto.toDomain(): SubmittedProductReview =
    SubmittedProductReview(
        id = ProductReviewId(id),
        productId = ProductId(productId),
        rating = Rating.fromVerified(rating),
        comment = comment,
    )
