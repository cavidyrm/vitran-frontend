package com.vitran.shop.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.home.domain.model.HomeRail
import com.vitran.shop.feature.home.domain.model.HomeScreen
import com.vitran.shop.feature.home.domain.model.appendPage
import com.vitran.shop.feature.home.domain.model.canLoadMore
import com.vitran.shop.feature.home.domain.repository.HomeRepository
import com.vitran.shop.feature.location.domain.model.CityId
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Content(
        val screen: HomeScreen,
        val loadingMoreKeys: Set<String> = emptySet(),
        val isRefreshing: Boolean = false,
    ) : HomeUiState

    data class Error(
        val message: String?,
        val isRefreshing: Boolean = false,
    ) : HomeUiState
}

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val sessionRepository: SessionRepository,
    private val cityId: CityId? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var boundScope: CoroutineScope? = null
    private var sessionJob: Job? = null
    private var loadJob: Job? = null

    /**
     * Starts the first load on [scope], which is the home screen's composition coroutine.
     * Wasm/JS builds this ViewModel with `koin.get()` outside a ViewModelStore, so
     * [viewModelScope] must not be the only place the first request is launched.
     */
    fun bind(scope: CoroutineScope) {
        boundScope = scope
        if (sessionJob?.isActive != true) {
            sessionJob = scope.launch { observeSessionChanges() }
        }
        if (_uiState.value is HomeUiState.Loading) {
            launchLoad(forceRefresh = false)
        }
    }

    fun retry() = launchLoad(forceRefresh = true)

    fun refresh() {
        val current = _uiState.value
        if (current is HomeUiState.Content) {
            _uiState.value = current.copy(isRefreshing = true)
        }
        launchLoad(forceRefresh = true, isRefresh = true)
    }

    fun loadMore(sectionKey: String) {
        val current = _uiState.value as? HomeUiState.Content ?: return
        val rail = current.screen.sections.firstOrNull { it.key == sectionKey } ?: return
        if (!rail.canLoadMore() || sectionKey in current.loadingMoreKeys) return
        val path = rail.morePath ?: return
        val cursor = rail.items.nextCursor ?: return
        _uiState.value = current.copy(loadingMoreKeys = current.loadingMoreKeys + sectionKey)
        (boundScope ?: viewModelScope).launch {
            when (
                val result = homeRepository.getHomeSection(
                    path = path,
                    limit = rail.items.perPage.takeIf { it > 0 } ?: 12,
                    cursor = cursor,
                    cursorParam = rail.cursorParam,
                    cityId = cityId,
                )
            ) {
                is AppResult.Success -> applyPage(sectionKey, result.value)
                is AppResult.Failure -> {
                    if (result.error is AppError.NotFound) {
                        applyPage(
                            sectionKey,
                            rail.copy(items = rail.items.copy(hasMore = false, nextCursor = null)),
                        )
                    } else {
                        stopLoadingMore(sectionKey)
                    }
                }
            }
        }
    }

    private fun applyPage(sectionKey: String, page: HomeRail) {
        _uiState.update { state ->
            val content = state as? HomeUiState.Content ?: return@update state
            content.copy(
                loadingMoreKeys = content.loadingMoreKeys - sectionKey,
                screen = content.screen.copy(
                    sections = content.screen.sections.map { rail ->
                        if (rail.key == sectionKey) rail.appendPage(page) else rail
                    },
                ),
            )
        }
    }

    private fun stopLoadingMore(sectionKey: String) {
        _uiState.update { state ->
            val content = state as? HomeUiState.Content ?: return@update state
            content.copy(loadingMoreKeys = content.loadingMoreKeys - sectionKey)
        }
    }

    private suspend fun observeSessionChanges() {
        sessionRepository.sessionState
            .drop(1)
            .distinctUntilChanged { old, new ->
                old::class == new::class
            }
            .collect {
                if (_uiState.value is HomeUiState.Content || _uiState.value is HomeUiState.Error) {
                    launchLoad(forceRefresh = true)
                }
            }
    }

    private fun launchLoad(forceRefresh: Boolean, isRefresh: Boolean = false) {
        if (loadJob?.isActive == true && !forceRefresh) return
        val scope = boundScope ?: viewModelScope
        loadJob = scope.launch {
            performLoad(forceRefresh, isRefresh)
        }
    }

    private suspend fun performLoad(forceRefresh: Boolean, isRefresh: Boolean) {
        try {
            if (!isRefresh && _uiState.value !is HomeUiState.Content) {
                _uiState.value = HomeUiState.Loading
            }
            when (val result = homeRepository.getHomeScreen(cityId, forceRefresh)) {
                is AppResult.Success -> {
                    _uiState.value = HomeUiState.Content(
                        screen = result.value,
                        isRefreshing = false,
                    )
                }
                is AppResult.Failure -> {
                    val previous = _uiState.value as? HomeUiState.Content
                    _uiState.value = if (previous != null && isRefresh) {
                        previous.copy(isRefreshing = false)
                    } else {
                        HomeUiState.Error(result.error.message, isRefreshing = false)
                    }
                }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            if (_uiState.value !is HomeUiState.Content) {
                _uiState.value = HomeUiState.Error(throwable.message, isRefreshing = false)
            }
        }
    }
}
