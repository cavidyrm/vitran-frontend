package com.vitran.shop.feature.account.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.core.platform.file.safeFileName
import com.vitran.shop.feature.account.data.remote.dto.GetCurrentUserDataDto
import com.vitran.shop.feature.account.data.remote.dto.UpdateProfileDataDto
import com.vitran.shop.feature.account.data.remote.dto.UpdateProfileRequestDto
import com.vitran.shop.feature.account.data.remote.dto.UsernameCheckDataDto
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

internal class AccountApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getCurrentUser(): AppResult<GetCurrentUserDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/auth/me")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun checkUsername(username: String): AppResult<UsernameCheckDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/auth/check-username")) {
                authMode(AuthMode.Required)
                parameter("username", username)
            }
        }

    suspend fun uploadAvatar(image: SelectedFile): AppResult<UpdateProfileDataDto> {
        val body = buildProfileAvatarMultipart(image)
        return executor.execute {
            client.post(environment.apiUrl("/auth/profile/avatar")) {
                authMode(AuthMode.Required)
                setBody(body)
            }
        }
    }

    suspend fun updateProfile(request: UpdateProfileRequestDto): AppResult<UpdateProfileDataDto> =
        executor.execute {
            client.put(environment.apiUrl("/auth/profile")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
}

internal suspend fun buildProfileAvatarMultipart(image: SelectedFile): MultiPartFormDataContent {
    val part = image.preparePart()
    return MultiPartFormDataContent(formData { appendFile("image", part) })
}

private data class PreparedFilePart(
    val fileName: String,
    val contentType: ContentType,
    val bytes: ByteArray,
)

private suspend fun SelectedFile.preparePart() =
    PreparedFilePart(
        fileName = safeFileName(name),
        contentType = contentType?.let { ContentType.parse(it) } ?: ContentType.Application.OctetStream,
        bytes = readBytes(),
    )

private fun FormBuilder.appendFile(key: String, part: PreparedFilePart) {
    append(
        key = key,
        value = part.bytes,
        headers =
            Headers.build {
                append(HttpHeaders.ContentType, part.contentType.toString())
                append(HttpHeaders.ContentDisposition, "filename=\"${part.fileName}\"")
            },
    )
}
