package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vitran.shop.feature.admin.plans.domain.AdminPlan
import com.vitran.shop.feature.admin.plans.domain.CreatePlanCommand
import com.vitran.shop.feature.admin.plans.domain.UpdatePlanCommand
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug
import com.vitran.shop.ui.components.admin.AdminSelectOption
import com.vitran.shop.ui.sections.account.toPersianDigits
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import org.jetbrains.compose.resources.DrawableResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_diamond
import vitranshop.shared.generated.resources.ic_send
import vitranshop.shared.generated.resources.ic_star_outline

enum class AdminPlanStatus {
    Active,
    Archived,
}

enum class AdminPlanAnalytics {
    Basic,
    Advanced,
    Professional,
}

@Immutable
data class AdminPlanDefinition(
    val id: String,
    val name: String,
    val slug: String,
    val tagline: String,
    val monthlyPriceToman: Int,
    val yearlyPriceToman: Int,
    val productLimit: Int?,
    val specialSlots: Int?,
    val analytics: AdminPlanAnalytics,
    val teamMembers: Int?,
    val status: AdminPlanStatus,
    val popular: Boolean,
    val higherInList: Boolean,
    val customReports: Boolean,
    val prioritySupport: Boolean,
    val icon: DrawableResource,
)

@Immutable
data class AdminPlansStats(
    val activePlans: Int,
    val popularPlanName: String,
    val activeStores: Int,
    val monthlyRevenueToman: Int,
)

@Immutable
data class AdminPlanFormState(
    val id: String? = null,
    val name: String = "",
    val slug: String = "",
    val tagline: String = "",
    val monthlyPrice: String = "",
    val yearlyPrice: String = "",
    val productLimit: String = "",
    val specialSlots: String = "",
    val teamMembers: String = "",
    val statusId: String = AdminPlanStatus.Active.name,
    val analyticsId: String = AdminPlanAnalytics.Basic.name,
    val higherInList: Boolean = false,
    val customReports: Boolean = false,
    val prioritySupport: Boolean = false,
    val isNew: Boolean = true,
)

fun AdminPlanDefinition.toFormState(): AdminPlanFormState =
    AdminPlanFormState(
        id = id,
        name = name,
        slug = slug,
        tagline = tagline,
        monthlyPrice = monthlyPriceToman.toString(),
        yearlyPrice = yearlyPriceToman.toString(),
        productLimit = productLimit?.toString().orEmpty(),
        specialSlots = specialSlots?.toString().orEmpty(),
        teamMembers = teamMembers?.toString().orEmpty(),
        statusId = status.name,
        analyticsId = analytics.name,
        higherInList = higherInList,
        customReports = customReports,
        prioritySupport = prioritySupport,
        isNew = false,
    )

fun emptyAdminPlanForm(): AdminPlanFormState = AdminPlanFormState(isNew = true)

fun AdminPlanFormState.yearlyDiscountPercent(): Int? {
    val monthly = monthlyPrice.toIntOrNull() ?: return null
    val yearly = yearlyPrice.toIntOrNull() ?: return null
    if (monthly <= 0 || yearly <= 0) return null
    val full = monthly * 12
    if (yearly >= full) return null
    return (((full - yearly).toDouble() / full) * 100).toInt().coerceAtLeast(1)
}

fun AdminPlanFormState.toDefinition(existingIcon: DrawableResource?): AdminPlanDefinition {
    val monthly = monthlyPrice.toIntOrNull() ?: 0
    val yearly = yearlyPrice.toIntOrNull() ?: monthly * 12
    return AdminPlanDefinition(
        id = id ?: "plan-${slug.ifBlank { name }.ifBlank { "new" }}",
        name = name.ifBlank { "پلن جدید" },
        slug = slug.ifBlank { "new-plan" },
        tagline = tagline.ifBlank { "توضیح پلن" },
        monthlyPriceToman = monthly,
        yearlyPriceToman = yearly,
        productLimit = productLimit.toIntOrNull(),
        specialSlots = specialSlots.toIntOrNull(),
        analytics = runCatching { AdminPlanAnalytics.valueOf(analyticsId) }
            .getOrDefault(AdminPlanAnalytics.Basic),
        teamMembers = teamMembers.toIntOrNull(),
        status = runCatching { AdminPlanStatus.valueOf(statusId) }
            .getOrDefault(AdminPlanStatus.Active),
        popular = false,
        higherInList = higherInList,
        customReports = customReports,
        prioritySupport = prioritySupport,
        icon = existingIcon ?: Res.drawable.ic_star_outline,
    )
}

fun AdminPlan.toUiDefinition(): AdminPlanDefinition {
    val basicAnalytics = features["basic_analytics"]?.let { (it as? JsonPrimitive)?.booleanOrNull } == true
    val advancedAnalytics = features["advanced_analytics"]?.let { (it as? JsonPrimitive)?.booleanOrNull } == true
    return AdminPlanDefinition(
        id = id.value.toString(),
        name = title,
        slug = slug.rawValue,
        tagline = description.orEmpty(),
        monthlyPriceToman = priceAmount.toInt(),
        yearlyPriceToman = priceAmount.toInt(),
        productLimit = maxProducts,
        specialSlots = null,
        analytics = when {
            advancedAnalytics -> AdminPlanAnalytics.Advanced
            basicAnalytics -> AdminPlanAnalytics.Basic
            else -> AdminPlanAnalytics.Professional
        },
        teamMembers = null,
        status = if (active) AdminPlanStatus.Active else AdminPlanStatus.Archived,
        popular = false,
        higherInList = sortOrder < 10,
        customReports = features.boolean("custom_reports"),
        prioritySupport = features.boolean("priority_support"),
        icon = Res.drawable.ic_star_outline,
    )
}

fun AdminPlanFormState.toCreateCommand(): CreatePlanCommand =
    CreatePlanCommand(
        slug = PlanSlug.of(slug.trim()),
        title = name.trim(),
        description = tagline.trim().ifBlank { null },
        priceAmount = monthlyPrice.toLongOrNull() ?: 0L,
        durationDays = 30,
        maxProducts = productLimit.toIntOrNull() ?: 0,
        maxImages = specialSlots.toIntOrNull() ?: 0,
        maxShops = teamMembers.toIntOrNull() ?: 1,
        features = featureObject(JsonObject(emptyMap())),
        active = statusId == AdminPlanStatus.Active.name,
        sortOrder = if (higherInList) 0 else 100,
    )

fun AdminPlanFormState.toUpdateCommand(existing: AdminPlan): UpdatePlanCommand =
    UpdatePlanCommand(
        id = PlanId(existing.id.value),
        slug = PlanSlug.of(slug.trim()),
        title = name.trim(),
        description = tagline.trim(),
        priceAmount = monthlyPrice.toLongOrNull() ?: existing.priceAmount,
        durationDays = existing.durationDays,
        maxProducts = productLimit.toIntOrNull() ?: existing.maxProducts,
        maxImages = specialSlots.toIntOrNull() ?: existing.maxImages,
        maxShops = teamMembers.toIntOrNull() ?: existing.maxShops,
        features = featureObject(existing.features),
        featuresUpdated = true,
        active = statusId == AdminPlanStatus.Active.name,
        sortOrder = if (higherInList) 0 else existing.sortOrder.coerceAtLeast(10),
    )

private fun AdminPlanFormState.featureObject(existing: JsonObject): JsonObject {
    val values = existing.toMutableMap()
    values["contact_buttons"] = JsonPrimitive(true)
    values["basic_analytics"] = JsonPrimitive(analyticsId == AdminPlanAnalytics.Basic.name)
    values["advanced_analytics"] = JsonPrimitive(analyticsId != AdminPlanAnalytics.Basic.name)
    values["offers_discounts"] = JsonPrimitive(higherInList)
    values["custom_reports"] = JsonPrimitive(customReports)
    values["priority_support"] = JsonPrimitive(prioritySupport)
    return JsonObject(values)
}

private fun JsonObject.boolean(key: String): Boolean =
    (get(key) as? JsonPrimitive)?.booleanOrNull == true

fun AdminPlanAnalytics.label(): String =
    when (this) {
        AdminPlanAnalytics.Basic -> "پایه"
        AdminPlanAnalytics.Advanced -> "پیشرفته"
        AdminPlanAnalytics.Professional -> "حرفه‌ای"
    }

fun AdminPlanStatus.label(): String =
    when (this) {
        AdminPlanStatus.Active -> "فعال"
        AdminPlanStatus.Archived -> "آرشیو"
    }

fun productLimitLabel(limit: Int?): String =
    when (limit) {
        null -> "نامحدود"
        else -> toPersianDigits(limit)
    }

fun specialSlotsLabel(slots: Int?): String =
    when (slots) {
        null, 0 -> "ندارد"
        else -> toPersianDigits(slots)
    }

fun teamMembersLabel(count: Int?): String =
    when (count) {
        null -> "نامحدود"
        else -> "${toPersianDigits(count)} نفر"
    }

val AdminPlanStatusOptions = listOf(
    AdminSelectOption(AdminPlanStatus.Active.name, "فعال"),
    AdminSelectOption(AdminPlanStatus.Archived.name, "آرشیو"),
)

val AdminPlanAnalyticsOptions = listOf(
    AdminSelectOption(AdminPlanAnalytics.Basic.name, "پایه"),
    AdminSelectOption(AdminPlanAnalytics.Advanced.name, "پیشرفته"),
    AdminSelectOption(AdminPlanAnalytics.Professional.name, "حرفه‌ای"),
)

fun mockAdminPlans(): List<AdminPlanDefinition> =
    listOf(
        AdminPlanDefinition(
            id = "start",
            name = "شروع",
            slug = "shoro",
            tagline = "برای شروع یک فروشگاه حرفه‌ای",
            monthlyPriceToman = 390_000,
            yearlyPriceToman = 3_900_000,
            productLimit = 50,
            specialSlots = null,
            analytics = AdminPlanAnalytics.Basic,
            teamMembers = null,
            status = AdminPlanStatus.Active,
            popular = false,
            higherInList = false,
            customReports = false,
            prioritySupport = false,
            icon = Res.drawable.ic_send,
        ),
        AdminPlanDefinition(
            id = "growth",
            name = "رشد",
            slug = "roshd",
            tagline = "برای رشد و توسعه کسب‌وکار",
            monthlyPriceToman = 990_000,
            yearlyPriceToman = 9_900_000,
            productLimit = 500,
            specialSlots = 20,
            analytics = AdminPlanAnalytics.Advanced,
            teamMembers = 5,
            status = AdminPlanStatus.Active,
            popular = true,
            higherInList = true,
            customReports = false,
            prioritySupport = true,
            icon = Res.drawable.ic_star_outline,
        ),
        AdminPlanDefinition(
            id = "pro",
            name = "حرفه‌ای",
            slug = "herfei",
            tagline = "برای کسب‌وکارهای بزرگ",
            monthlyPriceToman = 2_990_000,
            yearlyPriceToman = 29_900_000,
            productLimit = null,
            specialSlots = 100,
            analytics = AdminPlanAnalytics.Professional,
            teamMembers = null,
            status = AdminPlanStatus.Active,
            popular = false,
            higherInList = true,
            customReports = true,
            prioritySupport = true,
            icon = Res.drawable.ic_diamond,
        ),
    )

fun mockAdminPlansStats(plans: List<AdminPlanDefinition>): AdminPlansStats =
    AdminPlansStats(
        activePlans = plans.count { it.status == AdminPlanStatus.Active },
        popularPlanName = plans.firstOrNull { it.popular }?.name ?: plans.firstOrNull()?.name.orEmpty(),
        activeStores = 120,
        monthlyRevenueToman = 99_000_000,
    )

object AdminPlansTokens {
    val SoftPurple = Color(0xFFF3EEFF)
    val PopularBorder = Color(0xFF5A31F4)
    val SuccessSoft = Color(0xFFDCFCE7)
    val SuccessText = Color(0xFF15803D)
}
