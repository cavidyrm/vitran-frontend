package com.vitran.shop.feature.engagement.favorite.data.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.data.remote.EngagementApi
import com.vitran.shop.feature.engagement.favorite.data.mapper.toDomainPage
import com.vitran.shop.feature.engagement.favorite.domain.model.FavoriteShop
import com.vitran.shop.feature.engagement.favorite.domain.repository.ShopFavoriteRepository
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

internal class DefaultShopFavoriteRepository(
    private val api: EngagementApi,
) : ShopFavoriteRepository {
    override suspend fun getFavoriteShops(
        pagination: CursorPagination,
    ): AppResult<CursorPage<FavoriteShop>> =
        when (val result = api.getFavoriteShops(pagination)) {
            is AppResult.Success -> AppResult.Success(result.value.toDomainPage())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun setFavorite(shopId: ShopId, favorite: Boolean): AppResult<Unit> =
        if (favorite) api.addFavoriteShop(shopId) else api.removeFavoriteShop(shopId)
}
