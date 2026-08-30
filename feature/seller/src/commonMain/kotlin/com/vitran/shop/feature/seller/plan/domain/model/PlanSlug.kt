package com.vitran.shop.feature.seller.plan.domain.model

/**
 * Server plan slug. Known values are helpers only — capabilities come from
 * [PlanCapabilities] / limits, not from slug checks in UI.
 */
data class PlanSlug(val rawValue: String) {
    val isFree: Boolean get() = rawValue.equals(FREE, ignoreCase = true)
    val isStarter: Boolean get() = rawValue.equals(STARTER, ignoreCase = true)
    val isGrowth: Boolean get() = rawValue.equals(GROWTH, ignoreCase = true)
    val isBusiness: Boolean get() = rawValue.equals(BUSINESS, ignoreCase = true)

    companion object {
        const val FREE = "free"
        const val STARTER = "starter"
        const val GROWTH = "growth"
        const val BUSINESS = "business"

        fun of(raw: String): PlanSlug = PlanSlug(raw)
    }
}
