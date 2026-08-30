package com.vitran.shop.feature.engagement.comment.domain.usecase

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.comment.domain.model.SubmitShopCommentCommand
import com.vitran.shop.feature.engagement.comment.domain.model.SubmittedShopComment
import com.vitran.shop.feature.engagement.comment.domain.repository.ShopCommentRepository
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

class SubmitShopCommentUseCase(
    private val shopCommentRepository: ShopCommentRepository,
) {
    suspend operator fun invoke(
        shopId: ShopId,
        title: String,
        description: String,
    ): AppResult<SubmittedShopComment> {
        val trimmedTitle = title.trim()
        val trimmedDescription = description.trim()
        if (trimmedTitle.isEmpty() || trimmedDescription.isEmpty()) {
            return AppResult.Failure(
                AppError.Validation(message = "Title and description are required"),
            )
        }
        return shopCommentRepository.submitComment(
            SubmitShopCommentCommand(
                shopId = shopId,
                title = trimmedTitle,
                description = trimmedDescription,
            ),
        )
    }
}
