package com.vitran.shop.feature.seller.boost.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.boost.domain.model.ActiveBoosts
import com.vitran.shop.feature.seller.boost.domain.repository.SellerBoostRepository
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopFilter
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ActiveBoostsContentState {
    data object Loading : ActiveBoostsContentState()
    data object Empty : ActiveBoostsContentState()
    data class Unmapped(val count: Int) : ActiveBoostsContentState()
    data class Error(val error: AppError) : ActiveBoostsContentState()
}

data class SellerBoostsUiState(
    val shops: List<SellerShopSummary> = emptyList(),
    val selectedShopId: ShopId? = null,
    val content: ActiveBoostsContentState = ActiveBoostsContentState.Loading,
    val refreshing: Boolean = false,
)

class SellerBoostsViewModel(
    private val sellerShopRepository: SellerShopRepository,
    private val boostRepository: SellerBoostRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SellerBoostsUiState())
    val uiState: StateFlow<SellerBoostsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(content = ActiveBoostsContentState.Loading) }
            when (
                val shopsResult =
                    sellerShopRepository.getMyShops(
                        SellerShopListQuery(
                            activeFilter = SellerShopFilter.All,
                            pagination = CursorPagination(perPage = 50),
                        ),
                    )
            ) {
                is AppResult.Failure ->
                    _uiState.update {
                        it.copy(content = ActiveBoostsContentState.Error(shopsResult.error))
                    }
                is AppResult.Success -> {
                    val shops = shopsResult.value.items
                    val selected = shops.firstOrNull()
                    if (selected == null) {
                        _uiState.update {
                            it.copy(
                                shops = emptyList(),
                                content = ActiveBoostsContentState.Empty,
                            )
                        }
                        return@launch
                    }
                    _uiState.update { it.copy(shops = shops, selectedShopId = selected.id) }
                    loadBoosts(selected.id)
                }
            }
        }
    }

    fun selectShop(shopId: ShopId) {
        if (_uiState.value.selectedShopId == shopId) return
        loadJob?.cancel()
        _uiState.update { it.copy(selectedShopId = shopId) }
        loadJob = viewModelScope.launch { loadBoosts(shopId) }
    }

    fun refresh() {
        val shopId = _uiState.value.selectedShopId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(refreshing = true) }
            loadBoosts(shopId, forceRefresh = true)
            _uiState.update { it.copy(refreshing = false) }
        }
    }

    private suspend fun loadBoosts(shopId: ShopId, forceRefresh: Boolean = false) {
        when (val result = boostRepository.getActiveBoosts(shopId, forceRefresh)) {
            is AppResult.Failure -> {
                if (_uiState.value.selectedShopId == shopId) {
                    _uiState.update { it.copy(content = ActiveBoostsContentState.Error(result.error)) }
                }
            }
            is AppResult.Success -> {
                if (_uiState.value.selectedShopId != shopId) return
                _uiState.update {
                    it.copy(
                        content =
                            when (val boosts = result.value) {
                                ActiveBoosts.Empty -> ActiveBoostsContentState.Empty
                                is ActiveBoosts.Unmapped -> ActiveBoostsContentState.Unmapped(boosts.count)
                            },
                    )
                }
            }
        }
    }
}
