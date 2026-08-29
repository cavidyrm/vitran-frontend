package com.vitran.shop.core.network.model

import kotlinx.serialization.Serializable

/**
 * Backend shared transport envelope. Do not expose outside data/network boundaries.
 */
@Serializable
data class ApiEnvelope<T>(
    val success: Boolean,
    val message: String,
    val code: Int,
    val data: T? = null,
    val errors: List<ApiErrorDto> = emptyList(),
)
