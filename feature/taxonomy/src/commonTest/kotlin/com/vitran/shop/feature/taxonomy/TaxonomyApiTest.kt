package com.vitran.shop.feature.taxonomy

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.taxonomy.data.remote.TaxonomyApi
import com.vitran.shop.feature.taxonomy.data.remote.dto.CategoriesDataDto
import com.vitran.shop.feature.taxonomy.data.remote.dto.CategoryDataDto
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class TaxonomyApiTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createTaxonomyTestExecutor()

    @Test
    fun getCategoryTree_usesPublicPath_andNoAuthorizationHeader() = runTest {
        var authorization: String? = null
        val api = TaxonomyApi(
            client = createTaxonomyTestClient(
                MockEngine { request ->
                    authorization = request.headers[HttpHeaders.Authorization]
                    assertEquals("/api/v1/categories", request.url.encodedPath)
                    jsonResponse(HttpStatusCode.OK, categoryTreeEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getCategoryTree()

        assertIs<AppResult.Success<CategoriesDataDto>>(result)
        assertNull(authorization)
        assertEquals(2, result.value.categories.size)
        assertEquals("aa-1-1-1-1", result.value.categories.first().slug)
    }

    @Test
    fun getCategory_usesDirectSlugPath() = runTest {
        val api = TaxonomyApi(
            client = createTaxonomyTestClient(
                MockEngine { request ->
                    assertEquals("/api/v1/categories/aa-1-2-3-4", request.url.encodedPath)
                    jsonResponse(HttpStatusCode.OK, categoryDetailEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getCategory(CategorySlug("aa-1-2-3-4"))

        assertIs<AppResult.Success<CategoryDataDto>>(result)
        assertEquals("T-Shirts", result.value.category.title)
        assertNull(result.value.category.name)
    }
}
