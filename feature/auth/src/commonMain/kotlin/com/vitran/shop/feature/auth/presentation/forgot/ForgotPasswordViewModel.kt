package com.vitran.shop.feature.auth.presentation.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.auth.domain.error.toAuthError
import com.vitran.shop.feature.auth.domain.usecase.RequestPasswordResetUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val isSubmitting: Boolean = false,
    val generalError: String? = null,
)

sealed interface ForgotPasswordUiEffect {
    data class NavigateToReset(val phone: String) : ForgotPasswordUiEffect
}

class ForgotPasswordViewModel(
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase,
    private val validatePhone: (String) -> Boolean,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ForgotPasswordUiEffect>()
    val effects: SharedFlow<ForgotPasswordUiEffect> = _effects.asSharedFlow()

    fun submit(phone: String) {
        if (_uiState.value.isSubmitting) return
        if (!validatePhone(phone)) {
            _uiState.update { it.copy(generalError = "شماره موبایل معتبر نیست") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, generalError = null) }
            when (val result = requestPasswordResetUseCase(phone.trim())) {
                is AppResult.Success -> _effects.emit(ForgotPasswordUiEffect.NavigateToReset(phone.trim()))
                is AppResult.Failure -> _uiState.update {
                    it.copy(generalError = result.error.toAuthError().message)
                }
            }
            _uiState.update { it.copy(isSubmitting = false) }
        }
    }
}
