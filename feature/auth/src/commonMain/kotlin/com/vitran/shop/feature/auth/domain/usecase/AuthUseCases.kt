package com.vitran.shop.feature.auth.domain.usecase

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.auth.domain.flow.AuthFlowStateHolder
import com.vitran.shop.feature.auth.domain.model.LoginResult
import com.vitran.shop.feature.auth.domain.model.RegisterCommand
import com.vitran.shop.feature.auth.domain.model.VerificationChallenge
import com.vitran.shop.feature.auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository,
    private val authFlowStateHolder: AuthFlowStateHolder,
) {
    suspend operator fun invoke(command: RegisterCommand): AppResult<VerificationChallenge> =
        when (val result = authRepository.register(command)) {
            is AppResult.Success -> {
                authFlowStateHolder.setVerificationChallenge(result.value)
                result
            }
            is AppResult.Failure -> result
        }
}

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val authFlowStateHolder: AuthFlowStateHolder,
) {
    suspend operator fun invoke(phone: String, password: String): AppResult<LoginResult> =
        when (val result = authRepository.login(phone, password)) {
            is AppResult.Success -> {
                if (result.value is LoginResult.VerificationRequired) {
                    authFlowStateHolder.setVerificationChallenge(
                        (result.value as LoginResult.VerificationRequired).challenge,
                    )
                }
                result
            }
            is AppResult.Failure -> result
        }
}

class VerifyPhoneUseCase(
    private val authRepository: AuthRepository,
    private val authFlowStateHolder: AuthFlowStateHolder,
) {
    suspend operator fun invoke(code: String): AppResult<Unit> {
        val challenge = authFlowStateHolder.verificationChallenge.value
            ?: return AppResult.Failure(
                com.vitran.shop.core.domain.error.AppError.Unexpected(
                    message = "Verification session expired",
                ),
            )
        return when (val result = authRepository.verify(challenge.tempToken, code)) {
            is AppResult.Success -> {
                authFlowStateHolder.setVerificationChallenge(null)
                result
            }
            is AppResult.Failure -> result
        }
    }
}

class ResendOtpUseCase(
    private val authRepository: AuthRepository,
    private val authFlowStateHolder: AuthFlowStateHolder,
) {
    suspend operator fun invoke(): AppResult<String?> {
        val phone = authFlowStateHolder.verificationChallenge.value?.phone
            ?: return AppResult.Failure(
                com.vitran.shop.core.domain.error.AppError.Unexpected(message = "Verification session expired"),
            )
        return authRepository.resendOtp(phone)
    }
}

class RequestPasswordResetUseCase(
    private val authRepository: AuthRepository,
    private val authFlowStateHolder: AuthFlowStateHolder,
) {
    suspend operator fun invoke(phone: String) =
        when (val result = authRepository.requestPasswordReset(phone)) {
            is AppResult.Success -> {
                authFlowStateHolder.setPasswordResetContext(result.value)
                result
            }
            is AppResult.Failure -> result
        }
}

class ResetPasswordUseCase(
    private val authRepository: AuthRepository,
    private val authFlowStateHolder: AuthFlowStateHolder,
) {
    suspend operator fun invoke(code: String, newPassword: String): AppResult<Unit> {
        val context = authFlowStateHolder.passwordResetContext.value
            ?: return AppResult.Failure(
                com.vitran.shop.core.domain.error.AppError.Unexpected(message = "Reset session expired"),
            )
        return when (val result = authRepository.resetPassword(context.phone, code, newPassword)) {
            is AppResult.Success -> {
                authFlowStateHolder.setPasswordResetContext(null)
                result
            }
            is AppResult.Failure -> result
        }
    }
}

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val authFlowStateHolder: AuthFlowStateHolder,
) {
    suspend operator fun invoke(): AppResult<Unit> {
        authFlowStateHolder.clear()
        return authRepository.logout()
    }
}
