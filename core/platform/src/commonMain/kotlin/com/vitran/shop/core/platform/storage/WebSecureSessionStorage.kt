package com.vitran.shop.core.platform.storage

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal const val WEB_SESSION_CREDENTIALS_KEY = "vitran_session_credentials"

/**
 * Browser key/value adapter used by [WebSecureSessionStorage].
 * Wasm/JS bind `localStorage`; tests use an in-memory map.
 */
interface WebStorageBackend {
    fun getItem(key: String): String?
    fun setItem(key: String, value: String)
    fun removeItem(key: String)
}

/**
 * Persists session credentials as JSON in a browser storage backend.
 * Quota / private-mode failures on write/clear are swallowed so in-tab login still succeeds.
 */
class WebSecureSessionStorage(
    private val json: Json,
    private val backend: WebStorageBackend,
) : SecureSessionStorage {

    private val mutex = Mutex()

    override suspend fun readCredentials(): StoredSessionCredentials? = mutex.withLock {
        val raw = runCatching { backend.getItem(WEB_SESSION_CREDENTIALS_KEY) }.getOrNull()
            ?: return@withLock null
        runCatching { json.decodeFromString<StoredSessionCredentials>(raw) }.getOrNull()
    }

    override suspend fun writeCredentials(credentials: StoredSessionCredentials) {
        mutex.withLock {
            runCatching {
                backend.setItem(WEB_SESSION_CREDENTIALS_KEY, json.encodeToString(credentials))
            }
        }
    }

    override suspend fun clearCredentials() {
        mutex.withLock {
            runCatching { backend.removeItem(WEB_SESSION_CREDENTIALS_KEY) }
        }
    }
}
