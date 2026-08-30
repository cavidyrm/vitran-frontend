package com.vitran.shop.core.platform.file

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * ImagePicker that delegates to a runtime host (e.g. Compose Activity Result / browser input).
 * Until [bind] is called, [pickImages] returns an empty list (treated as cancel).
 */
class HostedImagePicker : ImagePicker {
    private val mutex = Mutex()
    private var host: (suspend (maxCount: Int) -> List<SelectedFile>)? = null

    fun bind(handler: suspend (maxCount: Int) -> List<SelectedFile>) {
        host = handler
    }

    fun unbind() {
        host = null
    }

    override suspend fun pickImages(maxCount: Int): List<SelectedFile> {
        val handler = mutex.withLock { host } ?: return emptyList()
        return runCatching { handler(maxCount) }.getOrDefault(emptyList())
    }
}

/**
 * One-shot bridge for platforms that complete picking asynchronously.
 */
suspend fun awaitPickedFiles(
    start: (onResult: (List<SelectedFile>) -> Unit) -> Unit,
): List<SelectedFile> {
    val deferred = CompletableDeferred<List<SelectedFile>>()
    start { files -> deferred.complete(files) }
    return deferred.await()
}
