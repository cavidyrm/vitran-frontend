package com.vitran.shop.core.platform.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val PREFS_FILE = "vitran_secure_session"
private const val CREDENTIALS_KEY = "session_credentials"

class AndroidSecureSessionStorage(
    context: Context,
    private val json: Json,
) : SecureSessionStorage {

    private val appContext = context.applicationContext

    private val masterKey by lazy {
        MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            appContext,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override suspend fun readCredentials(): StoredSessionCredentials? = withContext(Dispatchers.IO) {
        val raw = prefs.getString(CREDENTIALS_KEY, null) ?: return@withContext null
        runCatching { json.decodeFromString<StoredSessionCredentials>(raw) }.getOrNull()
    }

    override suspend fun writeCredentials(credentials: StoredSessionCredentials) {
        withContext(Dispatchers.IO) {
            prefs.edit()
                .putString(CREDENTIALS_KEY, json.encodeToString(credentials))
                .apply()
        }
    }

    override suspend fun clearCredentials() {
        withContext(Dispatchers.IO) {
            prefs.edit().remove(CREDENTIALS_KEY).apply()
        }
    }
}
