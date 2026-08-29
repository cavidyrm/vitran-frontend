package com.vitran.shop.core.network.health

import kotlinx.serialization.Serializable

@Serializable
data class HealthDto(
    val status: String,
)

@Serializable
data class VersionedHealthDto(
    val status: String,
    val version: String? = null,
)
