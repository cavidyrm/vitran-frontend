package com.vitran.shop.feature.engagement.analytics.domain.model

import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug

/**
 * User personalization — POST /events field `event_type`.
 */
sealed interface UserPersonalizationEvent {
    data class ViewProduct(
        val productId: ProductId,
        val shopId: ShopId? = null,
    ) : UserPersonalizationEvent

    data class ViewShop(
        val shopId: ShopId,
    ) : UserPersonalizationEvent

    data class Wishlist(
        val productId: ProductId,
        val shopId: ShopId? = null,
    ) : UserPersonalizationEvent

    data class FollowShop(
        val shopId: ShopId,
    ) : UserPersonalizationEvent

    data class PurchaseIntent(
        val productId: ProductId,
        val shopId: ShopId,
    ) : UserPersonalizationEvent

    /** Typed for transport; do not emit until query payload is verified. */
    data object Search : UserPersonalizationEvent

    /** Typed for transport; do not emit until category field is verified. */
    data object ClickCategory : UserPersonalizationEvent
}

/**
 * Shop analytics — POST /shops/{id}/analytics/events field `event`.
 */
sealed interface ShopAnalyticsEvent {
    data object ShopView : ShopAnalyticsEvent

    data class ProductView(
        val productId: ProductId,
        val categorySlug: CategorySlug? = null,
        val cityId: CityId? = null,
    ) : ShopAnalyticsEvent

    data class ProductClick(
        val productId: ProductId,
        val categorySlug: CategorySlug? = null,
        val cityId: CityId? = null,
    ) : ShopAnalyticsEvent

    data object ShareClick : ShopAnalyticsEvent

    data object CategoryView : ShopAnalyticsEvent

    data object SearchResultView : ShopAnalyticsEvent

    data object PromotionImpression : ShopAnalyticsEvent

    data object PromotionClick : ShopAnalyticsEvent
}

interface MarketplaceAnalyticsTracker {
    fun track(event: UserPersonalizationEvent)

    fun track(shopId: ShopId, event: ShopAnalyticsEvent)
}
