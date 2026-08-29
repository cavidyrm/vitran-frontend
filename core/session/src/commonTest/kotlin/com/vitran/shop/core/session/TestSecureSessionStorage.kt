package com.vitran.shop.core.session

import com.vitran.shop.core.platform.storage.SecureSessionStorage
import com.vitran.shop.core.platform.storage.StoredSessionCredentials
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class TestSecureSessionStorage : SecureSessionStorage {
    private val mutex = Mutex()
    private var stored: StoredSessionCredentials? = null

    override suspend fun readCredentials(): StoredSessionCredentials? = mutex.withLock { stored }

    override suspend fun writeCredentials(credentials: StoredSessionCredentials) {
        mutex.withLock { stored = credentials }
    }

    override suspend fun clearCredentials() {
        mutex.withLock { stored = null }
    }
}
