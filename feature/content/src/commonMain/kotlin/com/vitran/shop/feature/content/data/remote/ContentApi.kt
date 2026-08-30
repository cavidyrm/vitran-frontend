package com.vitran.shop.feature.content.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.content.data.remote.dto.StaticPageDataDto
import com.vitran.shop.feature.content.data.remote.dto.StaticPagesDataDto
import com.vitran.shop.feature.content.domain.model.StaticPageSlug
import io.ktor.client.HttpClient
import io.ktor.client.request.get

internal class ContentApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getStaticPages(): AppResult<StaticPagesDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/static-pages")) {
                authMode(AuthMode.None)
            }
        }

    suspend fun getStaticPageBySlug(slug: StaticPageSlug): AppResult<StaticPageDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/static-pages/slug/${slug.value}")) {
                authMode(AuthMode.None)
            }
        }
}
