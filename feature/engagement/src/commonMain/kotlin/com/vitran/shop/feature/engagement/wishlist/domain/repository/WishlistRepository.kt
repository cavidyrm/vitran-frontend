package com.vitran.shop.feature.engagement.wishlist.domain.repository

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.wishlist.domain.error.PublicWishlistResult
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistItem
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSettings
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSlug
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId

interface WishlistRepository {
    suspend fun getWishlist(pagination: CursorPagination = CursorPagination()): AppResult<CursorPage<WishlistItem>>

    suspend fun setSaved(productId: ProductId, saved: Boolean): AppResult<Unit>

    suspend fun getShareSettings(): AppResult<WishlistShareSettings>

    suspend fun updateShareVisibility(isPublic: Boolean): AppResult<WishlistShareSettings>

    suspend fun getPublicWishlist(
        shareSlug: WishlistShareSlug,
        pagination: CursorPagination = CursorPagination(),
    ): PublicWishlistResult
}
