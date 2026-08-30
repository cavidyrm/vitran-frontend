package com.vitran.shop.feature.engagement.favorite.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.favorite.domain.repository.ShopFavoriteRepository
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.state.FavoriteShopStatus
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

class SetShopFavoriteUseCase(
    private val shopFavoriteRepository: ShopFavoriteRepository,
    private val stateStore: EngagementStateStore,
) {
    suspend operator fun invoke(shopId: ShopId, favorite: Boolean): AppResult<Unit> {
        val previous = stateStore.favoriteShopStatus(shopId)
        stateStore.setFavoriteShopStatus(
            shopId,
            if (favorite) FavoriteShopStatus.Favorited else FavoriteShopStatus.NotFavorited,
        )
        return when (val result = shopFavoriteRepository.setFavorite(shopId, favorite)) {
            is AppResult.Success -> result
            is AppResult.Failure -> {
                stateStore.setFavoriteShopStatus(shopId, previous)
                result
            }
        }
    }
}
