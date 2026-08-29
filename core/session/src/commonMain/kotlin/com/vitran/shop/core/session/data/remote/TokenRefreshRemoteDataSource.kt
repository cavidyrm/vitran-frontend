package com.vitran.shop.core.session.data.remote

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.domain.SessionCredentials

interface TokenRefreshRemoteDataSource {
    suspend fun refresh(refreshToken: String): AppResult<SessionCredentials>
}
