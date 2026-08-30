package com.vitran.shop.feature.seller.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.common.presentation.CursorListController
import com.vitran.shop.feature.marketplace.common.presentation.CursorListState
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
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

data class SellerShopsUiState(
    val filter: SellerShopFilter = SellerShopFilter.All,
    val list: CursorListState<SellerShopSummary> = CursorListState(),
)

/** Deferred UI — ViewModel ready for seller shop list screen. */
class SellerShopsViewModel(
    private val sellerShopRepository: SellerShopRepository,
) : ViewModel() {
    private val controller = CursorListController<SellerShopSummary, ShopId>(idOf = { it.id })
    private val _uiState = MutableStateFlow(SellerShopsUiState())
    val uiState: StateFlow<SellerShopsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun setFilter(filter: SellerShopFilter) {
        if (_uiState.value.filter == filter) return
        _uiState.update {
            it.copy(filter = filter, list = CursorListState(isLoadingInitial = true))
        }
        loadInitial()
    }

    fun refresh() {
        val generation = controller.resetForNewQuery()
        _uiState.update {
            it.copy(
                list =
                    controller.beginRefresh(it.list).copy(
                        isLoadingInitial = it.list.items.isEmpty(),
                    ),
            )
        }
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                val query =
                    SellerShopListQuery(
                        activeFilter = _uiState.value.filter,
                        pagination = CursorPagination(),
                    )
                when (val result = sellerShopRepository.getMyShops(query)) {
                    is AppResult.Success -> {
                        _uiState.update {
                            it.copy(list = controller.applyRefreshPage(it.list, result.value, generation))
                        }
                    }
                    is AppResult.Failure -> {
                        _uiState.update {
                            it.copy(
                                list =
                                    if (it.list.items.isEmpty()) {
                                        controller.applyInitialFailure(it.list, result.error)
                                    } else {
                                        controller.applyRefreshFailure(it.list, result.error)
                                    },
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
        viewModelScope.launch {
            val query =
                SellerShopListQuery(
                    activeFilter = _uiState.value.filter,
                    pagination = pagination,
                )
            when (val result = sellerShopRepository.getMyShops(query)) {
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
        val generation = controller.resetForNewQuery()
        _uiState.update { it.copy(list = controller.beginInitialLoad(it.list)) }
        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                val query =
                    SellerShopListQuery(
                        activeFilter = _uiState.value.filter,
                        pagination = CursorPagination(),
                    )
                when (val result = sellerShopRepository.getMyShops(query)) {
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
}
