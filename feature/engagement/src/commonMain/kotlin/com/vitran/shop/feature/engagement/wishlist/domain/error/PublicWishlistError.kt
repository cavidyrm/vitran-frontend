package com.vitran.shop.feature.engagement.wishlist.domain.error

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.feature.engagement.wishlist.domain.model.PublicWishlistItem

/**
 * Public shared wishlist is AuthMode.None — 403 means the list is private,
 * not a session/auth failure.
 */
sealed interface PublicWishlistResult {
    data class Content(val page: CursorPage<PublicWishlistItem>) : PublicWishlistResult

    data object Private : PublicWishlistResult

    data object NotFound : PublicWishlistResult

    data class Failure(val error: AppError) : PublicWishlistResult
}
