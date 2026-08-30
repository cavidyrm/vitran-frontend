package com.vitran.shop.feature.engagement.comment.domain.model

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import kotlin.jvm.JvmInline
import kotlinx.datetime.Instant

@JvmInline
value class ShopCommentId(val value: Long)

data class PublicShopComment(
    val id: ShopCommentId,
    val title: String,
    val confirmed: Boolean,
)

data class SubmittedShopComment(
    val id: ShopCommentId,
    val shopId: ShopId,
    val authorUserId: Long,
    val title: String,
    val description: String,
    val confirmed: Boolean,
    val createdAt: Instant,
)

data class SubmitShopCommentCommand(
    val shopId: ShopId,
    val title: String,
    val description: String,
)
