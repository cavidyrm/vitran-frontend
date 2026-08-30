package com.vitran.shop.feature.seller.analytics.domain.model

/**
 * Opaque CSV (or other) export payload. Do not parse columns.
 */
data class AnalyticsExport(
    val bytes: ByteArray,
    val contentType: String?,
    val serverSuggestedFileName: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AnalyticsExport
        if (!bytes.contentEquals(other.bytes)) return false
        if (contentType != other.contentType) return false
        if (serverSuggestedFileName != other.serverSuggestedFileName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + (contentType?.hashCode() ?: 0)
        result = 31 * result + (serverSuggestedFileName?.hashCode() ?: 0)
        return result
    }
}
