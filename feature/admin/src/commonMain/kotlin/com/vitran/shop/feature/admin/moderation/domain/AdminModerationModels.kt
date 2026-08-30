package com.vitran.shop.feature.admin.moderation.domain

import com.vitran.shop.feature.engagement.comment.domain.model.ShopCommentId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.product.domain.model.ProductPublicationState
import com.vitran.shop.feature.seller.shop.domain.model.ShopPublicationState
import kotlinx.serialization.json.JsonElement

data class AdminShopSummary(
    val id: ShopId,
    val slug: String,
    val active: Boolean,
    val confirmed: Boolean,
    val publication: ShopPublicationState,
    val title: String?,
    val ownerId: Long?,
    val type: String?,
    val shareUrl: String?,
    val categorySlugs: List<String>,
    val updatedAt: String?,
)

data class AdminProductSummary(
    val id: ProductId,
    val shopId: ShopId,
    val categorySlug: String?,
    val title: String,
    val priceAmount: Long?,
    val active: Boolean,
    val confirmed: Boolean,
    val publication: ProductPublicationState,
    val images: List<JsonElement>,
    val updatedAt: String?,
)

data class AdminProductDetails(
    val id: ProductId,
    val shopId: ShopId,
    val categorySlug: String?,
    val title: String,
    val priceAmount: Long?,
    val active: Boolean,
    val confirmed: Boolean,
    val publication: ProductPublicationState,
    val images: List<JsonElement>,
    val updatedAt: String?,
)

data class ConfirmedAdminComment(
    val id: ShopCommentId,
    val shopId: ShopId,
    val userId: Long,
    val title: String,
    val confirmed: Boolean,
)

data class AdminModerationQuery(
    val page: Int = 1,
    val perPage: Int = 20,
    val active: Boolean? = null,
    val cityId: Long? = null,
    val categorySlug: String? = null,
    val userId: Long? = null,
    val shopId: ShopId? = null,
)
