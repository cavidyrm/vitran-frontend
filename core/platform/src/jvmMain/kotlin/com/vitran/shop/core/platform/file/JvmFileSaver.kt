package com.vitran.shop.core.platform.file

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Desktop save dialog. Writes bytes to the user-chosen path without exposing [File] to callers.
 */
class JvmFileSaver : FileSaver {
    override suspend fun saveFile(
        suggestedName: String,
        mimeType: String,
        content: ByteArray,
    ): FileSaveResult =
        withContext(Dispatchers.IO) {
            val dialog = FileDialog(null as Frame?, "Save file", FileDialog.SAVE)
            dialog.file = sanitizeDownloadFileName(suggestedName, suggestedName)
            dialog.isVisible = true
            val directory = dialog.directory
            val name = dialog.file
            if (directory == null || name == null) {
                return@withContext FileSaveResult.Cancelled
            }
            val target = File(directory, sanitizeDownloadFileName(name, suggestedName))
            runCatching {
                target.writeBytes(content)
                FileSaveResult.Saved
            }.getOrElse { FileSaveResult.Failed(it.message) }
        }
}
