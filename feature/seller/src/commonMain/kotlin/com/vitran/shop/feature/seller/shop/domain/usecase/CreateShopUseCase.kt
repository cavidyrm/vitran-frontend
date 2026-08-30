package com.vitran.shop.feature.seller.shop.domain.usecase

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopResult
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository

/**
 * Creates a shop, applies optional first-shop access-token replacement (preserving refresh),
 * then best-effort refreshes current user roles. Role refresh failure does not roll back create.
 */
class CreateShopUseCase(
    private val sellerShopRepository: SellerShopRepository,
    private val sessionRepository: SessionRepository,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(command: CreateShopCommand): AppResult<CreateShopResult> {
        val title = command.title.trim()
        if (title.isBlank()) {
            return AppResult.Failure(AppError.Validation(message = "title required"))
        }

        return when (val created = sellerShopRepository.createShop(command.copy(title = title))) {
            is AppResult.Failure -> created
            is AppResult.Success -> {
                created.value.sessionAccessUpdate?.let { update ->
                    sessionRepository.updateAccessToken(update.accessToken, update.expiresAt)
                }
                // Best-effort; never undo shop creation or discard the new access token.
                accountRepository.refreshCurrentUser()
                AppResult.Success(created.value)
            }
        }
    }
}
