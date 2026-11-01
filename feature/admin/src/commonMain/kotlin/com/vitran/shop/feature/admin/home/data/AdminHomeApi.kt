package com.vitran.shop.feature.admin.home.data

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.admin.home.domain.SaveAdminHomeBannerCommand
import com.vitran.shop.feature.admin.home.domain.SaveAdminHomeSectionCommand
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class AdminHomeApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun listSections(): AppResult<AdminHomeSectionsDataDto> = executor.execute {
        client.get(environment.apiUrl("/admin/home/sections")) {
            authMode(AuthMode.Required)
        }
    }

    suspend fun getSection(id: Long): AppResult<AdminHomeSectionDataDto> = executor.execute {
        client.get(environment.apiUrl("/admin/home/sections/$id")) {
            authMode(AuthMode.Required)
        }
    }

    suspend fun createSection(command: SaveAdminHomeSectionCommand): AppResult<AdminHomeSectionDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/admin/home/sections")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(command.toCreateDto())
            }
        }

    suspend fun updateSection(id: Long, command: SaveAdminHomeSectionCommand): AppResult<AdminHomeSectionDataDto> =
        executor.execute {
            client.patch(environment.apiUrl("/admin/home/sections/$id")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(command.toUpdateDto())
            }
        }

    suspend fun deleteSection(id: Long): AppResult<Unit> = executor.executeEmpty {
        client.delete(environment.apiUrl("/admin/home/sections/$id")) {
            authMode(AuthMode.Required)
        }
    }

    suspend fun getBanner(id: Long): AppResult<AdminHomeBannerDataDto> = executor.execute {
        client.get(environment.apiUrl("/admin/home/banners/$id")) {
            authMode(AuthMode.Required)
        }
    }

    suspend fun listBanners(sectionId: Long): AppResult<AdminHomeBannersDataDto> = executor.execute {
        client.get(environment.apiUrl("/admin/home/sections/$sectionId/banners")) {
            authMode(AuthMode.Required)
        }
    }

    suspend fun createBanner(
        sectionId: Long,
        command: SaveAdminHomeBannerCommand,
    ): AppResult<AdminHomeBannerDataDto> = executor.execute {
        client.post(environment.apiUrl("/admin/home/sections/$sectionId/banners")) {
            authMode(AuthMode.Required)
            contentType(ContentType.Application.Json)
            setBody(command.toRequestDto())
        }
    }

    suspend fun updateBanner(id: Long, command: SaveAdminHomeBannerCommand): AppResult<AdminHomeBannerDataDto> =
        executor.execute {
            client.patch(environment.apiUrl("/admin/home/banners/$id")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(command.toRequestDto())
            }
        }

    suspend fun deleteBanner(id: Long): AppResult<Unit> = executor.executeEmpty {
        client.delete(environment.apiUrl("/admin/home/banners/$id")) {
            authMode(AuthMode.Required)
        }
    }
}
