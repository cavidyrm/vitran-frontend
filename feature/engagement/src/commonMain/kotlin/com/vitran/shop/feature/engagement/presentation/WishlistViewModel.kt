package com.vitran.shop.feature.engagement.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.state.SaveStatus
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistItem
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSettings
import com.vitran.shop.feature.engagement.wishlist.domain.repository.WishlistRepository
import com.vitran.shop.feature.engagement.wishlist.domain.usecase.SetProductSavedUseCase
import com.vitran.shop.feature.engagement.wishlist.domain.usecase.UpdateWishlistSharingUseCase
import com.vitran.shop.feature.marketplace.common.presentation.CursorListController
import com.vitran.shop.feature.marketplace.common.presentation.CursorListState
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

sealed interface WishlistScreenState {
    data object RequiresAuth : WishlistScreenState

    data class Content(
        val list: CursorListState<WishlistItem>,
        val shareSettings: WishlistShareSettings?,
        val isShareUpdating: Boolean,
        val shareError: String?,
        val pendingProductIds: Set<Long>,
    ) : WishlistScreenState
}

class WishlistViewModel(
    private val wishlistRepository: WishlistRepository,
    private val setProductSaved: SetProductSavedUseCase,
    private val updateWishlistSharing: UpdateWishlistSharingUseCase,
    private val sessionRepository: SessionRepository,
    private val stateStore: EngagementStateStore,
) : ViewModel() {

    private val controller = CursorListController<WishlistItem, Long>(idOf = { it.product.id.value })
    private val shareMutex = Mutex()
    private val _uiState = MutableStateFlow<WishlistScreenState>(
        if (sessionRepository.sessionState.value != SessionState.Authenticated) {
            WishlistScreenState.RequiresAuth
        } else {
            WishlistScreenState.Content(
                list = CursorListState(),
                shareSettings = null,
                isShareUpdating = false,
                shareError = null,
                pendingProductIds = emptySet(),
            )
        },
    )
    val uiState: StateFlow<WishlistScreenState> = _uiState.asStateFlow()

    init {
        if (sessionRepository.sessionState.value == SessionState.Authenticated) {
            loadInitial()
            loadShareSettings()
        }
    }

    fun retry() = loadInitial()

    fun refresh() {
        val current = contentOrNull() ?: return
        viewModelScope.launch {
            val generation = controller.resetForNewQuery()
            setContent(current.copy(list = controller.beginRefresh(current.list)))
            loadPage(CursorPagination(), isRefresh = true, generation = generation)
        }
    }

    fun loadNextPage() {
        val current = contentOrNull() ?: return
        val loadingMore = controller.beginLoadMore(current.list) ?: return
        setContent(current.copy(list = loadingMore))
        viewModelScope.launch {
            loadPage(
                CursorPagination(cursor = current.list.nextCursor),
                isRefresh = false,
            )
        }
    }

    fun remove(productId: ProductId) {
        val current = contentOrNull() ?: return
        if (productId.value in current.pendingProductIds) return
        viewModelScope.launch {
            setContent(current.copy(pendingProductIds = current.pendingProductIds + productId.value))
            when (setProductSaved(productId, saved = false)) {
                is AppResult.Success -> {
                    val after = contentOrNull() ?: return@launch
                    setContent(
                        after.copy(
                            list = after.list.copy(
                                items = after.list.items.filterNot { it.product.id == productId },
                            ),
                            pendingProductIds = after.pendingProductIds - productId.value,
                        ),
                    )
                    stateStore.setSaveStatus(productId, SaveStatus.NotSaved)
                }
                is AppResult.Failure -> {
                    val after = contentOrNull() ?: return@launch
                    setContent(after.copy(pendingProductIds = after.pendingProductIds - productId.value))
                }
            }
        }
    }

    fun setSharePublic(isPublic: Boolean) {
        val current = contentOrNull() ?: return
        if (current.isShareUpdating) return
        viewModelScope.launch {
            shareMutex.withLock {
                val snapshot = contentOrNull() ?: return@withLock
                setContent(snapshot.copy(isShareUpdating = true, shareError = null))
                when (val result = updateWishlistSharing(isPublic)) {
                    is AppResult.Success -> {
                        val after = contentOrNull() ?: return@withLock
                        setContent(
                            after.copy(
                                shareSettings = result.value,
                                isShareUpdating = false,
                            ),
                        )
                    }
                    is AppResult.Failure -> {
                        val after = contentOrNull() ?: return@withLock
                        setContent(
                            after.copy(
                                isShareUpdating = false,
                                shareError = result.error.message,
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun loadShareSettings() {
        viewModelScope.launch {
            when (val result = wishlistRepository.getShareSettings()) {
                is AppResult.Success -> {
                    stateStore.setShareSettings(result.value)
                    val current = contentOrNull() ?: return@launch
                    setContent(current.copy(shareSettings = result.value))
                }
                is AppResult.Failure -> Unit
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            val generation = controller.resetForNewQuery()
            val current = contentOrNull() ?: WishlistScreenState.Content(
                list = CursorListState(),
                shareSettings = null,
                isShareUpdating = false,
                shareError = null,
                pendingProductIds = emptySet(),
            )
            setContent(current.copy(list = controller.beginInitialLoad(current.list)))
            loadPage(CursorPagination(), isRefresh = false, generation = generation)
        }
    }

    private suspend fun loadPage(
        pagination: CursorPagination,
        isRefresh: Boolean,
        generation: Int = controller.currentGeneration(),
    ) {
        when (val result = wishlistRepository.getWishlist(pagination)) {
            is AppResult.Success -> {
                result.value.items.forEach {
                    stateStore.setSaveStatus(it.product.id, SaveStatus.Saved)
                }
                val current = contentOrNull() ?: return
                val updated = when {
                    isRefresh -> controller.applyRefreshPage(current.list, result.value, generation)
                    pagination.cursor == null ->
                        controller.applyInitialPage(current.list, result.value, generation)
                    else -> controller.applyLoadMorePage(current.list, result.value)
                }
                setContent(current.copy(list = updated))
            }
            is AppResult.Failure -> {
                val current = contentOrNull() ?: return
                val updated = when {
                    isRefresh -> controller.applyRefreshFailure(current.list, result.error)
                    pagination.cursor == null ->
                        controller.applyInitialFailure(current.list, result.error)
                    else -> controller.applyLoadMoreFailure(current.list, result.error)
                }
                setContent(current.copy(list = updated))
            }
        }
    }

    private fun contentOrNull(): WishlistScreenState.Content? =
        _uiState.value as? WishlistScreenState.Content

    private fun setContent(content: WishlistScreenState.Content) {
        _uiState.value = content
    }
}
