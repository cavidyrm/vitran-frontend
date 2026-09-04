package com.vitran.shop.feature.auth.presentation.reset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.ApiEnvironments
import com.vitran.shop.feature.auth.domain.error.splitForForm
import com.vitran.shop.feature.auth.domain.error.toAuthError
import com.vitran.shop.feature.auth.domain.flow.AuthFlowStateHolder
import com.vitran.shop.feature.auth.domain.usecase.ResetPasswordUseCase
import com.vitran.shop.feature.auth.presentation.AuthFormFields
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResetPasswordUiState(
    val isSubmitting: Boolean = false,
    val generalError: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val debugOtpCode: String? = null,
    val contextMissing: Boolean = false,
)

sealed interface ResetPasswordUiEffect {
    data object ResetSucceeded : ResetPasswordUiEffect
    data object ContextMissing : ResetPasswordUiEffect
}

class ResetPasswordViewModel(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val authFlowStateHolder: AuthFlowStateHolder,
    apiEnvironment: ApiEnvironment,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ResetPasswordUiEffect>()
    val effects: SharedFlow<ResetPasswordUiEffect> = _effects.asSharedFlow()

    init {
        val context = authFlowStateHolder.passwordResetContext.value
        if (context == null) {
            _uiState.update { it.copy(contextMissing = true) }
        } else if (apiEnvironment == ApiEnvironments.Local) {
            _uiState.update { it.copy(debugOtpCode = context.developmentOtp) }
        }
    }

    fun submit(code: String, newPassword: String) {
        if (_uiState.value.isSubmitting) return
        if (authFlowStateHolder.passwordResetContext.value == null) {
            viewModelScope.launch { _effects.emit(ResetPasswordUiEffect.ContextMissing) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, generalError = null, fieldErrors = emptyMap()) }
            when (val result = resetPasswordUseCase(code, newPassword)) {
                is AppResult.Success -> _effects.emit(ResetPasswordUiEffect.ResetSucceeded)
                is AppResult.Failure -> {
                    val authError = result.error.toAuthError()
                    val split = authError.splitForForm(
                        knownReasons = AuthFormFields.reset,
                        reasonAliases = AuthFormFields.resetAliases,
                        fallbackMessage = authError.message,
                    )
                    _uiState.update {
                        it.copy(
                            fieldErrors = split.fieldErrors,
                            generalError = split.generalMessage,
                        )
                    }
                }
            }
            _uiState.update { it.copy(isSubmitting = false) }
        }
    }

    fun clearFieldError(reason: String) {
        val key = reason.lowercase()
        val mapped = AuthFormFields.resetAliases[key] ?: key
        _uiState.update { state ->
            val next = state.fieldErrors - key - mapped
            if (next == state.fieldErrors) state else state.copy(fieldErrors = next)
        }
    }
}
