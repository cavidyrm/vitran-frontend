package com.vitran.shop.feature.location.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug

interface LocationRepository {
    suspend fun getCities(forceRefresh: Boolean = false): AppResult<List<City>>
    suspend fun getCityById(id: CityId): AppResult<City>
    suspend fun getCityBySlug(slug: CitySlug): AppResult<City>
}
