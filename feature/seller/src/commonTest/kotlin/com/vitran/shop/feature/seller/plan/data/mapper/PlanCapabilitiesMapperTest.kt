package com.vitran.shop.feature.seller.plan.data.mapper

import com.vitran.shop.feature.seller.plan.domain.model.RankingBoostLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class PlanCapabilitiesMapperTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun mapsHeterogeneousVerifiedFeatures() {
        val features =
            buildJsonObject {
                put("ranking_boost", "slight")
                put("contact_buttons", true)
                put("basic_analytics", true)
                put("offers_discounts", true)
            }
        val caps = PlanCapabilitiesMapper.map(features)
        assertEquals(RankingBoostLevel.Slight, caps.rankingBoost)
        assertTrue(caps.contactButtons)
        assertTrue(caps.basicAnalytics)
        assertTrue(caps.offersDiscounts)
        assertFalse(caps.advancedAnalytics)
    }

    @Test
    fun missingBooleanFeatures_mapToFalse() {
        val caps = PlanCapabilitiesMapper.map(JsonObject(emptyMap()))
        assertEquals(RankingBoostLevel.None, caps.rankingBoost)
        assertFalse(caps.contactButtons)
        assertFalse(caps.basicAnalytics)
        assertFalse(caps.offersDiscounts)
        assertFalse(caps.advancedAnalytics)
    }

    @Test
    fun unknownFeatureKey_doesNotCrash() {
        val features =
            buildJsonObject {
                put("ranking_boost", "slight")
                putJsonObject("future_feature") {
                    put("something", true)
                }
            }
        val caps = PlanCapabilitiesMapper.map(features)
        assertEquals(RankingBoostLevel.Slight, caps.rankingBoost)
        assertFalse(caps.contactButtons)
    }

    @Test
    fun unknownRankingBoost_mapsSafely() {
        val features = buildJsonObject { put("ranking_boost", "extreme") }
        val caps = PlanCapabilitiesMapper.map(features)
        val boost = assertIs<RankingBoostLevel.Unknown>(caps.rankingBoost)
        assertEquals("extreme", boost.raw)
    }

    @Test
    fun unexpectedBooleanType_doesNotCoerceString() {
        val features = buildJsonObject { put("contact_buttons", "yes") }
        val caps = PlanCapabilitiesMapper.map(features)
        assertFalse(caps.contactButtons)
    }

    @Test
    fun advancedAnalytics_whenPresent() {
        val features = buildJsonObject { put("advanced_analytics", true) }
        assertTrue(PlanCapabilitiesMapper.map(features).advancedAnalytics)
    }

    @Test
    fun parseFromEncodedJsonObject() {
        val element =
            json.parseToJsonElement(
                """{"ranking_boost":"none","contact_buttons":false}""",
            ) as JsonObject
        val caps = PlanCapabilitiesMapper.map(element)
        assertEquals(RankingBoostLevel.None, caps.rankingBoost)
        assertFalse(caps.contactButtons)
    }
}
