package com.vitran.shop.core.platform.file

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.withScopedAccess

/**
 * [ImagePicker] backed by FileKit native dialogs (Android Photo Picker, iOS PHPicker,
 * desktop chooser, browser file input on JS/Wasm).
 */
class FileKitImagePicker : ImagePicker {
    override suspend fun pickImages(maxCount: Int): List<SelectedFile> {
        if (maxCount <= 0) return emptyList()
        return runCatching {
            if (maxCount == 1) {
                val file = FileKit.openFilePicker(type = FileKitType.Image) ?: return emptyList()
                listOfNotNull(file.toSelectedFile())
            } else {
                val files =
                    FileKit.openFilePicker(
                        type = FileKitType.Image,
                        mode = FileKitMode.Multiple(maxItems = maxCount.coerceIn(1, 50)),
                    ).orEmpty()
                files.mapNotNull { it.toSelectedFile() }
            }
        }.getOrDefault(emptyList())
    }
}

private suspend fun PlatformFile.toSelectedFile(): SelectedFile? =
    runCatching {
        val bytes = withScopedAccess { readBytes() }
        SelectedFile.fromBytes(
            name = name,
            bytes = bytes,
            contentType = guessImageContentType(name),
        )
    }.getOrNull()

private fun guessImageContentType(fileName: String): String? {
    val lower = fileName.lowercase()
    return when {
        lower.endsWith(".jpg") || lower.endsWith(".jpeg") -> "image/jpeg"
        lower.endsWith(".png") -> "image/png"
        lower.endsWith(".webp") -> "image/webp"
        lower.endsWith(".gif") -> "image/gif"
        lower.endsWith(".heic") || lower.endsWith(".heif") -> "image/heic"
        else -> null
    }
}
