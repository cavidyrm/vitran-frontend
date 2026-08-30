package com.vitran.shop.feature.admin.catalog.location.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.admin.catalog.location.data.remote.AdminLocationApi
import com.vitran.shop.feature.admin.catalog.location.data.remote.dto.toDomain
import com.vitran.shop.feature.admin.catalog.location.domain.AdminLocationRepository
import com.vitran.shop.feature.admin.catalog.location.domain.CreateCityCommand
import com.vitran.shop.feature.admin.catalog.location.domain.UpdateCityCommand
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.repository.LocationRepository

internal class DefaultAdminLocationRepository(
    private val api: AdminLocationApi,
    private val locationRepository: LocationRepository,
) : AdminLocationRepository {
    override suspend fun createCity(command: CreateCityCommand): AppResult<City> =
        when (val result = api.createCity(command)) {
            is AppResult.Success -> {
                locationRepository.invalidateCities()
                AppResult.Success(result.value.city.toDomain())
            }
            is AppResult.Failure -> result
        }

    override suspend fun updateCity(command: UpdateCityCommand): AppResult<City> =
        when (val result = api.updateCity(command)) {
            is AppResult.Success -> {
                locationRepository.invalidateCities()
                AppResult.Success(result.value.city.toDomain())
            }
            is AppResult.Failure -> result
        }

    override suspend fun deleteCity(id: CityId): AppResult<Unit> =
        when (val result = api.deleteCity(id)) {
            is AppResult.Success -> {
                locationRepository.invalidateCities()
                AppResult.Success(Unit)
            }
            is AppResult.Failure -> result
        }
}
