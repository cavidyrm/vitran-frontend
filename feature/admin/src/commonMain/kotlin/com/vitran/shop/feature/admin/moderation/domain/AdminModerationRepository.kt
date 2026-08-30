package com.vitran.shop.feature.admin.moderation.domain

import com.vitran.shop.core.domain.pagination.PageResult
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.engagement.comment.domain.model.ShopCommentId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

interface AdminModerationRepository {
    suspend fun getShops(query: AdminModerationQuery): AppResult<PageResult<AdminShopSummary>>
    suspend fun confirmShop(id: ShopId): AppResult<AdminShopSummary>
    suspend fun getProducts(query: AdminModerationQuery): AppResult<PageResult<AdminProductSummary>>
    suspend fun getProduct(id: ProductId): AppResult<AdminProductDetails>
    suspend fun confirmProduct(id: ProductId): AppResult<AdminProductDetails>
    suspend fun confirmComment(id: ShopCommentId): AppResult<ConfirmedAdminComment>
}
