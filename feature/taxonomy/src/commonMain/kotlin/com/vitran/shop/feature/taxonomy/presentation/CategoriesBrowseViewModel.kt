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

sealed interface CategoriesBrowseUiState {
    data object Loading : CategoriesBrowseUiState

    data class Content(
        val rootCategories: List<CategoryNode>,
    ) : CategoriesBrowseUiState

    data object Empty : CategoriesBrowseUiState

    data class Error(
        val message: String,
    ) : CategoriesBrowseUiState
}

class CategoriesBrowseViewModel(
    private val taxonomyRepository: TaxonomyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoriesBrowseUiState>(CategoriesBrowseUiState.Loading)
    val uiState: StateFlow<CategoriesBrowseUiState> = _uiState.asStateFlow()

    init {
        loadRoots()
    }

    fun retry() {
        loadRoots(forceRefresh = true)
    }

    private fun loadRoots(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = CategoriesBrowseUiState.Loading
            when (val result = taxonomyRepository.getCategoryTree(forceRefresh = forceRefresh)) {
                is AppResult.Success -> {
                    _uiState.value = when {
                        result.value.isEmpty() -> CategoriesBrowseUiState.Empty
                        else -> CategoriesBrowseUiState.Content(result.value)
                    }
                }
                is AppResult.Failure -> {
                    _uiState.value = CategoriesBrowseUiState.Error(
                        message = result.error.message ?: "خطا در دریافت دسته‌بندی‌ها",
                    )
                }
            }
        }
    }
}
