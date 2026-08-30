package com.vitran.shop.feature.marketplace.shop.data.repository

import com.vitran.shop.core.database.VitranDatabase
import com.vitran.shop.core.database.entity.ShopDetailEntity
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.data.mapper.toBrowseShopSummaryPage
import com.vitran.shop.feature.marketplace.shop.data.mapper.toDomain
import com.vitran.shop.feature.marketplace.shop.data.mapper.toShopSummaryPage
import com.vitran.shop.feature.marketplace.shop.data.remote.PublicShopApi
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.PublicShopDetailsDto
import com.vitran.shop.feature.marketplace.shop.domain.error.toShopResultError
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopDetails
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSummary
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopBrowseQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopListQuery
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopRepository
import kotlinx.serialization.json.Json
import kotlin.time.Clock

internal class DefaultShopRepository(
    private val publicShopApi: PublicShopApi,
    private val database: VitranDatabase,
) : ShopRepository {

    private val shopDetailDao get() = database.shopDetailDao()

    private val json = Json { ignoreUnknownKeys = true }

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
        shopDetailDao.getById(id.value)?.let { entity ->
            if (entity.unavailable) {
                return AppResult.Failure(AppError.NotFound(message = "فروشگاه در دسترس نیست").toShopResultError())
            }
            return AppResult.Success(entity.toDomain(json))
        }
        return when (val result = publicShopApi.getShopById(id)) {
            is AppResult.Success -> {
                val dto = result.value.shop
                persistShop(dto)
                AppResult.Success(dto.toDomain())
            }
            is AppResult.Failure -> handleShopFailure(result.error, id = id.value, slug = null)
        }
    }

    override suspend fun getShop(slug: ShopSlug): AppResult<ShopDetails> {
        shopDetailDao.getBySlug(slug.value)?.let { entity ->
            if (entity.unavailable) {
                return AppResult.Failure(AppError.NotFound(message = "فروشگاه در دسترس نیست").toShopResultError())
            }
            return AppResult.Success(entity.toDomain(json))
        }
        return when (val result = publicShopApi.getShopBySlug(slug)) {
            is AppResult.Success -> {
                val dto = result.value.shop
                persistShop(dto)
                AppResult.Success(dto.toDomain())
            }
            is AppResult.Failure -> handleShopFailure(result.error, id = null, slug = slug.value)
        }
    }

    override suspend fun invalidateShop(id: ShopId) {
        shopDetailDao.deleteById(id.value)
    }

    private suspend fun persistShop(dto: PublicShopDetailsDto) {
        val now = Clock.System.now().toEpochMilliseconds()
        shopDetailDao.upsert(
            ShopDetailEntity(
                id = dto.id,
                slug = dto.slug,
                payloadJson = json.encodeToString(PublicShopDetailsDto.serializer(), dto),
                unavailable = false,
                fetchedAt = now,
            ),
        )
    }

    private suspend fun handleShopFailure(
        error: AppError,
        id: Long?,
        slug: String?,
    ): AppResult<ShopDetails> {
        if (error is AppError.NotFound) {
            when {
                id != null -> shopDetailDao.markUnavailable(id)
                slug != null -> {
                    shopDetailDao.getBySlug(slug)?.let { shopDetailDao.markUnavailable(it.id) }
                }
            }
            return AppResult.Failure(error.toShopResultError())
        }
        val cached = when {
            id != null -> shopDetailDao.getById(id)
            slug != null -> shopDetailDao.getBySlug(slug)
            else -> null
        }
        if (cached != null && !cached.unavailable) {
            return AppResult.Success(cached.toDomain(json))
        }
        return AppResult.Failure(error.toShopResultError())
    }
}

private fun ShopDetailEntity.toDomain(json: Json): ShopDetails =
    json.decodeFromString(PublicShopDetailsDto.serializer(), payloadJson).toDomain()
