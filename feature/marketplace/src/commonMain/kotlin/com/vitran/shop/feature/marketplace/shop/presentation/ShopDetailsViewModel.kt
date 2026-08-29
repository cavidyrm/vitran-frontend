package com.vitran.shop.feature.marketplace.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.common.domain.filter.ShopFilter
import com.vitran.shop.feature.marketplace.common.presentation.CursorListController
import com.vitran.shop.feature.marketplace.common.presentation.CursorListState
import com.vitran.shop.feature.marketplace.product.domain.model.ProductSummary
import com.vitran.shop.feature.marketplace.product.domain.query.ProductBrowseQuery
import com.vitran.shop.feature.marketplace.product.domain.repository.ProductRepository
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopDetails
import com.vitran.shop.feature.marketplace.shop.domain.model.parseShopNavigationKey
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ShopDetailsUiState {
    data object Loading : ShopDetailsUiState

    data class Content(
        val shop: ShopDetails,
        val products: CursorListState<ProductSummary>,
    ) : ShopDetailsUiState

    data class NotFound(
        val message: String?,
    ) : ShopDetailsUiState

    data class Error(
        val message: String?,
        val retryable: Boolean = true,
    ) : ShopDetailsUiState
}

class ShopDetailsViewModel(
    private val shopRepository: ShopRepository,
    private val productRepository: ProductRepository,
    shopNavigationKey: String,
) : ViewModel() {

    private val navigation = parseShopNavigationKey(shopNavigationKey)
    private val productController = CursorListController<ProductSummary, Long>(idOf = { it.id.value })

    private val _uiState = MutableStateFlow<ShopDetailsUiState>(ShopDetailsUiState.Loading)
    val uiState: StateFlow<ShopDetailsUiState> = _uiState.asStateFlow()

    init {
        loadInitial()
    }

    fun retry() {
        loadInitial()
    }

    fun refreshProducts() {
        val current = _uiState.value as? ShopDetailsUiState.Content ?: return
        viewModelScope.launch {
            val generation = productController.resetForNewQuery()
            val refreshing = productController.beginRefresh(current.products)
            _uiState.value = current.copy(products = refreshing)
            loadProductsPage(
                shop = current.shop,
                pagination = CursorPagination(),
                generation = generation,
                isRefresh = true,
            )
        }
    }

    fun loadNextProductsPage() {
        val current = _uiState.value as? ShopDetailsUiState.Content ?: return
        val loadingMore = productController.beginLoadMore(current.products) ?: return
        _uiState.value = current.copy(products = loadingMore)
        viewModelScope.launch {
            loadProductsPage(
                shop = current.shop,
                pagination = CursorPagination(cursor = current.products.nextCursor),
                generation = productController.currentGeneration(),
                isRefresh = false,
            )
        }
    }

    fun retryProductsPagination() {
        loadNextProductsPage()
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _uiState.value = ShopDetailsUiState.Loading
            val shopResult = when {
                navigation.first != null -> shopRepository.getShop(navigation.first!!)
                navigation.second != null -> shopRepository.getShop(navigation.second!!)
                else -> AppResult.Failure(AppError.Validation(message = "Invalid shop"))
            }
            when (shopResult) {
                is AppResult.Success -> {
                    val generation = productController.resetForNewQuery()
                    val productsState = productController.beginInitialLoad(CursorListState())
                    _uiState.value = ShopDetailsUiState.Content(
                        shop = shopResult.value,
                        products = productsState,
                    )
                    loadProductsPage(
                        shop = shopResult.value,
                        pagination = CursorPagination(),
                        generation = generation,
                        isRefresh = false,
                    )
                }
                is AppResult.Failure -> {
                    _uiState.value = mapShopError(shopResult.error)
                }
            }
        }
    }

    private suspend fun loadProductsPage(
        shop: ShopDetails,
        pagination: CursorPagination,
        generation: Int,
        isRefresh: Boolean,
    ) {
        val query = ProductBrowseQuery(
            shop = ShopFilter.BySlug(shop.slug),
            pagination = pagination,
        )
        when (val result = productRepository.getProducts(query)) {
            is AppResult.Success -> {
                val current = _uiState.value as? ShopDetailsUiState.Content ?: return
                val updatedProducts = if (isRefresh) {
                    productController.applyRefreshPage(current.products, result.value, generation)
                } else if (pagination.cursor == null) {
                    productController.applyInitialPage(current.products, result.value, generation)
                } else {
                    productController.applyLoadMorePage(current.products, result.value)
                }
                _uiState.value = current.copy(products = updatedProducts)
            }
            is AppResult.Failure -> {
                val current = _uiState.value as? ShopDetailsUiState.Content ?: return
                val updated = when {
                    isRefresh -> productController.applyRefreshFailure(current.products, result.error)
                    pagination.cursor == null ->
                        productController.applyInitialFailure(current.products, result.error)
                    else -> productController.applyLoadMoreFailure(current.products, result.error)
                }
                _uiState.value = current.copy(products = updated)
            }
        }
    }

    private fun mapShopError(error: AppError): ShopDetailsUiState =
        when (error) {
            is AppError.NotFound -> ShopDetailsUiState.NotFound(error.message)
            else ->
                ShopDetailsUiState.Error(
                    message = error.message,
                    retryable = error !is AppError.Validation,
                )
        }
}
