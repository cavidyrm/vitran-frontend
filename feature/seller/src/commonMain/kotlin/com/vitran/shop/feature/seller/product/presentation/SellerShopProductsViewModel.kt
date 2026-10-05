package com.vitran.shop.feature.seller.product.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.common.presentation.CursorListController
import com.vitran.shop.feature.marketplace.common.presentation.CursorListState
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.product.domain.model.SellerProductSummary
import com.vitran.shop.feature.seller.product.domain.query.SellerProductActiveFilter
import com.vitran.shop.feature.seller.product.domain.query.SellerProductListQuery
import com.vitran.shop.feature.seller.product.domain.repository.SellerProductRepository
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SellerShopProductsUiState(
    val activeFilter: SellerProductActiveFilter = SellerProductActiveFilter.All,
    val categorySlug: CategorySlug? = null,
    val list: CursorListState<SellerProductSummary> = CursorListState(),
)

/**
 * Products of one owned shop for `/account/stores/{id}`.
 * Calls `GET /seller/products` with a fixed `shop_id`, optional `active` and `category_slug`,
 * and `per_page` 20.
 */
class SellerShopProductsViewModel(
    private val shopId: ShopId,
    private val sellerProductRepository: SellerProductRepository,
) : ViewModel() {
    private val controller =
        CursorListController<SellerProductSummary, ProductId>(idOf = { it.id })
    private val _uiState = MutableStateFlow(SellerShopProductsUiState())
    val uiState: StateFlow<SellerShopProductsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        refresh()
    }

    fun setActiveFilter(filter: SellerProductActiveFilter) {
        if (_uiState.value.activeFilter == filter) return
        _uiState.update {
            it.copy(activeFilter = filter, list = CursorListState(isLoadingInitial = true))
        }
        loadInitial()
    }

    fun setCategorySlug(slug: CategorySlug?) {
        val normalized = slug?.value?.trim()?.takeIf { it.isNotEmpty() }?.let(::CategorySlug)
        if (_uiState.value.categorySlug == normalized) return
        _uiState.update {
            it.copy(categorySlug = normalized, list = CursorListState(isLoadingInitial = true))
        }
        loadInitial()
    }

    fun clearFilters() {
        val state = _uiState.value
        if (state.activeFilter == SellerProductActiveFilter.All && state.categorySlug == null) return
        _uiState.update {
            it.copy(
                activeFilter = SellerProductActiveFilter.All,
                categorySlug = null,
                list = CursorListState(isLoadingInitial = true),
            )
        }
        loadInitial()
    }

    fun refresh() {
        loadMoreJob?.cancel()
        val generation = controller.resetForNewQuery()
        _uiState.update { state ->
            val list = state.list
            state.copy(
                list = list.copy(
                    isLoadingInitial = list.items.isEmpty(),
                    isRefreshing = true,
                    initialError = null,
                    refreshError = null,
                    paginationError = null,
                ),
            )
        }
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                when (val result = sellerProductRepository.getProducts(query(CursorPagination()))) {
                    is AppResult.Success -> {
                        _uiState.update {
                            it.copy(list = controller.applyRefreshPage(it.list, result.value, generation))
                        }
                    }
                    is AppResult.Failure -> {
                        _uiState.update {
                            val failed =
                                if (it.list.items.isEmpty()) {
                                    controller.applyInitialFailure(it.list, result.error)
                                } else {
                                    controller.applyRefreshFailure(it.list, result.error)
                                }
                            it.copy(
                                list = failed.copy(
                                    isLoadingInitial = false,
                                    isRefreshing = false,
                                ),
                            )
                        }
                    }
                }
            }
    }

    fun loadNextPage() {
        val state = _uiState.value.list
        val begun = controller.beginLoadMore(state) ?: return
        val pagination = controller.nextPagination(state) ?: return
        _uiState.update { it.copy(list = begun) }
        loadMoreJob?.cancel()
        loadMoreJob =
            viewModelScope.launch {
                when (val result = sellerProductRepository.getProducts(query(pagination))) {
                    is AppResult.Success -> {
                        _uiState.update {
                            it.copy(list = controller.applyLoadMorePage(it.list, result.value))
                        }
                    }
                    is AppResult.Failure -> {
                        _uiState.update {
                            it.copy(list = controller.applyLoadMoreFailure(it.list, result.error))
                        }
                    }
                }
            }
    }

    private fun loadInitial() {
        loadMoreJob?.cancel()
        val generation = controller.resetForNewQuery()
        _uiState.update { it.copy(list = controller.beginInitialLoad(it.list)) }
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                when (val result = sellerProductRepository.getProducts(query(CursorPagination()))) {
                    is AppResult.Success -> {
                        _uiState.update {
                            it.copy(list = controller.applyInitialPage(it.list, result.value, generation))
                        }
                    }
                    is AppResult.Failure -> {
                        _uiState.update {
                            it.copy(list = controller.applyInitialFailure(it.list, result.error))
                        }
                    }
                }
            }
    }

    private fun query(pagination: CursorPagination): SellerProductListQuery {
        val state = _uiState.value
        return SellerProductListQuery(
            shopId = shopId,
            activeFilter = state.activeFilter,
            categorySlug = state.categorySlug,
            pagination = pagination.copy(perPage = CursorPagination.DEFAULT_PER_PAGE),
        )
    }
}
