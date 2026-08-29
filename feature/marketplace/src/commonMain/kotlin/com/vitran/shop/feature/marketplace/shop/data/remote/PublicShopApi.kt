package com.vitran.shop.feature.marketplace.shop.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.marketplace.common.data.remote.appendShopBrowseQuery
import com.vitran.shop.feature.marketplace.common.data.remote.appendShopListQuery
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.BrowseShopsDataDto
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.ShopDataDto
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.ShopsDataDto
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopBrowseQuery
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopListQuery
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.encodeURLPathPart

internal class PublicShopApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getShops(query: ShopListQuery): AppResult<ShopsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/shops")) {
                authMode(AuthMode.None)
                url {
                    parameters.appendShopListQuery(query)
                }
            }
        }

    suspend fun browseShops(query: ShopBrowseQuery): AppResult<BrowseShopsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/shops/browse")) {
                authMode(AuthMode.None)
                url {
                    parameters.appendShopBrowseQuery(query)
                }
            }
        }

    suspend fun getShopById(id: ShopId): AppResult<ShopDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/shops/${id.value}")) {
                authMode(AuthMode.None)
            }
        }

    suspend fun getShopBySlug(slug: ShopSlug): AppResult<ShopDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/shops/slug/${slug.value.encodeURLPathPart()}")) {
                authMode(AuthMode.None)
            }
        }
}
