package com.vitran.shop.feature.taxonomy.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.taxonomy.data.remote.dto.CategoriesDataDto
import com.vitran.shop.feature.taxonomy.data.remote.dto.CategoryDataDto
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.encodeURLPathPart

internal class TaxonomyApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getCategoryTree(): AppResult<CategoriesDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/categories")) {
                authMode(AuthMode.None)
            }
        }

    suspend fun getCategory(slug: CategorySlug): AppResult<CategoryDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/categories/${slug.value.encodeURLPathPart()}")) {
                authMode(AuthMode.None)
            }
        }
}
