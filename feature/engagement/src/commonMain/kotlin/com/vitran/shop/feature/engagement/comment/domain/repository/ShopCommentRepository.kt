package com.vitran.shop.feature.engagement.comment.domain.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.comment.domain.model.PublicShopComment
import com.vitran.shop.feature.engagement.comment.domain.model.SubmitShopCommentCommand
import com.vitran.shop.feature.engagement.comment.domain.model.SubmittedShopComment
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

interface ShopCommentRepository {
    suspend fun getComments(
        shopId: ShopId,
        pagination: CursorPagination = CursorPagination(),
    ): AppResult<CursorPage<PublicShopComment>>

    suspend fun submitComment(command: SubmitShopCommentCommand): AppResult<SubmittedShopComment>
}
