package com.vitran.shop.feature.seller.shop.domain.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollection
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionDetail
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopCollectionDetailQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopCollectionListQuery
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCollectionCommand
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopResult
import com.vitran.shop.feature.seller.shop.domain.model.FulfillmentMode
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopDetails
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.model.ShopApiKey
import com.vitran.shop.feature.seller.shop.domain.model.ShopSlugAvailability
import com.vitran.shop.feature.seller.shop.domain.model.ShopTitleAvailability
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCollectionCommand
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCommand
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery

interface SellerShopRepository {
    suspend fun checkSlugAvailability(
        slug: ShopSlug,
        excludeId: ShopId? = null,
    ): AppResult<ShopSlugAvailability>

    suspend fun checkTitleAvailability(
        title: String,
        excludeId: ShopId? = null,
    ): AppResult<ShopTitleAvailability>

    suspend fun createShop(command: CreateShopCommand): AppResult<CreateShopResult>

    suspend fun getMyShops(query: SellerShopListQuery): AppResult<CursorPage<SellerShopSummary>>

    suspend fun getMyShop(shopId: ShopId): AppResult<SellerShopDetails>

    suspend fun updateShop(command: UpdateShopCommand): AppResult<SellerShopDetails>

    suspend fun getFulfillmentOptions(shopId: ShopId): AppResult<List<FulfillmentMode>>

    suspend fun regenerateApiKey(shopId: ShopId): AppResult<ShopApiKey>

    suspend fun getMyCollections(query: ShopCollectionListQuery): AppResult<CursorPage<ShopCollection>>

    suspend fun getMyCollection(query: ShopCollectionDetailQuery): AppResult<ShopCollectionDetail>

    suspend fun createCollection(command: CreateShopCollectionCommand): AppResult<ShopCollection>

    suspend fun updateCollection(command: UpdateShopCollectionCommand): AppResult<ShopCollection>

    suspend fun deleteCollection(shopId: ShopId, collectionId: ShopCollectionId): AppResult<Unit>
}
