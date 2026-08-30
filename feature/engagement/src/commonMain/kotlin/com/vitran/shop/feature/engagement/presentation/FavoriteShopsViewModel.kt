package com.vitran.shop.feature.engagement.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.engagement.favorite.domain.model.FavoriteShop
import com.vitran.shop.feature.engagement.favorite.domain.repository.ShopFavoriteRepository
import com.vitran.shop.feature.engagement.favorite.domain.usecase.SetShopFavoriteUseCase
import com.vitran.shop.feature.engagement.state.EngagementStateStore
import com.vitran.shop.feature.engagement.state.FavoriteShopStatus
import com.vitran.shop.feature.marketplace.common.presentation.CursorListController
import com.vitran.shop.feature.marketplace.common.presentation.CursorListState
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FavoriteShopsScreenState {
    data object RequiresAuth : FavoriteShopsScreenState

    data class Content(
        val list: CursorListState<FavoriteShop>,
        val pendingShopIds: Set<Long>,
    ) : FavoriteShopsScreenState
}

class FavoriteShopsViewModel(
    private val shopFavoriteRepository: ShopFavoriteRepository,
    private val setShopFavorite: SetShopFavoriteUseCase,
    private val sessionRepository: SessionRepository,
    private val stateStore: EngagementStateStore,
) : ViewModel() {

    private val controller = CursorListController<FavoriteShop, Long>(idOf = { it.shop.id.value })
    private val _uiState = MutableStateFlow<FavoriteShopsScreenState>(
        if (sessionRepository.sessionState.value != SessionState.Authenticated) {
            FavoriteShopsScreenState.RequiresAuth
        } else {
            FavoriteShopsScreenState.Content(list = CursorListState(), pendingShopIds = emptySet())
        },
    )
    val uiState: StateFlow<FavoriteShopsScreenState> = _uiState.asStateFlow()

    init {
        if (sessionRepository.sessionState.value == SessionState.Authenticated) {
            loadInitial()
        }
    }

    fun retry() = loadInitial()

    fun loadNextPage() {
        val current = contentOrNull() ?: return
        val loadingMore = controller.beginLoadMore(current.list) ?: return
        setContent(current.copy(list = loadingMore))
        viewModelScope.launch {
            loadPage(CursorPagination(cursor = current.list.nextCursor), isRefresh = false)
        }
    }

    fun remove(shopId: ShopId) {
        val current = contentOrNull() ?: return
        if (shopId.value in current.pendingShopIds) return
        viewModelScope.launch {
            setContent(current.copy(pendingShopIds = current.pendingShopIds + shopId.value))
            when (setShopFavorite(shopId, favorite = false)) {
                is AppResult.Success -> {
                    val after = contentOrNull() ?: return@launch
                    setContent(
                        after.copy(
                            list = after.list.copy(
                                items = after.list.items.filterNot { it.shop.id == shopId },
                            ),
                            pendingShopIds = after.pendingShopIds - shopId.value,
                        ),
                    )
                    stateStore.setFavoriteShopStatus(shopId, FavoriteShopStatus.NotFavorited)
                }
                is AppResult.Failure -> {
                    val after = contentOrNull() ?: return@launch
                    setContent(after.copy(pendingShopIds = after.pendingShopIds - shopId.value))
                }
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            val generation = controller.resetForNewQuery()
            val current = contentOrNull() ?: FavoriteShopsScreenState.Content(
                list = CursorListState(),
                pendingShopIds = emptySet(),
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
        when (val result = shopFavoriteRepository.getFavoriteShops(pagination)) {
            is AppResult.Success -> {
                result.value.items.forEach {
                    stateStore.setFavoriteShopStatus(it.shop.id, FavoriteShopStatus.Favorited)
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

    private fun contentOrNull(): FavoriteShopsScreenState.Content? =
        _uiState.value as? FavoriteShopsScreenState.Content

    private fun setContent(content: FavoriteShopsScreenState.Content) {
        _uiState.value = content
    }
}
