package com.vitran.shop.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.auth.domain.error.toAuthError
import com.vitran.shop.feature.auth.domain.model.RegisterCommand
import com.vitran.shop.feature.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isSubmitting: Boolean = false,
    val generalError: String? = null,
)

sealed interface RegisterUiEffect {
    data class NavigateToVerification(val phone: String) : RegisterUiEffect
}

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val validatePhone: (String) -> Boolean,
    private val validatePassword: (String) -> Boolean,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<RegisterUiEffect>()
    val effects: SharedFlow<RegisterUiEffect> = _effects.asSharedFlow()

    fun submit(phone: String, password: String, referralCode: String?) {
        if (_uiState.value.isSubmitting) return
        if (!validatePhone(phone) || !validatePassword(password)) {
            _uiState.update { it.copy(generalError = "اطلاعات ثبت‌نام را بررسی کنید") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, generalError = null) }
            when (val result = registerUseCase(RegisterCommand(phone.trim(), password, referralCode))) {
                is AppResult.Success -> _effects.emit(RegisterUiEffect.NavigateToVerification(phone.trim()))
                is AppResult.Failure -> _uiState.update {
                    it.copy(generalError = result.error.toAuthError().message ?: "ثبت‌نام ناموفق بود")
                }
            }
            _uiState.update { it.copy(isSubmitting = false) }
        }
    }
}
