package com.vitran.shop.feature.home.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.home.domain.model.HomeFeed
import com.vitran.shop.feature.home.domain.model.HomeScreen
import com.vitran.shop.feature.home.domain.model.HomeSectionPage
import com.vitran.shop.feature.location.domain.model.CityId

interface HomeRepository {
    suspend fun getHome(cityId: CityId? = null, forceRefresh: Boolean = false): AppResult<HomeFeed>

    /** Server-driven rails. Live Home UI still uses [getHome] until a dedicated screen pass. */
    suspend fun getHomeScreen(cityId: CityId? = null): AppResult<HomeScreen>

    suspend fun getPickedForYouSection(
        limit: Int = 12,
        cursor: String? = null,
        cityId: CityId? = null,
    ): AppResult<HomeSectionPage>
}
