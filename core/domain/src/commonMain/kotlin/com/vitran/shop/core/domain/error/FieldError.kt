package com.vitran.shop.core.domain.error

/**
 * Structured backend field/global validation error from the API envelope `errors` array.
 */
data class FieldError(
    val reason: String,
    val messages: List<String>,
)
