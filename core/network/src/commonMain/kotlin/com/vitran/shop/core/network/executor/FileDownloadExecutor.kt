package com.vitran.shop.core.network.executor

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.download.DownloadResponse
import com.vitran.shop.core.network.download.extractContentDispositionFileName
import com.vitran.shop.core.network.error.ApiErrorMapper
import com.vitran.shop.core.network.error.NetworkExceptionMapper
import com.vitran.shop.core.network.logging.NetworkLogger
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

/**
 * Authenticated raw-file download. Does not decode [ApiEnvelope] on success.
 * Non-2xx bodies are parsed as JSON API errors when possible so error JSON is never saved as a file.
 */
class FileDownloadExecutor(
    private val json: Json,
    private val logger: NetworkLogger,
) {
    suspend fun execute(block: suspend () -> HttpResponse): AppResult<DownloadResponse> {
        return try {
            val response = block()
            parseDownload(response)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            AppResult.Failure(NetworkExceptionMapper.map(throwable, logger))
        }
    }

    private suspend fun parseDownload(response: HttpResponse): AppResult<DownloadResponse> {
        val status = response.status
        val contentType = response.contentType()?.toString()
        val bytes = runCatching { response.readRawBytes() }.getOrElse { throwable ->
            if (throwable is CancellationException) throw throwable
            return AppResult.Failure(NetworkExceptionMapper.map(throwable, logger))
        }

        if (!status.isSuccess()) {
            val rawBody = runCatching { bytes.decodeToString() }.getOrNull()
            return AppResult.Failure(ApiErrorMapper.mapHttpFailure(status, rawBody, json))
        }

        if (isHtmlContentType(contentType)) {
            return AppResult.Failure(
                AppError.Unexpected(message = "Download returned HTML instead of a file"),
            )
        }

        val suggested =
            extractContentDispositionFileName(response.headers[HttpHeaders.ContentDisposition])
        val contentLength =
            response.headers[HttpHeaders.ContentLength]?.toLongOrNull() ?: bytes.size.toLong()

        logger.debug("File download completed (${bytes.size} bytes)")

        return AppResult.Success(
            DownloadResponse(
                bytes = bytes,
                contentType = contentType,
                suggestedFileName = suggested,
                contentLength = contentLength,
            ),
        )
    }

    private fun isHtmlContentType(contentType: String?): Boolean {
        if (contentType.isNullOrBlank()) return false
        val media = contentType.substringBefore(';').trim().lowercase()
        return media == "text/html" || media == "application/xhtml+xml"
    }
}
