package com.vitran.shop.feature.marketplace.shop.domain.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopDetails
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSummary
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopBrowseQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopListQuery

interface ShopRepository {
    suspend fun getShops(query: ShopListQuery): AppResult<CursorPage<ShopSummary>>
    suspend fun browseShops(query: ShopBrowseQuery): AppResult<CursorPage<ShopSummary>>
    suspend fun getShop(id: ShopId): AppResult<ShopDetails>
    suspend fun getShop(slug: ShopSlug): AppResult<ShopDetails>
}
