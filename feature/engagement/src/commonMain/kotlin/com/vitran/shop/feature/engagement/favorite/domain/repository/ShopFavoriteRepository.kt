package com.vitran.shop.feature.engagement.favorite.domain.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.favorite.domain.model.FavoriteShop
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

interface ShopFavoriteRepository {
    suspend fun getFavoriteShops(pagination: CursorPagination = CursorPagination()): AppResult<CursorPage<FavoriteShop>>

    suspend fun setFavorite(shopId: ShopId, favorite: Boolean): AppResult<Unit>
}
