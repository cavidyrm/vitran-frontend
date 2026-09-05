package com.vitran.shop.core.platform.storage

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-process fallback for tests and platforms without durable storage.
 * Not production-secure for refresh tokens.
 */
class InMemorySecureSessionStorage : SecureSessionStorage {
    private val mutex = Mutex()
    private var stored: StoredSessionCredentials? = null

    override suspend fun readCredentials(): StoredSessionCredentials? = mutex.withLock { stored }

    override suspend fun writeCredentials(credentials: StoredSessionCredentials) {
        mutex.withLock { stored = credentials }
    }

    override suspend fun clearCredentials() {
        mutex.withLock { stored = null }
    }

    /** Test-only inspection. */
    internal fun peek(): StoredSessionCredentials? = stored
}
