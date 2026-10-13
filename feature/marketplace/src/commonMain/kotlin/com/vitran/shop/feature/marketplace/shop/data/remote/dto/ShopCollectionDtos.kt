package com.vitran.shop.feature.marketplace.shop.data.remote.dto

import com.vitran.shop.core.network.pagination.CursorPageDto
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShopCollectionsDataDto(
    val collections: CursorPageDto<ShopCollectionDto>,
)

@Serializable
data class ShopCollectionDataDto(
    val collection: ShopCollectionDto,
)

@Serializable
data class ShopCollectionDetailDataDto(
    val collection: ShopCollectionDto,
    val products: CursorPageDto<ShopCollectionProductDto>,
)

@Serializable
data class ShopCollectionDto(
    val id: Long,
    @SerialName("shop_id") val shopId: Long,
    val title: String,
    val description: String? = null,
    @SerialName("product_count") val productCount: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class ShopCollectionProductDto(
    val id: Long,
    val title: String,
    val slug: String? = null,
    val price: Long? = null,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CreateShopCollectionRequestDto(
    val title: String,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val description: String? = null,
    @SerialName("product_ids")
    val productIds: List<Long> = emptyList(),
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class UpdateShopCollectionRequestDto(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val title: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val description: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("product_ids")
    val productIds: List<Long>? = null,
)
