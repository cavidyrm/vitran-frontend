package com.vitran.shop.core.platform.file

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Resolves content [Uri]s into [SelectedFile] without exposing Uri to commonMain.
 */
object AndroidSelectedFileFactory {
    suspend fun fromUri(context: Context, uri: Uri): SelectedFile? =
        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver
            val name = queryDisplayName(resolver, uri) ?: "image"
            val type = resolver.getType(uri)
            val bytes =
                resolver.openInputStream(uri)?.use { it.readBytes() } ?: return@withContext null
            SelectedFile.fromBytes(
                name = name,
                bytes = bytes,
                contentType = type,
            )
        }

    private fun queryDisplayName(resolver: ContentResolver, uri: Uri): String? {
        val cursor = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) it.getString(index) else null
            } else {
                null
            }
        }
    }
}
