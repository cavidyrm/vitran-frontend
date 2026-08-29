package com.vitran.shop.feature.marketplace.product.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.marketplace.common.data.remote.appendProductBrowseQuery
import com.vitran.shop.feature.marketplace.common.data.remote.appendProductSearchQuery
import com.vitran.shop.feature.marketplace.product.data.remote.dto.ProductDataDto
import com.vitran.shop.feature.marketplace.product.data.remote.dto.ProductsDataDto
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.query.ProductBrowseQuery
import com.vitran.shop.feature.marketplace.product.domain.query.ProductSearchQuery
import io.ktor.client.HttpClient
import io.ktor.client.request.get

internal class PublicProductApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getProducts(query: ProductBrowseQuery): AppResult<ProductsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/products")) {
                authMode(AuthMode.None)
                url {
                    parameters.appendProductBrowseQuery(query)
                }
            }
        }

    suspend fun searchProducts(query: ProductSearchQuery): AppResult<ProductsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/products/search")) {
                authMode(AuthMode.None)
                url {
                    parameters.appendProductSearchQuery(query)
                }
            }
        }

    suspend fun getProductById(id: ProductId): AppResult<ProductDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/products/${id.value}")) {
                authMode(AuthMode.None)
            }
        }
}
