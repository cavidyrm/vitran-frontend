package com.vitran.shop.feature.auth.presentation.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.ApiEnvironments
import com.vitran.shop.feature.auth.domain.error.splitForForm
import com.vitran.shop.feature.auth.domain.error.toAuthError
import com.vitran.shop.feature.auth.domain.flow.AuthFlowStateHolder
import com.vitran.shop.feature.auth.domain.usecase.ResendOtpUseCase
import com.vitran.shop.feature.auth.domain.usecase.VerifyPhoneUseCase
import com.vitran.shop.feature.auth.presentation.AuthFormFields
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterVerifyUiState(
    val code: String = "",
    val phoneDisplay: String = "",
    val isSubmitting: Boolean = false,
    val isResending: Boolean = false,
    val error: String? = null,
    val debugOtpCode: String? = null,
    val challengeMissing: Boolean = false,
)

sealed interface RegisterVerifyUiAction {
    data class CodeChanged(val value: String) : RegisterVerifyUiAction
    data object Submit : RegisterVerifyUiAction
    data object Resend : RegisterVerifyUiAction
}

sealed interface RegisterVerifyUiEffect {
    data object Verified : RegisterVerifyUiEffect
    data object ChallengeMissing : RegisterVerifyUiEffect
}

class RegisterVerifyViewModel(
    private val verifyPhoneUseCase: VerifyPhoneUseCase,
    private val resendOtpUseCase: ResendOtpUseCase,
    private val authFlowStateHolder: AuthFlowStateHolder,
    apiEnvironment: ApiEnvironment,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterVerifyUiState())
    val uiState: StateFlow<RegisterVerifyUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<RegisterVerifyUiEffect>()
    val effects: SharedFlow<RegisterVerifyUiEffect> = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            val challenge = authFlowStateHolder.verificationChallenge.value
            if (challenge == null) {
                _uiState.update { it.copy(challengeMissing = true) }
                _effects.emit(RegisterVerifyUiEffect.ChallengeMissing)
            } else {
                _uiState.update {
                    it.copy(
                        phoneDisplay = challenge.phone,
                        debugOtpCode = if (apiEnvironment == ApiEnvironments.Local) challenge.developmentOtp else null,
                    )
                }
            }
        }
    }

    fun verifyCode(code: String) {
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            when (val result = verifyPhoneUseCase(code)) {
                is AppResult.Success -> _effects.emit(RegisterVerifyUiEffect.Verified)
                is AppResult.Failure -> {
                    val authError = result.error.toAuthError()
                    val split = authError.splitForForm(
                        knownReasons = AuthFormFields.verify,
                        fallbackMessage = authError.message ?: "کد تأیید نادرست است",
                    )
                    _uiState.update {
                        it.copy(
                            error = split.fieldErrors[AuthFormFields.Code]
                                ?: split.generalMessage
                                ?: "کد تأیید نادرست است",
                        )
                    }
                }
            }
            _uiState.update { it.copy(isSubmitting = false) }
        }
    }

    fun resendCode() {
        if (_uiState.value.isResending) return
        viewModelScope.launch {
            _uiState.update { it.copy(isResending = true, error = null) }
            when (val result = resendOtpUseCase()) {
                is AppResult.Success -> _uiState.update {
                    it.copy(debugOtpCode = result.value ?: it.debugOtpCode)
                }
                is AppResult.Failure -> {
                    val authError = result.error.toAuthError()
                    _uiState.update { it.copy(error = authError.message) }
                }
            }
            _uiState.update { it.copy(isResending = false) }
        }
    }

    fun clearFieldError(reason: String) {
        if (reason.lowercase() != AuthFormFields.Code) return
        _uiState.update { if (it.error == null) it else it.copy(error = null) }
    }
}
