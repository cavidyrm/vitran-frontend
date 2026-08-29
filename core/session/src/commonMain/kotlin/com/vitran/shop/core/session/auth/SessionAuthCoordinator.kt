package com.vitran.shop.core.session.auth

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.domain.SessionCredentials
import io.ktor.client.statement.HttpResponse

/**
 * Network-layer integration for authenticated requests.
 * Implemented by session infrastructure — features never call this directly.
 */
interface SessionAuthCoordinator {
    suspend fun resolveAccessToken(authMode: AuthMode): AppResult<String?>

    suspend fun handleUnauthorizedResponse(
        authMode: AuthMode,
        retryOnce: suspend () -> HttpResponse,
    ): AppResult<HttpResponse>
}

sealed interface RefreshOutcome {
    data class Success(val credentials: SessionCredentials) : RefreshOutcome
    data class TransientFailure(val error: AppError) : RefreshOutcome
    data object TerminalFailure : RefreshOutcome
}

internal interface TokenRefreshCoordinator {
    suspend fun refreshIfNeeded(force: Boolean = false): RefreshOutcome
    suspend fun getValidAccessToken(authMode: AuthMode): AppResult<String?>
    suspend fun invalidateSession()
}

internal fun AppError.isTerminalRefreshFailure(): Boolean = when (this) {
    is AppError.Authentication.Unauthorized,
    is AppError.Authentication.SessionExpired,
    -> true
    is AppError.Forbidden -> httpStatus == 401 || httpStatus == 403
    else -> httpStatus == 401
}

internal fun AppError.isTransientRefreshFailure(): Boolean = when (this) {
    is AppError.Network -> true
    is AppError.Server -> httpStatus?.let { it in 500..599 } == true
    else -> false
}
