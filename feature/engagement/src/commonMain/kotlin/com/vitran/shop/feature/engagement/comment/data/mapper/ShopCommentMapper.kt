package com.vitran.shop.feature.engagement.comment.data.mapper

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.network.pagination.toDomain
import com.vitran.shop.feature.engagement.comment.data.remote.dto.CommentsDataDto
import com.vitran.shop.feature.engagement.comment.data.remote.dto.ShopCommentListItemDto
import com.vitran.shop.feature.engagement.comment.data.remote.dto.SubmittedShopCommentDto
import com.vitran.shop.feature.engagement.comment.domain.model.PublicShopComment
import com.vitran.shop.feature.engagement.comment.domain.model.ShopCommentId
import com.vitran.shop.feature.engagement.comment.domain.model.SubmittedShopComment
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import kotlinx.datetime.Instant

internal fun CommentsDataDto.toDomainPage(): CursorPage<PublicShopComment> {
    val page = comments.toDomain()
    return CursorPage(
        items = page.items.map { it.toDomain() },
        nextCursor = page.nextCursor,
        hasMore = page.hasMore,
    )
}

internal fun ShopCommentListItemDto.toDomain(): PublicShopComment =
    PublicShopComment(
        id = ShopCommentId(id),
        title = title,
        confirmed = confirmed,
    )

internal fun SubmittedShopCommentDto.toDomain(): SubmittedShopComment =
    SubmittedShopComment(
        id = ShopCommentId(id),
        shopId = ShopId(shopId),
        authorUserId = userId,
        title = title,
        description = description,
        confirmed = confirmed,
        createdAt = Instant.parse(createdAt),
    )
