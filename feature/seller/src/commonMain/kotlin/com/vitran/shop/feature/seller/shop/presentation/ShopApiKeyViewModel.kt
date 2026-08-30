package com.vitran.shop.feature.seller.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.shop.domain.model.ShopApiKey
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ShopApiKeyUiState {
    data object Hidden : ShopApiKeyUiState()
    data object ConfirmingRegeneration : ShopApiKeyUiState()
    data object Regenerating : ShopApiKeyUiState()
    data class Generated(val key: ShopApiKey) : ShopApiKeyUiState()
    data class Error(val error: AppError) : ShopApiKeyUiState()
}

/** Deferred UI — ephemeral API-key state; never persisted. */
class ShopApiKeyViewModel(
    private val shopId: ShopId,
    private val sellerShopRepository: SellerShopRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ShopApiKeyUiState>(ShopApiKeyUiState.Hidden)
    val uiState: StateFlow<ShopApiKeyUiState> = _uiState.asStateFlow()

    private var regenerateJob: Job? = null

    fun requestRegeneration() {
        if (_uiState.value is ShopApiKeyUiState.Regenerating) return
        _uiState.value = ShopApiKeyUiState.ConfirmingRegeneration
    }

    fun cancelConfirmation() {
        if (_uiState.value is ShopApiKeyUiState.ConfirmingRegeneration) {
            _uiState.value = ShopApiKeyUiState.Hidden
        }
    }

    fun confirmRegeneration() {
        if (_uiState.value is ShopApiKeyUiState.Regenerating) return
        if (_uiState.value !is ShopApiKeyUiState.ConfirmingRegeneration &&
            _uiState.value !is ShopApiKeyUiState.Error
        ) {
            return
        }
        regenerateJob?.cancel()
        _uiState.value = ShopApiKeyUiState.Regenerating
        regenerateJob =
            viewModelScope.launch {
                when (val result = sellerShopRepository.regenerateApiKey(shopId)) {
                    is AppResult.Success -> {
                        _uiState.value = ShopApiKeyUiState.Generated(result.value)
                    }
                    is AppResult.Failure -> {
                        _uiState.value = ShopApiKeyUiState.Error(result.error)
                    }
                }
            }
    }

    fun dismiss() {
        regenerateJob?.cancel()
        regenerateJob = null
        _uiState.value = ShopApiKeyUiState.Hidden
    }
}
