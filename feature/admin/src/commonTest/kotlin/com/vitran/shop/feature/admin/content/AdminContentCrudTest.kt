package com.vitran.shop.feature.admin.content

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.admin.content.data.AdminContentApi
import com.vitran.shop.feature.admin.content.data.DefaultAdminContentRepository
import com.vitran.shop.feature.admin.content.domain.CreateStaticPageCommand
import com.vitran.shop.feature.admin.content.domain.UpdateStaticPageCommand
import com.vitran.shop.feature.admin.createAdminTestClient
import com.vitran.shop.feature.admin.createAdminTestExecutor
import com.vitran.shop.feature.admin.jsonResponse
import com.vitran.shop.feature.content.domain.model.HtmlContent
import com.vitran.shop.feature.content.domain.model.StaticPageId
import com.vitran.shop.feature.content.domain.model.StaticPageSlug
import com.vitran.shop.feature.content.domain.repository.ContentCacheInvalidator
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AdminContentCrudTest {
    @Test
    fun crud_usesAdminPaths_andInvalidatesPublicContentAfterMutations() = runTest {
        val requests = mutableListOf<Pair<HttpMethod, String>>()
        var invalidations = 0
        val engine = MockEngine { request ->
            requests += request.method to request.url.encodedPath
            when {
                request.method == HttpMethod.Get && request.url.encodedPath.endsWith("/static-pages") ->
                    jsonResponse(HttpStatusCode.OK, listEnvelope)
                request.method == HttpMethod.Delete ->
                    jsonResponse(HttpStatusCode.OK, """{"success":true,"message":"deleted","code":1,"data":{},"errors":[]}""")
                else -> jsonResponse(HttpStatusCode.OK, detailEnvelope)
            }
        }
        val api = AdminContentApi(
            createAdminTestClient(engine), ApiEnvironment("http://localhost:8080"), createAdminTestExecutor(),
        )
        val repository = DefaultAdminContentRepository(api, ContentCacheInvalidator { invalidations++ })

        assertEquals(1, assertIs<AppResult.Success<*>>(repository.getPages()).value.let { it as List<*> }.size)
        repository.getPage(StaticPageId(1))
        repository.create(CreateStaticPageCommand(StaticPageSlug("about"), "درباره", HtmlContent("<p>x</p>"), false, 2))
        repository.update(UpdateStaticPageCommand(StaticPageId(1), title = "ویرایش"))
        repository.delete(StaticPageId(1))

        assertEquals(3, invalidations)
        assertEquals(listOf(
            HttpMethod.Get to "/api/v1/admin/static-pages",
            HttpMethod.Get to "/api/v1/admin/static-pages/1",
            HttpMethod.Post to "/api/v1/admin/static-pages",
            HttpMethod.Patch to "/api/v1/admin/static-pages/1",
            HttpMethod.Delete to "/api/v1/admin/static-pages/1",
        ), requests)
    }

    private companion object {
        val listEnvelope =
            """{"success":true,"message":"ok","code":1,"data":{"static_pages":[{"id":1,"slug":"about","title":"درباره","active":false,"sort_order":2}]},"errors":[]}"""
        val detailEnvelope =
            """{"success":true,"message":"ok","code":1,"data":{"static_page":{"id":1,"slug":"about","title":"درباره","body":"<p>x</p>","active":false,"sort_order":2}},"errors":[]}"""
    }
}
