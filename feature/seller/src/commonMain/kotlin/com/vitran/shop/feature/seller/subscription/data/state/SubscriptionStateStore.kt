package com.vitran.shop.feature.seller.subscription.data.state

import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.subscription.domain.model.ShopSubscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * User-scoped in-memory map of ShopId → ShopSubscription.
 * Cleared on logout / terminal session invalidation.
 */
class SubscriptionStateStore(
    invalidationListeners: MutableList<SessionInvalidationListener>,
) : SessionInvalidationListener {

    private val _byShopId = MutableStateFlow<Map<ShopId, ShopSubscription>>(emptyMap())
    val byShopId: StateFlow<Map<ShopId, ShopSubscription>> = _byShopId.asStateFlow()

    init {
        invalidationListeners.add(this)
    }

    fun put(subscription: ShopSubscription) {
        _byShopId.update { it + (subscription.shopId to subscription) }
    }

    fun get(shopId: ShopId): ShopSubscription? = _byShopId.value[shopId]

    fun clear() {
        _byShopId.value = emptyMap()
    }

    override suspend fun onSessionInvalidated() {
        clear()
    }
}
