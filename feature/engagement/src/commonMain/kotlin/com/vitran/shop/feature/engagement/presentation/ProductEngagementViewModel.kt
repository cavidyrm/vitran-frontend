package com.vitran.shop.feature.engagement.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.engagement.analytics.domain.model.MarketplaceAnalyticsTracker
import com.vitran.shop.feature.engagement.analytics.domain.model.ShopAnalyticsEvent
import com.vitran.shop.feature.engagement.analytics.domain.model.UserPersonalizationEvent
import com.vitran.shop.feature.engagement.follow.domain.usecase.SetShopFollowedUseCase
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.state.FollowStatus
import com.vitran.shop.feature.engagement.state.SaveStatus
import com.vitran.shop.feature.engagement.wishlist.domain.usecase.SetProductSavedUseCase
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
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

sealed interface ProductEngagementEffect {
    data object RequestLogin : ProductEngagementEffect

    data class Message(val text: String) : ProductEngagementEffect
}

data class ProductEngagementUiState(
    val saveStatus: SaveStatus = SaveStatus.Unknown,
    val followStatus: FollowStatus = FollowStatus.Unknown,
    val isSavePending: Boolean = false,
    val isFollowPending: Boolean = false,
    val mutationError: String? = null,
)

class ProductEngagementViewModel(
    private val productId: ProductId,
    private val shopId: ShopId?,
    private val setProductSaved: SetProductSavedUseCase,
    private val setShopFollowed: SetShopFollowedUseCase,
    private val stateStore: EngagementStateStore,
    private val sessionRepository: SessionRepository,
    private val analyticsTracker: MarketplaceAnalyticsTracker,
) : ViewModel() {

    private val saveMutex = Mutex()
    private val followMutex = Mutex()
    private val _savePending = MutableStateFlow(false)
    private val _followPending = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _effects = MutableSharedFlow<ProductEngagementEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<ProductEngagementEffect> = _effects.asSharedFlow()

    private var viewTracked = false

    val uiState: StateFlow<ProductEngagementUiState> = combine(
        stateStore.wishlistStateByProductId,
        stateStore.followStateByShopId,
        _savePending,
        _followPending,
        _error,
    ) { wishlist, follows, savePending, followPending, error ->
        ProductEngagementUiState(
            saveStatus = wishlist[productId] ?: SaveStatus.Unknown,
            followStatus = shopId?.let { follows[it] } ?: FollowStatus.Unknown,
            isSavePending = savePending,
            isFollowPending = followPending,
            mutationError = error,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ProductEngagementUiState(),
    )

    fun onProductDisplayed() {
        if (viewTracked) return
        viewTracked = true
        analyticsTracker.track(
            UserPersonalizationEvent.ViewProduct(productId = productId, shopId = shopId),
        )
        shopId?.let { id ->
            analyticsTracker.track(id, ShopAnalyticsEvent.ProductView(productId = productId))
        }
    }

    fun onSaveClick() {
        if (!requireAuthenticated()) return
        if (_savePending.value) return
        val current = uiState.value.saveStatus
        val nextSaved = current != SaveStatus.Saved
        _savePending.value = true
        viewModelScope.launch {
            saveMutex.withLock {
                _error.value = null
                when (val result = setProductSaved(productId, nextSaved, shopId)) {
                    is AppResult.Success -> Unit
                    is AppResult.Failure -> {
                        _error.value = result.error.message
                        _effects.tryEmit(
                            ProductEngagementEffect.Message(
                                result.error.message ?: "ذخیره انجام نشد",
                            ),
                        )
                    }
                }
                _savePending.value = false
            }
        }
    }

    fun onFollowClick() {
        val id = shopId ?: return
        if (!requireAuthenticated()) return
        if (_followPending.value) return
        val current = uiState.value.followStatus
        val nextFollowed = current != FollowStatus.Followed
        _followPending.value = true
        viewModelScope.launch {
            followMutex.withLock {
                _error.value = null
                when (val result = setShopFollowed(id, nextFollowed)) {
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

    private fun requireAuthenticated(): Boolean {
        if (sessionRepository.sessionState.value == SessionState.Authenticated) return true
        _effects.tryEmit(ProductEngagementEffect.RequestLogin)
        return false
    }
}

data class CatalogEngagementUiState(
    val saveByProductId: Map<Long, SaveStatus> = emptyMap(),
    val pendingProductIds: Set<Long> = emptySet(),
)

class CatalogEngagementViewModel(
    private val setProductSaved: SetProductSavedUseCase,
    private val stateStore: EngagementStateStore,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _pending = MutableStateFlow<Set<Long>>(emptySet())
    private val _effects = MutableSharedFlow<ProductEngagementEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<ProductEngagementEffect> = _effects.asSharedFlow()

    val uiState: StateFlow<CatalogEngagementUiState> = combine(
        stateStore.wishlistStateByProductId,
        _pending,
    ) { wishlist, pending ->
        CatalogEngagementUiState(
            saveByProductId = wishlist.mapKeys { it.key.value },
            pendingProductIds = pending,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        CatalogEngagementUiState(),
    )

    fun onSaveClick(productIdValue: Long, shopIdValue: Long? = null) {
        if (sessionRepository.sessionState.value != SessionState.Authenticated) {
            _effects.tryEmit(ProductEngagementEffect.RequestLogin)
            return
        }
        if (productIdValue in _pending.value) return
        val productId = ProductId(productIdValue)
        val shopId = shopIdValue?.let(::ShopId)
        val nextSaved = stateStore.saveStatus(productId) != SaveStatus.Saved
        _pending.value = _pending.value + productIdValue
        viewModelScope.launch {
            when (val result = setProductSaved(productId, nextSaved, shopId)) {
                is AppResult.Success -> Unit
                is AppResult.Failure -> {
                    _effects.tryEmit(
                        ProductEngagementEffect.Message(
                            result.error.message ?: "ذخیره انجام نشد",
                        ),
                    )
                }
            }
            _pending.value = _pending.value - productIdValue
        }
    }
}
