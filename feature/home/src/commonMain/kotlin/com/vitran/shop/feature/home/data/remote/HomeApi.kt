package com.vitran.shop.feature.home.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.home.data.HomeClientContext
import com.vitran.shop.feature.home.data.remote.dto.HomeDataDto
import com.vitran.shop.feature.home.data.remote.dto.HomeScreenDataDto
import com.vitran.shop.feature.home.data.remote.dto.HomeSectionDataDto
import com.vitran.shop.feature.location.domain.model.CityId
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class HomeApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
    private val clientContext: HomeClientContext = HomeClientContext.current(),
) {
    suspend fun getHome(cityId: CityId? = null): AppResult<HomeDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/home")) {
                authMode(AuthMode.Optional)
                cityId?.let { parameter("city_id", it.value) }
            }
        }

    suspend fun getHomeScreen(cityId: CityId? = null): AppResult<HomeScreenDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/home/screen")) {
                authMode(AuthMode.Optional)
                cityId?.let { parameter("city_id", it.value) }
                parameter("platform", clientContext.platform)
                parameter("app_version", clientContext.appVersion)
            }
        }

    /**
     * @param path `more.path` from a rail, for example `/home/sections/popular` (no `/api/v1`).
     * A 404 means this app's platform or version is not allowed to see the rail.
     */
    suspend fun getHomeSection(
        path: String,
        limit: Int,
        cursor: String?,
        cursorParam: String,
        cityId: CityId? = null,
    ): AppResult<HomeSectionDataDto> =
        executor.execute {
            client.get(environment.apiUrl(normalizeSectionPath(path))) {
                authMode(AuthMode.Optional)
                parameter("limit", limit)
                parameter("platform", clientContext.platform)
                parameter("app_version", clientContext.appVersion)
                cursor?.let { parameter(cursorParam.ifBlank { "cursor" }, it) }
                cityId?.let { parameter("city_id", it.value) }
            }
        }
}

internal fun normalizeSectionPath(path: String): String {
    val withoutQuery = path.substringBefore("?")
    val stripped = withoutQuery.removePrefix("/api/v1")
    return if (stripped.startsWith("/")) stripped else "/$stripped"
}
