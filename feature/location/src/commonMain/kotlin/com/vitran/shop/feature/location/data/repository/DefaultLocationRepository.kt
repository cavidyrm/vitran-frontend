package com.vitran.shop.feature.location.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.location.data.mapper.toDomain
import com.vitran.shop.feature.location.data.remote.LocationApi
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug
import com.vitran.shop.feature.location.domain.repository.LocationRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultLocationRepository(
    private val locationApi: LocationApi,
) : LocationRepository {

    private val cacheMutex = Mutex()
    private var cachedCities: List<City>? = null

    override suspend fun getCities(forceRefresh: Boolean): AppResult<List<City>> {
        if (!forceRefresh) {
            cacheMutex.withLock {
                cachedCities?.let { return AppResult.Success(it) }
            }
        }

        return when (val result = locationApi.getCities()) {
            is AppResult.Success -> {
                val cities = result.value.cities.map { it.toDomain() }
                cacheMutex.withLock { cachedCities = cities }
                AppResult.Success(cities)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun getCityById(id: CityId): AppResult<City> =
        when (val result = locationApi.getCityById(id)) {
            is AppResult.Success -> AppResult.Success(result.value.city.toDomain())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun getCityBySlug(slug: CitySlug): AppResult<City> =
        when (val result = locationApi.getCityBySlug(slug)) {
            is AppResult.Success -> AppResult.Success(result.value.city.toDomain())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun invalidateCities() {
        cacheMutex.withLock {
            cachedCities = null
        }
    }
}
