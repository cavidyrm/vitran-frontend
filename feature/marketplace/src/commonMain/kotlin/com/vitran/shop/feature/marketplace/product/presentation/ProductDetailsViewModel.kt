package com.vitran.shop.feature.marketplace.product.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.domain.model.ProductDetails
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProductDetailsUiState {
    data object Loading : ProductDetailsUiState

    data class Content(
        val product: ProductDetails,
    ) : ProductDetailsUiState

    data class NotFound(
        val message: String?,
    ) : ProductDetailsUiState

    data class Error(
        val message: String?,
        val retryable: Boolean = true,
    ) : ProductDetailsUiState
}

class ProductDetailsViewModel(
    private val productRepository: ProductRepository,
    private val productId: ProductId,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailsUiState>(ProductDetailsUiState.Loading)
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = ProductDetailsUiState.Loading
            when (val result = productRepository.getProduct(productId)) {
                is AppResult.Success -> {
                    _uiState.value = ProductDetailsUiState.Content(result.value)
                }
                is AppResult.Failure -> {
                    _uiState.value = mapError(result.error)
                }
            }
        }
    }

    private fun mapError(error: AppError): ProductDetailsUiState =
        when (error) {
            is AppError.NotFound ->
                ProductDetailsUiState.NotFound(error.message)
            else ->
                ProductDetailsUiState.Error(
                    message = error.message,
                    retryable = error !is AppError.Validation,
                )
        }
}
