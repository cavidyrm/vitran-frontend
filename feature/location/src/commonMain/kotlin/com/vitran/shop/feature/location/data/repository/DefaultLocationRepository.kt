package com.vitran.shop.feature.location.data.repository

import com.vitran.shop.core.database.VitranDatabase
import com.vitran.shop.core.database.entity.CityEntity
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.location.data.mapper.toDomain
import com.vitran.shop.feature.location.data.remote.LocationApi
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug
import com.vitran.shop.feature.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

internal class DefaultLocationRepository(
    private val locationApi: LocationApi,
    private val database: VitranDatabase,
) : LocationRepository {

    private val cityDao get() = database.cityDao()

    fun observeCities(): Flow<List<City>> =
        cityDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override suspend fun getCities(forceRefresh: Boolean): AppResult<List<City>> {
        if (!forceRefresh) {
            val cached = cityDao.getAll()
            if (cached.isNotEmpty()) {
                return AppResult.Success(cached.map { it.toDomain() })
            }
        }

        return when (val result = locationApi.getCities()) {
            is AppResult.Success -> {
                val cities = result.value.cities.map { it.toDomain() }
                val now = Clock.System.now().toEpochMilliseconds()
                cityDao.replaceAll(cities.map { it.toEntity(now) })
                AppResult.Success(cities)
            }
            is AppResult.Failure -> {
                val cached = cityDao.getAll()
                if (cached.isNotEmpty()) {
                    AppResult.Success(cached.map { it.toDomain() })
                } else {
                    AppResult.Failure(result.error)
                }
            }
        }
    }

    override suspend fun getCityById(id: CityId): AppResult<City> =
        when (val result = locationApi.getCityById(id)) {
            is AppResult.Success -> AppResult.Success(result.value.city.toDomain())
            is AppResult.Failure -> {
                val cached = cityDao.getAll().firstOrNull { it.id == id.value }
                if (cached != null) AppResult.Success(cached.toDomain())
                else AppResult.Failure(result.error)
            }
        }

    override suspend fun getCityBySlug(slug: CitySlug): AppResult<City> =
        when (val result = locationApi.getCityBySlug(slug)) {
            is AppResult.Success -> AppResult.Success(result.value.city.toDomain())
            is AppResult.Failure -> {
                val cached = cityDao.getAll().firstOrNull { it.slug == slug.value }
                if (cached != null) AppResult.Success(cached.toDomain())
                else AppResult.Failure(result.error)
            }
        }

    override suspend fun invalidateCities() {
        cityDao.deleteAll()
    }
}

private fun CityEntity.toDomain(): City =
    City(id = CityId(id), slug = CitySlug(slug), name = name)

private fun City.toEntity(fetchedAt: Long): CityEntity =
    CityEntity(id = id.value, slug = slug.value, name = name, fetchedAt = fetchedAt)
