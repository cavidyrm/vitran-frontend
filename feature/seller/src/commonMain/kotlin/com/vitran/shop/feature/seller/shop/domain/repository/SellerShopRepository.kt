package com.vitran.shop.feature.seller.shop.domain.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopResult
import com.vitran.shop.feature.seller.shop.domain.model.FulfillmentMode
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopDetails
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.model.ShopApiKey
import com.vitran.shop.feature.seller.shop.domain.model.ShopSlugAvailability
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCommand
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery

interface SellerShopRepository {
    suspend fun checkSlugAvailability(
        slug: ShopSlug,
        excludeId: ShopId? = null,
    ): AppResult<ShopSlugAvailability>

    suspend fun createShop(command: CreateShopCommand): AppResult<CreateShopResult>

    suspend fun getMyShops(query: SellerShopListQuery): AppResult<CursorPage<SellerShopSummary>>

    suspend fun getMyShop(shopId: ShopId): AppResult<SellerShopDetails>

    suspend fun updateShop(command: UpdateShopCommand): AppResult<SellerShopDetails>

    suspend fun getFulfillmentOptions(shopId: ShopId): AppResult<List<FulfillmentMode>>

    suspend fun regenerateApiKey(shopId: ShopId): AppResult<ShopApiKey>
}
