package com.vitran.shop.feature.seller.shop.data.mapper

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.network.pagination.CursorPageDto
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.seller.shop.data.remote.dto.CreateShopDataDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.CreateShopRequestDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.CreateShopTokensDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.SellerShopCreateResponseDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.SellerShopDetailsDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.SellerShopListItemDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.SlugCheckDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.UpdateShopRequestDto
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopResult
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopDetails
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.model.SessionAccessUpdate
import com.vitran.shop.feature.seller.shop.domain.model.ShopSlugAvailability
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.shopPublicationState
import kotlinx.datetime.Instant

internal fun SlugCheckDto.toDomain(): ShopSlugAvailability =
    ShopSlugAvailability(
        slug = ShopSlug(slug),
        isAvailable = available,
    )

internal fun CreateShopCommand.toRequestDto(): CreateShopRequestDto =
    CreateShopRequestDto(
        title = title,
        slug = slug?.value?.takeIf { it.isNotBlank() },
        description = description?.takeIf { it.isNotBlank() },
        address = address?.takeIf { it.isNotBlank() },
        phoneNumber = phoneNumber?.takeIf { it.isNotBlank() },
        supportTimes = supportTimes?.takeIf { it.isNotBlank() },
        type = type,
        cityId = cityId.value,
        categorySlugs = categoryNumericIds,
        whatsapp = whatsapp?.takeIf { it.isNotBlank() },
        telegram = telegram?.takeIf { it.isNotBlank() },
        instagram = instagram?.takeIf { it.isNotBlank() },
        website = website?.takeIf { it.isNotBlank() },
    )

internal fun UpdateShopCommand.toRequestDto(): UpdateShopRequestDto =
    UpdateShopRequestDto(
        title = title,
        slug = slug?.value,
        description = description,
        address = address,
        phoneNumber = phoneNumber,
        supportTimes = supportTimes,
        type = type,
        cityId = cityId?.value,
        categorySlugs = categoryNumericIds,
        whatsapp = whatsapp,
        telegram = telegram,
        instagram = instagram,
        website = website,
    )

internal fun CreateShopDataDto.toDomain(): CreateShopResult =
    CreateShopResult(
        shop = shop.toDomain(),
        sessionAccessUpdate = tokens?.toDomain(),
    )

internal fun CreateShopTokensDto.toDomain(): SessionAccessUpdate =
    SessionAccessUpdate(
        accessToken = accessToken,
        expiresAt = Instant.parse(expiresAt),
    )

internal fun SellerShopCreateResponseDto.toDomain(): SellerShopDetails =
    SellerShopDetails(
        id = ShopId(id),
        slug = ShopSlug(slug),
        active = active,
        confirmed = confirmed,
        publicationState = shopPublicationState(active, confirmed),
        ownerId = ownerId,
        cityId = CityId(cityId),
        title = title,
        description = description,
        type = type,
        shareUrl = shareUrl,
        qrCodeUrl = qrCodeUrl,
        categoryNumericIds = categorySlugs,
        createdAt = createdAt?.let { Instant.parse(it) },
        updatedAt = updatedAt?.let { Instant.parse(it) },
    )

internal fun SellerShopListItemDto.toDomain(): SellerShopSummary =
    SellerShopSummary(
        id = ShopId(id),
        title = title,
        active = active,
        confirmed = confirmed,
        publicationState = shopPublicationState(active, confirmed),
    )

internal fun SellerShopDetailsDto.toDomain(): SellerShopDetails =
    SellerShopDetails(
        id = ShopId(id),
        slug = ShopSlug(slug),
        active = active,
        confirmed = confirmed,
        publicationState = shopPublicationState(active, confirmed),
        ownerId = ownerId,
        cityId = cityId?.let { CityId(it) },
        title = title,
        description = description,
        address = address,
        phoneNumber = phoneNumber,
        supportTimes = supportTimes,
        type = type,
        shareUrl = shareUrl,
        qrCodeUrl = qrCodeUrl,
        categoryNumericIds = categorySlugs,
        whatsapp = whatsapp,
        telegram = telegram,
        instagram = instagram,
        website = website,
        createdAt = createdAt?.let { Instant.parse(it) },
        updatedAt = updatedAt?.let { Instant.parse(it) },
    )

internal fun CursorPageDto<SellerShopListItemDto>.toSellerSummaryPage(): CursorPage<SellerShopSummary> =
    CursorPage(
        items = results.map { it.toDomain() },
        nextCursor = nextCursor,
        hasMore = hasMore,
    )

internal fun SellerShopDetails.toSummary(): SellerShopSummary =
    SellerShopSummary(
        id = id,
        title = title.orEmpty(),
        active = active,
        confirmed = confirmed,
        publicationState = publicationState,
    )
