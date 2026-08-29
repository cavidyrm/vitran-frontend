package com.vitran.shop.core.platform.storage

import com.liftric.kvault.KVault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val VAULT_ID = "vitran_secure_session"
private const val CREDENTIALS_KEY = "session_credentials"

class IosSecureSessionStorage(
    private val json: Json,
) : SecureSessionStorage {

    private val vault = KVault(VAULT_ID)

    override suspend fun readCredentials(): StoredSessionCredentials? = withContext(Dispatchers.Default) {
        val raw = vault.string(CREDENTIALS_KEY) ?: return@withContext null
        runCatching { json.decodeFromString<StoredSessionCredentials>(raw) }.getOrNull()
    }

    override suspend fun writeCredentials(credentials: StoredSessionCredentials) {
        withContext(Dispatchers.Default) {
            vault.set(CREDENTIALS_KEY, json.encodeToString(credentials))
        }
    }

    override suspend fun clearCredentials() {
        withContext(Dispatchers.Default) {
            vault.deleteObject(CREDENTIALS_KEY)
        }
    }
}
