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
    // Redact multipart binary payloads (file parts) — keep field names only.
    sanitized =
        sanitized.replace(
            Regex(
                """Content-Disposition:\s*form-data;\s*name="images"[^\n]*\n(?:Content-Type:[^\n]*\n)?(?:Content-Length:[^\n]*\n)?\r?\n[\s\S]*?(?=\r?\n--|\z)""",
                RegexOption.IGNORE_CASE,
            ),
            "Content-Disposition: form-data; name=\"images\"\n\n***BINARY_REDACTED***\n",
        )
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
    // Redact payment Authority / Status query params in payment URLs.
    sanitized =
        sanitized.replace(
            Regex("""([?&]Authority=)([^&\s"']+)""", RegexOption.IGNORE_CASE),
            "$1***REDACTED***",
        )
    sanitized =
        sanitized.replace(
            Regex("""([?&]authority=)([^&\s"']+)""", RegexOption.IGNORE_CASE),
            "$1***REDACTED***",
        )
    sanitized =
        sanitized.replace(
            Regex("""("payment_url"\s*:\s*")([^"]*)(")""", RegexOption.IGNORE_CASE),
            "$1***REDACTED***$3",
        )
    return sanitized
}
