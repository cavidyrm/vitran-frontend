package com.vitran.shop.feature.marketplace.product

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.marketplace.createMarketplaceTestClient
import com.vitran.shop.feature.marketplace.createMarketplaceTestExecutor
import com.vitran.shop.feature.marketplace.jsonResponse
import com.vitran.shop.feature.marketplace.product.data.remote.dto.ProductsDataDto
import com.vitran.shop.feature.marketplace.product.data.remote.PublicProductApi
import com.vitran.shop.feature.marketplace.product.data.remote.dto.ProductDataDto
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.query.ProductSearchQuery
import com.vitran.shop.feature.marketplace.productDetailEnvelope
import com.vitran.shop.feature.marketplace.productListEnvelope
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class PublicProductApiTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createMarketplaceTestExecutor()

    @Test
    fun getProducts_usesPublicPath_andNoAuthorizationHeader() = runTest {
        var authorization: String? = "present"
        val api = PublicProductApi(
            client = createMarketplaceTestClient(
                MockEngine { request ->
                    authorization = request.headers[HttpHeaders.Authorization]
                    assertEquals("/api/v1/products", request.url.encodedPath)
                    assertEquals("20", request.url.parameters["per_page"])
                    jsonResponse(HttpStatusCode.OK, productListEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getProducts(
            com.vitran.shop.feature.marketplace.product.domain.query.ProductBrowseQuery(),
        )

        assertIs<AppResult.Success<ProductsDataDto>>(result)
        assertNull(authorization)
    }

    @Test
    fun searchProducts_encodesQueryParameter() = runTest {
        val api = PublicProductApi(
            client = createMarketplaceTestClient(
                MockEngine { request ->
                    assertEquals("widget", request.url.parameters["q"])
                    jsonResponse(HttpStatusCode.OK, productListEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.searchProducts(ProductSearchQuery(query = "widget"))

        assertIs<AppResult.Success<ProductsDataDto>>(result)
    }

    @Test
    fun getProductById_mapsDetailWrapper() = runTest {
        val api = PublicProductApi(
            client = createMarketplaceTestClient(
                MockEngine { request ->
                    assertEquals("/api/v1/products/1", request.url.encodedPath)
                    jsonResponse(HttpStatusCode.OK, productDetailEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getProductById(ProductId(1))

        assertIs<AppResult.Success<ProductDataDto>>(result)
        assertEquals("Blue Widget", result.value.product.title)
    }
}
