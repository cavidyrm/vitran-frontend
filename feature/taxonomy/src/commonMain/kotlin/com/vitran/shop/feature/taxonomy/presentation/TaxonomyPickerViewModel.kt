package com.vitran.shop.feature.taxonomy.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.feature.taxonomy.domain.repository.TaxonomyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TaxonomyPickerUiState {
    data object Loading : TaxonomyPickerUiState

    data class Content(
        val roots: List<CategoryNode>,
    ) : TaxonomyPickerUiState

    data object Empty : TaxonomyPickerUiState

    data class Error(
        val message: String,
    ) : TaxonomyPickerUiState
}

class TaxonomyPickerViewModel(
    private val taxonomyRepository: TaxonomyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TaxonomyPickerUiState>(TaxonomyPickerUiState.Loading)
    val uiState: StateFlow<TaxonomyPickerUiState> = _uiState.asStateFlow()

    init {
        loadTree()
    }

    fun retry() {
        loadTree(forceRefresh = true)
    }

    private fun loadTree(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = TaxonomyPickerUiState.Loading
            when (val result = taxonomyRepository.getCategoryTree(forceRefresh = forceRefresh)) {
                is AppResult.Success -> {
                    _uiState.value = when {
                        result.value.isEmpty() -> TaxonomyPickerUiState.Empty
                        else -> TaxonomyPickerUiState.Content(result.value)
                    }
                }
                is AppResult.Failure -> {
                    _uiState.value = TaxonomyPickerUiState.Error(
                        message = result.error.message ?: "خطا در دریافت دسته‌بندی‌ها",
                    )
                }
            }
        }
    }
}
