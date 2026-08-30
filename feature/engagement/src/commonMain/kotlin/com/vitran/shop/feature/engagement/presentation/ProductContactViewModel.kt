package com.vitran.shop.feature.engagement.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.contact.domain.model.ContactProductResult
import com.vitran.shop.feature.engagement.contact.domain.model.ContactRoute
import com.vitran.shop.feature.engagement.contact.domain.model.PurchaseIntentId
import com.vitran.shop.feature.engagement.contact.domain.usecase.ContactProductUseCase
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

sealed interface ProductContactUiState {
    data object Idle : ProductContactUiState
    data object Submitting : ProductContactUiState
    data class RouteReady(val result: ContactProductResult) : ProductContactUiState
    data class Error(val message: String?) : ProductContactUiState
}

sealed interface ProductContactEffect {
    data class OpenExternalUrl(val url: String) : ProductContactEffect
}

class ProductContactViewModel(
    private val productId: ProductId,
    private val contactProduct: ContactProductUseCase,
) : ViewModel() {

    private val mutex = Mutex()
    private val _uiState = MutableStateFlow<ProductContactUiState>(ProductContactUiState.Idle)
    val uiState: StateFlow<ProductContactUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ProductContactEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<ProductContactEffect> = _effects.asSharedFlow()

    val lastPurchaseIntentId: PurchaseIntentId?
        get() = (_uiState.value as? ProductContactUiState.RouteReady)?.result?.intent?.id

    fun contact() {
        if (_uiState.value is ProductContactUiState.Submitting) return
        _uiState.value = ProductContactUiState.Submitting
        viewModelScope.launch {
            mutex.withLock {
                when (val result = contactProduct(productId)) {
                    is AppResult.Success -> {
                        _uiState.value = ProductContactUiState.RouteReady(result.value)
                        val route = result.value.route
                        if (route is ContactRoute.WhatsApp) {
                            _effects.tryEmit(ProductContactEffect.OpenExternalUrl(route.url))
                        }
                    }
                    is AppResult.Failure -> {
                        _uiState.value = ProductContactUiState.Error(result.error.message)
                    }
                }
            }
        }
    }
}
