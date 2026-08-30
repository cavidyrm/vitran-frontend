package com.vitran.shop.core.platform.file

/**
 * Platform capability for selecting product images.
 * Cancellation returns an empty list (never throws).
 */
interface ImagePicker {
    /**
     * @param maxCount maximum images to return (caller enforces product limits).
     */
    suspend fun pickImages(maxCount: Int): List<SelectedFile>
}

/** Test / Preview double — always empty. */
class NoOpImagePicker : ImagePicker {
    override suspend fun pickImages(maxCount: Int): List<SelectedFile> = emptyList()
}
