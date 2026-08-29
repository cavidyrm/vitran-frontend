package com.vitran.shop.feature.home.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.home.data.mapper.toDomain
import com.vitran.shop.feature.home.data.remote.HomeApi
import com.vitran.shop.feature.home.domain.model.HomeFeed
import com.vitran.shop.feature.home.domain.repository.HomeRepository
import com.vitran.shop.feature.location.domain.model.CityId
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultHomeRepository(
    private val homeApi: HomeApi,
) : HomeRepository {

    private val cacheMutex = Mutex()
    private var cachedFeed: HomeFeed? = null
    private var cachedCityId: CityId? = null

    override suspend fun getHome(cityId: CityId?, forceRefresh: Boolean): AppResult<HomeFeed> {
        if (!forceRefresh) {
            cacheMutex.withLock {
                if (cachedFeed != null && cachedCityId == cityId) {
                    return AppResult.Success(cachedFeed!!)
                }
            }
        }

        return when (val result = homeApi.getHome(cityId)) {
            is AppResult.Success -> {
                val feed = result.value.home.toDomain(cityId)
                cacheMutex.withLock {
                    cachedFeed = feed
                    cachedCityId = cityId
                }
                AppResult.Success(feed)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }
}
