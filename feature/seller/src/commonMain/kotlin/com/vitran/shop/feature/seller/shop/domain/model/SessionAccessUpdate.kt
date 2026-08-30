package com.vitran.shop.feature.seller.shop.domain.model

import kotlinx.datetime.Instant

data class SessionAccessUpdate(
    val accessToken: String,
    val expiresAt: Instant,
)
