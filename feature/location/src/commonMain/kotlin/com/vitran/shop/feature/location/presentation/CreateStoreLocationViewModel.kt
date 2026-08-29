package com.vitran.shop.feature.location.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CreateStoreLocationUiState {
    data object Loading : CreateStoreLocationUiState

    data class Content(
        val cities: List<City>,
    ) : CreateStoreLocationUiState

    data object Empty : CreateStoreLocationUiState

    data class Error(
        val message: String,
    ) : CreateStoreLocationUiState
}

class CreateStoreLocationViewModel(
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateStoreLocationUiState>(CreateStoreLocationUiState.Loading)
    val uiState: StateFlow<CreateStoreLocationUiState> = _uiState.asStateFlow()

    init {
        loadCities()
    }

    fun retry() {
        loadCities(forceRefresh = true)
    }

    private fun loadCities(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = CreateStoreLocationUiState.Loading
            when (val result = locationRepository.getCities(forceRefresh = forceRefresh)) {
                is AppResult.Success -> {
                    _uiState.value = when {
                        result.value.isEmpty() -> CreateStoreLocationUiState.Empty
                        else -> CreateStoreLocationUiState.Content(result.value)
                    }
                }
                is AppResult.Failure -> {
                    _uiState.value = CreateStoreLocationUiState.Error(
                        message = result.error.message ?: "خطا در دریافت لیست شهرها",
                    )
                }
            }
        }
    }
}
