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

    private val mutex = Mutex()
    private var cachedCities: List<City> = emptyList()

    override suspend fun getCities(forceRefresh: Boolean): AppResult<List<City>> {
        if (!forceRefresh) {
            val cached = mutex.withLock { cachedCities }
            if (cached.isNotEmpty()) {
                return AppResult.Success(cached)
            }
        }

        return when (val result = locationApi.getCities()) {
            is AppResult.Success -> {
                val cities = result.value.cities.map { it.toDomain() }
                mutex.withLock { cachedCities = cities }
                AppResult.Success(cities)
            }
            is AppResult.Failure -> {
                val cached = mutex.withLock { cachedCities }
                if (cached.isNotEmpty()) {
                    AppResult.Success(cached)
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
                val cached = mutex.withLock { cachedCities.firstOrNull { it.id == id } }
                if (cached != null) AppResult.Success(cached)
                else AppResult.Failure(result.error)
            }
        }

    override suspend fun getCityBySlug(slug: CitySlug): AppResult<City> =
        when (val result = locationApi.getCityBySlug(slug)) {
            is AppResult.Success -> AppResult.Success(result.value.city.toDomain())
            is AppResult.Failure -> {
                val cached = mutex.withLock { cachedCities.firstOrNull { it.slug == slug } }
                if (cached != null) AppResult.Success(cached)
                else AppResult.Failure(result.error)
            }
        }

    override suspend fun invalidateCities() {
        mutex.withLock { cachedCities = emptyList() }
    }
}
