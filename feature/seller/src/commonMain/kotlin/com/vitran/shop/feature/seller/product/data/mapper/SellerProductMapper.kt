package com.vitran.shop.feature.seller.product.data.mapper

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.network.pagination.CursorPageDto
import com.vitran.shop.feature.marketplace.common.data.serializer.FlexibleCategorySlugSerializer
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.product.data.remote.dto.SellerProductDto
import com.vitran.shop.feature.seller.product.data.remote.dto.SellerProductImageDto
import com.vitran.shop.feature.seller.product.data.remote.dto.SellerProductListItemDto
import com.vitran.shop.feature.seller.product.domain.model.ProductImageId
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.model.SellerProductImage
import com.vitran.shop.feature.seller.product.domain.model.SellerProductSummary
import kotlinx.datetime.Instant

internal fun CursorPageDto<SellerProductListItemDto>.toSellerSummaryPage(): CursorPage<SellerProductSummary> =
    CursorPage(
        items = results.map { it.toDomain() },
        nextCursor = nextCursor,
        hasMore = hasMore,
    )

internal fun SellerProductListItemDto.toDomain(): SellerProductSummary =
    SellerProductSummary(
        id = ProductId(id),
        shopId = ShopId(shopId),
        title = title,
        active = active,
        confirmed = confirmed,
    )

internal fun SellerProductImageDto.toDomain(): SellerProductImage =
    SellerProductImage(
        id = ProductImageId(id),
        url = url,
        sortOrder = sortOrder,
    )

internal fun SellerProductDto.toDomain(
    fallbackShopId: ShopId? = null,
    previous: SellerProductDetails? = null,
): SellerProductDetails {
    val resolvedShopId =
        shopId?.let { ShopId(it) }
            ?: fallbackShopId
            ?: previous?.shopId
            ?: ShopId(0)
    val active = active ?: previous?.active ?: false
    val confirmed = confirmed ?: previous?.confirmed ?: false
    return SellerProductDetails(
        id = ProductId(id),
        shopId = resolvedShopId,
        categorySlug = categorySlug?.let { FlexibleCategorySlugSerializer.toDomain(it) }
            ?: previous?.categorySlug,
        title = title ?: previous?.title.orEmpty(),
        description = description ?: previous?.description,
        priceAmount = price ?: previous?.priceAmount,
        active = active,
        confirmed = confirmed,
        images = images.map { it.toDomain() }.sortedBy { it.sortOrder },
        createdAt = createdAt?.let { runCatching { Instant.parse(it) }.getOrNull() }
            ?: previous?.createdAt,
        updatedAt = updatedAt?.let { runCatching { Instant.parse(it) }.getOrNull() }
            ?: previous?.updatedAt,
    )
}

internal fun SellerProductDetails.toSummary(): SellerProductSummary =
    SellerProductSummary(
        id = id,
        shopId = shopId,
        title = title,
        active = active,
        confirmed = confirmed,
    )
