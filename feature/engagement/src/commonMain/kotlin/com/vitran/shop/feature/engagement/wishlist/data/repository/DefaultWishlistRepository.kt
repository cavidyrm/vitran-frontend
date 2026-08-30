package com.vitran.shop.feature.engagement.wishlist.data.repository

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.feature.engagement.data.remote.EngagementApi
import com.vitran.shop.feature.engagement.wishlist.data.mapper.toDomain
import com.vitran.shop.feature.engagement.wishlist.data.mapper.toDomainPage
import com.vitran.shop.feature.engagement.wishlist.data.remote.dto.UpdateWishlistShareRequestDto
import com.vitran.shop.feature.engagement.wishlist.domain.error.PublicWishlistResult
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistItem
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSettings
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSlug
import com.vitran.shop.feature.engagement.wishlist.domain.repository.WishlistRepository
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId

internal class DefaultWishlistRepository(
    private val api: EngagementApi,
    invalidationListeners: MutableList<SessionInvalidationListener>,
) : WishlistRepository, SessionInvalidationListener {

    private var cachedShareSettings: WishlistShareSettings? = null

    init {
        invalidationListeners.add(this)
    }

    override suspend fun getWishlist(
        pagination: CursorPagination,
    ): AppResult<CursorPage<WishlistItem>> =
        when (val result = api.getWishlist(pagination)) {
            is AppResult.Success -> AppResult.Success(result.value.toDomainPage())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun setSaved(productId: ProductId, saved: Boolean): AppResult<Unit> =
        if (saved) api.addWishlistProduct(productId) else api.removeWishlistProduct(productId)

    override suspend fun getShareSettings(): AppResult<WishlistShareSettings> =
        when (val result = api.getWishlistShareSettings()) {
            is AppResult.Success -> {
                val settings = result.value.toDomain()
                cachedShareSettings = settings
                AppResult.Success(settings)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun updateShareVisibility(isPublic: Boolean): AppResult<WishlistShareSettings> =
        when (val result = api.updateWishlistShareSettings(UpdateWishlistShareRequestDto(public = isPublic))) {
            is AppResult.Success -> {
                val settings = result.value.toDomain()
                cachedShareSettings = settings
                AppResult.Success(settings)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun getPublicWishlist(
        shareSlug: WishlistShareSlug,
        pagination: CursorPagination,
    ): PublicWishlistResult =
        when (val result = api.getPublicWishlist(shareSlug, pagination)) {
            is AppResult.Success -> PublicWishlistResult.Content(result.value.toDomainPage())
            is AppResult.Failure -> when (val error = result.error) {
                is AppError.Forbidden -> PublicWishlistResult.Private
                is AppError.NotFound -> PublicWishlistResult.NotFound
                else -> PublicWishlistResult.Failure(error)
            }
        }

    override suspend fun onSessionInvalidated() {
        cachedShareSettings = null
    }
}
