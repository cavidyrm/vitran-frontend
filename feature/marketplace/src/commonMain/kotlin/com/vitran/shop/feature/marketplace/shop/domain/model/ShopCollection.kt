package com.vitran.shop.feature.marketplace.shop.domain.model

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import kotlin.jvm.JvmInline
import kotlinx.datetime.Instant

@JvmInline
value class ShopCollectionId(val value: Long)

/**
 * Collection row. Public and seller lists embed [productCount] only — products are not included.
 */
data class ShopCollection(
    val id: ShopCollectionId,
    val shopId: ShopId,
    val title: String,
    val description: String? = null,
    val productCount: Int,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)

/**
 * Thin product row inside a collection page. Postman example is `id`, `title`, `slug`, `price` only.
 */
data class ShopCollectionProduct(
    val id: ProductId,
    val title: String,
    val slug: String? = null,
    val priceAmount: Long? = null,
)

data class ShopCollectionDetail(
    val collection: ShopCollection,
    val products: CursorPage<ShopCollectionProduct>,
)
