package com.vitran.shop.feature.seller.plan.domain.model

/**
 * Typed plan capabilities mapped from heterogeneous backend `features` JSON.
 * Missing boolean keys map to false. Unknown keys are ignored at the Data layer.
 */
data class PlanCapabilities(
    val rankingBoost: RankingBoostLevel = RankingBoostLevel.None,
    val contactButtons: Boolean = false,
    val basicAnalytics: Boolean = false,
    val offersDiscounts: Boolean = false,
    /** Phase 10 readiness — Postman gates Business CSV export on this key when present. */
    val advancedAnalytics: Boolean = false,
)

sealed class RankingBoostLevel {
    data object None : RankingBoostLevel()
    data object Slight : RankingBoostLevel()
    data class Unknown(val raw: String) : RankingBoostLevel()

    companion object {
        fun parse(raw: String?): RankingBoostLevel =
            when (raw?.lowercase()) {
                null, "", "none" -> None
                "slight" -> Slight
                else -> Unknown(raw)
            }
    }
}
