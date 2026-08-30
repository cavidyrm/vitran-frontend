package com.vitran.shop.feature.seller.plan.domain.model

data class PlanSummary(
    val id: PlanId,
    val slug: PlanSlug,
    val title: String,
    val priceAmount: Long,
    val durationDays: Int?,
    val limits: PlanLimits,
    val capabilities: PlanCapabilities,
    val sortOrder: Int,
    val active: Boolean = true,
)

data class PlanDetails(
    val id: PlanId,
    val slug: PlanSlug,
    val title: String,
    val description: String?,
    val priceAmount: Long,
    val durationDays: Int?,
    val limits: PlanLimits,
    val capabilities: PlanCapabilities,
    val sortOrder: Int,
    val active: Boolean = true,
) {
    fun toSummary(): PlanSummary =
        PlanSummary(
            id = id,
            slug = slug,
            title = title,
            priceAmount = priceAmount,
            durationDays = durationDays,
            limits = limits,
            capabilities = capabilities,
            sortOrder = sortOrder,
            active = active,
        )
}
