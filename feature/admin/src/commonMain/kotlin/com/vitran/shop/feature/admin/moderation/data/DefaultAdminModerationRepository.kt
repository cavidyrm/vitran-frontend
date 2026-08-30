package com.vitran.shop.feature.admin.moderation.data

import com.vitran.shop.core.domain.pagination.PageResult
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.admin.moderation.domain.AdminModerationQuery
import com.vitran.shop.feature.admin.moderation.domain.AdminModerationRepository
import com.vitran.shop.feature.admin.moderation.domain.AdminProductDetails
import com.vitran.shop.feature.admin.moderation.domain.AdminProductSummary
import com.vitran.shop.feature.admin.moderation.domain.AdminShopSummary
import com.vitran.shop.feature.admin.moderation.domain.ConfirmedAdminComment
import com.vitran.shop.feature.engagement.comment.domain.model.ShopCommentId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.repository.ProductPublicCacheInvalidator
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.repository.ShopPublicCacheInvalidator
import com.vitran.shop.feature.seller.product.domain.model.productPublicationState
import com.vitran.shop.feature.seller.shop.domain.model.shopPublicationState

internal class DefaultAdminModerationRepository(
    private val api: AdminModerationApi,
    private val shopCacheInvalidator: ShopPublicCacheInvalidator,
    private val productCacheInvalidator: ProductPublicCacheInvalidator,
) : AdminModerationRepository {
    override suspend fun getShops(query: AdminModerationQuery) =
        api.listShops(query).mapSuccess { page ->
            page.shops.run {
                PageResult(results.map { it.toDomain() }, this.page, perPage, lastPage, total, hasMore)
            }
        }

    override suspend fun confirmShop(id: ShopId): AppResult<AdminShopSummary> =
        api.confirmShop(id).mapSuccessSuspend {
            it.shop.toDomain().also { shopCacheInvalidator.invalidate(id) }
        }

    override suspend fun getProducts(query: AdminModerationQuery) =
        api.listProducts(query).mapSuccess { page ->
            page.products.run {
                PageResult(results.map { it.toDomain() }, this.page, perPage, lastPage, total, hasMore)
            }
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
)

private fun AdminProductSummaryDto.toDomain() = AdminProductSummary(
    id = ProductId(id), shopId = ShopId(shopId), categorySlug = categorySlug, title = title,
    priceAmount = price, active = active, confirmed = confirmed,
    publication = productPublicationState(active, confirmed), images = images, updatedAt = updatedAt,
)

private fun AdminProductDetailsDto.toDomain() = AdminProductDetails(
    id = ProductId(id), shopId = ShopId(shopId), categorySlug = categorySlug, title = title,
    priceAmount = price, active = active, confirmed = confirmed,
    publication = productPublicationState(active, confirmed), images = images, updatedAt = updatedAt,
)

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
