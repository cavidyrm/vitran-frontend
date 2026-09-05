package com.vitran.shop.core.platform.storage

import com.vitran.shop.core.platform.serialization.createPlatformJson
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WebSecureSessionStorageTest {

    private val json = createPlatformJson()

    @Test
    fun write_thenRead_returnsSameCredentials() = runTest {
        val backend = MapWebStorageBackend()
        val storage = WebSecureSessionStorage(json, backend)
        val credentials = sampleCredentials()

        storage.writeCredentials(credentials)

        assertEquals(credentials, storage.readCredentials())
    }

    @Test
    fun clear_removesStoredCredentials() = runTest {
        val backend = MapWebStorageBackend()
        val storage = WebSecureSessionStorage(json, backend)
        storage.writeCredentials(sampleCredentials())

        storage.clearCredentials()

        assertNull(storage.readCredentials())
        assertNull(backend.getItem(WEB_SESSION_CREDENTIALS_KEY))
    }

    @Test
    fun corruptJson_returnsNull() = runTest {
        val backend = MapWebStorageBackend()
        backend.setItem(WEB_SESSION_CREDENTIALS_KEY, "{not-json")
        val storage = WebSecureSessionStorage(json, backend)

        assertNull(storage.readCredentials())
    }

    @Test
    fun storageFailures_doNotThrowOnWriteOrClear() = runTest {
        val storage = WebSecureSessionStorage(json, ThrowingWebStorageBackend())

        storage.writeCredentials(sampleCredentials())
        storage.clearCredentials()
        assertNull(storage.readCredentials())
    }

    private fun sampleCredentials() = StoredSessionCredentials(
        accessToken = "access",
        refreshToken = "refresh",
        accessTokenExpiresAt = "2026-01-01T13:00:00Z",
    )
}

private class MapWebStorageBackend : WebStorageBackend {
    private val items = mutableMapOf<String, String>()

    override fun getItem(key: String): String? = items[key]

    override fun setItem(key: String, value: String) {
        items[key] = value
    }

    override fun removeItem(key: String) {
        items.remove(key)
    }
}

private class ThrowingWebStorageBackend : WebStorageBackend {
    override fun getItem(key: String): String? = error("unavailable")

    override fun setItem(key: String, value: String) {
        error("quota")
    }

    override fun removeItem(key: String) {
        error("quota")
    }
}
