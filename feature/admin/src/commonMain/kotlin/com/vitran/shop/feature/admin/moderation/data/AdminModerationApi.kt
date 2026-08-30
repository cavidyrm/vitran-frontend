package com.vitran.shop.feature.admin.moderation.data

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.admin.moderation.domain.AdminModerationQuery
import com.vitran.shop.feature.engagement.comment.domain.model.ShopCommentId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.parameter

internal class AdminModerationApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun listShops(query: AdminModerationQuery): AppResult<AdminShopsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/admin/shops")) {
                authMode(AuthMode.Required)
                applyQuery(query, includeShop = false)
            }
        }

    suspend fun confirmShop(id: ShopId): AppResult<AdminShopDataDto> =
        executor.execute {
            client.patch(environment.apiUrl("/admin/shops/${id.value}/confirm")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun listProducts(query: AdminModerationQuery): AppResult<AdminProductsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/admin/products")) {
                authMode(AuthMode.Required)
                applyQuery(query, includeShop = true)
            }
        }

    suspend fun getProduct(id: ProductId): AppResult<AdminProductDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/admin/products/${id.value}")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun confirmProduct(id: ProductId): AppResult<AdminProductDataDto> =
        executor.execute {
            client.patch(environment.apiUrl("/admin/products/${id.value}/confirm")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun confirmComment(id: ShopCommentId): AppResult<AdminCommentDataDto> =
        executor.execute {
            client.patch(environment.apiUrl("/admin/comments/${id.value}/confirm")) {
                authMode(AuthMode.Required)
            }
        }

    private fun io.ktor.client.request.HttpRequestBuilder.applyQuery(
        query: AdminModerationQuery,
        includeShop: Boolean,
    ) {
        parameter("per_page", query.perPage)
        parameter("page", query.page)
        query.active?.let { parameter("active", it) }
        query.cityId?.let { parameter("city_id", it) }
        query.categorySlug?.let { parameter("category_slug", it) }
        query.userId?.let { parameter("user_id", it) }
        if (includeShop) query.shopId?.let { parameter("shop_id", it.value) }
    }
}
