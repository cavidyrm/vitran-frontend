package com.vitran.shop.core.session.auth

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.data.remote.TokenRefreshRemoteDataSource
import com.vitran.shop.core.session.domain.SessionCredentials
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.core.session.time.AppClock
import com.vitran.shop.core.session.time.TokenExpirationPolicy
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultTokenRefreshCoordinator(
    private val sessionRepository: SessionRepository,
    private val refreshRemote: TokenRefreshRemoteDataSource,
    private val clock: AppClock,
) : TokenRefreshCoordinator {

    private val refreshMutex = Mutex()

    override suspend fun getValidAccessToken(authMode: AuthMode): AppResult<String?> {
        when (authMode) {
            AuthMode.None -> return AppResult.Success(null)
            AuthMode.Optional -> {
                val current = sessionRepository.readCredentials()
                    ?: return AppResult.Success(null)
                if (isExpiredOrNearExpiry(current)) {
                    return when (refreshIfNeeded(force = true)) {
                        is RefreshOutcome.Success ->
                            AppResult.Success(sessionRepository.readCredentials()?.accessToken)
                        RefreshOutcome.TerminalFailure -> AppResult.Success(null)
                        is RefreshOutcome.TransientFailure -> AppResult.Success(current.accessToken)
                    }
                }
                return AppResult.Success(current.accessToken)
            }
            AuthMode.Required -> {
                val current = sessionRepository.readCredentials()
                    ?: return AppResult.Failure(
                        AppError.Authentication.SessionExpired(message = "Authentication required"),
                    )
                if (isExpiredOrNearExpiry(current)) {
                    return when (val outcome = refreshIfNeeded(force = true)) {
                        is RefreshOutcome.Success -> AppResult.Success(outcome.credentials.accessToken)
                        is RefreshOutcome.TransientFailure -> AppResult.Failure(outcome.error)
                        RefreshOutcome.TerminalFailure -> AppResult.Failure(
                            AppError.Authentication.SessionExpired(message = "Session expired"),
                        )
                    }
                }
                return AppResult.Success(current.accessToken)
            }
        }
    }

    override suspend fun refreshIfNeeded(force: Boolean): RefreshOutcome {
        val current = sessionRepository.readCredentials() ?: return RefreshOutcome.TerminalFailure
        if (!force && !isExpiredOrNearExpiry(current)) {
            return RefreshOutcome.Success(current)
        }

        return refreshMutex.withLock {
            val latest = sessionRepository.readCredentials() ?: return@withLock RefreshOutcome.TerminalFailure
            if (!force && !isExpiredOrNearExpiry(latest)) {
                return@withLock RefreshOutcome.Success(latest)
            }

            when (val result = refreshRemote.refresh(latest.refreshToken)) {
                is AppResult.Success -> {
                    sessionRepository.establishSession(result.value)
                    RefreshOutcome.Success(result.value)
                }
                is AppResult.Failure -> when {
                    result.error.isTerminalRefreshFailure() -> {
                        sessionRepository.invalidateSession()
                        RefreshOutcome.TerminalFailure
                    }
                    result.error.isTransientRefreshFailure() ->
                        RefreshOutcome.TransientFailure(result.error)
                    else -> RefreshOutcome.TransientFailure(result.error)
                }
            }
        }
    }

    override suspend fun invalidateSession() {
        refreshMutex.withLock {
            sessionRepository.invalidateSession()
        }
    }

    private fun isExpiredOrNearExpiry(credentials: SessionCredentials): Boolean {
        val skewSeconds = TokenExpirationPolicy.expirationSkew.inWholeSeconds
        val threshold = credentials.accessTokenExpiresAt.epochSeconds - skewSeconds
        return clock.now().epochSeconds >= threshold
    }
}

internal class DefaultSessionAuthCoordinator(
    private val refreshCoordinator: TokenRefreshCoordinator,
) : SessionAuthCoordinator {

    override suspend fun resolveAccessToken(authMode: AuthMode): AppResult<String?> =
        refreshCoordinator.getValidAccessToken(authMode)

    override suspend fun handleUnauthorizedResponse(
        authMode: AuthMode,
        retryOnce: suspend () -> HttpResponse,
    ): AppResult<HttpResponse> {
        if (authMode == AuthMode.None) {
            return AppResult.Failure(AppError.Authentication.Unauthorized())
        }

        return when (val outcome = refreshCoordinator.refreshIfNeeded(force = true)) {
            is RefreshOutcome.Success -> {
                val response = retryOnce()
                if (response.status.value == 401) {
                    refreshCoordinator.invalidateSession()
                    AppResult.Failure(AppError.Authentication.SessionExpired())
                } else {
                    AppResult.Success(response)
                }
            }
            RefreshOutcome.TerminalFailure ->
                AppResult.Failure(AppError.Authentication.SessionExpired())
            is RefreshOutcome.TransientFailure -> AppResult.Failure(outcome.error)
        }
    }
}
