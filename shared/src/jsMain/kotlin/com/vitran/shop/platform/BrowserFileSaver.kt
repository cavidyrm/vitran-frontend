package com.vitran.shop.platform

import com.vitran.shop.core.platform.file.FileSaveResult
import com.vitran.shop.core.platform.file.FileSaver
import com.vitran.shop.core.platform.file.sanitizeDownloadFileName
import kotlinx.browser.document
import org.khronos.webgl.Uint8Array
import org.w3c.dom.HTMLAnchorElement
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

/**
 * Browser download via Blob + object URL. Revokes the URL after click.
 */
class BrowserFileSaver : FileSaver {
    override suspend fun saveFile(
        suggestedName: String,
        mimeType: String,
        content: ByteArray,
    ): FileSaveResult =
        runCatching {
            val bytes = Uint8Array(content.size)
            for (index in content.indices) {
                bytes.asDynamic()[index] = content[index]
            }
            val blob =
                Blob(
                    arrayOf(bytes),
                    BlobPropertyBag(type = mimeType.ifBlank { "application/octet-stream" }),
                )
            val url = createObjectUrl(blob)
            val anchor = document.createElement("a") as HTMLAnchorElement
            anchor.href = url
            anchor.download = sanitizeDownloadFileName(suggestedName, "download")
            document.body?.appendChild(anchor)
            anchor.click()
            runCatching { document.body?.removeChild(anchor) }
            revokeObjectUrl(url)
            FileSaveResult.Saved
        }.getOrElse { FileSaveResult.Failed(it.message) }
}

@Suppress("UNUSED_PARAMETER")
private fun createObjectUrl(blob: Blob): String = js("URL.createObjectURL(blob)") as String

@Suppress("UNUSED_PARAMETER")
private fun revokeObjectUrl(url: String) {
    js("URL.revokeObjectURL(url)")
}
