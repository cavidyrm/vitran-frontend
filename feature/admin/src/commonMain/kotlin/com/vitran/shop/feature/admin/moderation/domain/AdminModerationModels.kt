package com.vitran.shop.feature.admin.moderation.domain

import com.vitran.shop.feature.engagement.comment.domain.model.ShopCommentId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.product.domain.model.ProductPublicationState
import com.vitran.shop.feature.seller.shop.domain.model.ShopPublicationState
import com.vitran.shop.feature.seller.shop.domain.model.ShopRule
import com.vitran.shop.feature.seller.shop.domain.model.ShopShippingMethod
import com.vitran.shop.feature.seller.shop.domain.model.ShopWorkingHours
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
    val description: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val supportTimes: String? = null,
    val cityId: Long? = null,
    val avatarUrl: String? = null,
    val seoTitle: String? = null,
    val seoDescription: String? = null,
    val noindex: Boolean = false,
    val rules: List<ShopRule> = emptyList(),
    val shippingMethods: List<ShopShippingMethod> = emptyList(),
    val workingHours: List<ShopWorkingHours> = emptyList(),
    val fulfillmentType: String? = null,
    val webhookUrl: String? = null,
    val whatsapp: String? = null,
    val telegram: String? = null,
    val instagram: String? = null,
    val website: String? = null,
    val bale: String? = null,
    val rubika: String? = null,
    val tiktok: String? = null,
)

/**
 * List rows and confirm bodies omit different profile fields.
 * Keep whichever copy already had a value.
 */
fun AdminShopSummary.withStoredFieldsFrom(previous: AdminShopSummary): AdminShopSummary =
    copy(
        title = title ?: previous.title,
        ownerId = ownerId ?: previous.ownerId,
        type = type ?: previous.type,
        shareUrl = shareUrl ?: previous.shareUrl,
        categorySlugs = categorySlugs.ifEmpty { previous.categorySlugs },
        updatedAt = updatedAt ?: previous.updatedAt,
        description = description ?: previous.description,
        address = address ?: previous.address,
        phoneNumber = phoneNumber ?: previous.phoneNumber,
        supportTimes = supportTimes ?: previous.supportTimes,
        cityId = cityId ?: previous.cityId,
        avatarUrl = avatarUrl ?: previous.avatarUrl,
        seoTitle = seoTitle ?: previous.seoTitle,
        seoDescription = seoDescription ?: previous.seoDescription,
        noindex = noindex || previous.noindex,
        rules = rules.ifEmpty { previous.rules },
        shippingMethods = shippingMethods.ifEmpty { previous.shippingMethods },
        workingHours = workingHours.ifEmpty { previous.workingHours },
        fulfillmentType = fulfillmentType ?: previous.fulfillmentType,
        webhookUrl = webhookUrl ?: previous.webhookUrl,
        whatsapp = whatsapp ?: previous.whatsapp,
        telegram = telegram ?: previous.telegram,
        instagram = instagram ?: previous.instagram,
        website = website ?: previous.website,
        bale = bale ?: previous.bale,
        rubika = rubika ?: previous.rubika,
        tiktok = tiktok ?: previous.tiktok,
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

data class AdminProductImage(
    val id: Long,
    val url: String,
    val sortOrder: Int,
    val isMain: Boolean,
)

data class AdminProductDetails(
    val id: ProductId,
    val shopId: ShopId,
    val categorySlug: String?,
    val title: String,
    val description: String? = null,
    val priceAmount: Long?,
    val compareAtPriceAmount: Long? = null,
    val active: Boolean,
    val confirmed: Boolean,
    val publication: ProductPublicationState,
    val mainImage: AdminProductImage? = null,
    val images: List<AdminProductImage> = emptyList(),
    val slug: String? = null,
    val seoTitle: String? = null,
    val seoDescription: String? = null,
    val noindex: Boolean = false,
    val updatedAt: String?,
)

/**
 * Confirm responses often omit description, images, and SEO.
 * Keep those from the product already on screen.
 */
fun AdminProductDetails.withStoredFieldsFrom(previous: AdminProductDetails): AdminProductDetails =
    copy(
        description = description ?: previous.description,
        compareAtPriceAmount = compareAtPriceAmount ?: previous.compareAtPriceAmount,
        categorySlug = categorySlug ?: previous.categorySlug,
        priceAmount = priceAmount ?: previous.priceAmount,
        mainImage = mainImage ?: previous.mainImage,
        images = images.ifEmpty { previous.images },
        slug = slug ?: previous.slug,
        seoTitle = seoTitle ?: previous.seoTitle,
        seoDescription = seoDescription ?: previous.seoDescription,
        noindex = noindex || previous.noindex,
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
