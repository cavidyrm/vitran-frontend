package com.vitran.shop.feature.seller.shop.data.mapper

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.network.pagination.CursorPageDto
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.CreateShopCollectionRequestDto
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.UpdateShopCollectionRequestDto
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.seller.shop.data.remote.dto.CreateShopDataDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.CreateShopRequestDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.CreateShopTokensDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.SellerShopCreateResponseDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.SellerShopDetailsDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.SellerShopListItemDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.SlugCheckDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.ShopRuleDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.ShopShippingMethodDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.ShopWorkingHoursDto
import com.vitran.shop.feature.seller.shop.data.remote.dto.UpdateShopRequestDto
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCollectionCommand
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopResult
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopDetails
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.model.SessionAccessUpdate
import com.vitran.shop.feature.seller.shop.domain.model.ShopSlugAvailability
import com.vitran.shop.feature.seller.shop.domain.model.ShopRule
import com.vitran.shop.feature.seller.shop.domain.model.ShopShippingMethod
import com.vitran.shop.feature.seller.shop.domain.model.ShopWorkingHours
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCollectionCommand
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.shopPublicationState
import kotlinx.datetime.Instant

internal fun SlugCheckDto.toDomain(): ShopSlugAvailability =
    ShopSlugAvailability(
        slug = ShopSlug(slug),
        isAvailable = available,
    )

internal fun com.vitran.shop.feature.seller.shop.data.remote.dto.TitleCheckDto.toDomain() =
    com.vitran.shop.feature.seller.shop.domain.model.ShopTitleAvailability(
        title = title,
        isAvailable = available,
    )

internal fun CreateShopCommand.toRequestDto(): CreateShopRequestDto =
    CreateShopRequestDto(
        title = title,
        slug = slug?.value?.takeIf { it.isNotBlank() },
        avatarUrl = avatarUrl?.takeIf { it.isNotBlank() },
        description = description?.takeIf { it.isNotBlank() },
        address = address?.takeIf { it.isNotBlank() },
        phoneNumber = phoneNumber?.takeIf { it.isNotBlank() },
        supportTimes = supportTimes?.takeIf { it.isNotBlank() },
        type = type,
        cityId = cityId.value,
        categorySlugs = categorySlugs,
        seoTitle = seoTitle?.takeIf { it.isNotBlank() },
        seoDescription = seoDescription?.takeIf { it.isNotBlank() },
        noindex = noindex,
        rules = rules.takeIf { it.isNotEmpty() }?.map { it.toDto() },
        shippingMethods = shippingMethods.takeIf { it.isNotEmpty() }?.map { it.toDto() },
        workingHours = workingHours.takeIf { it.isNotEmpty() }?.map { it.toDto() },
        fulfillmentType = fulfillmentType?.takeIf { it.isNotBlank() },
        webhookUrl = webhookUrl?.takeIf { it.isNotBlank() },
        whatsapp = whatsapp?.takeIf { it.isNotBlank() },
        bale = bale?.takeIf { it.isNotBlank() },
        rubika = rubika?.takeIf { it.isNotBlank() },
        telegram = telegram?.takeIf { it.isNotBlank() },
        instagram = instagram?.takeIf { it.isNotBlank() },
        tiktok = tiktok?.takeIf { it.isNotBlank() },
        website = website?.takeIf { it.isNotBlank() },
    )

internal fun CreateShopCollectionCommand.toRequestDto(): CreateShopCollectionRequestDto =
    CreateShopCollectionRequestDto(
        title = title,
        description = description?.takeIf { it.isNotBlank() },
        productIds = productIds.map { it.value },
    )

internal fun UpdateShopCollectionCommand.toRequestDto(): UpdateShopCollectionRequestDto =
    UpdateShopCollectionRequestDto(
        title = title?.takeIf { it.isNotBlank() },
        description = description?.takeIf { it.isNotBlank() },
        productIds = productIds?.map { it.value },
    )

internal fun UpdateShopCommand.toRequestDto(): UpdateShopRequestDto =
    UpdateShopRequestDto(
        title = title,
        slug = slug?.value,
        avatarUrl = avatarUrl,
        description = description,
        address = address,
        phoneNumber = phoneNumber,
        supportTimes = supportTimes,
        type = type,
        cityId = cityId?.value,
        categorySlugs = categorySlugs,
        seoTitle = seoTitle,
        seoDescription = seoDescription,
        noindex = noindex,
        rules = rules?.map { it.toDto() },
        shippingMethods = shippingMethods?.map { it.toDto() },
        workingHours = workingHours?.map { it.toDto() },
        fulfillmentType = fulfillmentType,
        webhookUrl = webhookUrl,
        clearAvatarUrl = true.takeIf { clearAvatarUrl },
        clearWebhookUrl = true.takeIf { clearWebhookUrl },
        whatsapp = whatsapp,
        bale = bale,
        rubika = rubika,
        telegram = telegram,
        instagram = instagram,
        tiktok = tiktok,
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
        avatarUrl = avatarUrl,
        description = description,
        type = type,
        shareUrl = shareUrl,
        qrCodeUrl = qrCodeUrl,
        categorySlugs = categorySlugs,
        categoryNumericIds = categorySlugs.toNumericCategoryIds(),
        seoTitle = seoTitle,
        seoDescription = seoDescription,
        noindex = noindex,
        rules = rules.map { it.toDomain() },
        shippingMethods = shippingMethods.map { it.toDomain() },
        workingHours = workingHours.map { it.toDomain() },
        fulfillmentType = fulfillmentType,
        webhookUrl = webhookUrl,
        bale = bale,
        rubika = rubika,
        tiktok = tiktok,
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
        avatarUrl = avatarUrl,
        description = description,
        address = address,
        phoneNumber = phoneNumber,
        supportTimes = supportTimes,
        type = type,
        shareUrl = shareUrl,
        qrCodeUrl = qrCodeUrl,
        categorySlugs = categorySlugs,
        categoryNumericIds = categorySlugs.toNumericCategoryIds(),
        seoTitle = seoTitle,
        seoDescription = seoDescription,
        noindex = noindex,
        rules = rules.map { it.toDomain() },
        shippingMethods = shippingMethods.map { it.toDomain() },
        workingHours = workingHours.map { it.toDomain() },
        fulfillmentType = fulfillmentType,
        webhookUrl = webhookUrl,
        whatsapp = whatsapp,
        bale = bale,
        rubika = rubika,
        telegram = telegram,
        instagram = instagram,
        tiktok = tiktok,
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

private fun List<String>.toNumericCategoryIds(): List<Long> =
    mapNotNull { it.toLongOrNull() }

private fun ShopRule.toDto() = ShopRuleDto(title = title, body = body)

private fun ShopRuleDto.toDomain() = ShopRule(title = title, body = body)

private fun ShopShippingMethod.toDto() =
    ShopShippingMethodDto(
        code = code,
        title = title,
        cost = cost,
        minDays = minDays,
        maxDays = maxDays,
        active = active,
    )

private fun ShopShippingMethodDto.toDomain() =
    ShopShippingMethod(
        code = code,
        title = title,
        cost = cost,
        minDays = minDays,
        maxDays = maxDays,
        active = active,
    )

private fun ShopWorkingHours.toDto() =
    ShopWorkingHoursDto(
        day = day,
        opensAt = opensAt,
        closesAt = closesAt,
        closed = closed,
    )

private fun ShopWorkingHoursDto.toDomain() =
    ShopWorkingHours(
        day = day,
        opensAt = opensAt,
        closesAt = closesAt,
        closed = closed,
    )
