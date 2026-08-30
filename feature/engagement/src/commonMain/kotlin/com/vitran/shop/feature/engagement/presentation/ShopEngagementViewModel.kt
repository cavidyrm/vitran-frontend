package com.vitran.shop.feature.engagement.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.engagement.analytics.domain.model.MarketplaceAnalyticsTracker
import com.vitran.shop.feature.engagement.analytics.domain.model.ShopAnalyticsEvent
import com.vitran.shop.feature.engagement.analytics.domain.model.UserPersonalizationEvent
import com.vitran.shop.feature.engagement.favorite.domain.usecase.SetShopFavoriteUseCase
import com.vitran.shop.feature.engagement.follow.domain.usecase.SetShopFollowedUseCase
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.state.FavoriteShopStatus
import com.vitran.shop.feature.engagement.state.FollowStatus
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class ShopEngagementUiState(
    val followStatus: FollowStatus = FollowStatus.Unknown,
    val favoriteStatus: FavoriteShopStatus = FavoriteShopStatus.Unknown,
    val isFollowPending: Boolean = false,
    val isFavoritePending: Boolean = false,
    val mutationError: String? = null,
)

class ShopEngagementViewModel(
    private val shopId: ShopId,
    private val setShopFollowed: SetShopFollowedUseCase,
    private val setShopFavorite: SetShopFavoriteUseCase,
    private val stateStore: EngagementStateStore,
    private val sessionRepository: SessionRepository,
    private val analyticsTracker: MarketplaceAnalyticsTracker,
) : ViewModel() {

    private val followMutex = Mutex()
    private val favoriteMutex = Mutex()
    private val _followPending = MutableStateFlow(false)
    private val _favoritePending = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _effects = MutableSharedFlow<ProductEngagementEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<ProductEngagementEffect> = _effects.asSharedFlow()

    private var viewTracked = false

    val uiState: StateFlow<ShopEngagementUiState> = combine(
        stateStore.followStateByShopId,
        stateStore.favoriteShopStateByShopId,
        _followPending,
        _favoritePending,
        _error,
    ) { follows, favorites, followPending, favoritePending, error ->
        ShopEngagementUiState(
            followStatus = follows[shopId] ?: FollowStatus.Unknown,
            favoriteStatus = favorites[shopId] ?: FavoriteShopStatus.Unknown,
            isFollowPending = followPending,
            isFavoritePending = favoritePending,
            mutationError = error,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ShopEngagementUiState(),
    )

    fun onShopDisplayed() {
        if (viewTracked) return
        viewTracked = true
        analyticsTracker.track(UserPersonalizationEvent.ViewShop(shopId))
        analyticsTracker.track(shopId, ShopAnalyticsEvent.ShopView)
    }

    fun onFollowClick() {
        if (!requireAuthenticated()) return
        if (_followPending.value) return
        val nextFollowed = uiState.value.followStatus != FollowStatus.Followed
        _followPending.value = true
        viewModelScope.launch {
            followMutex.withLock {
                _error.value = null
                when (val result = setShopFollowed(shopId, nextFollowed)) {
                    is AppResult.Success -> Unit
                    is AppResult.Failure -> {
                        _error.value = result.error.message
                        _effects.tryEmit(
                            ProductEngagementEffect.Message(
                                result.error.message ?: "دنبال کردن انجام نشد",
                            ),
                        )
                    }
                }
                _followPending.value = false
            }
        }
    }

    fun onFavoriteClick() {
        if (!requireAuthenticated()) return
        if (_favoritePending.value) return
        val nextFavorite = uiState.value.favoriteStatus != FavoriteShopStatus.Favorited
        _favoritePending.value = true
        viewModelScope.launch {
            favoriteMutex.withLock {
                _error.value = null
                when (val result = setShopFavorite(shopId, nextFavorite)) {
                    is AppResult.Success -> Unit
                    is AppResult.Failure -> {
                        _error.value = result.error.message
                        _effects.tryEmit(
                            ProductEngagementEffect.Message(
                                result.error.message ?: "علاقه‌مندی انجام نشد",
                            ),
                        )
                    }
                }
                _favoritePending.value = false
            }
        }
    }

    fun onShareClick() {
        analyticsTracker.track(shopId, ShopAnalyticsEvent.ShareClick)
    }

    private fun requireAuthenticated(): Boolean {
        if (sessionRepository.sessionState.value == SessionState.Authenticated) return true
        _effects.tryEmit(ProductEngagementEffect.RequestLogin)
        return false
    }
}
