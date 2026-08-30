package com.vitran.shop.feature.engagement.state

import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSettings
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory, user-scoped follow / favorite-shop / wishlist status.
 * Cleared on logout and terminal session invalidation.
 */
class EngagementStateStore(
    invalidationListeners: MutableList<SessionInvalidationListener>,
) : SessionInvalidationListener {

    private val _followStateByShopId = MutableStateFlow<Map<ShopId, FollowStatus>>(emptyMap())
    val followStateByShopId: StateFlow<Map<ShopId, FollowStatus>> = _followStateByShopId.asStateFlow()

    private val _favoriteShopStateByShopId =
        MutableStateFlow<Map<ShopId, FavoriteShopStatus>>(emptyMap())
    val favoriteShopStateByShopId: StateFlow<Map<ShopId, FavoriteShopStatus>> =
        _favoriteShopStateByShopId.asStateFlow()

    private val _wishlistStateByProductId = MutableStateFlow<Map<ProductId, SaveStatus>>(emptyMap())
    val wishlistStateByProductId: StateFlow<Map<ProductId, SaveStatus>> =
        _wishlistStateByProductId.asStateFlow()

    private val _shareSettings = MutableStateFlow<WishlistShareSettings?>(null)
    val shareSettings: StateFlow<WishlistShareSettings?> = _shareSettings.asStateFlow()

    init {
        invalidationListeners.add(this)
    }

    fun followStatus(shopId: ShopId): FollowStatus =
        _followStateByShopId.value[shopId] ?: FollowStatus.Unknown

    fun favoriteShopStatus(shopId: ShopId): FavoriteShopStatus =
        _favoriteShopStateByShopId.value[shopId] ?: FavoriteShopStatus.Unknown

    fun saveStatus(productId: ProductId): SaveStatus =
        _wishlistStateByProductId.value[productId] ?: SaveStatus.Unknown

    fun setFollowStatus(shopId: ShopId, status: FollowStatus) {
        _followStateByShopId.update { it + (shopId to status) }
    }

    fun setFavoriteShopStatus(shopId: ShopId, status: FavoriteShopStatus) {
        _favoriteShopStateByShopId.update { it + (shopId to status) }
    }

    fun setSaveStatus(productId: ProductId, status: SaveStatus) {
        _wishlistStateByProductId.update { it + (productId to status) }
    }

    fun setShareSettings(settings: WishlistShareSettings?) {
        _shareSettings.value = settings
    }

    fun clear() {
        _followStateByShopId.value = emptyMap()
        _favoriteShopStateByShopId.value = emptyMap()
        _wishlistStateByProductId.value = emptyMap()
        _shareSettings.value = null
    }

    override suspend fun onSessionInvalidated() {
        clear()
    }
}
