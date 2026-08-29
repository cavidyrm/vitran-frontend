package com.vitran.shop.core.platform.storage

/**
 * Platform-backed secure credential persistence.
 * Implementations must store refresh tokens without plaintext fallbacks on production targets.
 */
interface SecureSessionStorage {
    suspend fun readCredentials(): StoredSessionCredentials?
    suspend fun writeCredentials(credentials: StoredSessionCredentials)
    suspend fun clearCredentials()
}
