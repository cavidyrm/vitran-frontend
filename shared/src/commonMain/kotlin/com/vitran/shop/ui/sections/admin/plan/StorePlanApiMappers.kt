package com.vitran.shop.ui.sections.admin.plan

import com.vitran.shop.feature.seller.plan.domain.model.PlanCapabilities
import com.vitran.shop.feature.seller.plan.domain.model.PlanSummary
import com.vitran.shop.feature.seller.plan.domain.model.RankingBoostLevel
import com.vitran.shop.feature.seller.subscription.domain.model.ShopEntitlements
import com.vitran.shop.feature.seller.subscription.domain.model.ShopSubscription
import com.vitran.shop.ui.sections.account.toPersianDigits
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_chart
import vitranshop.shared.generated.resources.ic_diamond
import vitranshop.shared.generated.resources.ic_package
import vitranshop.shared.generated.resources.ic_send
import vitranshop.shared.generated.resources.ic_star_outline

/**
 * Maps Phase 9 domain models into existing Store Plan UI models.
 * Billing history is intentionally empty (no API). Yearly pricing unused.
 */
fun mapShopSubscriptionToUi(
    subscription: ShopSubscription,
    entitlements: ShopEntitlements?,
    storeName: String,
): StorePlanSubscription {
    val caps = entitlements?.capabilities ?: PlanCapabilities()
    val limits = entitlements?.limits ?: subscription.plan.limits
    val priceAmount = subscription.plan.priceAmount ?: 0L
    val priceLabel =
        if (priceAmount == 0L) {
            "رایگان"
        } else {
            "${formatTomanAmount(priceAmount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())} / دوره"
        }
    val renewalLabel =
        when {
            subscription.expiresAt == null -> "بدون تاریخ انقضا"
            subscription.daysRemaining != null ->
                "روزهای باقی‌مانده: ${toPersianDigits(subscription.daysRemaining.toString())}"
            else -> "تاریخ انقضا: ${subscription.expiresAt}"
        }

    return StorePlanSubscription(
        tierId = visualTierForSlug(subscription.plan.slug.rawValue),
        tierTitle = subscription.plan.title,
        priceLabel = priceLabel,
        renewalLabel = renewalLabel,
        storeName = storeName,
        benefits = capabilityBenefitLines(caps),
        usage =
            listOf(
                StorePlanUsageItem(
                    id = "products",
                    title = "محصولات",
                    valueLabel = "حداکثر ${toPersianDigits(limits.maxProducts.toString())}",
                    icon = Res.drawable.ic_package,
                    kind = StorePlanUsageKind.Meter,
                    progress = null,
                ),
                StorePlanUsageItem(
                    id = "images",
                    title = "تصاویر هر محصول",
                    valueLabel = "حداکثر ${toPersianDigits(limits.maxImages.toString())}",
                    icon = Res.drawable.ic_star_outline,
                    kind = StorePlanUsageKind.Meter,
                    progress = null,
                ),
                StorePlanUsageItem(
                    id = "analytics",
                    title = "تحلیل‌ها",
                    valueLabel =
                        when {
                            caps.advancedAnalytics -> "پیشرفته"
                            caps.basicAnalytics -> "پایه"
                            else -> "غیرفعال"
                        },
                    icon = Res.drawable.ic_chart,
                    kind = StorePlanUsageKind.Status,
                    statusLabel = if (caps.basicAnalytics || caps.advancedAnalytics) "فعال" else "غیرفعال",
                ),
            ),
        payments = emptyList(),
    )
}

fun mapPlansToCatalog(
    plans: List<PlanSummary>,
    currentPlanId: Long?,
): StorePlanCatalog {
    val paidOrAll = plans.filter { !it.slug.isFree }.ifEmpty { plans }
    val tiers =
        plans.map { plan ->
            StorePlanTier(
                id = visualTierForSlug(plan.slug.rawValue),
                planId = plan.id.value,
                title = plan.title,
                tagline = plan.descriptionOrLimits(),
                monthlyPriceToman = plan.priceAmount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                yearlyPriceToman = plan.priceAmount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                icon = iconForSlug(plan.slug.rawValue),
                recommended = currentPlanId != null && plan.id.value != currentPlanId && plan.slug.isGrowth,
                bullets =
                    listOf(
                        StorePlanFeatureBullet("${toPersianDigits(plan.limits.maxProducts.toString())} محصول"),
                        StorePlanFeatureBullet("${toPersianDigits(plan.limits.maxImages.toString())} تصویر"),
                    ) + capabilityBenefitLines(plan.capabilities).map { StorePlanFeatureBullet(it) },
            )
        }

    val comparisonPlans = paidOrAll.take(3)
    val comparisonRows =
        if (comparisonPlans.size >= 2) {
            buildComparisonRows(comparisonPlans)
        } else {
            emptyList()
        }

    return StorePlanCatalog(
        yearlyDiscountPercent = 0,
        tiers = tiers,
        comparisonRows = comparisonRows,
    )
}

private fun PlanSummary.descriptionOrLimits(): String =
    "${toPersianDigits(limits.maxProducts.toString())} محصول · ${toPersianDigits(limits.maxImages.toString())} تصویر"

private fun capabilityBenefitLines(capabilities: PlanCapabilities): List<String> =
    buildList {
        when (capabilities.rankingBoost) {
            RankingBoostLevel.None -> Unit
            RankingBoostLevel.Slight -> add("نمایش بهتر محصولات در لیست‌ها")
            is RankingBoostLevel.Unknown -> add("رتبه‌بندی ویژه")
        }
        if (capabilities.contactButtons) add("دکمه‌های تماس فعال")
        if (capabilities.basicAnalytics) add("تحلیل‌های پایه")
        if (capabilities.advancedAnalytics) add("تحلیل‌های پیشرفته")
        if (capabilities.offersDiscounts) add("پیشنهادها و تخفیف‌ها")
    }

private fun visualTierForSlug(slug: String): StorePlanTierId =
    when (slug.lowercase()) {
        "free", "starter" -> StorePlanTierId.Start
        "growth" -> StorePlanTierId.Growth
        else -> StorePlanTierId.Professional
    }

private fun iconForSlug(slug: String) =
    when (slug.lowercase()) {
        "free", "starter" -> Res.drawable.ic_send
        "growth" -> Res.drawable.ic_star_outline
        else -> Res.drawable.ic_diamond
    }

private fun buildComparisonRows(plans: List<PlanSummary>): List<StorePlanComparisonRow> {
    fun cell(index: Int, text: String? = null, checked: Boolean = false, empty: Boolean = false) =
        when (index) {
            0 -> StorePlanComparisonCell(text = text, checked = checked, empty = empty)
            1 -> StorePlanComparisonCell(text = text, checked = checked, empty = empty)
            else -> StorePlanComparisonCell(text = text, checked = checked, empty = empty)
        }

    fun row(
        label: String,
        values: List<String?>,
        checks: List<Boolean> = List(3) { false },
    ): StorePlanComparisonRow {
        val padded = values + List(3 - values.size) { null }
        val checkPad = checks + List(3 - checks.size) { false }
        return StorePlanComparisonRow(
            featureLabel = label,
            start =
                if (padded[0] == null && !checkPad[0]) {
                    StorePlanComparisonCell(empty = true)
                } else {
                    cell(0, text = padded[0], checked = checkPad[0])
                },
            growth =
                if (padded.getOrNull(1) == null && !checkPad.getOrElse(1) { false }) {
                    StorePlanComparisonCell(empty = true)
                } else {
                    cell(1, text = padded.getOrNull(1), checked = checkPad.getOrElse(1) { false })
                },
            professional =
                if (padded.getOrNull(2) == null && !checkPad.getOrElse(2) { false }) {
                    StorePlanComparisonCell(empty = true)
                } else {
                    cell(2, text = padded.getOrNull(2), checked = checkPad.getOrElse(2) { false })
                },
        )
    }

    return listOf(
        row(
            "تعداد محصولات",
            plans.map { toPersianDigits(it.limits.maxProducts.toString()) },
        ),
        row(
            "تصاویر هر محصول",
            plans.map { toPersianDigits(it.limits.maxImages.toString()) },
        ),
        row(
            "تحلیل پایه",
            plans.map { null },
            plans.map { it.capabilities.basicAnalytics },
        ),
        row(
            "تحلیل پیشرفته",
            plans.map { null },
            plans.map { it.capabilities.advancedAnalytics },
        ),
        row(
            "دکمه تماس",
            plans.map { null },
            plans.map { it.capabilities.contactButtons },
        ),
    )
}
