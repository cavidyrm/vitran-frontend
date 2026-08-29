package com.vitran.shop.feature.auth.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.auth.domain.model.LoginResult
import com.vitran.shop.feature.auth.domain.model.PasswordResetContext
import com.vitran.shop.feature.auth.domain.model.RegisterCommand
import com.vitran.shop.feature.auth.domain.model.VerificationChallenge

interface AuthRepository {
    suspend fun register(command: RegisterCommand): AppResult<VerificationChallenge>
    suspend fun login(phone: String, password: String): AppResult<LoginResult>
    suspend fun verify(tempToken: String, code: String): AppResult<Unit>
    suspend fun resendOtp(phone: String): AppResult<String?>
    suspend fun requestPasswordReset(phone: String): AppResult<PasswordResetContext>
    suspend fun resetPassword(phone: String, code: String, newPassword: String): AppResult<Unit>
    suspend fun logout(): AppResult<Unit>
}
