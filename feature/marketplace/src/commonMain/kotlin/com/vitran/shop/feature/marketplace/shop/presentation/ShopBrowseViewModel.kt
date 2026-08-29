package com.vitran.shop.feature.marketplace.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.common.presentation.CursorListController
import com.vitran.shop.feature.marketplace.common.presentation.CursorListState
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSummary
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopBrowseQuery
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopRepository
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ShopBrowseUiState {
    data object Loading : ShopBrowseUiState

    data class Content(
        val shops: CursorListState<ShopSummary>,
        val categorySlug: CategorySlug?,
    ) : ShopBrowseUiState

    data object Empty : ShopBrowseUiState

    data class Error(
        val message: String?,
    ) : ShopBrowseUiState
}

class ShopBrowseViewModel(
    private val shopRepository: ShopRepository,
    private val categorySlug: CategorySlug?,
) : ViewModel() {

    private val controller = CursorListController<ShopSummary, Long>(idOf = { it.id.value })
    private val _uiState = MutableStateFlow<ShopBrowseUiState>(ShopBrowseUiState.Loading)
    val uiState: StateFlow<ShopBrowseUiState> = _uiState.asStateFlow()

    init {
        loadInitial()
    }

    fun retry() = loadInitial()

    fun loadNextPage() {
        val current = _uiState.value as? ShopBrowseUiState.Content ?: return
        val loadingMore = controller.beginLoadMore(current.shops) ?: return
        _uiState.value = current.copy(shops = loadingMore)
        viewModelScope.launch {
            loadPage(CursorPagination(cursor = current.shops.nextCursor), controller.currentGeneration(), false)
        }
    }

    fun retryPagination() = loadNextPage()

    private fun loadInitial() {
        viewModelScope.launch {
            val generation = controller.resetForNewQuery()
            _uiState.value = ShopBrowseUiState.Loading
            loadPage(CursorPagination(), generation, false)
        }
    }

    private suspend fun loadPage(pagination: CursorPagination, generation: Int, isRefresh: Boolean) {
        val query = ShopBrowseQuery(categorySlug = categorySlug, pagination = pagination)
        when (val result = shopRepository.browseShops(query)) {
            is AppResult.Success -> {
                val base = (_uiState.value as? ShopBrowseUiState.Content)?.shops
                    ?: controller.beginInitialLoad(CursorListState())
                val updated = when {
                    pagination.cursor == null -> controller.applyInitialPage(base, result.value, generation)
                    else -> controller.applyLoadMorePage(base, result.value)
                }
                _uiState.value = if (updated.items.isEmpty()) {
                    ShopBrowseUiState.Empty
                } else {
                    ShopBrowseUiState.Content(updated, categorySlug)
                }
            }
            is AppResult.Failure -> {
                if (pagination.cursor == null) {
                    _uiState.value = ShopBrowseUiState.Error(result.error.message)
                } else {
                    val current = _uiState.value as? ShopBrowseUiState.Content ?: return
                    _uiState.value = current.copy(
                        shops = controller.applyLoadMoreFailure(current.shops, result.error),
                    )
                }
            }
        }
    }
}
