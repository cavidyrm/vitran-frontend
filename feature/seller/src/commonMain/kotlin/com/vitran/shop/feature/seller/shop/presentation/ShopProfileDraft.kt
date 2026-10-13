package com.vitran.shop.feature.seller.shop.presentation

import com.vitran.shop.feature.seller.shop.domain.model.SellerShopDetails
import com.vitran.shop.feature.seller.shop.domain.model.ShopRule
import com.vitran.shop.feature.seller.shop.domain.model.ShopShippingMethod
import com.vitran.shop.feature.seller.shop.domain.model.ShopWorkingHours

data class ShopRuleDraft(
    val title: String = "",
    val body: String = "",
)

data class ShopShippingDraft(
    val code: String = "",
    val title: String = "",
    val cost: String = "",
    val minDays: String = "",
    val maxDays: String = "",
    val active: Boolean = true,
)

data class ShopHoursDraft(
    val day: String = "sat",
    val opensAt: String = "09:00",
    val closesAt: String = "18:00",
    val closed: Boolean = false,
)

/**
 * Seller shop profile fields collected by create and edit forms.
 * [touched] lists API keys the user changed. Update omits untouched keys so a GET
 * that still drops SEO, rules, hours, and socials cannot clear them.
 */
data class ShopProfileDraft(
    val seoTitle: String = "",
    val seoDescription: String = "",
    val noindex: Boolean = false,
    val rules: List<ShopRuleDraft> = emptyList(),
    val shippingMethods: List<ShopShippingDraft> = emptyList(),
    val workingHours: List<ShopHoursDraft> = emptyList(),
    val fulfillmentType: String = "",
    val webhookUrl: String = "",
    val clearAvatar: Boolean = false,
    val clearWebhook: Boolean = false,
    val touched: Set<String> = emptySet(),
) {
    fun touch(key: String, transform: (ShopProfileDraft) -> ShopProfileDraft): ShopProfileDraft =
        transform(this).copy(touched = touched + key)
}

fun ShopProfileDraft.toRules(): List<ShopRule> =
    rules.mapNotNull { draft ->
        val title = draft.title.trim()
        val body = draft.body.trim()
        if (title.isEmpty() && body.isEmpty()) {
            null
        } else {
            ShopRule(title = title.ifBlank { body.take(40) }, body = body)
        }
    }

fun ShopProfileDraft.toShippingMethods(): List<ShopShippingMethod> =
    shippingMethods.mapNotNull { draft ->
        val code = draft.code.trim()
        val title = draft.title.trim()
        if (code.isEmpty() && title.isEmpty()) {
            null
        } else {
            ShopShippingMethod(
                code = code.ifBlank { "custom" },
                title = title.ifBlank { code },
                cost = draft.cost.filter { it.isDigit() }.toLongOrNull(),
                minDays = draft.minDays.filter { it.isDigit() }.toIntOrNull(),
                maxDays = draft.maxDays.filter { it.isDigit() }.toIntOrNull(),
                active = draft.active,
            )
        }
    }

fun ShopProfileDraft.toWorkingHours(): List<ShopWorkingHours> =
    workingHours.mapNotNull { draft ->
        val day = draft.day.trim()
        if (day.isEmpty()) {
            null
        } else {
            ShopWorkingHours(
                day = day,
                opensAt = draft.opensAt.trim().takeIf { it.isNotEmpty() && !draft.closed },
                closesAt = draft.closesAt.trim().takeIf { it.isNotEmpty() && !draft.closed },
                closed = draft.closed,
            )
        }
    }

fun SellerShopDetails.toProfileDraft(): ShopProfileDraft =
    ShopProfileDraft(
        seoTitle = seoTitle.orEmpty(),
        seoDescription = seoDescription.orEmpty(),
        noindex = noindex == true,
        rules = rules.map { ShopRuleDraft(title = it.title, body = it.body) },
        shippingMethods =
            shippingMethods.map { method ->
                ShopShippingDraft(
                    code = method.code,
                    title = method.title,
                    cost = method.cost?.toString().orEmpty(),
                    minDays = method.minDays?.toString().orEmpty(),
                    maxDays = method.maxDays?.toString().orEmpty(),
                    active = method.active != false,
                )
            },
        workingHours =
            workingHours.map { hours ->
                ShopHoursDraft(
                    day = hours.day,
                    opensAt = hours.opensAt ?: "09:00",
                    closesAt = hours.closesAt ?: "18:00",
                    closed = hours.closed,
                )
            },
        fulfillmentType = fulfillmentType.orEmpty(),
        webhookUrl = webhookUrl.orEmpty(),
    )
