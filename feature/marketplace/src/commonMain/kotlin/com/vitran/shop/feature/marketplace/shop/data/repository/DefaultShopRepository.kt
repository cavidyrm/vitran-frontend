package com.vitran.shop.feature.marketplace.shop.data.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.data.mapper.toBrowseShopSummaryPage
import com.vitran.shop.feature.marketplace.shop.data.mapper.toDomain
import com.vitran.shop.feature.marketplace.shop.data.mapper.toShopSummaryPage
import com.vitran.shop.feature.marketplace.shop.data.remote.PublicShopApi
import com.vitran.shop.feature.marketplace.shop.domain.error.toShopResultError
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopDetails
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSummary
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopBrowseQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopListQuery
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultShopRepository(
    private val publicShopApi: PublicShopApi,
) : ShopRepository {

    private val detailMutex = Mutex()
    private val detailById = mutableMapOf<ShopId, ShopDetails>()
    private val detailBySlug = mutableMapOf<ShopSlug, ShopDetails>()

    override suspend fun getShops(query: ShopListQuery): AppResult<CursorPage<ShopSummary>> =
        when (val result = publicShopApi.getShops(query)) {
            is AppResult.Success -> AppResult.Success(result.value.shops.toShopSummaryPage())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun browseShops(query: ShopBrowseQuery): AppResult<CursorPage<ShopSummary>> =
        when (val result = publicShopApi.browseShops(query)) {
            is AppResult.Success -> AppResult.Success(result.value.shops.toBrowseShopSummaryPage())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun getShop(id: ShopId): AppResult<ShopDetails> {
        detailMutex.withLock { detailById[id] }?.let { return AppResult.Success(it) }
        return when (val result = publicShopApi.getShopById(id)) {
            is AppResult.Success -> {
                val details = result.value.shop.toDomain()
                cacheDetails(details)
                AppResult.Success(details)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toShopResultError())
        }
    }

    override suspend fun getShop(slug: ShopSlug): AppResult<ShopDetails> {
        detailMutex.withLock { detailBySlug[slug] }?.let { return AppResult.Success(it) }
        return when (val result = publicShopApi.getShopBySlug(slug)) {
            is AppResult.Success -> {
                val details = result.value.shop.toDomain()
                cacheDetails(details)
                AppResult.Success(details)
            }
            is AppResult.Failure -> AppResult.Failure(result.error.toShopResultError())
        }
    }

    override suspend fun invalidateShop(id: ShopId) {
        detailMutex.withLock {
            val cached = detailById.remove(id)
            if (cached != null) {
                detailBySlug.remove(cached.slug)
            }
        }
    }

    private suspend fun cacheDetails(details: ShopDetails) {
        detailMutex.withLock {
            detailById[details.id] = details
            detailBySlug[details.slug] = details
        }
    }
}
