package com.vitran.shop.core.platform.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Desktop credential store: AES-256-GCM encrypted file under the user config directory.
 * Prefer OS keychain in a future iteration; this is not plaintext and is owner-restricted where POSIX allows.
 */
class JvmSecureSessionStorage(
    private val json: Json,
    private val rootDir: Path = defaultRoot(),
) : SecureSessionStorage {

    private val credentialsFile: Path = rootDir.resolve("session.enc")
    private val keyFile: Path = rootDir.resolve("master.key")

    override suspend fun readCredentials(): StoredSessionCredentials? = withContext(Dispatchers.IO) {
        if (!Files.exists(credentialsFile)) return@withContext null
        val bytes = Files.readAllBytes(credentialsFile)
        val plain = decrypt(bytes) ?: return@withContext null
        runCatching { json.decodeFromString<StoredSessionCredentials>(plain.decodeToString()) }.getOrNull()
    }

    override suspend fun writeCredentials(credentials: StoredSessionCredentials) {
        withContext(Dispatchers.IO) {
            ensureDirs()
            val payload = json.encodeToString(credentials).encodeToByteArray()
            Files.write(credentialsFile, encrypt(payload))
            restrictOwnerOnly(credentialsFile)
        }
    }

    override suspend fun clearCredentials() {
        withContext(Dispatchers.IO) {
            Files.deleteIfExists(credentialsFile)
        }
    }

    private fun ensureDirs() {
        Files.createDirectories(rootDir)
        restrictOwnerOnly(rootDir)
        if (!Files.exists(keyFile)) {
            val keyBytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
            Files.write(keyFile, keyBytes)
            restrictOwnerOnly(keyFile)
        }
    }

    private fun loadKey(): SecretKey {
        ensureDirs()
        val bytes = Files.readAllBytes(keyFile)
        return SecretKeySpec(bytes, "AES")
    }

    private fun encrypt(plain: ByteArray): ByteArray {
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, loadKey(), GCMParameterSpec(128, iv))
        val cipherText = cipher.doFinal(plain)
        return iv + cipherText
    }

    private fun decrypt(blob: ByteArray): ByteArray? {
        if (blob.size < 13) return null
        return runCatching {
            val iv = blob.copyOfRange(0, 12)
            val cipherText = blob.copyOfRange(12, blob.size)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, loadKey(), GCMParameterSpec(128, iv))
            cipher.doFinal(cipherText)
        }.getOrNull()
    }

    private fun restrictOwnerOnly(path: Path) {
        runCatching {
            Files.setPosixFilePermissions(
                path,
                setOf(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE),
            )
        }
    }

    companion object {
        fun defaultRoot(): Path {
            val home = System.getProperty("user.home")
            val os = System.getProperty("os.name").orEmpty().lowercase()
            return when {
                os.contains("mac") -> Paths.get(home, "Library", "Application Support", "VitranShop", "secure")
                os.contains("win") -> Paths.get(
                    System.getenv("LOCALAPPDATA") ?: home,
                    "VitranShop",
                    "secure",
                )
                else -> Paths.get(home, ".config", "vitranshop", "secure")
            }
        }
    }
}
