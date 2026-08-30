package com.vitran.shop.feature.engagement.follow.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

/**
 * Follow mutations only. List and GET-by-id response schemas are unresolved
 * (Postman has no examples) and are not invented.
 */
interface FollowRepository {
    suspend fun setFollowed(shopId: ShopId, followed: Boolean): AppResult<Unit>
}
