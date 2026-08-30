package com.vitran.shop.feature.engagement.wishlist.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSettings
import com.vitran.shop.feature.engagement.wishlist.domain.repository.WishlistRepository

class UpdateWishlistSharingUseCase(
    private val wishlistRepository: WishlistRepository,
    private val stateStore: EngagementStateStore,
) {
    suspend operator fun invoke(isPublic: Boolean): AppResult<WishlistShareSettings> {
        val previous = stateStore.shareSettings.value
        return when (val result = wishlistRepository.updateShareVisibility(isPublic)) {
            is AppResult.Success -> {
                stateStore.setShareSettings(result.value)
                result
            }
            is AppResult.Failure -> {
                stateStore.setShareSettings(previous)
                result
            }
        }
    }
}
