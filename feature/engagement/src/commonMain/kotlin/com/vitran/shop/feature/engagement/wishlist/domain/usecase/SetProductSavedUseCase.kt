package com.vitran.shop.feature.engagement.wishlist.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.analytics.domain.model.MarketplaceAnalyticsTracker
import com.vitran.shop.feature.engagement.analytics.domain.model.UserPersonalizationEvent
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.state.SaveStatus
import com.vitran.shop.feature.engagement.wishlist.domain.repository.WishlistRepository
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

class SetProductSavedUseCase(
    private val wishlistRepository: WishlistRepository,
    private val stateStore: EngagementStateStore,
    private val analyticsTracker: MarketplaceAnalyticsTracker,
) {
    suspend operator fun invoke(
        productId: ProductId,
        saved: Boolean,
        shopId: ShopId? = null,
    ): AppResult<Unit> {
        val previous = stateStore.saveStatus(productId)
        stateStore.setSaveStatus(
            productId,
            if (saved) SaveStatus.Saved else SaveStatus.NotSaved,
        )
        return when (val result = wishlistRepository.setSaved(productId, saved)) {
            is AppResult.Success -> {
                if (saved) {
                    analyticsTracker.track(
                        UserPersonalizationEvent.Wishlist(productId = productId, shopId = shopId),
                    )
                }
                result
            }
            is AppResult.Failure -> {
                stateStore.setSaveStatus(productId, previous)
                result
            }
        }
    }
}
