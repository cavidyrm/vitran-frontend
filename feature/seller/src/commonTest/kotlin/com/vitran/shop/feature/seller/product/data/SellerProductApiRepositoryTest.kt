package com.vitran.shop.feature.seller.product.data

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.client.createHttpClient
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.NetworkConfig
import com.vitran.shop.core.network.config.NetworkDiagnosticsConfig
import com.vitran.shop.core.network.logging.NoOpNetworkLogger
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.core.platform.file.safeFileName
import com.vitran.shop.feature.marketplace.common.data.serializer.FlexibleCategorySlugSerializer
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.createSellerProductRepository
import com.vitran.shop.feature.seller.createSellerTestExecutor
import com.vitran.shop.feature.seller.hasAuthBearer
import com.vitran.shop.feature.seller.jsonResponse
import com.vitran.shop.feature.seller.product.data.remote.SellerProductApi
import com.vitran.shop.feature.seller.product.data.remote.dto.SellerProductDto
import com.vitran.shop.feature.seller.product.data.state.SellerProductStateStore
import com.vitran.shop.feature.seller.product.domain.model.CreateProductCommand
import com.vitran.shop.feature.seller.product.domain.model.ProductImageId
import com.vitran.shop.feature.seller.product.domain.model.ProductPublicationState
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.model.SellerProductImage
import com.vitran.shop.feature.seller.product.domain.model.UpdateProductCommand
import com.vitran.shop.feature.seller.product.domain.query.SellerProductActiveFilter
import com.vitran.shop.feature.seller.product.domain.query.SellerProductListQuery
import com.vitran.shop.feature.seller.sellerProductCreatePendingBody
import com.vitran.shop.feature.seller.sellerProductDeleteImageBody
import com.vitran.shop.feature.seller.sellerProductDeleteOkBody
import com.vitran.shop.feature.seller.sellerProductDetailPendingBody
import com.vitran.shop.feature.seller.sellerProductListBody
import com.vitran.shop.feature.seller.sellerProductSetActiveLiveBody
import com.vitran.shop.feature.seller.sellerProductUpdateReapprovalBody
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.copyTo
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.writeFully
import com.vitran.shop.feature.seller.product.data.remote.buildCreateMultipart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.io.readByteArray
import kotlinx.serialization.json.JsonPrimitive

class SellerProductApiRepositoryTest {

    @Test
    fun listMyProducts_filtersAndAuth() = runTest {
        val engine =
            MockEngine { request ->
                assertEquals(HttpMethod.Get, request.method)
                assertTrue(request.hasAuthBearer("OLD_ACCESS"))
                assertEquals("1", request.url.parameters["shop_id"])
                assertEquals("false", request.url.parameters["active"])
                assertEquals("aa-1-2-3-4", request.url.parameters["category_slug"])
                jsonResponse(HttpStatusCode.OK, sellerProductListBody)
            }
        val (repo, _) = createSellerProductRepository(engine)
        val result =
            repo.getProducts(
                SellerProductListQuery(
                    shopId = ShopId(1),
                    activeFilter = SellerProductActiveFilter.Inactive,
                    categorySlug = CategorySlug("aa-1-2-3-4"),
                ),
            )
        assertIs<AppResult.Success<*>>(result)
        val page = (result as AppResult.Success).value
        assertEquals(1, page.items.size)
        assertEquals(ProductPublicationState.PendingApproval, page.items.first().publicationState)
    }

    @Test
    fun getProduct_pendingLoads() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, sellerProductDetailPendingBody) }
        val (repo, _) = createSellerProductRepository(engine)
        val result = repo.getProduct(ProductId(1))
        assertIs<AppResult.Success<*>>(result)
        assertEquals(
            ProductPublicationState.PendingApproval,
            (result as AppResult.Success).value.publicationState,
        )
    }

    @Test
    fun createProduct_multipartFields_andResponseAuthority() = runTest {
        var capturedContentType: ContentType? = null
        val engine =
            MockEngine { request ->
                assertEquals(HttpMethod.Post, request.method)
                assertTrue(request.url.encodedPath.endsWith("/seller/shops/1/products"))
                assertTrue(request.hasAuthBearer("OLD_ACCESS"))
                capturedContentType = request.body.contentType
                jsonResponse(HttpStatusCode.Created, sellerProductCreatePendingBody)
            }
        val command =
            CreateProductCommand(
                shopId = ShopId(1),
                title = "Blue Widget",
                description = "High quality widget",
                priceAmount = 150000,
                category = CategorySlug("1"),
                desiredActive = true,
                images =
                    listOf(
                        SelectedFile.fromBytes("photo.jpg", byteArrayOf(1, 2, 3), "image/jpeg"),
                        SelectedFile.fromBytes("/tmp/path/second.png", byteArrayOf(4, 5), "image/png"),
                    ),
            )
        val bodyText = readBodyText(buildCreateMultipart(command))
        val (repo, store) = createSellerProductRepository(engine)
        val result = repo.createProduct(command)
        assertIs<AppResult.Success<*>>(result)
        val details = (result as AppResult.Success).value
        assertEquals(false, details.active)
        assertEquals(false, details.confirmed)
        assertEquals(ProductPublicationState.PendingApproval, details.publicationState)
        assertEquals(CategorySlug("1"), details.categorySlug)

        assertNotNullMultipart(capturedContentType)
        assertNotNullMultipart(buildCreateMultipart(command).contentType)
        assertTrue(
            bodyText.contains("name=title") || bodyText.contains("name=\"title\""),
            bodyText.take(240),
        )
        assertTrue(bodyText.contains("Blue Widget"))
        assertTrue(bodyText.contains("name=category_slug") || bodyText.contains("name=\"category_slug\""))
        assertTrue(bodyText.contains("name=price") || bodyText.contains("name=\"price\""))
        assertTrue(bodyText.contains("150000"))
        assertTrue(bodyText.contains("name=description") || bodyText.contains("name=\"description\""))
        assertTrue(bodyText.contains("name=active") || bodyText.contains("name=\"active\""))
        assertTrue(bodyText.contains("true"))
        assertTrue(bodyText.contains("name=images") || bodyText.contains("name=\"images\""))
        assertTrue(
            bodyText.contains("filename=photo.jpg") || bodyText.contains("filename=\"photo.jpg\""),
        )
        assertTrue(
            bodyText.contains("filename=second.png") || bodyText.contains("filename=\"second.png\""),
        )
        assertFalse(bodyText.contains("/tmp/path"))
        assertTrue(bodyText.contains("image/jpeg") || bodyText.contains("Content-Type: image/jpeg"))

        assertEquals(1, store.summaries.value.size)
    }

    @Test
    fun createProduct_noAutoRetryOnTransportFailure() = runTest {
        var attempts = 0
        val engine =
            MockEngine {
                attempts += 1
                error("simulated transport failure")
            }
        val client =
            createHttpClient(
                config =
                    NetworkConfig(
                        apiEnvironment = ApiEnvironment(origin = "http://localhost:8080"),
                        diagnostics = NetworkDiagnosticsConfig(enableHttpLogging = false),
                        maxRetryCount = 3,
                    ),
                json = createNetworkJson(),
                sessionAuthCoordinator =
                    object : com.vitran.shop.core.session.auth.SessionAuthCoordinator {
                        override suspend fun resolveAccessToken(authMode: com.vitran.shop.core.domain.auth.AuthMode) =
                            AppResult.Success("OLD_ACCESS")

                        override suspend fun handleUnauthorizedResponse(
                            authMode: com.vitran.shop.core.domain.auth.AuthMode,
                            retryOnce: suspend () -> io.ktor.client.statement.HttpResponse,
                        ) = AppResult.Failure(com.vitran.shop.core.domain.error.AppError.Authentication.Unauthorized())
                    },
                networkLogger = NoOpNetworkLogger,
                engine = engine,
            )
        val api = SellerProductApi(client, ApiEnvironment(origin = "http://localhost:8080"), createSellerTestExecutor())
        val result =
            api.createProduct(
                CreateProductCommand(
                    shopId = ShopId(1),
                    title = "X",
                    description = "",
                    priceAmount = 1,
                    category = CategorySlug("1"),
                    desiredActive = false,
                ),
            )
        assertIs<AppResult.Failure>(result)
        assertEquals(1, attempts)
    }

    @Test
    fun categorySlug_flexibleDecode() {
        assertEquals("1", FlexibleCategorySlugSerializer.decodeElement(JsonPrimitive(1)))
        assertEquals("aa-1", FlexibleCategorySlugSerializer.decodeElement(JsonPrimitive("aa-1")))
        val json = createNetworkJson()
        val dto =
            json.decodeFromString(
                SellerProductDto.serializer(),
                """{"id":1,"shop_id":1,"category_slug":1,"title":"T","active":false,"confirmed":false}""",
            )
        assertEquals("1", dto.categorySlug)
    }

    @Test
    fun updateProduct_reapprovalUsesResponseState() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, sellerProductUpdateReapprovalBody) }
        val (repo, store) = createSellerProductRepository(engine)
        store.putDetails(
            SellerProductDetails(
                id = ProductId(1),
                shopId = ShopId(1),
                categorySlug = CategorySlug("1"),
                title = "Blue Widget",
                description = null,
                priceAmount = 150000,
                active = true,
                confirmed = true,
                images = emptyList(),
                createdAt = null,
                updatedAt = null,
            ),
        )
        val result =
            repo.updateProduct(
                UpdateProductCommand(
                    productId = ProductId(1),
                    title = "Blue Widget Pro",
                    desiredActive = true,
                ),
            )
        assertIs<AppResult.Success<*>>(result)
        assertEquals(
            ProductPublicationState.PendingApproval,
            (result as AppResult.Success).value.publicationState,
        )
    }

    @Test
    fun setProductActive_publishSuccess() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, sellerProductSetActiveLiveBody) }
        val (repo, store) = createSellerProductRepository(engine)
        store.putDetails(
            SellerProductDetails(
                id = ProductId(1),
                shopId = ShopId(1),
                categorySlug = null,
                title = "Blue Widget",
                description = null,
                priceAmount = 1,
                active = false,
                confirmed = true,
                images = emptyList(),
                createdAt = null,
                updatedAt = null,
            ),
        )
        val result = repo.setProductActive(ProductId(1), active = true)
        assertIs<AppResult.Success<*>>(result)
        assertEquals(ProductPublicationState.Live, (result as AppResult.Success).value.publicationState)
    }

    @Test
    fun deleteProduct_removesFromStore() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, sellerProductDeleteOkBody) }
        val (repo, store) = createSellerProductRepository(engine)
        store.upsertSummary(
            com.vitran.shop.feature.seller.product.domain.model.SellerProductSummary(
                id = ProductId(1),
                shopId = ShopId(1),
                title = "Blue Widget",
                active = false,
                confirmed = false,
            ),
        )
        val result = repo.deleteProduct(ProductId(1))
        assertIs<AppResult.Success<*>>(result)
        assertTrue(store.summaries.value.isEmpty())
    }

    @Test
    fun deleteImage_updatesLocalImages() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, sellerProductDeleteImageBody) }
        val (repo, store) = createSellerProductRepository(engine)
        store.putDetails(
            SellerProductDetails(
                id = ProductId(1),
                shopId = ShopId(1),
                categorySlug = null,
                title = "Blue Widget",
                description = null,
                priceAmount = 1,
                active = false,
                confirmed = false,
                images =
                    listOf(
                        SellerProductImage(ProductImageId(9), "http://x", 0),
                    ),
                createdAt = null,
                updatedAt = null,
            ),
        )
        val result = repo.deleteProductImage(ProductId(1), ProductImageId(9))
        assertIs<AppResult.Success<*>>(result)
        assertTrue((result as AppResult.Success).value.images.isEmpty())
    }

    @Test
    fun safeFileName_stripsPath() {
        assertEquals("photo.jpg", safeFileName("/Users/me/photos/photo.jpg"))
        assertEquals("photo.jpg", safeFileName("C:\\Users\\me\\photo.jpg"))
    }

    @Test
    fun logout_clearsSellerProductStore() = runTest {
        val listeners = mutableListOf<com.vitran.shop.core.session.repository.SessionInvalidationListener>()
        val store = SellerProductStateStore(listeners)
        store.upsertSummary(
            com.vitran.shop.feature.seller.product.domain.model.SellerProductSummary(
                id = ProductId(1),
                shopId = ShopId(1),
                title = "X",
                active = false,
                confirmed = false,
            ),
        )
        listeners.single().onSessionInvalidated()
        assertTrue(store.summaries.value.isEmpty())
    }
}

private fun assertNotNullMultipart(contentType: ContentType?) {
    assertTrue(contentType != null, "contentType was null")
    val type = contentType!!
    assertTrue(
        type.match(ContentType.MultiPart.FormData) ||
            type.contentType.equals("multipart", ignoreCase = true),
        "Expected multipart, was $type",
    )
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
