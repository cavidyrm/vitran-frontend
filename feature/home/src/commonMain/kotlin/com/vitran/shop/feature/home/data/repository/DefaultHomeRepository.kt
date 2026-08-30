package com.vitran.shop.feature.home.data.repository

import com.vitran.shop.core.database.VitranDatabase
import com.vitran.shop.core.database.entity.HomeSnapshotEntity
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.home.data.mapper.toDomain
import com.vitran.shop.feature.home.data.remote.HomeApi
import com.vitran.shop.feature.home.data.remote.dto.HomeSectionsDto
import com.vitran.shop.feature.home.domain.model.HomeFeed
import com.vitran.shop.feature.home.domain.repository.HomeRepository
import com.vitran.shop.feature.location.domain.model.CityId
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
                return AppResult.Success(entity.toDomain(json, cityId))
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
                val cached = homeSnapshotDao.get(cityKey)
                if (cached != null) {
                    AppResult.Success(cached.toDomain(json, cityId))
                } else {
                    AppResult.Failure(result.error)
                }
            }
        }
    }
}

private fun CityId?.toCityKey(): String = this?.value?.toString().orEmpty()

private fun HomeSnapshotEntity.toDomain(json: Json, cityId: CityId?): HomeFeed {
    val sections = json.decodeFromString(HomeSectionsDto.serializer(), payloadJson)
    return sections.toDomain(cityId)
}
