package com.vitran.shop.core.platform.file

/**
 * Sanitizes a server-provided download filename before any filesystem write.
 * Treats Content-Disposition as untrusted input.
 */
fun sanitizeDownloadFileName(raw: String?, fallback: String): String {
    if (raw.isNullOrBlank()) return fallback
    val base = safeFileName(raw)
    val cleaned = buildString(base.length) {
        for (c in base) {
            if (c.isISOControl()) continue
            if (c == '/' || c == '\\') continue
            append(c)
        }
    }.replace("..", "").trim()
    val withoutReserved = cleaned.trimStart('.').ifBlank { fallback }
    return withoutReserved.ifBlank { fallback }
}

fun analyticsExportFallbackFileName(shopId: Long, periodQuery: String): String =
    "vitran-shop-$shopId-analytics-$periodQuery.csv"
