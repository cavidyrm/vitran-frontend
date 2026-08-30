package com.vitran.shop.feature.admin.users.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.admin.rbac.AdminPermissions
import com.vitran.shop.feature.admin.users.domain.model.AdminUserDetails
import com.vitran.shop.feature.admin.users.domain.model.UpdateAdminUserCommand
import com.vitran.shop.feature.admin.users.domain.repository.AdminUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUserDetailUiState(
    val isLoading: Boolean = true,
    val detail: AdminUserDetails? = null,
    val isActive: Boolean = true,
    val selectedEditableRoles: Set<UserRole> = emptySet(),
    val assignableRoles: List<UserRole> = emptyList(),
    val canAccessAdmin: Boolean = false,
    val isSubmitting: Boolean = false,
    val loadError: String? = null,
    val submitError: String? = null,
)

class AdminUserDetailViewModel(
    private val userId: Long,
    private val repository: AdminUserRepository,
    private val accountRepository: AccountRepository,
    private val permissions: AdminPermissions,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUserDetailUiState())
    val uiState: StateFlow<AdminUserDetailUiState> = _uiState.asStateFlow()

    private var actorId: Long? = null
    private var actorRoles: Set<UserRole> = emptySet()

    init {
        viewModelScope.launch {
            accountRepository.currentUserState.collect { current ->
                val user = (current as? CurrentUserState.Available)?.user
                actorId = user?.id
                actorRoles = user?.roles.orEmpty()
                _uiState.update { state ->
                    val assignable = permissions.assignableRoles(actorRoles)
                    state.copy(
                        assignableRoles = assignable,
                        selectedEditableRoles = state.detail?.roles?.intersect(assignable.toSet())
                            ?: state.selectedEditableRoles.intersect(assignable.toSet()),
                        canAccessAdmin = permissions.canAccessAdmin(actorRoles),
                    )
                }
            }
        }
        load()
    }

    fun load() {
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            when (val result = repository.getUser(userId)) {
                is AppResult.Success -> applyDetail(result.value)
                is AppResult.Failure -> _uiState.update {
                    it.copy(isLoading = false, loadError = result.error.message)
                }
            }
        }
    }

    fun setActive(isActive: Boolean) {
        _uiState.update { it.copy(isActive = isActive, submitError = null) }
    }

    fun setRoleSelected(role: UserRole, selected: Boolean) {
        if (role !in _uiState.value.assignableRoles) return
        _uiState.update { state ->
            val roles = if (selected) {
                state.selectedEditableRoles + role
            } else {
                state.selectedEditableRoles - role
            }
            state.copy(selectedEditableRoles = roles, submitError = null)
        }
    }

    fun submit() {
        val state = _uiState.value
        val existing = state.detail ?: return
        if (state.isSubmitting) return
        val roles = permissions.buildRolesUpdatePayload(
            actorRoles = actorRoles,
            existingTargetRoles = existing.roles,
            selectedEditableRoles = state.selectedEditableRoles,
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            when (
                val result = repository.updateUser(
                    UpdateAdminUserCommand(
                        userId = userId,
                        isActive = state.isActive,
                        roles = roles,
                    ),
                )
            ) {
                is AppResult.Success -> {
                    applyDetail(result.value)
                    if (actorId == result.value.id) {
                        accountRepository.refreshCurrentUser()
                    }
                }
                is AppResult.Failure -> _uiState.update {
                    it.copy(isSubmitting = false, submitError = result.error.message)
                }
            }
        }
    }

    private fun applyDetail(detail: AdminUserDetails) {
        val assignable = permissions.assignableRoles(actorRoles)
        _uiState.update {
            it.copy(
                isLoading = false,
                detail = detail,
                isActive = detail.isActive,
                selectedEditableRoles = detail.roles.intersect(assignable.toSet()),
                assignableRoles = assignable,
                isSubmitting = false,
                loadError = null,
                submitError = null,
            )
        }
    }
}
