package com.vitran.shop.feature.admin.moderation

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.admin.createAdminTestClient
import com.vitran.shop.feature.admin.createAdminTestExecutor
import com.vitran.shop.feature.admin.jsonResponse
import com.vitran.shop.feature.admin.moderation.data.AdminModerationApi
import com.vitran.shop.feature.admin.moderation.data.DefaultAdminModerationRepository
import com.vitran.shop.feature.engagement.comment.domain.model.ShopCommentId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.repository.ProductPublicCacheInvalidator
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopPublicCacheInvalidator
import com.vitran.shop.feature.seller.product.domain.model.ProductPublicationState
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AdminModerationTest {
    private val environment = ApiEnvironment(origin = "http://localhost:8080")

    @Test
    fun confirmEndpoints_usePatchPathsAndEmptyBodies_andInvalidatePublicCaches() = runTest {
        val paths = mutableListOf<String>()
        var shopInvalidated: ShopId? = null
        var productInvalidated: ProductId? = null
        val engine = MockEngine { request ->
            assertEquals(HttpMethod.Patch, request.method)
            assertFalse(request.body is TextContent)
            paths += request.url.encodedPath
            when {
                request.url.encodedPath.contains("/shops/") -> jsonResponse(HttpStatusCode.OK, shopEnvelope)
                request.url.encodedPath.contains("/products/") -> jsonResponse(HttpStatusCode.OK, productEnvelope)
                else -> jsonResponse(HttpStatusCode.OK, commentEnvelope)
            }
        }
        val api = AdminModerationApi(createAdminTestClient(engine), environment, createAdminTestExecutor())
        val repository = DefaultAdminModerationRepository(
            api,
            ShopPublicCacheInvalidator { shopInvalidated = it },
            ProductPublicCacheInvalidator { productInvalidated = it },
        )

        repository.confirmShop(ShopId(7))
        val product = assertIs<AppResult.Success<*>>(repository.confirmProduct(ProductId(8))).value
        val comment = assertIs<AppResult.Success<*>>(repository.confirmComment(ShopCommentId(9))).value

        assertEquals(listOf(
            "/api/v1/admin/shops/7/confirm",
            "/api/v1/admin/products/8/confirm",
            "/api/v1/admin/comments/9/confirm",
        ), paths)
        assertEquals(ShopId(7), shopInvalidated)
        assertEquals(ProductId(8), productInvalidated)
        assertIs<ProductPublicationState.ApprovedHidden>(
            (product as com.vitran.shop.feature.admin.moderation.domain.AdminProductDetails).publication,
        )
        assertFalse(product.active)
        assertEquals(ShopCommentId(9), (comment as com.vitran.shop.feature.admin.moderation.domain.ConfirmedAdminComment).id)
    }

    @Test
    fun getProduct_readsStoredFieldsAndSkipsNonObjectImages() = runTest {
        val engine = MockEngine { jsonResponse(HttpStatusCode.OK, richProductEnvelope) }
        val api = AdminModerationApi(createAdminTestClient(engine), environment, createAdminTestExecutor())
        val repository = DefaultAdminModerationRepository(
            api,
            ShopPublicCacheInvalidator {},
            ProductPublicCacheInvalidator {},
        )
        val product = assertIs<AppResult.Success<com.vitran.shop.feature.admin.moderation.domain.AdminProductDetails>>(
            repository.getProduct(ProductId(1)),
        ).value
        assertEquals("High quality widget", product.description)
        assertEquals(200000L, product.compareAtPriceAmount)
        assertEquals("blue-widget", product.slug)
        assertEquals("Blue Widget | Vitran", product.seoTitle)
        assertTrue(product.noindex)
        assertEquals(1, product.images.size)
        assertEquals("http://localhost:9000/cover.jpg", product.images.first().url)
        assertTrue(product.images.first().isMain)
        assertNull(product.images.firstOrNull { it.url.isBlank() })
    }

    private companion object {
        val shopEnvelope = envelope("""{"shop":{"id":7,"slug":"pending","active":false,"confirmed":true}}""")
        val richProductEnvelope = envelope(
            """{"product":{"id":1,"shop_id":1,"category_slug":"aa-1-2","title":"Blue Widget","description":"High quality widget","price":150000,"compare_at_price":200000,"active":false,"confirmed":false,"slug":"blue-widget","seo_title":"Blue Widget | Vitran","seo_description":"Meta","noindex":true,"main_image":{"id":2,"url":"http://localhost:9000/cover.jpg","sort_order":1,"is_main":true},"images":["skip-me",{"id":2,"url":"http://localhost:9000/cover.jpg","sort_order":1,"is_main":true}]}}""",
        )
        val productEnvelope = envelope(
            """{"product":{"id":8,"shop_id":7,"title":"کالا","active":false,"confirmed":true,"images":[]}}""",
        )
        val commentEnvelope = envelope(
            """{"comment":{"id":9,"shop_id":7,"user_id":3,"title":"نظر","confirmed":true}}""",
        )
        fun envelope(data: String) =
            """{"success":true,"message":"ok","code":1,"data":$data,"errors":[]}"""
    }
}
