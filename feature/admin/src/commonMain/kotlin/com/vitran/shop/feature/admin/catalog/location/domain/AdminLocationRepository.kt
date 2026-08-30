package com.vitran.shop.feature.admin.catalog.location.domain

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId

interface AdminLocationRepository {
    suspend fun createCity(command: CreateCityCommand): AppResult<City>

    suspend fun updateCity(command: UpdateCityCommand): AppResult<City>

    suspend fun deleteCity(id: CityId): AppResult<Unit>
}
