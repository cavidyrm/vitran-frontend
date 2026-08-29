package com.vitran.shop.core.session.domain

import kotlinx.datetime.Instant

/** Internal session credential model — not exposed to presentation. */
data class SessionCredentials(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresAt: Instant,
)
