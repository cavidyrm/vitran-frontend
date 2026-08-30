package com.vitran.shop.core.platform.file

/**
 * Result of a user-controlled file save. Cancellation is a normal outcome, not an application error.
 */
sealed class FileSaveResult {
    data object Saved : FileSaveResult()
    data object Cancelled : FileSaveResult()
    data class Failed(val message: String? = null) : FileSaveResult()
}

/**
 * Platform capability for saving downloaded bytes to a user-selected destination.
 * Domain must not depend on this interface; Presentation owns save.
 */
interface FileSaver {
    suspend fun saveFile(
        suggestedName: String,
        mimeType: String,
        content: ByteArray,
    ): FileSaveResult
}

/** Test / Preview double — treated as user cancellation. */
class NoOpFileSaver : FileSaver {
    override suspend fun saveFile(
        suggestedName: String,
        mimeType: String,
        content: ByteArray,
    ): FileSaveResult = FileSaveResult.Cancelled
}
