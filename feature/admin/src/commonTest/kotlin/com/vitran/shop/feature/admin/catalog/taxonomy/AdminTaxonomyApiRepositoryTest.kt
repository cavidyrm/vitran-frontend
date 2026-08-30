package com.vitran.shop.feature.admin.catalog.taxonomy

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.admin.catalog.taxonomy.data.remote.AdminTaxonomyApi
import com.vitran.shop.feature.admin.catalog.taxonomy.data.remote.buildCategoryIconMultipart
import com.vitran.shop.feature.admin.catalog.taxonomy.data.remote.buildTaxonomyImportMultipart
import com.vitran.shop.feature.admin.catalog.taxonomy.data.repository.DefaultAdminTaxonomyRepository
import com.vitran.shop.feature.admin.createAdminTestClient
import com.vitran.shop.feature.admin.createAdminTestExecutor
import com.vitran.shop.feature.admin.jsonResponse
import com.vitran.shop.feature.taxonomy.domain.model.AttributeSlug
import com.vitran.shop.feature.taxonomy.domain.model.AttributeValueSlug
import com.vitran.shop.feature.taxonomy.domain.model.CategoryDetails
import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.feature.taxonomy.domain.repository.TaxonomyRepository
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.copyTo
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.writeFully
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.io.readByteArray

class AdminTaxonomyApiRepositoryTest {
    private val environment = ApiEnvironment(origin = "http://localhost:8080")

    @Test
    fun importTaxonomy_usesBothMultipartKeys_andTimeoutIsNotRetried() = runTest {
        val categories = SelectedFile.fromBytes("categories.csv", byteArrayOf(1), "text/csv")
        val attributes = SelectedFile.fromBytes("attributes.json", byteArrayOf(2), "application/json")
        val bodyText = readBodyText(buildTaxonomyImportMultipart(categories, attributes))
        assertTrue(bodyText.hasPart("categories"), bodyText)
        assertTrue(bodyText.hasPart("attributes"), bodyText)

        var attempts = 0
        val engine =
            MockEngine { request ->
                attempts += 1
                assertEquals(HttpMethod.Post, request.method)
                assertEquals("/api/v1/admin/taxonomy/import", request.url.encodedPath)
                throw HttpRequestTimeoutException("simulated timeout", 1_000)
            }
        val api =
            AdminTaxonomyApi(
                createAdminTestClient(engine, maxRetryCount = 3),
                environment,
                createAdminTestExecutor(),
            )

        assertIs<AppResult.Failure>(api.importTaxonomy(categories, attributes))
        assertEquals(1, attempts)
    }

    @Test
    fun categoryIcon_usesImageMultipartKey() = runTest {
        val body =
            readBodyText(
                buildCategoryIconMultipart(
                    SelectedFile.fromBytes("/tmp/icon.png", byteArrayOf(1, 2), "image/png"),
                ),
            )

        assertTrue(body.hasPart("image"), body)
        assertTrue(body.contains("filename=icon.png") || body.contains("filename=\"icon.png\""), body)
    }

    @Test
    fun attributeAndValueNames_useSlugPathsAndExactBodies() = runTest {
        var requestCount = 0
        val engine =
            MockEngine { request ->
                requestCount += 1
                assertEquals(HttpMethod.Patch, request.method)
                when (requestCount) {
                    1 -> {
                        assertEquals("/api/v1/admin/attributes/color/name", request.url.encodedPath)
                        assertEquals("""{"name":"رنگ"}""", (request.body as TextContent).text)
                    }
                    else -> {
                        assertEquals("/api/v1/admin/values/red/name", request.url.encodedPath)
                        assertEquals("""{"name":"قرمز"}""", (request.body as TextContent).text)
                    }
                }
                jsonResponse(HttpStatusCode.OK, emptyEnvelope)
            }
        val api = AdminTaxonomyApi(createAdminTestClient(engine), environment, createAdminTestExecutor())

        assertIs<AppResult.Success<*>>(api.renameAttribute(AttributeSlug("color"), "رنگ"))
        assertIs<AppResult.Success<*>>(api.renameValue(AttributeValueSlug("red"), "قرمز"))
        assertEquals(2, requestCount)
    }

    @Test
    fun successfulMutation_invalidatesTaxonomyCache() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, emptyEnvelope) }
        val fakeTaxonomy = FakeTaxonomyRepository()
        val api = AdminTaxonomyApi(createAdminTestClient(engine), environment, createAdminTestExecutor())
        val repository = DefaultAdminTaxonomyRepository(api, fakeTaxonomy)

        assertIs<AppResult.Success<*>>(repository.renameCategory(CategorySlug("shirts"), "پیراهن"))
        assertEquals(1, fakeTaxonomy.invalidateCalls)
    }

    private class FakeTaxonomyRepository : TaxonomyRepository {
        var invalidateCalls = 0

        override suspend fun getCategoryTree(forceRefresh: Boolean): AppResult<List<CategoryNode>> =
            AppResult.Success(emptyList())

        override suspend fun getCategory(
            slug: CategorySlug,
            forceRefresh: Boolean,
        ): AppResult<CategoryDetails> = AppResult.Failure(AppError.NotFound())

        override suspend fun invalidateTaxonomy() {
            invalidateCalls += 1
        }
    }

    private fun String.hasPart(key: String): Boolean =
        contains("name=$key") || contains("name=\"$key\"")

    private companion object {
        val emptyEnvelope = """
            {"success":true,"message":"ok","code":1,"data":{},"errors":[]}
        """.trimIndent()
    }
}

private suspend fun readBodyText(body: OutgoingContent): String {
    val channel = ByteChannel(autoFlush = true)
    when (body) {
        is OutgoingContent.WriteChannelContent -> {
            body.writeTo(channel)
            channel.flushAndClose()
        }
        is OutgoingContent.ByteArrayContent -> {
            channel.writeFully(body.bytes())
            channel.flushAndClose()
        }
        is OutgoingContent.ReadChannelContent -> {
            body.readFrom().copyTo(channel)
            channel.flushAndClose()
        }
        else -> {
            channel.flushAndClose()
            return "unsupported-body:${body::class.simpleName}"
        }
    }
    return channel.readRemaining().readByteArray().decodeToString()
}
