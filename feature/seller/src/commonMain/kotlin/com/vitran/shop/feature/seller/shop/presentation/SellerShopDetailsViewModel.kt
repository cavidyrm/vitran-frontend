package com.vitran.shop.feature.seller.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.shop.domain.model.FulfillmentMode
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopDetails
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class FulfillmentOptionsUiState {
    data object Idle : FulfillmentOptionsUiState()
    data object Loading : FulfillmentOptionsUiState()
    data class Content(val modes: List<FulfillmentMode>) : FulfillmentOptionsUiState()
    data class Error(val error: AppError) : FulfillmentOptionsUiState()
}

data class SellerShopDetailsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val shop: SellerShopDetails? = null,
    val error: AppError? = null,
    val fulfillment: FulfillmentOptionsUiState = FulfillmentOptionsUiState.Idle,
)

/** Deferred UI — loads owner shop via seller GET (not public). */
class SellerShopDetailsViewModel(
    private val shopId: ShopId,
    private val sellerShopRepository: SellerShopRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SellerShopDetailsUiState())
    val uiState: StateFlow<SellerShopDetailsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = sellerShopRepository.getMyShop(shopId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, shop = result.value, error = null)
                    }
                    loadFulfillment()
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.error, shop = null)
                    }
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            when (val result = sellerShopRepository.getMyShop(shopId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(isRefreshing = false, shop = result.value, error = null)
                    }
                    loadFulfillment()
                }
                is AppResult.Failure -> {
                    _uiState.update { it.copy(isRefreshing = false, error = result.error) }
                }
            }
        }
    }

    fun retryFulfillment() {
        loadFulfillment()
    }

    private fun loadFulfillment() {
        viewModelScope.launch {
            _uiState.update { it.copy(fulfillment = FulfillmentOptionsUiState.Loading) }
            when (val result = sellerShopRepository.getFulfillmentOptions(shopId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(fulfillment = FulfillmentOptionsUiState.Content(result.value))
                    }
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(fulfillment = FulfillmentOptionsUiState.Error(result.error))
                    }
                }
            }
        }
    }
}
