package com.vitran.shop.feature.home.data.repository

import com.vitran.shop.core.database.VitranDatabase
import com.vitran.shop.core.database.entity.HomeSnapshotEntity
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.home.data.mapper.toDomain
import com.vitran.shop.feature.home.data.remote.HomeApi
import com.vitran.shop.feature.home.data.remote.dto.HomeSectionsDto
import com.vitran.shop.feature.home.domain.model.HomeFeed
import com.vitran.shop.feature.home.domain.model.HomeRail
import com.vitran.shop.feature.home.domain.model.HomeScreen
import com.vitran.shop.feature.home.domain.repository.HomeRepository
import com.vitran.shop.feature.location.domain.model.CityId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.time.Clock

internal class DefaultHomeRepository(
    private val homeApi: HomeApi,
    private val database: VitranDatabase,
) : HomeRepository {

    private val homeSnapshotDao get() = database.homeSnapshotDao()

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getHome(cityId: CityId?, forceRefresh: Boolean): AppResult<HomeFeed> {
        val cityKey = cityId.toCityKey()
        if (!forceRefresh) {
            homeSnapshotDao.get(cityKey)?.let { entity ->
                entity.toFeed(json, cityId)?.let { return AppResult.Success(it) }
            }
        }

        return when (val result = homeApi.getHome(cityId)) {
            is AppResult.Success -> {
                val sections = result.value.home
                val feed = sections.toDomain(cityId)
                val now = Clock.System.now().toEpochMilliseconds()
                homeSnapshotDao.upsert(
                    HomeSnapshotEntity(
                        cityKey = cityKey,
                        payloadJson = json.encodeToString(HomeSectionsDto.serializer(), sections),
                        fetchedAt = now,
                    ),
                )
                AppResult.Success(feed)
            }
            is AppResult.Failure -> {
                val cached = homeSnapshotDao.get(cityKey)?.toFeed(json, cityId)
                if (cached != null) {
                    AppResult.Success(cached)
                } else {
                    AppResult.Failure(result.error)
                }
            }
        }
    }

    override suspend fun getHomeScreen(
        cityId: CityId?,
        forceRefresh: Boolean,
    ): AppResult<HomeScreen> = withContext(Dispatchers.Default) {
        // Room is not on this path: the wasm SQLite worker resumes on the UI thread,
        // and waiting for it there freezes the page. forceRefresh still always refetches.
        when (val result = homeApi.getHomeScreen(cityId)) {
            is AppResult.Success -> AppResult.Success(result.value.toDomain(cityId))
            is AppResult.Failure -> result
        }
    }

    override suspend fun getHomeSection(
        path: String,
        limit: Int,
        cursor: String?,
        cursorParam: String,
        cityId: CityId?,
    ): AppResult<HomeRail> =
        withContext(Dispatchers.Default) {
            homeApi.getHomeSection(path, limit, cursor, cursorParam, cityId).mapSuccess { it.section.toDomain() }
        }
}

private inline fun <T, R> AppResult<T>.mapSuccess(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(value))
    is AppResult.Failure -> this
}

private fun CityId?.toCityKey(): String = this?.value?.toString().orEmpty()

private fun HomeSnapshotEntity.toFeed(json: Json, cityId: CityId?): HomeFeed? =
    runCatching {
        json.decodeFromString(HomeSectionsDto.serializer(), payloadJson).toDomain(cityId)
    }.getOrNull()
