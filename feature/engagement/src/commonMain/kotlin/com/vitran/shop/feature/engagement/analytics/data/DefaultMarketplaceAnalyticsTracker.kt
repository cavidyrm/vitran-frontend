package com.vitran.shop.feature.engagement.analytics.data

import com.vitran.shop.core.network.logging.NetworkLogger
import com.vitran.shop.feature.engagement.analytics.data.remote.ShopAnalyticsApi
import com.vitran.shop.feature.engagement.analytics.data.remote.UserEventApi
import com.vitran.shop.feature.engagement.analytics.data.remote.dto.ShopAnalyticsEventRequestDto
import com.vitran.shop.feature.engagement.analytics.data.remote.dto.UserEventRequestDto
import com.vitran.shop.feature.engagement.analytics.domain.model.MarketplaceAnalyticsTracker
import com.vitran.shop.feature.engagement.analytics.domain.model.ShopAnalyticsEvent
import com.vitran.shop.feature.engagement.analytics.domain.model.UserPersonalizationEvent
import com.vitran.shop.feature.engagement.session.VisitorSessionProvider
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class DefaultMarketplaceAnalyticsTracker(
    private val userEventApi: UserEventApi,
    private val shopAnalyticsApi: ShopAnalyticsApi,
    private val visitorSessionProvider: VisitorSessionProvider,
    private val logger: NetworkLogger,
    private val scope: CoroutineScope,
) : MarketplaceAnalyticsTracker {

    override fun track(event: UserPersonalizationEvent) {
        scope.launch {
            try {
                val result = userEventApi.track(event.toRequest(visitorSessionProvider.sessionId()))
                if (result.isFailure) logger.debug("User personalization event dropped")
            } catch (_: Throwable) {
                logger.debug("User personalization event dropped")
            }
        }
    }

    override fun track(shopId: ShopId, event: ShopAnalyticsEvent) {
        scope.launch {
            try {
                val result = shopAnalyticsApi.track(
                    shopId,
                    event.toRequest(visitorSessionProvider.sessionId()),
                )
                if (result.isFailure) logger.debug("Shop analytics event dropped")
            } catch (_: Throwable) {
                logger.debug("Shop analytics event dropped")
            }
        }
    }
}

internal fun UserPersonalizationEvent.toRequest(sessionId: String): UserEventRequestDto =
    when (this) {
        is UserPersonalizationEvent.ViewProduct -> UserEventRequestDto(
            eventType = "view_product",
            sessionId = sessionId,
            productId = productId.value,
            shopId = shopId?.value,
        )
        is UserPersonalizationEvent.ViewShop -> UserEventRequestDto(
            eventType = "view_shop",
            sessionId = sessionId,
            shopId = shopId.value,
        )
        is UserPersonalizationEvent.Wishlist -> UserEventRequestDto(
            eventType = "wishlist",
            sessionId = sessionId,
            productId = productId.value,
            shopId = shopId?.value,
        )
        is UserPersonalizationEvent.FollowShop -> UserEventRequestDto(
            eventType = "follow_shop",
            sessionId = sessionId,
            shopId = shopId.value,
        )
        is UserPersonalizationEvent.PurchaseIntent -> UserEventRequestDto(
            eventType = "purchase_intent",
            sessionId = sessionId,
            productId = productId.value,
            shopId = shopId.value,
        )
        UserPersonalizationEvent.Search -> UserEventRequestDto(
            eventType = "search",
            sessionId = sessionId,
        )
        UserPersonalizationEvent.ClickCategory -> UserEventRequestDto(
            eventType = "click_category",
            sessionId = sessionId,
        )
    }

internal fun ShopAnalyticsEvent.toRequest(sessionId: String): ShopAnalyticsEventRequestDto =
    when (this) {
        ShopAnalyticsEvent.ShopView -> ShopAnalyticsEventRequestDto(
            event = "shop_view",
            sessionId = sessionId,
        )
        is ShopAnalyticsEvent.ProductView -> ShopAnalyticsEventRequestDto(
            event = "product_view",
            sessionId = sessionId,
            productId = productId.value,
            categorySlug = categorySlug?.value,
            cityId = cityId?.value,
        )
        is ShopAnalyticsEvent.ProductClick -> ShopAnalyticsEventRequestDto(
            event = "product_click",
            sessionId = sessionId,
            productId = productId.value,
            categorySlug = categorySlug?.value,
            cityId = cityId?.value,
        )
        ShopAnalyticsEvent.ShareClick -> ShopAnalyticsEventRequestDto(
            event = "share_click",
            sessionId = sessionId,
        )
        ShopAnalyticsEvent.CategoryView -> ShopAnalyticsEventRequestDto(
            event = "category_view",
            sessionId = sessionId,
        )
        ShopAnalyticsEvent.SearchResultView -> ShopAnalyticsEventRequestDto(
            event = "search_result_view",
            sessionId = sessionId,
        )
        ShopAnalyticsEvent.PromotionImpression -> ShopAnalyticsEventRequestDto(
            event = "promotion_impression",
            sessionId = sessionId,
        )
        ShopAnalyticsEvent.PromotionClick -> ShopAnalyticsEventRequestDto(
            event = "promotion_click",
            sessionId = sessionId,
        )
    }
