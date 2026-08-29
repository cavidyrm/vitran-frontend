package com.vitran.shop.core.network.executor

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.error.ApiErrorMapper
import com.vitran.shop.core.network.error.NetworkExceptionMapper
import com.vitran.shop.core.network.logging.NetworkLogger
import com.vitran.shop.core.network.model.ApiEnvelope
import com.vitran.shop.core.network.model.ApiErrorDto
import com.vitran.shop.core.network.model.EmptyDataDto
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializer

class ApiRequestExecutor(
    private val json: Json,
    private val logger: NetworkLogger,
) {

    suspend inline fun <reified T> execute(
        authMode: AuthMode = AuthMode.None,
        noinline block: suspend () -> HttpResponse,
    ): AppResult<T> = execute(serializer<T>(), block)

    suspend fun <T> execute(
        dataSerializer: kotlinx.serialization.KSerializer<T>,
        block: suspend () -> HttpResponse,
    ): AppResult<T> {
        return try {
            val response = block()
            parseResponse(response, dataSerializer)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            AppResult.Failure(NetworkExceptionMapper.map(throwable, logger))
        }
    }

    suspend fun executeEmpty(
        authMode: AuthMode = AuthMode.None,
        block: suspend () -> HttpResponse,
    ): AppResult<Unit> =
        when (val result = execute(EmptyDataDto.serializer(), block)) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }

    suspend fun <T> parseResponse(
        response: HttpResponse,
        dataSerializer: kotlinx.serialization.KSerializer<T>,
    ): AppResult<T> {
        val status = response.status
        val rawBody = runCatching { response.readRawBytes().decodeToString() }.getOrDefault("")

        if (rawBody.isBlank()) {
            return if (status.isSuccess()) {
                AppResult.Failure(
                    AppError.Unexpected(message = "Successful response had no body for ${status.value}"),
                )
            } else {
                AppResult.Failure(ApiErrorMapper.mapHttpFailure(status, rawBody, json))
            }
        }

        val root = runCatching { json.parseToJsonElement(rawBody).jsonObject }.getOrElse { throwable ->
            if (throwable is CancellationException) throw throwable
            return AppResult.Failure(
                AppError.Serialization(message = throwable.message ?: "Failed to decode API envelope"),
            )
        }

        val envelope = runCatching { parseEnvelope(root) }.getOrElse { throwable ->
            if (throwable is CancellationException) throw throwable
            return AppResult.Failure(
                AppError.Serialization(message = throwable.message ?: "Failed to decode API envelope"),
            )
        }

        val errorDataJson = ApiErrorMapper.extractErrorDataJson(root["data"], json)

        if (ApiErrorMapper.isTransportSuccess(status, envelope)) {
            val dataElement = root["data"]
            if (dataElement == null || dataElement is JsonNull) {
                return AppResult.Failure(
                    AppError.Unexpected(message = "Successful envelope missing data payload"),
                )
            }
            val data = runCatching {
                json.decodeFromJsonElement(dataSerializer, dataElement)
            }.getOrElse { throwable ->
                if (throwable is CancellationException) throw throwable
                return AppResult.Failure(
                    AppError.Serialization(message = throwable.message ?: "Failed to decode response data"),
                )
            }
            return AppResult.Success(data)
        }

        return AppResult.Failure(
            ApiErrorMapper.mapEnvelopeFailure(
                httpStatus = status,
                envelope = envelope,
                errorDataJson = errorDataJson,
            ),
        )
    }

    private fun parseEnvelope(root: JsonObject): ApiEnvelope<JsonElement> {
        val errors = root["errors"]?.jsonArray.orEmpty().mapNotNull { element ->
            runCatching {
                val obj = element.jsonObject
                ApiErrorDto(
                    reason = obj.getValue("reason").jsonPrimitive.content,
                    messages = obj["messages"]?.jsonArray?.map { it.jsonPrimitive.content }.orEmpty(),
                )
            }.getOrNull()
        }

        return ApiEnvelope(
            success = root["success"]?.jsonPrimitive?.booleanOrNull ?: false,
            message = root["message"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            code = root["code"]?.jsonPrimitive?.intOrNull ?: 0,
            data = root["data"],
            errors = errors,
        )
    }
}
