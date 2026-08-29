package com.vitran.shop.core.session.data

import com.vitran.shop.core.session.domain.SessionCredentials
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Thread-safe in-memory credential cache for network auth. */
internal class CredentialStore {
    private val mutex = Mutex()
    private var credentials: SessionCredentials? = null

    suspend fun get(): SessionCredentials? = mutex.withLock { credentials }

    suspend fun set(value: SessionCredentials?) {
        mutex.withLock { credentials = value }
    }

    suspend fun clear() {
        mutex.withLock { credentials = null }
    }

    /** Synchronous read for Ktor auth plugin on the request thread. */
    fun peek(): SessionCredentials? = credentials
}
