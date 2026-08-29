package com.vitran.shop.core.network.error

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.error.FieldError
import com.vitran.shop.core.network.model.ApiEnvelope
import com.vitran.shop.core.network.model.ApiErrorDto
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.serializer

object ApiErrorMapper {

    fun <T> mapEnvelopeFailure(
        httpStatus: HttpStatusCode,
        envelope: ApiEnvelope<T>,
        errorDataJson: String?,
    ): AppError {
        val fieldErrors = envelope.errors.toFieldErrors()
        val message = envelope.message.takeIf { it.isNotBlank() }
        val backendCode = envelope.code
        val status = httpStatus.value

        return when (status) {
            401 -> AppError.Authentication.Unauthorized(
                message = message,
                httpStatus = status,
                backendCode = backendCode,
                fieldErrors = fieldErrors,
                errorDataJson = errorDataJson,
            )
            403 -> AppError.Forbidden(
                message = message,
                httpStatus = status,
                backendCode = backendCode,
                fieldErrors = fieldErrors,
                errorDataJson = errorDataJson,
            )
            404 -> AppError.NotFound(
                message = message,
                httpStatus = status,
                backendCode = backendCode,
                fieldErrors = fieldErrors,
                errorDataJson = errorDataJson,
            )
            409 -> AppError.Conflict(
                message = message,
                httpStatus = status,
                backendCode = backendCode,
                fieldErrors = fieldErrors,
                errorDataJson = errorDataJson,
            )
            in 500..599 -> AppError.Server(
                message = message,
                httpStatus = status,
                backendCode = backendCode,
                fieldErrors = fieldErrors,
                errorDataJson = errorDataJson,
            )
            else -> when {
                fieldErrors.isNotEmpty() || backendCode == VALIDATION_CODE -> AppError.Validation(
                    message = message,
                    httpStatus = status,
                    backendCode = backendCode,
                    fieldErrors = fieldErrors,
                    errorDataJson = errorDataJson,
                )
                status in 400..499 -> AppError.Validation(
                    message = message,
                    httpStatus = status,
                    backendCode = backendCode,
                    fieldErrors = fieldErrors,
                    errorDataJson = errorDataJson,
                )
                else -> AppError.Unexpected(message = message ?: "Request failed with status $status")
            }
        }
    }

    fun mapHttpFailure(
        httpStatus: HttpStatusCode,
        rawBody: String?,
        json: Json,
    ): AppError {
        val status = httpStatus.value
        if (rawBody.isNullOrBlank()) {
            return mapStatusOnlyFailure(status)
        }

        return runCatching {
            val envelope = json.decodeFromString(
                ApiEnvelope.serializer(JsonElement.serializer()),
                rawBody,
            )
            mapEnvelopeFailure(
                httpStatus = httpStatus,
                envelope = envelope,
                errorDataJson = extractErrorDataJson(envelope.data, json),
            )
        }.getOrElse {
            mapStatusOnlyFailure(status)
        }
    }

    private fun mapStatusOnlyFailure(status: Int): AppError =
        when (status) {
            401 -> AppError.Authentication.Unauthorized(httpStatus = status)
            403 -> AppError.Forbidden(httpStatus = status)
            404 -> AppError.NotFound(httpStatus = status)
            409 -> AppError.Conflict(httpStatus = status)
            in 500..599 -> AppError.Server(httpStatus = status)
            in 400..499 -> AppError.Validation(httpStatus = status)
            else -> AppError.Unexpected(message = "Request failed with status $status")
        }

    fun isTransportSuccess(httpStatus: HttpStatusCode, envelope: ApiEnvelope<*>): Boolean =
        httpStatus.isSuccess() && envelope.success

    fun extractErrorDataJson(data: JsonElement?, json: Json): String? {
        if (data == null || data is JsonNull) return null
        return runCatching { json.encodeToString(JsonElement.serializer(), data) }.getOrNull()
    }

    private fun List<ApiErrorDto>.toFieldErrors(): List<FieldError> =
        map { dto ->
            FieldError(reason = dto.reason, messages = dto.messages)
        }

    private const val VALIDATION_CODE: Int = -2
}
