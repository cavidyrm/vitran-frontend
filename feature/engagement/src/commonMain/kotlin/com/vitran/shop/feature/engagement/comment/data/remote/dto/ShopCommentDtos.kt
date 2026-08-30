package com.vitran.shop.feature.engagement.comment.data.remote.dto

import com.vitran.shop.core.network.pagination.CursorPageDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentsDataDto(
    val comments: CursorPageDto<ShopCommentListItemDto>,
)

@Serializable
data class ShopCommentListItemDto(
    val id: Long,
    val title: String,
    val confirmed: Boolean,
)

@Serializable
data class CreateShopCommentRequestDto(
    val title: String,
    val description: String,
)

@Serializable
data class SubmittedCommentDataDto(
    val comment: SubmittedShopCommentDto,
)

@Serializable
data class SubmittedShopCommentDto(
    val id: Long,
    @SerialName("shop_id")
    val shopId: Long,
    @SerialName("user_id")
    val userId: Long,
    val title: String,
    val description: String,
    val confirmed: Boolean,
    @SerialName("created_at")
    val createdAt: String,
)
