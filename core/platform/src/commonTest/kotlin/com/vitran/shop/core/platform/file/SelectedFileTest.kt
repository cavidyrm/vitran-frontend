package com.vitran.shop.core.platform.file

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SelectedFileTest {

    @Test
    fun fromBytes_safeNameAndContent() = runTest {
        val file =
            SelectedFile.fromBytes(
                name = "/var/folders/tmp/photo.jpg",
                bytes = byteArrayOf(9, 8, 7),
                contentType = "image/jpeg",
            )
        assertEquals("photo.jpg", file.name)
        assertEquals("image/jpeg", file.contentType)
        assertEquals(3, file.sizeBytes)
        assertTrue(file.readBytes().contentEquals(byteArrayOf(9, 8, 7)))
    }

    @Test
    fun noOpPicker_cancelIsEmpty() = runTest {
        assertTrue(NoOpImagePicker().pickImages(3).isEmpty())
    }
}
