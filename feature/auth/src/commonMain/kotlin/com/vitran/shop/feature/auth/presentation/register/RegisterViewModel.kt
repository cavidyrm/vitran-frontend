package com.vitran.shop.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.auth.domain.ReferralCodeValidator
import com.vitran.shop.feature.auth.domain.error.splitForForm
import com.vitran.shop.feature.auth.domain.error.toAuthError
import com.vitran.shop.feature.auth.domain.model.PhoneCheckNextStep
import com.vitran.shop.feature.auth.domain.model.RegisterCommand
import com.vitran.shop.feature.auth.domain.usecase.CheckPhoneUseCase
import com.vitran.shop.feature.auth.domain.usecase.RegisterUseCase
import com.vitran.shop.feature.auth.presentation.AuthFormFields
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ReferralCodeCheckUiStatus {
    data object Idle : ReferralCodeCheckUiStatus()
    data object Checking : ReferralCodeCheckUiStatus()
    data object Valid : ReferralCodeCheckUiStatus()
    data object Invalid : ReferralCodeCheckUiStatus()
    data object Error : ReferralCodeCheckUiStatus()
}

data class RegisterUiState(
    val isSubmitting: Boolean = false,
    val generalError: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val referralCheck: ReferralCodeCheckUiStatus = ReferralCodeCheckUiStatus.Idle,
)

sealed interface RegisterUiEffect {
    data class NavigateToVerification(val phone: String) : RegisterUiEffect
    data object NavigateToLogin : RegisterUiEffect
}

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val checkPhoneUseCase: CheckPhoneUseCase,
    private val referralCodeValidator: ReferralCodeValidator,
    private val validatePhone: (String) -> Boolean,
    private val validatePassword: (String) -> Boolean,
    private val referralDebounceMs: Long = 400L,
    private val phoneCheckDebounceMs: Long = 400L,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<RegisterUiEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<RegisterUiEffect> = _effects.asSharedFlow()

    private var referralCheckJob: Job? = null
    private var phoneCheckJob: Job? = null

    fun onPhoneCompleted(phone: String) {
        phoneCheckJob?.cancel()
        val trimmed = phone.trim()
        if (!validatePhone(trimmed)) return
        phoneCheckJob =
            viewModelScope.launch {
                delay(phoneCheckDebounceMs)
                when (val result = checkPhoneUseCase(trimmed)) {
                    is AppResult.Success ->
                        if (result.value.nextStep == PhoneCheckNextStep.Login) {
                            _effects.emit(RegisterUiEffect.NavigateToLogin)
                        }
                    is AppResult.Failure -> Unit
                }
            }
    }

    fun onInviteCodeChanged(raw: String) {
        referralCheckJob?.cancel()
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) {
            _uiState.update { it.copy(referralCheck = ReferralCodeCheckUiStatus.Idle) }
            return
        }
        referralCheckJob =
            viewModelScope.launch {
                delay(referralDebounceMs)
                _uiState.update { it.copy(referralCheck = ReferralCodeCheckUiStatus.Checking) }
                when (val result = referralCodeValidator.validate(trimmed)) {
                    is AppResult.Success ->
                        _uiState.update {
                            it.copy(
                                referralCheck =
                                    if (result.value) {
                                        ReferralCodeCheckUiStatus.Valid
                                    } else {
                                        ReferralCodeCheckUiStatus.Invalid
                                    },
                            )
                        }
                    is AppResult.Failure ->
                        _uiState.update { it.copy(referralCheck = ReferralCodeCheckUiStatus.Error) }
                }
            }
    }

    fun submit(phone: String, password: String, referralCode: String?) {
        if (_uiState.value.isSubmitting) return
        if (!validatePhone(phone) || !validatePassword(password)) {
            _uiState.update {
                it.copy(generalError = "اطلاعات ثبت‌نام را بررسی کنید", fieldErrors = emptyMap())
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, generalError = null, fieldErrors = emptyMap()) }
            when (val result = registerUseCase(RegisterCommand(phone.trim(), password, referralCode))) {
                is AppResult.Success -> _effects.emit(RegisterUiEffect.NavigateToVerification(phone.trim()))
                is AppResult.Failure -> {
                    val authError = result.error.toAuthError()
                    val split = authError.splitForForm(
                        knownReasons = AuthFormFields.register,
                        fallbackMessage = authError.message ?: "ثبت‌نام ناموفق بود",
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
        _uiState.update { state ->
            if (key !in state.fieldErrors) state
            else state.copy(fieldErrors = state.fieldErrors - key)
        }
    }
}
