package com.vitran.shop.feature.content.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.content.domain.html.HtmlSanitizer
import com.vitran.shop.feature.content.domain.model.StaticPage
import com.vitran.shop.feature.content.domain.model.StaticPageSlug
import com.vitran.shop.feature.content.domain.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicStaticPageViewModel(
    slug: String,
    private val contentRepository: ContentRepository,
    private val sanitizer: HtmlSanitizer,
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState
        data class Content(val page: StaticPage) : UiState
        data object NotFound : UiState
        data class Error(val message: String?) : UiState
    }

    private val pageSlug = StaticPageSlug(slug)
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() {
        load(forceRefresh = true)
    }

    private fun load(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = contentRepository.getStaticPageBySlug(pageSlug, forceRefresh)) {
                is AppResult.Success -> {
                    val page = result.value
                    _uiState.value = UiState.Content(
                        page.copy(bodyHtml = sanitizer.sanitize(page.bodyHtml)),
                    )
                }
                is AppResult.Failure -> {
                    _uiState.value = when (result.error) {
                        is AppError.NotFound -> UiState.NotFound
                        else -> UiState.Error(result.error.message)
                    }
                }
            }
        }
    }
}
