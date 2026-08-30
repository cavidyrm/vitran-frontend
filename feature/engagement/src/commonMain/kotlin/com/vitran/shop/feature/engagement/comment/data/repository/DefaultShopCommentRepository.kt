package com.vitran.shop.feature.engagement.comment.data.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.comment.data.mapper.toDomain
import com.vitran.shop.feature.engagement.comment.data.mapper.toDomainPage
import com.vitran.shop.feature.engagement.comment.data.remote.ShopCommentApi
import com.vitran.shop.feature.engagement.comment.data.remote.dto.CreateShopCommentRequestDto
import com.vitran.shop.feature.engagement.comment.domain.model.PublicShopComment
import com.vitran.shop.feature.engagement.comment.domain.model.SubmitShopCommentCommand
import com.vitran.shop.feature.engagement.comment.domain.model.SubmittedShopComment
import com.vitran.shop.feature.engagement.comment.domain.repository.ShopCommentRepository
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

internal class DefaultShopCommentRepository(
    private val api: ShopCommentApi,
) : ShopCommentRepository {
    override suspend fun getComments(
        shopId: ShopId,
        pagination: CursorPagination,
    ): AppResult<CursorPage<PublicShopComment>> =
        when (val result = api.getComments(shopId, pagination)) {
            is AppResult.Success -> AppResult.Success(result.value.toDomainPage())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun submitComment(
        command: SubmitShopCommentCommand,
    ): AppResult<SubmittedShopComment> {
        val request = CreateShopCommentRequestDto(
            title = command.title,
            description = command.description,
        )
        return when (val result = api.createComment(command.shopId, request)) {
            is AppResult.Success -> AppResult.Success(result.value.comment.toDomain())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }
}
