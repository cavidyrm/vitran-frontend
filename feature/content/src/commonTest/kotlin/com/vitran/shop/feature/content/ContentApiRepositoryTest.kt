package com.vitran.shop.feature.content

import com.vitran.shop.core.database.createInMemoryVitranDatabase
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.content.data.remote.ContentApi
import com.vitran.shop.feature.content.data.repository.DefaultContentRepository
import com.vitran.shop.feature.content.domain.model.StaticPageSlug
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class ContentApiRepositoryTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")

    @Test
    fun publicEndpoints_decodeListAndSlugDetail_withoutAuthorization() = runTest {
        var requestCount = 0
        val engine = MockEngine { request ->
            requestCount += 1
            assertNull(request.headers[HttpHeaders.Authorization])
            when (request.url.encodedPath) {
                "/api/v1/static-pages" ->
                    jsonResponse(HttpStatusCode.OK, listEnvelope)
                "/api/v1/static-pages/slug/about-us" ->
                    jsonResponse(HttpStatusCode.OK, detailEnvelope)
                else -> error("Unexpected path: ${request.url.encodedPath}")
            }
        }
        val api = ContentApi(
            client = createContentTestClient(engine),
            environment = environment,
            executor = createContentTestExecutor(),
        )
        val repository = DefaultContentRepository(api, createInMemoryVitranDatabase())

        val list = assertIs<AppResult.Success<*>>(repository.getStaticPages()).value
        val page = assertIs<AppResult.Success<*>>(
            repository.getStaticPageBySlug(StaticPageSlug("about-us")),
        ).value

        assertEquals(1, (list as List<*>).size)
        assertEquals("درباره ما", (page as com.vitran.shop.feature.content.domain.model.StaticPage).title)
        assertEquals(2, requestCount)

        repository.getStaticPages()
        repository.getStaticPageBySlug(StaticPageSlug("about-us"))
        assertEquals(2, requestCount)

        repository.invalidate()
        repository.getStaticPages()
        assertEquals(3, requestCount)
    }

    private companion object {
        val listEnvelope = """
            {
              "success": true,
              "message": "ok",
              "code": 1,
              "data": {
                "static_pages": [
                  {
                    "id": 1,
                    "slug": "about-us",
                    "title": "درباره ما",
                    "active": true,
                    "sort_order": 2
                  }
                ]
              },
              "errors": []
            }
        """.trimIndent()

        val detailEnvelope = """
            {
              "success": true,
              "message": "ok",
              "code": 1,
              "data": {
                "static_page": {
                  "id": 1,
                  "slug": "about-us",
                  "title": "درباره ما",
                  "body": "<p>متن صفحه</p>",
                  "active": true,
                  "sort_order": 2
                }
              },
              "errors": []
            }
        """.trimIndent()
    }
}
