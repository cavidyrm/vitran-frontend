package com.vitran.shop.feature.engagement.favorite.data.mapper

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.network.pagination.toDomain
import com.vitran.shop.feature.engagement.favorite.data.remote.dto.FavoriteShopItemDto
import com.vitran.shop.feature.engagement.favorite.data.remote.dto.FavoriteShopsDataDto
import com.vitran.shop.feature.engagement.favorite.domain.model.FavoriteShop
import com.vitran.shop.feature.engagement.favorite.domain.model.FavoriteShopSummary
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import kotlinx.datetime.Instant

internal fun FavoriteShopsDataDto.toDomainPage(): CursorPage<FavoriteShop> =
    favoriteShops.toDomain().let { page ->
        CursorPage(
            items = page.items.map { it.toDomain() },
            nextCursor = page.nextCursor,
            hasMore = page.hasMore,
        )
    }

internal fun FavoriteShopItemDto.toDomain(): FavoriteShop =
    FavoriteShop(
        favoritedAt = Instant.parse(favoritedAt),
        shop = FavoriteShopSummary(
            id = ShopId(shop.id),
            slug = ShopSlug(shop.slug),
            title = shop.title,
        ),
    )
