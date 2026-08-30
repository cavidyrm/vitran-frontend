package com.vitran.shop.feature.seller.subscription.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopFilter
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import com.vitran.shop.feature.seller.subscription.domain.model.ShopSubscription
import com.vitran.shop.feature.seller.subscription.domain.repository.SubscriptionRepository
import com.vitran.shop.feature.seller.subscription.domain.usecase.GetShopEntitlementsUseCase
import com.vitran.shop.feature.seller.subscription.domain.model.ShopEntitlements
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class StorePlanContentState {
    data object Loading : StorePlanContentState()
    data class Content(
        val subscription: ShopSubscription,
        val entitlements: ShopEntitlements?,
        val storeName: String,
    ) : StorePlanContentState()
    data class Error(val error: AppError) : StorePlanContentState()
}

data class StorePlanUiState(
    val shops: List<SellerShopSummary> = emptyList(),
    val selectedShopId: ShopId? = null,
    val content: StorePlanContentState = StorePlanContentState.Loading,
    val refreshing: Boolean = false,
)

class StorePlanViewModel(
    private val sellerShopRepository: SellerShopRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val getShopEntitlements: GetShopEntitlementsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StorePlanUiState())
    val uiState: StateFlow<StorePlanUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(content = StorePlanContentState.Loading) }
            when (
                val shopsResult =
                    sellerShopRepository.getMyShops(
                        SellerShopListQuery(
                            activeFilter = SellerShopFilter.All,
                            pagination = CursorPagination(perPage = 50),
                        ),
                    )
            ) {
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(content = StorePlanContentState.Error(shopsResult.error))
                    }
                    return@launch
                }
                is AppResult.Success -> {
                    val shops = shopsResult.value.items
                    val selected = shops.firstOrNull()
                    if (selected == null) {
                        _uiState.update {
                            it.copy(
                                shops = emptyList(),
                                content = StorePlanContentState.Error(
                                    AppError.Validation(message = "فروشگاهی یافت نشد"),
                                ),
                            )
                        }
                        return@launch
                    }
                    _uiState.update {
                        it.copy(shops = shops, selectedShopId = selected.id)
                    }
                    loadSubscription(selected.id, selected.title)
                }
            }
        }
    }

    fun selectShop(shopId: ShopId) {
        val shop = _uiState.value.shops.firstOrNull { it.id == shopId } ?: return
        _uiState.update { it.copy(selectedShopId = shopId) }
        viewModelScope.launch { loadSubscription(shopId, shop.title) }
    }

    fun refresh() {
        val shopId = _uiState.value.selectedShopId ?: return
        val name = _uiState.value.shops.firstOrNull { it.id == shopId }?.title.orEmpty()
        viewModelScope.launch {
            _uiState.update { it.copy(refreshing = true) }
            loadSubscription(shopId, name, forceRefresh = true)
            _uiState.update { it.copy(refreshing = false) }
        }
    }

    private suspend fun loadSubscription(
        shopId: ShopId,
        storeName: String,
        forceRefresh: Boolean = false,
    ) {
        when (val result = subscriptionRepository.getSubscription(shopId, forceRefresh)) {
            is AppResult.Success -> {
                val entitlements =
                    when (val e = getShopEntitlements(shopId, forceRefresh = false)) {
                        is AppResult.Success -> e.value
                        is AppResult.Failure -> null
                    }
                _uiState.update {
                    it.copy(
                        content = StorePlanContentState.Content(
                            subscription = result.value,
                            entitlements = entitlements,
                            storeName = storeName,
                        ),
                    )
                }
            }
            is AppResult.Failure -> {
                _uiState.update {
                    it.copy(content = StorePlanContentState.Error(result.error))
                }
            }
        }
    }
}
