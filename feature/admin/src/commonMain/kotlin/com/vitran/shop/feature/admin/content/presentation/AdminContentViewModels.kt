package com.vitran.shop.feature.admin.content.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.admin.content.domain.AdminContentRepository
import com.vitran.shop.feature.admin.content.domain.CreateStaticPageCommand
import com.vitran.shop.feature.admin.content.domain.UpdateStaticPageCommand
import com.vitran.shop.feature.admin.rbac.AdminPermissions
import com.vitran.shop.feature.content.domain.model.StaticPage
import com.vitran.shop.feature.content.domain.model.StaticPageId
import com.vitran.shop.feature.content.domain.model.StaticPageSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminStaticPagesUiState(
    val pages: List<StaticPageSummary> = emptyList(),
    val loading: Boolean = false,
    val deletingIds: Set<StaticPageId> = emptySet(),
    val canDeleteStaticPage: Boolean = false,
    val error: AppError? = null,
)

class AdminStaticPagesViewModel(
    private val repository: AdminContentRepository,
    private val permissions: AdminPermissions,
    accountRepository: com.vitran.shop.feature.account.domain.repository.AccountRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminStaticPagesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.currentUserState.collect { current ->
                val roles = (current as? com.vitran.shop.feature.account.domain.model.CurrentUserState.Available)
                    ?.user?.roles.orEmpty()
                _uiState.update { it.copy(canDeleteStaticPage = permissions.canDeleteStaticPage(roles)) }
            }
        }
        refresh()
    }
    fun refresh() = viewModelScope.launch {
        _uiState.update { it.copy(loading = true, error = null) }
        when (val result = repository.getPages()) {
            is AppResult.Success -> _uiState.update { it.copy(pages = result.value, loading = false) }
            is AppResult.Failure -> _uiState.update { it.copy(loading = false, error = result.error) }
        }
    }
    fun delete(id: StaticPageId) {
        if (!_uiState.value.canDeleteStaticPage || id in _uiState.value.deletingIds) return
        _uiState.update { it.copy(deletingIds = it.deletingIds + id, error = null) }
        viewModelScope.launch {
            when (val result = repository.delete(id)) {
                is AppResult.Success -> _uiState.update { it.copy(pages = it.pages.filterNot { page -> page.id == id }) }
                is AppResult.Failure -> _uiState.update { it.copy(error = result.error) }
            }
            _uiState.update { it.copy(deletingIds = it.deletingIds - id) }
        }
    }
}

sealed interface AdminStaticPageEditorUiState {
    data object Loading : AdminStaticPageEditorUiState
    data class Editing(val page: StaticPage? = null, val saving: Boolean = false) : AdminStaticPageEditorUiState
    data class Saved(val page: StaticPage) : AdminStaticPageEditorUiState
    data class Error(val error: AppError) : AdminStaticPageEditorUiState
}

class AdminStaticPageEditorViewModel(
    private val pageId: StaticPageId?,
    private val repository: AdminContentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AdminStaticPageEditorUiState>(
        if (pageId == null) AdminStaticPageEditorUiState.Editing() else AdminStaticPageEditorUiState.Loading,
    )
    val uiState = _uiState.asStateFlow()
    init { if (pageId != null) load(pageId) }

    private fun load(id: StaticPageId) = viewModelScope.launch {
        _uiState.value = when (val result = repository.getPage(id)) {
            is AppResult.Success -> AdminStaticPageEditorUiState.Editing(result.value)
            is AppResult.Failure -> AdminStaticPageEditorUiState.Error(result.error)
        }
    }
    fun create(command: CreateStaticPageCommand) = save { repository.create(command) }
    fun update(command: UpdateStaticPageCommand) = save { repository.update(command) }
    private fun save(block: suspend () -> AppResult<StaticPage>) {
        if ((_uiState.value as? AdminStaticPageEditorUiState.Editing)?.saving == true) return
        val editing = _uiState.value as? AdminStaticPageEditorUiState.Editing
        _uiState.value = (editing ?: AdminStaticPageEditorUiState.Editing()).copy(saving = true)
        viewModelScope.launch {
            _uiState.value = when (val result = block()) {
                is AppResult.Success -> AdminStaticPageEditorUiState.Saved(result.value)
                is AppResult.Failure -> AdminStaticPageEditorUiState.Error(result.error)
            }
        }
    }
}
