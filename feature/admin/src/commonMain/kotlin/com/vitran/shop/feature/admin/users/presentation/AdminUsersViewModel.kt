package com.vitran.shop.feature.admin.users.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.admin.rbac.AdminPermissions
import com.vitran.shop.feature.admin.users.domain.model.AdminUserQuery
import com.vitran.shop.feature.admin.users.domain.model.AdminUserSummary
import com.vitran.shop.feature.admin.users.domain.repository.AdminUserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AdminUsersUiState {
    val query: AdminUserQuery

    data class Loading(override val query: AdminUserQuery) : AdminUsersUiState

    data class Content(
        override val query: AdminUserQuery,
        val items: List<AdminUserSummary>,
        val page: Int,
        val perPage: Int,
        val lastPage: Int,
        val total: Long,
        val hasMore: Boolean,
        val isLoadingNextPage: Boolean = false,
        val paginationError: String? = null,
    ) : AdminUsersUiState

    data class Empty(override val query: AdminUserQuery) : AdminUsersUiState

    data class Error(
        override val query: AdminUserQuery,
        val message: String?,
    ) : AdminUsersUiState
}

class AdminUsersViewModel(
    private val repository: AdminUserRepository,
    accountRepository: AccountRepository,
    private val permissions: AdminPermissions,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AdminUsersUiState>(
        AdminUsersUiState.Loading(AdminUserQuery()),
    )
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    private val _canAccessAdmin = MutableStateFlow(false)
    val canAccessAdmin: StateFlow<Boolean> = _canAccessAdmin.asStateFlow()

    private var searchJob: Job? = null
    private var loadJob: Job? = null
    private var generation = 0

    init {
        viewModelScope.launch {
            accountRepository.currentUserState.collect { current ->
                val roles = (current as? CurrentUserState.Available)?.user?.roles.orEmpty()
                _canAccessAdmin.value = permissions.canAccessAdmin(roles)
            }
        }
        load()
    }

    fun load() {
        val query = _uiState.value.query.copy(page = 1)
        searchJob?.cancel()
        loadInitial(query)
    }

    fun setPhoneFilter(phone: String) {
        val query = _uiState.value.query.copy(phone = phone.trim().ifBlank { null }, page = 1)
        _uiState.value = _uiState.value.withQuery(query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            loadInitial(query)
        }
    }

    fun setRoleFilter(role: String?) {
        searchJob?.cancel()
        loadInitial(_uiState.value.query.copy(role = role?.ifBlank { null }, page = 1))
    }

    fun setActiveFilter(isActive: Boolean?) {
        searchJob?.cancel()
        loadInitial(_uiState.value.query.copy(isActive = isActive, page = 1))
    }

    fun setPage(page: Int) {
        searchJob?.cancel()
        loadInitial(_uiState.value.query.copy(page = page.coerceAtLeast(1)))
    }

    fun setPerPage(perPage: Int) {
        searchJob?.cancel()
        loadInitial(_uiState.value.query.copy(page = 1, perPage = perPage.coerceAtLeast(1)))
    }

    fun loadNextPage() {
        val current = _uiState.value as? AdminUsersUiState.Content ?: return
        if (!current.hasMore || current.isLoadingNextPage) return
        val requestGeneration = generation
        val nextQuery = current.query.copy(page = current.page + 1)
        _uiState.value = current.copy(isLoadingNextPage = true, paginationError = null)
        viewModelScope.launch {
            when (val result = repository.getUsers(nextQuery)) {
                is AppResult.Success -> {
                    if (requestGeneration != generation) return@launch
                    val page = result.value
                    val latest = _uiState.value as? AdminUsersUiState.Content ?: return@launch
                    _uiState.value = latest.copy(
                        query = nextQuery,
                        items = (latest.items + page.items).distinctBy { it.id },
                        page = page.page,
                        perPage = page.perPage,
                        lastPage = page.lastPage,
                        total = page.total,
                        hasMore = page.hasMore,
                        isLoadingNextPage = false,
                    )
                }
                is AppResult.Failure -> {
                    if (requestGeneration != generation) return@launch
                    val latest = _uiState.value as? AdminUsersUiState.Content ?: return@launch
                    _uiState.value = latest.copy(
                        isLoadingNextPage = false,
                        paginationError = result.error.message,
                    )
                }
            }
        }
    }

    private fun loadInitial(query: AdminUserQuery) {
        generation += 1
        val requestGeneration = generation
        loadJob?.cancel()
        _uiState.value = AdminUsersUiState.Loading(query)
        loadJob = viewModelScope.launch {
            when (val result = repository.getUsers(query)) {
                is AppResult.Success -> {
                    if (requestGeneration != generation) return@launch
                    val page = result.value
                    _uiState.value = if (page.items.isEmpty()) {
                        AdminUsersUiState.Empty(query)
                    } else {
                        AdminUsersUiState.Content(
                            query = query,
                            items = page.items,
                            page = page.page,
                            perPage = page.perPage,
                            lastPage = page.lastPage,
                            total = page.total,
                            hasMore = page.hasMore,
                        )
                    }
                }
                is AppResult.Failure -> {
                    if (requestGeneration != generation) return@launch
                    _uiState.value = AdminUsersUiState.Error(query, result.error.message)
                }
            }
        }
    }

    private fun AdminUsersUiState.withQuery(query: AdminUserQuery): AdminUsersUiState = when (this) {
        is AdminUsersUiState.Loading -> copy(query = query)
        is AdminUsersUiState.Content -> copy(query = query)
        is AdminUsersUiState.Empty -> copy(query = query)
        is AdminUsersUiState.Error -> copy(query = query)
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 400L
    }
}
