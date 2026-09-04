package com.vitran.shop.feature.auth.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.model.EmptyDataDto
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.auth.data.remote.dto.ForgotPasswordDataDto
import com.vitran.shop.feature.auth.data.remote.dto.ForgotPasswordRequestDto
import com.vitran.shop.feature.auth.data.remote.dto.LoginRequestDto
import com.vitran.shop.feature.auth.data.remote.dto.LoginTokensDataDto
import com.vitran.shop.feature.auth.data.remote.dto.LogoutRequestDto
import com.vitran.shop.feature.auth.data.remote.dto.RegisterDataDto
import com.vitran.shop.feature.auth.data.remote.dto.RegisterRequestDto
import com.vitran.shop.feature.auth.data.remote.dto.ResendOtpDataDto
import com.vitran.shop.feature.auth.data.remote.dto.ResendOtpRequestDto
import com.vitran.shop.feature.auth.data.remote.dto.ResetPasswordRequestDto
import com.vitran.shop.feature.auth.data.remote.dto.VerifyDataDto
import com.vitran.shop.feature.auth.data.remote.dto.VerifyRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class AuthApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun register(request: RegisterRequestDto): AppResult<RegisterDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/auth/register")) {
                authMode(AuthMode.None)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun verify(request: VerifyRequestDto): AppResult<VerifyDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/auth/verify")) {
                authMode(AuthMode.None)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun login(request: LoginRequestDto): AppResult<LoginTokensDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/auth/login")) {
                authMode(AuthMode.None)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun logout(request: LogoutRequestDto): AppResult<Unit> =
        executor.executeEmpty {
            client.post(environment.apiUrl("/auth/logout")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun resendOtp(request: ResendOtpRequestDto): AppResult<ResendOtpDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/auth/resend-otp")) {
                authMode(AuthMode.None)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun forgotPassword(request: ForgotPasswordRequestDto): AppResult<ForgotPasswordDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/auth/forgot-password")) {
                authMode(AuthMode.None)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun resetPassword(request: ResetPasswordRequestDto): AppResult<EmptyDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/auth/reset-password")) {
                authMode(AuthMode.None)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
}
