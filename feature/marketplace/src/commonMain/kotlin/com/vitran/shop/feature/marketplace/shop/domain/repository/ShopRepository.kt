package com.vitran.shop.feature.marketplace.shop.domain.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollection
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionDetail
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopDetails
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSummary
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopBrowseQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopCollectionDetailQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopCollectionListQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopListQuery

interface ShopRepository {
    suspend fun getShops(query: ShopListQuery): AppResult<CursorPage<ShopSummary>>
    suspend fun browseShops(query: ShopBrowseQuery): AppResult<CursorPage<ShopSummary>>
    suspend fun getShop(id: ShopId): AppResult<ShopDetails>
    suspend fun getShop(slug: ShopSlug): AppResult<ShopDetails>

    suspend fun getShopCollections(query: ShopCollectionListQuery): AppResult<CursorPage<ShopCollection>>

    suspend fun getShopCollection(query: ShopCollectionDetailQuery): AppResult<ShopCollectionDetail>

    /** Drop cached public detail for [id] (e.g. after seller update → pending). */
    suspend fun invalidateShop(id: ShopId)
}
