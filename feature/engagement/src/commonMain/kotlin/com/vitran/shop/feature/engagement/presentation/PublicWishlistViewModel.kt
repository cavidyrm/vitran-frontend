package com.vitran.shop.feature.engagement.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.feature.engagement.wishlist.domain.error.PublicWishlistResult
import com.vitran.shop.feature.engagement.wishlist.domain.model.PublicWishlistItem
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSlug
import com.vitran.shop.feature.engagement.wishlist.domain.repository.WishlistRepository
import com.vitran.shop.feature.marketplace.common.presentation.CursorListController
import com.vitran.shop.feature.marketplace.common.presentation.CursorListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PublicWishlistUiState {
    data object Loading : PublicWishlistUiState
    data object Private : PublicWishlistUiState
    data object NotFound : PublicWishlistUiState
    data class Error(val message: String?) : PublicWishlistUiState
    data class Content(val list: CursorListState<PublicWishlistItem>) : PublicWishlistUiState
}

class PublicWishlistViewModel(
    private val shareSlug: WishlistShareSlug,
    private val wishlistRepository: WishlistRepository,
) : ViewModel() {

    private val controller = CursorListController<PublicWishlistItem, Long>(idOf = { it.product.id.value })
    private val _uiState = MutableStateFlow<PublicWishlistUiState>(PublicWishlistUiState.Loading)
    val uiState: StateFlow<PublicWishlistUiState> = _uiState.asStateFlow()

    init {
        loadInitial()
    }

    fun retry() = loadInitial()

    fun loadNextPage() {
        val current = _uiState.value as? PublicWishlistUiState.Content ?: return
        val loadingMore = controller.beginLoadMore(current.list) ?: return
        _uiState.value = PublicWishlistUiState.Content(loadingMore)
        viewModelScope.launch {
            loadPage(CursorPagination(cursor = current.list.nextCursor), isInitial = false)
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            val generation = controller.resetForNewQuery()
            _uiState.value = PublicWishlistUiState.Content(controller.beginInitialLoad(CursorListState()))
            loadPage(CursorPagination(), isInitial = true, generation = generation)
        }
    }

    private suspend fun loadPage(
        pagination: CursorPagination,
        isInitial: Boolean,
        generation: Int = controller.currentGeneration(),
    ) {
        when (val result = wishlistRepository.getPublicWishlist(shareSlug, pagination)) {
            is PublicWishlistResult.Content -> {
                val current = (_uiState.value as? PublicWishlistUiState.Content)?.list
                    ?: CursorListState()
                val updated = if (isInitial || pagination.cursor == null) {
                    controller.applyInitialPage(current, result.page, generation)
                } else {
                    controller.applyLoadMorePage(current, result.page)
                }
                _uiState.value = PublicWishlistUiState.Content(updated)
            }
            PublicWishlistResult.Private -> _uiState.value = PublicWishlistUiState.Private
            PublicWishlistResult.NotFound -> _uiState.value = PublicWishlistUiState.NotFound
            is PublicWishlistResult.Failure -> {
                if (isInitial || pagination.cursor == null) {
                    _uiState.value = PublicWishlistUiState.Error(result.error.message)
                } else {
                    val current = (_uiState.value as? PublicWishlistUiState.Content) ?: return
                    _uiState.value = PublicWishlistUiState.Content(
                        controller.applyLoadMoreFailure(current.list, result.error),
                    )
                }
            }
        }
    }
}
