package com.vitran.shop.core.platform.file

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Desktop file chooser — converts selected files to [SelectedFile] without exposing [File] outside.
 */
class JvmFileImagePicker : ImagePicker {
    override suspend fun pickImages(maxCount: Int): List<SelectedFile> =
        withContext(Dispatchers.IO) {
            val dialog = FileDialog(null as Frame?, "Select images", FileDialog.LOAD)
            dialog.isMultipleMode = maxCount > 1
            dialog.setFilenameFilter { _, name ->
                val lower = name.lowercase()
                lower.endsWith(".jpg") ||
                    lower.endsWith(".jpeg") ||
                    lower.endsWith(".png") ||
                    lower.endsWith(".webp") ||
                    lower.endsWith(".gif")
            }
            dialog.isVisible = true
            val files: Array<File> = dialog.files ?: emptyArray()
            if (files.isEmpty()) return@withContext emptyList()
            files.take(maxCount).mapNotNull { file ->
                runCatching {
                    SelectedFile.fromBytes(
                        name = file.name,
                        bytes = file.readBytes(),
                        contentType = guessImageContentType(file.name),
                    )
                }.getOrNull()
            }
        }
}

internal fun guessImageContentType(fileName: String): String? {
    val lower = fileName.lowercase()
    return when {
        lower.endsWith(".jpg") || lower.endsWith(".jpeg") -> "image/jpeg"
        lower.endsWith(".png") -> "image/png"
        lower.endsWith(".webp") -> "image/webp"
        lower.endsWith(".gif") -> "image/gif"
        else -> null
    }
}
