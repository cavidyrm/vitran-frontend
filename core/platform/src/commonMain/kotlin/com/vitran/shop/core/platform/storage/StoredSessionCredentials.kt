package com.vitran.shop.core.platform.storage

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Atomic credential blob persisted by [SecureSessionStorage].
 * Mapped to session-domain types inside `:core:session`.
 */
@Serializable
data class StoredSessionCredentials(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresAt: String,
) {
    fun expiresAtInstant(): Instant = Instant.parse(accessTokenExpiresAt)

    companion object {
        fun from(accessToken: String, refreshToken: String, expiresAt: Instant): StoredSessionCredentials =
            StoredSessionCredentials(
                accessToken = accessToken,
                refreshToken = refreshToken,
                accessTokenExpiresAt = expiresAt.toString(),
            )
    }
}
