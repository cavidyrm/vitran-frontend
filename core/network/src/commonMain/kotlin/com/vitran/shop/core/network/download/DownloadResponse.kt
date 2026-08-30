package com.vitran.shop.core.network.download

/**
 * Raw authenticated download result. Body is opaque bytes — never JSON-decoded.
 *
 * [suggestedFileName] is the unsanitized Content-Disposition value (if any).
 * Callers must sanitize before writing to a filesystem.
 *
 * Memory: ByteArray is a bounded tradeoff for current analytics exports, not unlimited files.
 */
data class DownloadResponse(
    val bytes: ByteArray,
    val contentType: String?,
    val suggestedFileName: String?,
    val contentLength: Long?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DownloadResponse
        if (!bytes.contentEquals(other.bytes)) return false
        if (contentType != other.contentType) return false
        if (suggestedFileName != other.suggestedFileName) return false
        if (contentLength != other.contentLength) return false
        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + (contentType?.hashCode() ?: 0)
        result = 31 * result + (suggestedFileName?.hashCode() ?: 0)
        result = 31 * result + (contentLength?.hashCode() ?: 0)
        return result
    }
}
