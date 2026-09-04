package com.vitran.shop.feature.auth.data.repository

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.auth.data.mapper.toApiPhone
import com.vitran.shop.feature.auth.data.remote.AuthApi
import com.vitran.shop.feature.auth.data.remote.dto.LoginRequestDto
import com.vitran.shop.feature.auth.data.remote.dto.LoginVerificationRequiredDataDto
import com.vitran.shop.feature.auth.data.remote.dto.RegisterRequestDto
import com.vitran.shop.feature.auth.data.remote.dto.TokenSetDto
import com.vitran.shop.feature.auth.data.remote.dto.VerifyRequestDto
import com.vitran.shop.feature.auth.domain.model.LoginResult
import com.vitran.shop.feature.auth.domain.model.PasswordResetContext
import com.vitran.shop.feature.auth.domain.model.RegisterCommand
import com.vitran.shop.feature.auth.domain.model.VerificationChallenge
import com.vitran.shop.feature.auth.domain.repository.AuthRepository
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

internal class DefaultAuthRepository(
    private val authApi: AuthApi,
    private val sessionRepository: SessionRepository,
    private val json: Json,
) : AuthRepository {

    override suspend fun register(command: RegisterCommand): AppResult<VerificationChallenge> {
        val referral = command.referralCode?.trim()?.takeIf { it.isNotEmpty() }
        return when (val result = authApi.register(
            RegisterRequestDto(
                phone = command.phone.toApiPhone(),
                password = command.password,
                referralCode = referral,
            ),
        )) {
            is AppResult.Success -> AppResult.Success(
                VerificationChallenge(
                    phone = command.phone,
                    tempToken = result.value.tempToken,
                    developmentOtp = result.value.otpCode,
                ),
            )
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun login(phone: String, password: String): AppResult<LoginResult> =
        when (val result = authApi.login(LoginRequestDto(phone.toApiPhone(), password))) {
            is AppResult.Success -> {
                establishFromTokens(result.value.tokens)
                AppResult.Success(LoginResult.Authenticated)
            }
            is AppResult.Failure -> mapLoginFailure(result.error, phone)
        }

    override suspend fun verify(tempToken: String, code: String): AppResult<Unit> =
        when (val result = authApi.verify(VerifyRequestDto(tempToken, code))) {
            is AppResult.Success -> {
                establishFromTokens(result.value.tokens)
                AppResult.Success(Unit)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun resendOtp(phone: String): AppResult<String?> =
        when (val result = authApi.resendOtp(
            com.vitran.shop.feature.auth.data.remote.dto.ResendOtpRequestDto(phone.toApiPhone()),
        )) {
            is AppResult.Success -> AppResult.Success(result.value.otpCode)
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun requestPasswordReset(phone: String): AppResult<PasswordResetContext> =
        when (val result = authApi.forgotPassword(
            com.vitran.shop.feature.auth.data.remote.dto.ForgotPasswordRequestDto(phone.toApiPhone()),
        )) {
            is AppResult.Success -> AppResult.Success(
                PasswordResetContext(phone = phone, developmentOtp = result.value.otpCode),
            )
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun resetPassword(phone: String, code: String, newPassword: String): AppResult<Unit> =
        when (val result = authApi.resetPassword(
            com.vitran.shop.feature.auth.data.remote.dto.ResetPasswordRequestDto(
                phone = phone.toApiPhone(),
                code = code,
                newPassword = newPassword,
            ),
        )) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun logout(): AppResult<Unit> {
        val refreshToken = sessionRepository.currentRefreshToken()
        if (refreshToken == null) {
            sessionRepository.logoutLocal()
            return AppResult.Success(Unit)
        }
        return when (
            val result = authApi.logout(
                com.vitran.shop.feature.auth.data.remote.dto.LogoutRequestDto(refreshToken),
            )
        ) {
            is AppResult.Success -> {
                sessionRepository.logoutLocal()
                AppResult.Success(Unit)
            }
            is AppResult.Failure -> result
        }
    }

    private suspend fun establishFromTokens(tokens: TokenSetDto) {
        sessionRepository.establishSession(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            accessTokenExpiresAt = Instant.parse(tokens.expiresAt),
        )
    }

    private fun mapLoginFailure(error: AppError, phone: String): AppResult<LoginResult> {
        if (error is AppError.Forbidden) {
            val challenge = error.errorDataJson?.let { raw ->
                runCatching {
                    json.decodeFromString<LoginVerificationRequiredDataDto>(raw)
                }.getOrNull()
            }
            if (challenge?.tempToken != null) {
                return AppResult.Success(
                    LoginResult.VerificationRequired(
                        VerificationChallenge(
                            phone = phone,
                            tempToken = challenge.tempToken,
                            developmentOtp = challenge.otpCode,
                        ),
                    ),
                )
            }
        }
        return AppResult.Failure(error)
    }
}
