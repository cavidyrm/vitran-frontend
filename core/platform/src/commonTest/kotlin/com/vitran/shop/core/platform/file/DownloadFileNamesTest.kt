package com.vitran.shop.core.platform.file

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlinx.coroutines.test.runTest

class DownloadFileNamesTest {

    @Test
    fun pathTraversal_isStrippedToBasename() {
        val name = sanitizeDownloadFileName("../../secret.csv", "fallback.csv")
        assertEquals("secret.csv", name)
        assertFalse(name.contains(".."))
        assertFalse(name.contains("/"))
    }

    @Test
    fun backslashTraversal_isStripped() {
        assertEquals("secret.csv", sanitizeDownloadFileName("..\\..\\secret.csv", "fallback.csv"))
    }

    @Test
    fun controlCharacters_areRemoved() {
        val name = sanitizeDownloadFileName("ex\u0000port.csv", "fallback.csv")
        assertFalse(name.contains('\u0000'))
        assertEquals("export.csv", name)
    }

    @Test
    fun blank_usesFallback() {
        assertEquals("fallback.csv", sanitizeDownloadFileName("   ", "fallback.csv"))
        assertEquals("fallback.csv", sanitizeDownloadFileName(null, "fallback.csv"))
    }

    @Test
    fun fallbackExportName_doesNotEmbedShopTitle() {
        assertEquals(
            "vitran-shop-1-analytics-7d.csv",
            analyticsExportFallbackFileName(shopId = 1, periodQuery = "7d"),
        )
    }

    @Test
    fun noOpSaver_isCancellation() = runTest {
        assertEquals(
            FileSaveResult.Cancelled,
            NoOpFileSaver().saveFile("a.csv", "text/csv", byteArrayOf(1)),
        )
    }
}
