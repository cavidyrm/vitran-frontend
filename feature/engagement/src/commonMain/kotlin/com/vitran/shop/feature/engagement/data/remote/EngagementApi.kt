package com.vitran.shop.feature.engagement.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.pagination.appendCursorPagination
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.engagement.favorite.data.remote.dto.FavoriteShopsDataDto
import com.vitran.shop.feature.engagement.wishlist.data.remote.dto.FavoriteProductsDataDto
import com.vitran.shop.feature.engagement.wishlist.data.remote.dto.PublicWishlistDataDto
import com.vitran.shop.feature.engagement.wishlist.data.remote.dto.UpdateWishlistShareRequestDto
import com.vitran.shop.feature.engagement.wishlist.data.remote.dto.WishlistShareSettingsDto
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSlug
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class EngagementApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun followShop(shopId: ShopId): AppResult<Unit> =
        executor.executeEmpty {
            client.post(environment.apiUrl("/me/follows/shops/${shopId.value}")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun unfollowShop(shopId: ShopId): AppResult<Unit> =
        executor.executeEmpty {
            client.delete(environment.apiUrl("/me/follows/shops/${shopId.value}")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun getFavoriteShops(pagination: CursorPagination): AppResult<FavoriteShopsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/me/favorites/shops")) {
                authMode(AuthMode.Required)
                url { parameters.appendCursorPagination(pagination) }
            }
        }

    suspend fun addFavoriteShop(shopId: ShopId): AppResult<Unit> =
        executor.executeEmpty {
            client.post(environment.apiUrl("/me/favorites/shops/${shopId.value}")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun removeFavoriteShop(shopId: ShopId): AppResult<Unit> =
        executor.executeEmpty {
            client.delete(environment.apiUrl("/me/favorites/shops/${shopId.value}")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun getWishlist(pagination: CursorPagination): AppResult<FavoriteProductsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/me/favorites/products")) {
                authMode(AuthMode.Required)
                url { parameters.appendCursorPagination(pagination) }
            }
        }

    suspend fun addWishlistProduct(productId: ProductId): AppResult<Unit> =
        executor.executeEmpty {
            client.post(environment.apiUrl("/me/favorites/products/${productId.value}")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun removeWishlistProduct(productId: ProductId): AppResult<Unit> =
        executor.executeEmpty {
            client.delete(environment.apiUrl("/me/favorites/products/${productId.value}")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun getWishlistShareSettings(): AppResult<WishlistShareSettingsDto> =
        executor.execute {
            client.get(environment.apiUrl("/me/wishlist/share")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun updateWishlistShareSettings(
        request: UpdateWishlistShareRequestDto,
    ): AppResult<WishlistShareSettingsDto> =
        executor.execute {
            client.put(environment.apiUrl("/me/wishlist/share")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    suspend fun getPublicWishlist(
        shareSlug: WishlistShareSlug,
        pagination: CursorPagination,
    ): AppResult<PublicWishlistDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/wishlists/share/${shareSlug.value}")) {
                authMode(AuthMode.None)
                url { parameters.appendCursorPagination(pagination) }
            }
        }
}
