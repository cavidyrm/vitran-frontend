package com.vitran.shop.feature.seller.boost.data.state

import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.boost.domain.model.ActiveBoosts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * User-scoped in-memory active-boost cache. Cleared on logout.
 */
class SellerBoostStateStore(
    invalidationListeners: MutableList<SessionInvalidationListener>,
) : SessionInvalidationListener {

    private val _byShopId = MutableStateFlow<Map<ShopId, ActiveBoosts>>(emptyMap())
    val byShopId: StateFlow<Map<ShopId, ActiveBoosts>> = _byShopId.asStateFlow()

    init {
        invalidationListeners.add(this)
    }

    fun put(shopId: ShopId, boosts: ActiveBoosts) {
        _byShopId.update { it + (shopId to boosts) }
    }

    fun get(shopId: ShopId): ActiveBoosts? = _byShopId.value[shopId]

    fun clear() {
        _byShopId.value = emptyMap()
    }

    override suspend fun onSessionInvalidated() {
        clear()
    }
}
