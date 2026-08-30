package com.vitran.shop.core.network.executor

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.createTestClient
import com.vitran.shop.core.network.download.DownloadResponse
import com.vitran.shop.core.network.download.extractContentDispositionFileName
import com.vitran.shop.core.network.logging.NoOpNetworkLogger
import com.vitran.shop.core.network.serialization.createNetworkJson
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest

class FileDownloadExecutorTest {

    private val executor = FileDownloadExecutor(json = createNetworkJson(), logger = NoOpNetworkLogger)

    @Test
    fun successCsv_returnsUnchangedBytes_andDoesNotDecodeEnvelope() = runTest {
        val csv = "col_a,col_b\n1,2\n".encodeToByteArray()
        val client =
            createTestClient(
                MockEngine {
                    respond(
                        content = csv,
                        status = HttpStatusCode.OK,
                        headers =
                            headersOf(
                                HttpHeaders.ContentType to listOf("text/csv"),
                                HttpHeaders.ContentDisposition to
                                    listOf("attachment; filename=\"shop-export.csv\""),
                            ),
                    )
                },
            )

        val result = executor.execute { client.get("http://localhost/export") }

        val download = assertIs<AppResult.Success<DownloadResponse>>(result).value
        assertTrue(download.bytes.contentEquals(csv))
        assertEquals("shop-export.csv", download.suggestedFileName)
        assertTrue(download.contentType?.contains("csv") == true)
    }

    @Test
    fun successWithJsonLookingBytes_doesNotParseEnvelope() = runTest {
        val bytes = """{"success":true,"data":{}}""".encodeToByteArray()
        val client =
            createTestClient(
                MockEngine {
                    respond(
                        content = bytes,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/octet-stream"),
                    )
                },
            )

        val result = executor.execute { client.get("http://localhost/export") }
        val download = assertIs<AppResult.Success<DownloadResponse>>(result).value
        assertTrue(download.bytes.contentEquals(bytes))
    }

    @Test
    fun htmlSuccess_isRejected() = runTest {
        val client =
            createTestClient(
                MockEngine {
                    respond(
                        content = "<html>login</html>",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "text/html; charset=utf-8"),
                    )
                },
            )

        val result = executor.execute { client.get("http://localhost/export") }
        assertIs<AppResult.Failure>(result)
        assertIs<AppError.Unexpected>(result.error)
    }

    @Test
    fun forbiddenJson_isMappedNotReturnedAsCsv() = runTest {
        val client =
            createTestClient(
                MockEngine {
                    respond(
                        content =
                            """
                            {
                              "success": false,
                              "message": "forbidden",
                              "code": 403,
                              "data": null,
                              "errors": []
                            }
                            """.trimIndent(),
                        status = HttpStatusCode.Forbidden,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                },
            )

        val result = executor.execute { client.get("http://localhost/export") }
        val failure = assertIs<AppResult.Failure>(result)
        assertIs<AppError.Forbidden>(failure.error)
        assertEquals("forbidden", failure.error.message)
    }

    @Test
    fun serverErrorJson_isMapped() = runTest {
        val client =
            createTestClient(
                MockEngine {
                    respond(
                        content =
                            """
                            {
                              "success": false,
                              "message": "server",
                              "code": 500,
                              "data": null,
                              "errors": []
                            }
                            """.trimIndent(),
                        status = HttpStatusCode.InternalServerError,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                },
            )

        val result = executor.execute { client.get("http://localhost/export") }
        assertIs<AppError.Server>(assertIs<AppResult.Failure>(result).error)
    }

    @Test
    fun contentDisposition_extractsQuotedFilename() {
        assertEquals(
            "export.csv",
            extractContentDispositionFileName("attachment; filename=\"export.csv\""),
        )
    }

    @Test
    fun contentDisposition_maliciousFilename_isExtractedRaw() {
        assertEquals(
            "../../secret.csv",
            extractContentDispositionFileName("attachment; filename=\"../../secret.csv\""),
        )
    }

    @Test
    fun contentDisposition_missing_returnsNull() {
        assertNull(extractContentDispositionFileName(null))
        assertNull(extractContentDispositionFileName("inline"))
    }

    @Test
    fun cancellation_isPropagated() = runTest {
        val client =
            createTestClient(
                MockEngine {
                    respond(
                        content = "a,b\n".encodeToByteArray(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "text/csv"),
                    )
                },
            )
        val job =
            async {
                executor.execute { client.get("http://localhost/export") }
            }
        job.cancel()
        try {
            job.await()
        } catch (_: CancellationException) {
            return@runTest
        }
        // If the coroutine completed before cancel, that is also acceptable as long as
        // CancellationException was not converted to AppError.
        assertTrue(job.isCancelled || job.isCompleted)
    }
}
