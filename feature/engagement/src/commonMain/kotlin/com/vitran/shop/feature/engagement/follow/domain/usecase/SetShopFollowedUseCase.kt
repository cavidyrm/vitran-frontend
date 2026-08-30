package com.vitran.shop.feature.engagement.follow.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.analytics.domain.model.MarketplaceAnalyticsTracker
import com.vitran.shop.feature.engagement.analytics.domain.model.UserPersonalizationEvent
import com.vitran.shop.feature.engagement.follow.domain.repository.FollowRepository
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.state.FollowStatus
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

class SetShopFollowedUseCase(
    private val followRepository: FollowRepository,
    private val stateStore: EngagementStateStore,
    private val analyticsTracker: MarketplaceAnalyticsTracker,
) {
    suspend operator fun invoke(shopId: ShopId, followed: Boolean): AppResult<Unit> {
        val previous = stateStore.followStatus(shopId)
        stateStore.setFollowStatus(
            shopId,
            if (followed) FollowStatus.Followed else FollowStatus.NotFollowed,
        )
        return when (val result = followRepository.setFollowed(shopId, followed)) {
            is AppResult.Success -> {
                if (followed) {
                    analyticsTracker.track(UserPersonalizationEvent.FollowShop(shopId))
                }
                result
            }
            is AppResult.Failure -> {
                stateStore.setFollowStatus(shopId, previous)
                result
            }
        }
    }
}
