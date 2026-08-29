package com.vitran.shop.core.network.session

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.core.network.request.markSkipSessionAuth
import com.vitran.shop.core.session.data.remote.TokenRefreshRemoteDataSource
import com.vitran.shop.core.session.domain.SessionCredentials
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class RefreshRequestDto(
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
private data class TokenSetDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_at") val expiresAt: String,
)

@Serializable
private data class RefreshDataDto(
    val tokens: TokenSetDto,
)

class KtorTokenRefreshRemoteDataSource(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) : TokenRefreshRemoteDataSource {
    override suspend fun refresh(refreshToken: String): AppResult<SessionCredentials> =
        when (val result = executor.execute<RefreshDataDto> {
            client.post(environment.apiUrl("/auth/refresh")) {
                authMode(AuthMode.None)
                markSkipSessionAuth()
                contentType(ContentType.Application.Json)
                setBody(RefreshRequestDto(refreshToken = refreshToken))
            }
        }) {
            is AppResult.Success -> AppResult.Success(result.value.tokens.toDomain())
            is AppResult.Failure -> result
        }

    private fun TokenSetDto.toDomain(): SessionCredentials =
        SessionCredentials(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiresAt = Instant.parse(expiresAt),
        )
}
