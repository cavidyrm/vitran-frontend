package com.vitran.shop.core.platform.file

/**
 * Platform-neutral selected file for multipart upload.
 *
 * Bytes are loaded on demand via [readBytes]. Prefer modest image sizes
 * (seller product create allows up to 5 images). Phase 11 taxonomy import
 * may need a streaming-capable successor.
 */
data class SelectedFile(
    val name: String,
    val contentType: String? = null,
    val sizeBytes: Long? = null,
    private val bytesProvider: suspend () -> ByteArray,
) {
    suspend fun readBytes(): ByteArray = bytesProvider()

    companion object {
        fun fromBytes(
            name: String,
            bytes: ByteArray,
            contentType: String? = null,
        ): SelectedFile =
            SelectedFile(
                name = safeFileName(name),
                contentType = contentType,
                sizeBytes = bytes.size.toLong(),
                bytesProvider = { bytes },
            )
    }
}

/** Basename only — never send or store full local filesystem paths. */
fun safeFileName(raw: String): String {
    val trimmed = raw.trim().ifBlank { "file" }
    val slash = trimmed.lastIndexOf('/')
    val backslash = trimmed.lastIndexOf('\\')
    val cut = maxOf(slash, backslash)
    val base = if (cut >= 0) trimmed.substring(cut + 1) else trimmed
    return base.ifBlank { "file" }
}
