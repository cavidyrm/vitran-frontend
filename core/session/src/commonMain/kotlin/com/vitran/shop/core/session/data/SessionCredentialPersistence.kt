package com.vitran.shop.core.session.data

import com.vitran.shop.core.platform.storage.SecureSessionStorage
import com.vitran.shop.core.platform.storage.StoredSessionCredentials
import com.vitran.shop.core.session.domain.SessionCredentials

internal class SessionCredentialPersistence(
    private val secureStorage: SecureSessionStorage,
) {
    suspend fun read(): SessionCredentials? =
        secureStorage.readCredentials()?.toDomain()

    suspend fun write(credentials: SessionCredentials) {
        secureStorage.writeCredentials(credentials.toStored())
    }

    suspend fun clear() {
        secureStorage.clearCredentials()
    }

    private fun StoredSessionCredentials.toDomain(): SessionCredentials =
        SessionCredentials(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiresAt = expiresAtInstant(),
        )

    private fun SessionCredentials.toStored(): StoredSessionCredentials =
        StoredSessionCredentials.from(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresAt = accessTokenExpiresAt,
        )
}
