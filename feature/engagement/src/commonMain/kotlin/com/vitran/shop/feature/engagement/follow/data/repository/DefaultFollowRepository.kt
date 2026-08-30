package com.vitran.shop.feature.engagement.follow.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.data.remote.EngagementApi
import com.vitran.shop.feature.engagement.follow.domain.repository.FollowRepository
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

internal class DefaultFollowRepository(
    private val api: EngagementApi,
) : FollowRepository {
    override suspend fun setFollowed(shopId: ShopId, followed: Boolean): AppResult<Unit> =
        if (followed) api.followShop(shopId) else api.unfollowShop(shopId)
}
