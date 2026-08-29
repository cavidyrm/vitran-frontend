package com.vitran.shop.feature.marketplace.product.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.common.presentation.CursorListController
import com.vitran.shop.feature.marketplace.common.presentation.CursorListState
import com.vitran.shop.feature.marketplace.product.domain.model.ProductSummary
import com.vitran.shop.feature.marketplace.product.domain.query.ProductBrowseQuery
import com.vitran.shop.feature.marketplace.product.domain.query.ProductSearchQuery
import com.vitran.shop.feature.marketplace.product.domain.repository.ProductRepository
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProductListUiState {
    data object Loading : ProductListUiState

    data class Content(
        val products: CursorListState<ProductSummary>,
        val categorySlug: CategorySlug?,
    ) : ProductListUiState

    data object Empty : ProductListUiState

    data class Error(
        val message: String?,
    ) : ProductListUiState
}

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val categorySlug: CategorySlug?,
) : ViewModel() {

    private val controller = CursorListController<ProductSummary, Long>(idOf = { it.id.value })
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        loadInitial()
    }

    fun retry() = loadInitial(forceRefresh = true)

    fun refresh() {
        val current = _uiState.value as? ProductListUiState.Content ?: return
        viewModelScope.launch {
            val generation = controller.resetForNewQuery()
            val refreshing = controller.beginRefresh(current.products)
            _uiState.value = current.copy(products = refreshing)
            loadPage(CursorPagination(), generation, isRefresh = true)
        }
    }

    fun loadNextPage() {
        val current = _uiState.value as? ProductListUiState.Content ?: return
        val loadingMore = controller.beginLoadMore(current.products) ?: return
        _uiState.value = current.copy(products = loadingMore)
        viewModelScope.launch {
            loadPage(CursorPagination(cursor = current.products.nextCursor), controller.currentGeneration(), false)
        }
    }

    fun retryPagination() = loadNextPage()

    private fun loadInitial(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val generation = controller.resetForNewQuery()
            _uiState.value = ProductListUiState.Loading
            loadPage(CursorPagination(), generation, isRefresh = forceRefresh)
        }
    }

    private suspend fun loadPage(pagination: CursorPagination, generation: Int, isRefresh: Boolean) {
        val query = ProductBrowseQuery(categorySlug = categorySlug, pagination = pagination)
        when (val result = productRepository.getProducts(query)) {
            is AppResult.Success -> {
                val base = (_uiState.value as? ProductListUiState.Content)?.products
                    ?: controller.beginInitialLoad(CursorListState())
                val updated = when {
                    isRefresh -> controller.applyRefreshPage(base, result.value, generation)
                    pagination.cursor == null -> controller.applyInitialPage(base, result.value, generation)
                    else -> controller.applyLoadMorePage(base, result.value)
                }
                _uiState.value = if (updated.items.isEmpty()) {
                    ProductListUiState.Empty
                } else {
                    ProductListUiState.Content(updated, categorySlug)
                }
            }
            is AppResult.Failure -> {
                if (pagination.cursor == null) {
                    _uiState.value = ProductListUiState.Error(result.error.message)
                } else {
                    val current = _uiState.value as? ProductListUiState.Content ?: return
                    _uiState.value = current.copy(
                        products = controller.applyLoadMoreFailure(current.products, result.error),
                    )
                }
            }
        }
    }
}

sealed interface ProductSearchUiState {
    data class Idle(
        val query: String = "",
    ) : ProductSearchUiState

    data class Searching(
        val query: String,
        val results: CursorListState<ProductSummary> = CursorListState(isLoadingInitial = true),
    ) : ProductSearchUiState

    data class Results(
        val query: String,
        val results: CursorListState<ProductSummary>,
    ) : ProductSearchUiState

    data class Empty(
        val query: String,
    ) : ProductSearchUiState

    data class Error(
        val query: String,
        val message: String?,
    ) : ProductSearchUiState
}

class ProductSearchViewModel(
    private val productRepository: ProductRepository,
    initialQuery: String = "",
) : ViewModel() {

    private val controller = CursorListController<ProductSummary, Long>(idOf = { it.id.value })
    private val _uiState = MutableStateFlow<ProductSearchUiState>(
        if (initialQuery.isBlank()) ProductSearchUiState.Idle() else ProductSearchUiState.Searching(initialQuery),
    )
    val uiState: StateFlow<ProductSearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var pendingQuery: String = initialQuery

    init {
        if (initialQuery.isNotBlank()) {
            submitSearch(initialQuery)
        }
    }

    fun onQueryChanged(query: String) {
        pendingQuery = query
        if (query.isBlank()) {
            searchJob?.cancel()
            _uiState.value = ProductSearchUiState.Idle()
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            submitSearch(query)
        }
    }

    fun submitSearch(query: String = pendingQuery) {
        if (query.isBlank()) {
            _uiState.value = ProductSearchUiState.Idle()
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val generation = controller.resetForNewQuery()
            _uiState.value = ProductSearchUiState.Searching(
                query = query,
                results = controller.beginInitialLoad(CursorListState()),
            )
            val searchQuery = ProductSearchQuery(query = query, pagination = CursorPagination())
            when (val result = productRepository.searchProducts(searchQuery)) {
                is AppResult.Success -> {
                    if (generation != controller.currentGeneration()) return@launch
                    val page = controller.applyInitialPage(
                        CursorListState(isLoadingInitial = true),
                        result.value,
                        generation,
                    )
                    _uiState.value = if (page.items.isEmpty()) {
                        ProductSearchUiState.Empty(query)
                    } else {
                        ProductSearchUiState.Results(query, page)
                    }
                }
                is AppResult.Failure -> {
                    if (generation != controller.currentGeneration()) return@launch
                    _uiState.value = ProductSearchUiState.Error(query, result.error.message)
                }
            }
        }
    }

    fun loadNextPage() {
        val current = _uiState.value as? ProductSearchUiState.Results ?: return
        val loadingMore = controller.beginLoadMore(current.results) ?: return
        _uiState.value = current.copy(results = loadingMore)
        viewModelScope.launch {
            val searchQuery = ProductSearchQuery(
                query = current.query,
                pagination = CursorPagination(cursor = current.results.nextCursor),
            )
            when (val result = productRepository.searchProducts(searchQuery)) {
                is AppResult.Success -> {
                    val updated = controller.applyLoadMorePage(loadingMore, result.value)
                    _uiState.value = current.copy(results = updated)
                }
                is AppResult.Failure -> {
                    _uiState.value = current.copy(
                        results = controller.applyLoadMoreFailure(loadingMore, result.error),
                    )
                }
            }
        }
    }

    fun retry() = submitSearch()

    fun retryPagination() = loadNextPage()

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 400L
    }
}
