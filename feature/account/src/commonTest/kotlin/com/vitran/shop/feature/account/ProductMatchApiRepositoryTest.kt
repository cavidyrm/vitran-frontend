package com.vitran.shop.feature.account

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.account.data.remote.ProductMatchApi
import com.vitran.shop.feature.account.data.repository.DefaultProductMatchRepository
import com.vitran.shop.feature.account.domain.model.PersonId
import com.vitran.shop.feature.account.domain.model.ProductMatch
import com.vitran.shop.feature.account.domain.model.ProductMatchReason
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ProductMatchApiRepositoryTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createAccountTestExecutor()

    @Test
    fun listMatches_requiresAuthAndMapsReason() = runTest {
        var authorization: String? = null
        val repository = DefaultProductMatchRepository(
            ProductMatchApi(
                client = createAccountTestClient(
                    MockEngine { request ->
                        authorization = request.headers[HttpHeaders.Authorization]
                        assertEquals(HttpMethod.Get, request.method)
                        assertEquals("20", request.url.parameters["limit"])
                        jsonResponse(HttpStatusCode.OK, matchesBody)
                    },
                ),
                environment = environment,
                executor = executor,
            ),
        )

        val result = repository.listMatches(limit = 20)

        assertEquals("Bearer test-token", authorization)
        assertIs<AppResult.Success<List<ProductMatch>>>(result)
        val match = result.value.single()
        assertEquals(10L, match.id)
        assertEquals(501L, match.productId)
        assertEquals(PersonId(3), match.personId)
        assertEquals("upper_body", match.slot)
        assertEquals(ProductMatchReason.New, match.reason)
        assertEquals("Jacket", match.product?.title)
        assertEquals(890000L, match.product?.priceAmount)
        assertEquals(1200000L, match.product?.compareAtPriceAmount)
    }
}

private val matchesBody = """
{
  "success": true,
  "message": "لیست پیشنهادهای شخصی‌سازی‌شده با موفقیت دریافت شد",
  "code": 1,
  "data": {
    "matches": [
      {
        "id": 10,
        "product_id": 501,
        "product": {
          "id": 501,
          "title": "Jacket",
          "price": 890000,
          "compare_at_price": 1200000
        },
        "person_id": 3,
        "slot": "upper_body",
        "reason": "new",
        "created_at": "2026-09-01T10:00:00Z"
      }
    ]
  },
  "errors": []
}
""".trimIndent()
