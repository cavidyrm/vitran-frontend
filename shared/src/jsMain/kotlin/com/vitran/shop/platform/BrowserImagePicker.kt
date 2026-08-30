package com.vitran.shop.platform

import com.vitran.shop.core.platform.file.ImagePicker
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.core.platform.file.safeFileName
import kotlinx.coroutines.suspendCancellableCoroutine
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import org.w3c.files.get
import kotlin.coroutines.resume

/**
 * Browser file input picker for JS (accepts image files).
 */
class BrowserImagePicker : ImagePicker {
    override suspend fun pickImages(maxCount: Int): List<SelectedFile> =
        suspendCancellableCoroutine { cont ->
            val document = kotlinx.browser.document
            val input = document.createElement("input") as HTMLInputElement
            input.type = "file"
            input.accept = "image/*"
            input.multiple = maxCount > 1
            input.style.display = "none"
            document.body?.appendChild(input)

            fun cleanup() {
                runCatching { document.body?.removeChild(input) }
            }

            input.onchange = {
                val fileList = input.files
                if (fileList == null || fileList.length == 0) {
                    cleanup()
                    if (cont.isActive) cont.resume(emptyList())
                } else {
                    val count = minOf(fileList.length, maxCount)
                    val collected = mutableListOf<SelectedFile>()
                    var pending = count
                    fun finishOne() {
                        pending -= 1
                        if (pending <= 0) {
                            cleanup()
                            if (cont.isActive) cont.resume(collected.toList())
                        }
                    }
                    var i = 0
                    while (i < count) {
                        val index = i
                        i += 1
                        val file = fileList[index]
                        if (file == null) {
                            finishOne()
                        } else {
                            val reader = FileReader()
                            reader.onload = {
                                val buffer = reader.result as? ArrayBuffer
                                if (buffer != null) {
                                    collected +=
                                        SelectedFile.fromBytes(
                                            name = safeFileName(file.name),
                                            bytes = arrayBufferToByteArray(buffer),
                                            contentType = file.type.takeIf { it.isNotBlank() },
                                        )
                                }
                                finishOne()
                            }
                            reader.onerror = {
                                finishOne()
                            }
                            reader.readAsArrayBuffer(file)
                        }
                    }
                }
            }
            cont.invokeOnCancellation { cleanup() }
            input.click()
        }
}

private fun arrayBufferToByteArray(buffer: ArrayBuffer): ByteArray {
    val view = Uint8Array(buffer)
    return ByteArray(view.length) { idx -> view[idx] }
}
