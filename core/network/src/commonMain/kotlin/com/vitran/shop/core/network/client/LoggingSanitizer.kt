package com.vitran.shop.core.network.client

private val SENSITIVE_HEADER_NAMES = setOf(
    "authorization",
    "x-api-key",
    "api-key",
)

private val SENSITIVE_JSON_KEYS = setOf(
    "authorization",
    "access_token",
    "refresh_token",
    "temp_token",
    "otp_code",
    "password",
    "new_password",
    "api_key",
    "authority",
)

fun sanitizeHeaderValue(name: String, value: String): String =
    if (name.lowercase() in SENSITIVE_HEADER_NAMES) "***REDACTED***" else value

fun sanitizeLogMessage(message: String): String {
    var sanitized = message
    SENSITIVE_JSON_KEYS.forEach { key ->
        sanitized = sanitized.replace(
            Regex("""("$key"\s*:\s*")([^"]*)(")""", RegexOption.IGNORE_CASE),
            "$1***REDACTED***$3",
        )
        sanitized = sanitized.replace(
            Regex("""("$key"\s*:\s*)([^,\}\]]+)""", RegexOption.IGNORE_CASE),
            "$1\"***REDACTED***\"",
        )
    }
    return sanitized
}
