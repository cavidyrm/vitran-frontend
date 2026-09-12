package com.vitran.shop.feature.account.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.account.data.remote.dto.NotifySettingsDataDto
import com.vitran.shop.feature.account.data.remote.dto.PersonDataDto
import com.vitran.shop.feature.account.data.remote.dto.PersonWriteRequestDto
import com.vitran.shop.feature.account.data.remote.dto.PersonsDataDto
import com.vitran.shop.feature.account.data.remote.dto.SizingProfileDataDto
import com.vitran.shop.feature.account.data.remote.dto.UpdateNotifyRequestDto
import com.vitran.shop.feature.account.data.remote.dto.UpdateSizingRequestDto
import com.vitran.shop.feature.account.domain.model.PersonId
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class ProfileApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getSizingProfile(): AppResult<SizingProfileDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/me/profile/sizing")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun updateSizingProfile(request: UpdateSizingRequestDto): AppResult<SizingProfileDataDto> =
        executor.execute {
            client.put(environment.apiUrl("/me/profile/sizing")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun getNotifySettings(): AppResult<NotifySettingsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/me/profile/notify")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun updateNotifySettings(request: UpdateNotifyRequestDto): AppResult<NotifySettingsDataDto> =
        executor.execute {
            client.put(environment.apiUrl("/me/profile/notify")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun listPersons(): AppResult<PersonsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/me/persons")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun createPerson(request: PersonWriteRequestDto): AppResult<PersonDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/me/persons")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun updatePerson(id: PersonId, request: PersonWriteRequestDto): AppResult<PersonDataDto> =
        executor.execute {
            client.patch(environment.apiUrl("/me/persons/${id.value}")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun deletePerson(id: PersonId): AppResult<Unit> =
        executor.executeEmpty {
            client.delete(environment.apiUrl("/me/persons/${id.value}")) {
                authMode(AuthMode.Required)
            }
        }
}
