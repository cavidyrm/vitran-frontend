package com.vitran.shop.feature.admin.content.data

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.admin.content.domain.CreateStaticPageCommand
import com.vitran.shop.feature.admin.content.domain.UpdateStaticPageCommand
import com.vitran.shop.feature.content.domain.model.StaticPageId
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class AdminContentApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getPages(): AppResult<AdminStaticPagesDataDto> = executor.execute {
        client.get(environment.apiUrl("/admin/static-pages")) { authMode(AuthMode.Required) }
    }
    suspend fun getPage(id: StaticPageId): AppResult<AdminStaticPageDataDto> = executor.execute {
        client.get(environment.apiUrl("/admin/static-pages/${id.value}")) { authMode(AuthMode.Required) }
    }
    suspend fun create(command: CreateStaticPageCommand): AppResult<AdminStaticPageDataDto> = executor.execute {
        client.post(environment.apiUrl("/admin/static-pages")) {
            authMode(AuthMode.Required)
            contentType(ContentType.Application.Json)
            setBody(CreateStaticPageRequestDto(
                command.slug.value, command.title, command.bodyHtml.rawHtml, command.active, command.sortOrder,
            ))
        }
    }
    suspend fun update(command: UpdateStaticPageCommand): AppResult<AdminStaticPageDataDto> = executor.execute {
        client.patch(environment.apiUrl("/admin/static-pages/${command.id.value}")) {
            authMode(AuthMode.Required)
            contentType(ContentType.Application.Json)
            setBody(UpdateStaticPageRequestDto(
                command.slug?.value, command.title, command.bodyHtml?.rawHtml, command.active, command.sortOrder,
            ))
        }
    }
    suspend fun delete(id: StaticPageId): AppResult<Unit> = executor.executeEmpty {
        client.delete(environment.apiUrl("/admin/static-pages/${id.value}")) { authMode(AuthMode.Required) }
    }
}
