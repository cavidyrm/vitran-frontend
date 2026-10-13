package com.vitran.shop.feature.seller.shop.data.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.data.mapper.toDetail as toCollectionDetail
import com.vitran.shop.feature.marketplace.shop.data.mapper.toDomain as toCollectionDomain
import com.vitran.shop.feature.marketplace.shop.data.mapper.toShopCollectionPage
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollection
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionDetail
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopCollectionDetailQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopCollectionListQuery
import com.vitran.shop.feature.seller.shop.data.mapper.toDomain
import com.vitran.shop.feature.seller.shop.data.mapper.toRequestDto
import com.vitran.shop.feature.seller.shop.data.mapper.toSellerSummaryPage
import com.vitran.shop.feature.seller.shop.data.mapper.toSummary
import com.vitran.shop.feature.seller.shop.data.remote.SellerShopApi
import com.vitran.shop.feature.seller.shop.data.state.SellerShopStateStore
import com.vitran.shop.feature.seller.shop.domain.error.toSellerShopAppError
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
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository

internal class DefaultSellerShopRepository(
    private val api: SellerShopApi,
    private val stateStore: SellerShopStateStore,
) : SellerShopRepository {

    override suspend fun checkSlugAvailability(
        slug: ShopSlug,
        excludeId: ShopId?,
    ): AppResult<ShopSlugAvailability> =
        when (val result = api.checkSlug(slug, excludeId)) {
            is AppResult.Success -> AppResult.Success(result.value.slugCheck.toDomain())
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }

    override suspend fun checkTitleAvailability(
        title: String,
        excludeId: ShopId?,
    ): AppResult<ShopTitleAvailability> =
        when (val result = api.checkTitle(title, excludeId)) {
            is AppResult.Success -> AppResult.Success(result.value.titleCheck.toDomain())
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }

    override suspend fun createShop(command: CreateShopCommand): AppResult<CreateShopResult> =
        when (val result = api.createShop(command.toRequestDto())) {
            is AppResult.Success -> {
                val created = result.value.toDomain()
                stateStore.putDetails(created.shop)
                stateStore.upsertSummary(created.shop.toSummary())
                AppResult.Success(created)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }

    override suspend fun getMyShops(query: SellerShopListQuery): AppResult<CursorPage<SellerShopSummary>> =
        when (val result = api.listMyShops(query)) {
            is AppResult.Success -> {
                val page = result.value.shops.toSellerSummaryPage()
                if (query.pagination.cursor == null) {
                    stateStore.replaceSummaries(page.items)
                } else {
                    page.items.forEach { stateStore.upsertSummary(it) }
                }
                AppResult.Success(page)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }

    override suspend fun getMyShop(shopId: ShopId): AppResult<SellerShopDetails> =
        when (val result = api.getMyShop(shopId)) {
            is AppResult.Success -> {
                val details = result.value.shop.toDomain()
                stateStore.putDetails(details)
                stateStore.updateSummary(details.toSummary())
                AppResult.Success(details)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }

    override suspend fun updateShop(command: UpdateShopCommand): AppResult<SellerShopDetails> =
        when (val result = api.updateShop(command.shopId, command.toRequestDto())) {
            is AppResult.Success -> {
                val details = result.value.shop.toDomain()
                stateStore.putDetails(details)
                stateStore.updateSummary(details.toSummary())
                AppResult.Success(details)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }

    override suspend fun getFulfillmentOptions(shopId: ShopId): AppResult<List<FulfillmentMode>> =
        when (val result = api.getFulfillmentOptions(shopId)) {
            is AppResult.Success ->
                AppResult.Success(
                    result.value.fulfillmentOptions.map { FulfillmentMode.fromRaw(it) },
                )
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }

    override suspend fun regenerateApiKey(shopId: ShopId): AppResult<ShopApiKey> =
        when (val result = api.regenerateApiKey(shopId)) {
            is AppResult.Success -> AppResult.Success(ShopApiKey.of(result.value.apiKey))
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }

    override suspend fun getMyCollections(
        query: ShopCollectionListQuery,
    ): AppResult<CursorPage<ShopCollection>> =
        when (val result = api.listCollections(query)) {
            is AppResult.Success -> AppResult.Success(result.value.collections.toShopCollectionPage())
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }

    override suspend fun getMyCollection(query: ShopCollectionDetailQuery): AppResult<ShopCollectionDetail> =
        when (val result = api.getCollection(query)) {
            is AppResult.Success -> AppResult.Success(result.value.toCollectionDetail())
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }

    override suspend fun createCollection(command: CreateShopCollectionCommand): AppResult<ShopCollection> =
        when (val result = api.createCollection(command.shopId, command.toRequestDto())) {
            is AppResult.Success -> AppResult.Success(result.value.collection.toCollectionDomain())
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }

    override suspend fun updateCollection(command: UpdateShopCollectionCommand): AppResult<ShopCollection> =
        when (val result = api.updateCollection(command.shopId, command.collectionId, command.toRequestDto())) {
            is AppResult.Success -> AppResult.Success(result.value.collection.toCollectionDomain())
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }

    override suspend fun deleteCollection(shopId: ShopId, collectionId: ShopCollectionId): AppResult<Unit> =
        when (val result = api.deleteCollection(shopId, collectionId)) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> AppResult.Failure(result.error.toSellerShopAppError())
        }
}
