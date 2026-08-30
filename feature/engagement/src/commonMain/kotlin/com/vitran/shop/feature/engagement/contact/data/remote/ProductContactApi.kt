package com.vitran.shop.feature.engagement.contact.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.engagement.contact.data.remote.dto.ContactProductDataDto
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import io.ktor.client.HttpClient
import io.ktor.client.request.post

internal class ProductContactApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun contactProduct(
        productId: ProductId,
        sessionId: String,
    ): AppResult<ContactProductDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/products/${productId.value}/contact")) {
                authMode(AuthMode.Optional)
                url { parameters.append("session_id", sessionId) }
            }
        }
}
