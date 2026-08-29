package com.vitran.shop.feature.marketplace.shop.data.mapper

import com.vitran.shop.core.network.pagination.toDomain
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.common.data.serializer.FlexibleCategorySlugSerializer
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.BrowseShopItemDto
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.PublicShopDetailsDto
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.ShopListItemDto
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.ShopPlanSummaryDto
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopDetails
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopPlanSummary
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSummary
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlinx.datetime.Instant

internal fun ShopListItemDto.toDomain(): ShopSummary =
    ShopSummary(
        id = ShopId(id),
        slug = ShopSlug(slug),
        title = title,
        active = active,
        confirmed = confirmed,
    )

internal fun BrowseShopItemDto.toDomain(): ShopSummary =
    ShopSummary(
        id = ShopId(id),
        slug = ShopSlug(slug),
        title = title,
        plan = plan?.toDomain(),
    )

internal fun ShopPlanSummaryDto.toDomain(): ShopPlanSummary =
    ShopPlanSummary(slug = slug, title = title)

internal fun PublicShopDetailsDto.toDomain(): ShopDetails =
    ShopDetails(
        id = ShopId(id),
        ownerId = ownerId,
        cityId = CityId(cityId),
        title = title,
        slug = ShopSlug(slug),
        type = type,
        shareUrl = shareUrl,
        active = active,
        confirmed = confirmed,
        categorySlugs = categorySlugs.map { FlexibleCategorySlugSerializer.toDomain(it) },
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt),
    )

internal fun com.vitran.shop.core.network.pagination.CursorPageDto<ShopListItemDto>.toShopSummaryPage() =
    toDomain { it.toDomain() }

internal fun com.vitran.shop.core.network.pagination.CursorPageDto<BrowseShopItemDto>.toBrowseShopSummaryPage() =
    toDomain { it.toDomain() }

private inline fun <T, R> com.vitran.shop.core.network.pagination.CursorPageDto<T>.toDomain(
    mapItem: (T) -> R,
): com.vitran.shop.core.domain.pagination.CursorPage<R> =
    com.vitran.shop.core.domain.pagination.CursorPage(
        items = results.map(mapItem),
        nextCursor = nextCursor,
        hasMore = hasMore,
    )
