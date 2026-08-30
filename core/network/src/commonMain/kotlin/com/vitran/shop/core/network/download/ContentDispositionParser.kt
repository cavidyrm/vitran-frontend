package com.vitran.shop.core.network.download

/**
 * Extracts a raw filename from a Content-Disposition header.
 * The value is untrusted — sanitize before using as a filesystem name.
 */
internal fun extractContentDispositionFileName(header: String?): String? {
    if (header.isNullOrBlank()) return null
    val star =
        FILENAME_STAR_REGEX.find(header)
            ?: return FILENAME_REGEX.find(header)?.let { match ->
                match.groupValues[1].ifBlank { match.groupValues[2] }.trim().trim('"').ifBlank { null }
            }
    val encoded = star.groupValues[1].trim().trim('"')
    return decodeRfc5987(encoded).ifBlank { null }
}

private fun decodeRfc5987(value: String): String {
    val decoded = StringBuilder(value.length)
    var i = 0
    while (i < value.length) {
        val c = value[i]
        if (c == '%' && i + 2 < value.length) {
            val hex = value.substring(i + 1, i + 3)
            val parsed = hex.toIntOrNull(16)
            if (parsed != null) {
                decoded.append(parsed.toChar())
                i += 3
                continue
            }
        }
        decoded.append(c)
        i += 1
    }
    return decoded.toString()
}

private val FILENAME_STAR_REGEX =
    Regex("""filename\*\s*=\s*(?:UTF-8''|utf-8'')([^;]+)""", RegexOption.IGNORE_CASE)

private val FILENAME_REGEX =
    Regex("""filename\s*=\s*"([^"]+)"|filename\s*=\s*([^;]+)\s*""", RegexOption.IGNORE_CASE)
