package com.vitran.shop.feature.admin.moderation.data

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.pagination.toDomain
import com.vitran.shop.feature.admin.moderation.domain.AdminModerationQuery
import com.vitran.shop.feature.admin.moderation.domain.AdminModerationRepository
import com.vitran.shop.feature.admin.moderation.domain.AdminProductDetails
import com.vitran.shop.feature.admin.moderation.domain.AdminProductImage
import com.vitran.shop.feature.admin.moderation.domain.AdminProductSummary
import com.vitran.shop.feature.admin.moderation.domain.AdminShopSummary
import com.vitran.shop.feature.admin.moderation.domain.ConfirmedAdminComment
import com.vitran.shop.feature.engagement.comment.domain.model.ShopCommentId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.repository.ProductPublicCacheInvalidator
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopPublicCacheInvalidator
import com.vitran.shop.feature.seller.product.domain.model.productPublicationState
import com.vitran.shop.feature.seller.shop.domain.model.ShopRule
import com.vitran.shop.feature.seller.shop.domain.model.ShopShippingMethod
import com.vitran.shop.feature.seller.shop.domain.model.ShopWorkingHours
import com.vitran.shop.feature.seller.shop.domain.model.shopPublicationState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

internal class DefaultAdminModerationRepository(
    private val api: AdminModerationApi,
    private val shopCacheInvalidator: ShopPublicCacheInvalidator,
    private val productCacheInvalidator: ProductPublicCacheInvalidator,
    private val shopCache: AdminShopModerationCache = AdminShopModerationCache(),
) : AdminModerationRepository {
    override suspend fun getShops(query: AdminModerationQuery) =
        api.listShops(query).mapSuccess { page ->
            page.shops.toDomain { it.toDomain() }.also { shopCache.rememberAll(it.items) }
        }

    override suspend fun confirmShop(id: ShopId): AppResult<AdminShopSummary> =
        api.confirmShop(id).mapSuccessSuspend {
            val stored = shopCache.remember(it.shop.toDomain())
            shopCacheInvalidator.invalidate(id)
            stored
        }

    override suspend fun getProducts(query: AdminModerationQuery) =
        api.listProducts(query).mapSuccess { page ->
            page.products.toDomain { it.toDomain() }
        }

    override suspend fun getProduct(id: ProductId) =
        api.getProduct(id).mapSuccess { it.product.toDomain() }

    override suspend fun confirmProduct(id: ProductId): AppResult<AdminProductDetails> =
        api.confirmProduct(id).mapSuccessSuspend {
            it.product.toDomain().also { productCacheInvalidator.invalidate(id) }
        }

    override suspend fun confirmComment(id: ShopCommentId) =
        api.confirmComment(id).mapSuccess { it.comment.toDomain() }
}

private fun AdminShopSummaryDto.toDomain() = AdminShopSummary(
    id = ShopId(id), slug = slug, active = active, confirmed = confirmed,
    publication = shopPublicationState(active, confirmed), title = title, ownerId = ownerId,
    type = type, shareUrl = shareUrl,
    categorySlugs = categorySlugs.map { element ->
        element.toString().trim('"')
    },
    updatedAt = updatedAt,
    description = description?.takeIf { it.isNotBlank() },
    address = address?.takeIf { it.isNotBlank() },
    phoneNumber = phoneNumber?.takeIf { it.isNotBlank() },
    supportTimes = supportTimes?.takeIf { it.isNotBlank() },
    cityId = cityId,
    avatarUrl = avatarUrl?.takeIf { it.isNotBlank() },
    seoTitle = seoTitle?.takeIf { it.isNotBlank() },
    seoDescription = seoDescription?.takeIf { it.isNotBlank() },
    noindex = noindex == true,
    rules = rules.map { ShopRule(title = it.title, body = it.body) },
    shippingMethods = shippingMethods.map { method ->
        ShopShippingMethod(
            code = method.code,
            title = method.title,
            cost = method.cost,
            minDays = method.minDays,
            maxDays = method.maxDays,
            active = method.active,
        )
    },
    workingHours = workingHours.map { hours ->
        ShopWorkingHours(
            day = hours.day,
            opensAt = hours.opensAt,
            closesAt = hours.closesAt,
            closed = hours.closed,
        )
    },
    fulfillmentType = fulfillmentType?.takeIf { it.isNotBlank() },
    webhookUrl = webhookUrl?.takeIf { it.isNotBlank() },
    whatsapp = whatsapp?.takeIf { it.isNotBlank() },
    telegram = telegram?.takeIf { it.isNotBlank() },
    instagram = instagram?.takeIf { it.isNotBlank() },
    website = website?.takeIf { it.isNotBlank() },
    bale = bale?.takeIf { it.isNotBlank() },
    rubika = rubika?.takeIf { it.isNotBlank() },
    tiktok = tiktok?.takeIf { it.isNotBlank() },
)

private fun AdminProductSummaryDto.toDomain() = AdminProductSummary(
    id = ProductId(id), shopId = ShopId(shopId), categorySlug = categorySlug, title = title,
    priceAmount = price, active = active, confirmed = confirmed,
    publication = productPublicationState(active, confirmed), images = images, updatedAt = updatedAt,
)

private fun AdminProductDetailsDto.toDomain(): AdminProductDetails {
    val resolvedImages = resolveProductImages(images, mainImage)
    return AdminProductDetails(
        id = ProductId(id),
        shopId = ShopId(shopId),
        categorySlug = categorySlug,
        title = title,
        description = description?.takeIf { it.isNotBlank() },
        priceAmount = price,
        compareAtPriceAmount = compareAtPrice,
        active = active,
        confirmed = confirmed,
        publication = productPublicationState(active, confirmed),
        mainImage = mainImage?.toDomain()?.copy(isMain = true) ?: resolvedImages.firstOrNull { it.isMain },
        images = resolvedImages,
        slug = slug?.takeIf { it.isNotBlank() },
        seoTitle = seoTitle?.takeIf { it.isNotBlank() },
        seoDescription = seoDescription?.takeIf { it.isNotBlank() },
        noindex = noindex == true,
        updatedAt = updatedAt,
    )
}

private val productImageJson = Json { ignoreUnknownKeys = true }

private fun resolveProductImages(
    images: List<JsonElement>,
    mainImage: AdminProductImageDto?,
): List<AdminProductImage> {
    val mapped = images.mapNotNull { element -> element.toProductImage() }.toMutableList()
    val cover = mainImage?.toDomain()
    if (cover != null && mapped.none { it.id == cover.id }) {
        mapped += cover.copy(isMain = true)
    }
    val coverId = cover?.id ?: mapped.firstOrNull { it.isMain }?.id
    val flagged = if (coverId == null) mapped else mapped.map { it.copy(isMain = it.id == coverId) }
    return flagged.sortedWith(compareBy<AdminProductImage> { !it.isMain }.thenBy { it.sortOrder })
}

private fun JsonElement.toProductImage(): AdminProductImage? =
    runCatching { productImageJson.decodeFromJsonElement(AdminProductImageDto.serializer(), this) }
        .getOrNull()
        ?.toDomain()

private fun AdminProductImageDto.toDomain(): AdminProductImage? {
    if (url.isBlank()) return null
    return AdminProductImage(id = id, url = url, sortOrder = sortOrder, isMain = isMain)
}

private fun AdminCommentConfirmDto.toDomain() = ConfirmedAdminComment(
    id = ShopCommentId(id), shopId = ShopId(shopId), userId = userId, title = title, confirmed = confirmed,
)

private inline fun <T, R> AppResult<T>.mapSuccess(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(value))
    is AppResult.Failure -> this
}

private suspend inline fun <T, R> AppResult<T>.mapSuccessSuspend(crossinline transform: suspend (T) -> R): AppResult<R> =
    when (this) {
        is AppResult.Success -> AppResult.Success(transform(value))
        is AppResult.Failure -> this
    }
