package com.vitran.shop.feature.account.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.model.UpdateProfileCommand
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val error: String? = null,
)

sealed interface ProfileUiAction {
    data class UsernameChanged(val value: String) : ProfileUiAction
    data class EmailChanged(val value: String) : ProfileUiAction
    data object Retry : ProfileUiAction
    data object Save : ProfileUiAction
}

class ProfileViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.currentUserState.collect { state ->
                when (state) {
                    CurrentUserState.Unknown, CurrentUserState.Loading ->
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    is CurrentUserState.Available -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            username = state.user.username.orEmpty(),
                            email = state.user.email.orEmpty(),
                            phone = state.user.phone,
                            error = null,
                        )
                    }
                    is CurrentUserState.Error -> _uiState.update {
                        it.copy(isLoading = false, error = state.message ?: "بارگذاری ناموفق")
                    }
                }
            }
        }
        refresh()
    }

    fun onAction(action: ProfileUiAction) {
        when (action) {
            is ProfileUiAction.UsernameChanged -> _uiState.update { it.copy(username = action.value) }
            is ProfileUiAction.EmailChanged -> _uiState.update { it.copy(email = action.value) }
            ProfileUiAction.Retry -> refresh()
            ProfileUiAction.Save -> save()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            accountRepository.refreshCurrentUser()
        }
    }

    private fun save() {
        val state = _uiState.value
        if (state.isUpdating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, error = null) }
            when (
                val result = accountRepository.updateProfile(
                    UpdateProfileCommand(
                        username = state.username.ifBlank { null },
                        email = state.email.ifBlank { null },
                    ),
                )
            ) {
                is AppResult.Success -> _uiState.update { it.copy(isUpdating = false) }
                is AppResult.Failure -> _uiState.update {
                    it.copy(isUpdating = false, error = result.error.message)
                }
            }
        }
    }
}
