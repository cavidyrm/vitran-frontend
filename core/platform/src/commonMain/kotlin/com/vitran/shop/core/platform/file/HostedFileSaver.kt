package com.vitran.shop.core.platform.file

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * FileSaver that delegates to a runtime host (SAF / share sheet / browser download).
 * Until [bind] is called, [saveFile] returns [FileSaveResult.Cancelled].
 */
class HostedFileSaver : FileSaver {
    private val mutex = Mutex()
    private var host: (suspend (String, String, ByteArray) -> FileSaveResult)? = null

    fun bind(handler: suspend (suggestedName: String, mimeType: String, content: ByteArray) -> FileSaveResult) {
        host = handler
    }

    fun unbind() {
        host = null
    }

    override suspend fun saveFile(
        suggestedName: String,
        mimeType: String,
        content: ByteArray,
    ): FileSaveResult {
        val handler = mutex.withLock { host } ?: return FileSaveResult.Cancelled
        return runCatching { handler(suggestedName, mimeType, content) }
            .getOrElse { FileSaveResult.Failed(it.message) }
    }
}
