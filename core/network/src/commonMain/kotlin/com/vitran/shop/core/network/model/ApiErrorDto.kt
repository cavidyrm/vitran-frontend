package com.vitran.shop.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDto(
    val reason: String,
    val messages: List<String>,
)
