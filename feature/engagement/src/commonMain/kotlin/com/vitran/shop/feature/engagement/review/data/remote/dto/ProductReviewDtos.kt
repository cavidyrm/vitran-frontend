package com.vitran.shop.feature.engagement.review.data.remote.dto

import com.vitran.shop.core.network.pagination.CursorPageDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewsDataDto(
    val reviews: CursorPageDto<ProductReviewListItemDto>,
)

@Serializable
data class ProductReviewListItemDto(
    val id: Long,
    @SerialName("product_id")
    val productId: Long,
    @SerialName("user_id")
    val userId: Long,
    val rating: Int,
    val comment: String,
)

@Serializable
data class CreateReviewRequestDto(
    val rating: Int,
    val comment: String,
    @SerialName("intent_id")
    val intentId: Long? = null,
)

@Serializable
data class SubmittedReviewDataDto(
    val review: SubmittedProductReviewDto,
)

@Serializable
data class SubmittedProductReviewDto(
    val id: Long,
    @SerialName("product_id")
    val productId: Long,
    val rating: Int,
    val comment: String,
)
