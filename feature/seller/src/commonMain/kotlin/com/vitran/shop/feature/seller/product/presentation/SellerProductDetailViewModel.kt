package com.vitran.shop.feature.seller.product.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.repository.SellerProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SellerProductDetailUiState {
    data object Loading : SellerProductDetailUiState
    data class Content(val product: SellerProductDetails) : SellerProductDetailUiState
    data class Error(val error: AppError) : SellerProductDetailUiState
}

/**
 * Owned product detail for `/account/stores/{shopId}/products/{productId}`.
 * Loads `GET /seller/products/{id}`.
 */
class SellerProductDetailViewModel(
    private val productId: ProductId,
    private val repository: SellerProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<SellerProductDetailUiState>(SellerProductDetailUiState.Loading)
    val uiState: StateFlow<SellerProductDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = SellerProductDetailUiState.Loading
            _uiState.value = when (val result = repository.getProduct(productId)) {
                is AppResult.Success -> SellerProductDetailUiState.Content(result.value)
                is AppResult.Failure -> SellerProductDetailUiState.Error(result.error)
            }
        }
    }
}
