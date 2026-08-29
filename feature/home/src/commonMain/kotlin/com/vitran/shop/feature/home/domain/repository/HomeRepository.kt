package com.vitran.shop.feature.home.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.home.domain.model.HomeFeed
import com.vitran.shop.feature.location.domain.model.CityId

interface HomeRepository {
    suspend fun getHome(cityId: CityId? = null, forceRefresh: Boolean = false): AppResult<HomeFeed>
}
