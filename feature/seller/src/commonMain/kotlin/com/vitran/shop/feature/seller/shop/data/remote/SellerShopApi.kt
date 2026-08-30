package com.vitran.shop.feature.seller.shop.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.pagination.appendCursorPagination
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.seller.shop.data.remote.dto.CreateShopDataDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.CreateShopRequestDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.FulfillmentOptionsDataDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.RegenerateApiKeyDataDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.SellerShopDataDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.SellerShopsDataDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.SlugCheckDataDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.UpdateShopRequestDto
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopFilter
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class SellerShopApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun checkSlug(
        slug: ShopSlug,
        excludeId: ShopId?,
    ): AppResult<SlugCheckDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/seller/shops/check-slug")) {
                authMode(AuthMode.Required)
                parameter("slug", slug.value)
                if (excludeId != null) {
                    parameter("exclude_id", excludeId.value)
                }
            }
        }

    suspend fun createShop(request: CreateShopRequestDto): AppResult<CreateShopDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/seller/shops")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun listMyShops(query: SellerShopListQuery): AppResult<SellerShopsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/seller/shops")) {
                authMode(AuthMode.Required)
                url {
                    parameters.appendCursorPagination(query.pagination)
                    when (query.activeFilter) {
                        SellerShopFilter.All -> Unit
                        SellerShopFilter.Active -> parameters.append("active", "true")
                        SellerShopFilter.Inactive -> parameters.append("active", "false")
                    }
                }
            }
        }

    suspend fun getMyShop(shopId: ShopId): AppResult<SellerShopDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/seller/shops/${shopId.value}")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun updateShop(
        shopId: ShopId,
        request: UpdateShopRequestDto,
    ): AppResult<SellerShopDataDto> =
        executor.execute {
            client.patch(environment.apiUrl("/seller/shops/${shopId.value}")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun getFulfillmentOptions(shopId: ShopId): AppResult<FulfillmentOptionsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/seller/shops/${shopId.value}/fulfillment-options")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun regenerateApiKey(shopId: ShopId): AppResult<RegenerateApiKeyDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/seller/shops/${shopId.value}/regenerate-api-key")) {
                authMode(AuthMode.Required)
            }
        }
}
