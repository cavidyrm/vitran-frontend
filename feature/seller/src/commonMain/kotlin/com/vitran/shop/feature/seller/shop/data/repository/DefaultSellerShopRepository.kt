package com.vitran.shop.feature.seller.shop.data.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.seller.shop.data.mapper.toDomain
import com.vitran.shop.feature.seller.shop.data.mapper.toRequestDto
import com.vitran.shop.feature.seller.shop.data.mapper.toSellerSummaryPage
import com.vitran.shop.feature.seller.shop.data.mapper.toSummary
import com.vitran.shop.feature.seller.shop.data.remote.SellerShopApi
import com.vitran.shop.feature.seller.shop.data.state.SellerShopStateStore
import com.vitran.shop.feature.seller.shop.domain.error.toSellerShopAppError
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopResult
import com.vitran.shop.feature.seller.shop.domain.model.FulfillmentMode
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopDetails
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.model.ShopApiKey
import com.vitran.shop.feature.seller.shop.domain.model.ShopSlugAvailability
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
}
