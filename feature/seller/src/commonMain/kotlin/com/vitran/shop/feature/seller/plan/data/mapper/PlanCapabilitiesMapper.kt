package com.vitran.shop.feature.seller.plan.data.mapper

import com.vitran.shop.feature.seller.plan.domain.model.PlanCapabilities
import com.vitran.shop.feature.seller.plan.domain.model.RankingBoostLevel
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull

/**
 * Maps heterogeneous backend `features` JSON into typed [PlanCapabilities].
 * Unknown keys are ignored. Unexpected value types fall back safely.
 */
internal object PlanCapabilitiesMapper {

    private const val KEY_RANKING_BOOST = "ranking_boost"
    private const val KEY_CONTACT_BUTTONS = "contact_buttons"
    private const val KEY_BASIC_ANALYTICS = "basic_analytics"
    private const val KEY_OFFERS_DISCOUNTS = "offers_discounts"
    private const val KEY_ADVANCED_ANALYTICS = "advanced_analytics"

    fun map(features: JsonObject): PlanCapabilities =
        PlanCapabilities(
            rankingBoost = mapRankingBoost(features[KEY_RANKING_BOOST]),
            contactButtons = mapBoolean(features[KEY_CONTACT_BUTTONS]),
            basicAnalytics = mapBoolean(features[KEY_BASIC_ANALYTICS]),
            offersDiscounts = mapBoolean(features[KEY_OFFERS_DISCOUNTS]),
            advancedAnalytics = mapBoolean(features[KEY_ADVANCED_ANALYTICS]),
        )

    private fun mapRankingBoost(element: JsonElement?): RankingBoostLevel {
        if (element == null) return RankingBoostLevel.None
        val primitive = element as? JsonPrimitive ?: return RankingBoostLevel.Unknown(element.toString())
        if (primitive.isString) {
            return RankingBoostLevel.parse(primitive.contentOrNull)
        }
        // Unexpected non-string (e.g. boolean) — treat as contract mismatch, not crash.
        return RankingBoostLevel.Unknown(primitive.content)
    }

    private fun mapBoolean(element: JsonElement?): Boolean {
        if (element == null) return false
        val primitive = element as? JsonPrimitive ?: return false
        primitive.booleanOrNull?.let { return it }
        // Do not coerce arbitrary strings like "yes".
        return false
    }
}
