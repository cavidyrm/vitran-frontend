package com.vitran.shop.feature.admin.plans.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.admin.plans.domain.AdminPlan
import com.vitran.shop.feature.admin.plans.domain.AdminPlanRepository
import com.vitran.shop.feature.admin.plans.domain.CreatePlanCommand
import com.vitran.shop.feature.admin.plans.domain.DeleteAdminPlanResult
import com.vitran.shop.feature.admin.plans.domain.UpdatePlanCommand
import com.vitran.shop.feature.admin.rbac.AdminPermissions
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminPlansUiState(
    val plans: List<AdminPlan> = emptyList(),
    val selectedPlanId: PlanId? = null,
    val loading: Boolean = false,
    val saving: Boolean = false,
    val deleting: Boolean = false,
    val canDelete: Boolean = false,
    val freePlanDeleteBlocked: Boolean = false,
    val error: AppError? = null,
)

class AdminPlansViewModel(
    private val repository: AdminPlanRepository,
    private val permissions: AdminPermissions,
    accountRepository: com.vitran.shop.feature.account.domain.repository.AccountRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminPlansUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.currentUserState.collect { current ->
                val roles = (current as? com.vitran.shop.feature.account.domain.model.CurrentUserState.Available)
                    ?.user?.roles.orEmpty()
                _uiState.update { it.copy(canDelete = permissions.canDeletePlan(roles)) }
            }
        }
        refresh()
    }

    fun select(id: PlanId?) = _uiState.update { it.copy(selectedPlanId = id, freePlanDeleteBlocked = false) }

    fun refresh() = viewModelScope.launch {
        _uiState.update { it.copy(loading = true, error = null) }
        when (val result = repository.getPlans()) {
            is AppResult.Success -> _uiState.update {
                it.copy(
                    plans = result.value, loading = false,
                    selectedPlanId = it.selectedPlanId ?: result.value.firstOrNull()?.id,
                )
            }
            is AppResult.Failure -> _uiState.update { it.copy(loading = false, error = result.error) }
        }
    }

    fun create(command: CreatePlanCommand) = mutate { repository.create(command) }
    fun edit(command: UpdatePlanCommand) = mutate { repository.update(command) }

    fun delete(plan: AdminPlan) {
        if (!_uiState.value.canDelete || _uiState.value.deleting) return
        _uiState.update { it.copy(deleting = true, error = null, freePlanDeleteBlocked = false) }
        viewModelScope.launch {
            when (val result = repository.delete(plan)) {
                DeleteAdminPlanResult.Success -> _uiState.update {
                    val remaining = it.plans.filterNot { item -> item.id == plan.id }
                    it.copy(plans = remaining, selectedPlanId = remaining.firstOrNull()?.id, deleting = false)
                }
                DeleteAdminPlanResult.FreePlanCannotBeDeleted ->
                    _uiState.update { it.copy(deleting = false, freePlanDeleteBlocked = true) }
                is DeleteAdminPlanResult.Failure -> _uiState.update { it.copy(deleting = false, error = result.error) }
            }
        }
    }

    private fun mutate(block: suspend () -> AppResult<AdminPlan>) {
        if (_uiState.value.saving) return
        _uiState.update { it.copy(saving = true, error = null) }
        viewModelScope.launch {
            when (val result = block()) {
                is AppResult.Success -> _uiState.update {
                    val plans = if (it.plans.any { plan -> plan.id == result.value.id }) {
                        it.plans.map { plan -> if (plan.id == result.value.id) result.value else plan }
                    } else it.plans + result.value
                    it.copy(plans = plans, selectedPlanId = result.value.id, saving = false)
                }
                is AppResult.Failure -> _uiState.update { it.copy(saving = false, error = result.error) }
            }
        }
    }
}
