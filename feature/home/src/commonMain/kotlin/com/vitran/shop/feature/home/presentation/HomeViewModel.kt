package com.vitran.shop.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.home.domain.model.HomeFeed
import com.vitran.shop.feature.home.domain.repository.HomeRepository
import com.vitran.shop.feature.location.domain.model.CityId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Content(
        val feed: HomeFeed,
        /** When false, UI keeps preview/mock section data until item schemas are verified. */
        val useApiSections: Boolean,
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

    init {
        load(forceRefresh = false)
        observeSessionChanges()
    }

    fun retry() = load(forceRefresh = true)

    fun refresh() {
        val current = _uiState.value
        if (current is HomeUiState.Content) {
            _uiState.value = current.copy(isRefreshing = true)
        }
        load(forceRefresh = true, isRefresh = true)
    }

    private fun observeSessionChanges() {
        viewModelScope.launch {
            sessionRepository.sessionState
                .drop(1)
                .distinctUntilChanged { old, new ->
                    old::class == new::class
                }
                .collect {
                    if (_uiState.value is HomeUiState.Content || _uiState.value is HomeUiState.Error) {
                        load(forceRefresh = true)
                    }
                }
        }
    }

    private fun load(forceRefresh: Boolean, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh && _uiState.value !is HomeUiState.Content) {
                _uiState.value = HomeUiState.Loading
            }
            when (val result = homeRepository.getHome(cityId, forceRefresh)) {
                is AppResult.Success -> {
                    _uiState.value = HomeUiState.Content(
                        feed = result.value,
                        useApiSections = result.value.itemsVerified,
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
        }
    }
}
