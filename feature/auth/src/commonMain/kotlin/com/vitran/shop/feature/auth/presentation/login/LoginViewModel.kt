package com.vitran.shop.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.auth.domain.error.AuthError
import com.vitran.shop.feature.auth.domain.error.toAuthError
import com.vitran.shop.feature.auth.domain.model.LoginResult
import com.vitran.shop.feature.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isSubmitting: Boolean = false,
    val generalError: String? = null,
)

sealed interface LoginUiEffect {
    data object NavigateToVerification : LoginUiEffect
    data object LoginSucceeded : LoginUiEffect
}

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val validatePhone: (String) -> Boolean,
    private val validatePassword: (String) -> Boolean,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<LoginUiEffect>()
    val effects: SharedFlow<LoginUiEffect> = _effects.asSharedFlow()

    fun submit(phone: String, password: String) {
        if (_uiState.value.isSubmitting) return
        if (!validatePhone(phone) || !validatePassword(password)) {
            _uiState.update { it.copy(generalError = "اطلاعات ورود را بررسی کنید") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, generalError = null) }
            when (val result = loginUseCase(phone.trim(), password)) {
                is AppResult.Success -> when (result.value) {
                    LoginResult.Authenticated -> _effects.emit(LoginUiEffect.LoginSucceeded)
                    is LoginResult.VerificationRequired -> _effects.emit(LoginUiEffect.NavigateToVerification)
                }
                is AppResult.Failure -> _uiState.update {
                    it.copy(generalError = mapError(result.error.toAuthError()))
                }
            }
            _uiState.update { it.copy(isSubmitting = false) }
        }
    }

    private fun mapError(error: AuthError): String = when (error) {
        is AuthError.InvalidCredentials -> error.message ?: "اطلاعات ورود نادرست است"
        is AuthError.Network -> "خطای اتصال. دوباره تلاش کنید."
        is AuthError.Validation -> error.fieldErrors.firstOrNull()?.messages?.firstOrNull()
            ?: error.message ?: "اطلاعات وارد شده معتبر نیست"
        else -> error.message ?: "خطایی رخ داد"
    }
}
