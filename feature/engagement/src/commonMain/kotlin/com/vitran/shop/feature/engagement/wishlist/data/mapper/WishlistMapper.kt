package com.vitran.shop.feature.engagement.wishlist.data.mapper

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.network.pagination.toDomain
import com.vitran.shop.feature.engagement.wishlist.data.remote.dto.FavoriteProductsDataDto
import com.vitran.shop.feature.engagement.wishlist.data.remote.dto.PublicWishlistDataDto
import com.vitran.shop.feature.engagement.wishlist.data.remote.dto.PublicWishlistProductItemDto
import com.vitran.shop.feature.engagement.wishlist.data.remote.dto.WishlistProductItemDto
import com.vitran.shop.feature.engagement.wishlist.data.remote.dto.WishlistShareSettingsDto
import com.vitran.shop.feature.engagement.wishlist.domain.model.PublicWishlistItem
import com.vitran.shop.feature.engagement.wishlist.domain.model.PublicWishlistProductSummary
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistItem
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistProductSummary
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSettings
import com.vitran.shop.feature.engagement.wishlist.domain.model.WishlistShareSlug
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import kotlinx.datetime.Instant

internal fun FavoriteProductsDataDto.toDomainPage(): CursorPage<WishlistItem> {
    val page = favoriteProducts.toDomain()
    return CursorPage(
        items = page.items.map { it.toDomain() },
        nextCursor = page.nextCursor,
        hasMore = page.hasMore,
    )
}

internal fun WishlistProductItemDto.toDomain(): WishlistItem =
    WishlistItem(
        savedAt = Instant.parse(favoritedAt),
        product = WishlistProductSummary(
            id = ProductId(product.id),
            title = product.title,
            priceAmount = product.price,
        ),
    )

internal fun WishlistShareSettingsDto.toDomain(): WishlistShareSettings =
    WishlistShareSettings(
        shareSlug = WishlistShareSlug(shareSlug),
        isPublic = public,
    )

internal fun PublicWishlistDataDto.toDomainPage(): CursorPage<PublicWishlistItem> {
    val page = wishlist.toDomain()
    return CursorPage(
        items = page.items.map { it.toDomain() },
        nextCursor = page.nextCursor,
        hasMore = page.hasMore,
    )
}

internal fun PublicWishlistProductItemDto.toDomain(): PublicWishlistItem =
    PublicWishlistItem(
        savedAt = Instant.parse(savedAt),
        product = PublicWishlistProductSummary(
            id = ProductId(product.id),
            title = product.title,
        ),
    )
