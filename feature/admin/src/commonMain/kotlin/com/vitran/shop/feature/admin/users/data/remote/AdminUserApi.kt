package com.vitran.shop.feature.admin.users.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.admin.users.data.remote.dto.AdminUserDataDto
import com.vitran.shop.feature.admin.users.data.remote.dto.AdminUsersDataDto
import com.vitran.shop.feature.admin.users.data.remote.dto.UpdateAdminUserRequestDto
import com.vitran.shop.feature.admin.users.domain.model.AdminUserQuery
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class AdminUserApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getUsers(query: AdminUserQuery): AppResult<AdminUsersDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/admin/users")) {
                authMode(AuthMode.Required)
                query.role?.let { parameter("role", it) }
                query.phone?.let { parameter("phone", it) }
                query.isActive?.let { parameter("is_active", it) }
                parameter("page", query.page)
                parameter("per_page", query.perPage)
            }
        }

    suspend fun getUser(id: Long): AppResult<AdminUserDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/admin/users/$id")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun updateUser(
        id: Long,
        request: UpdateAdminUserRequestDto,
    ): AppResult<AdminUserDataDto> =
        executor.execute {
            client.patch(environment.apiUrl("/admin/users/$id")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
}
