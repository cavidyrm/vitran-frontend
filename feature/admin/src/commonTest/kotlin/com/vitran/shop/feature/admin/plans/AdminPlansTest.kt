package com.vitran.shop.feature.admin.plans

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.feature.admin.plans.data.isFreeDeletionProtected
import com.vitran.shop.feature.admin.plans.data.isFreePlanDeleteFailure
import com.vitran.shop.feature.admin.plans.data.toPatchBody
import com.vitran.shop.feature.admin.plans.domain.AdminPlan
import com.vitran.shop.feature.admin.plans.domain.UpdatePlanCommand
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

class AdminPlansTest {
    @Test
    fun updateFeatures_preservesUnknownKeys_andOmissionPolicy() {
        val features = buildJsonObject {
            put("basic_analytics", true)
            put("future_capability", buildJsonObject { put("level", 3) })
        }
        val included = UpdatePlanCommand(
            id = PlanId(1), features = features, featuresUpdated = true,
        ).toPatchBody()
        val omitted = UpdatePlanCommand(
            id = PlanId(1), features = features, featuresUpdated = false,
        ).toPatchBody()

        assertSame(features, included["features"])
        assertEquals(JsonPrimitive(3), included["features"]?.let { (it as kotlinx.serialization.json.JsonObject)["future_capability"] }
            ?.let { (it as kotlinx.serialization.json.JsonObject)["level"] })
        assertFalse("features" in omitted)
    }

    @Test
    fun freePlan_isRecognizedBeforeDeleteRequest() {
        val plan = AdminPlan(
            id = PlanId(1), slug = PlanSlug.of("FREE"), title = "رایگان", description = null,
            priceAmount = 0, durationDays = null, maxProducts = 1, maxImages = 1, maxShops = 1,
            features = buildJsonObject {}, active = true, sortOrder = 0,
        )
        assertEquals(true, plan.isFreeDeletionProtected())
        assertEquals("free", PlanSlug.FREE)
        assertEquals(true, isFreePlanDeleteFailure(AppError.Conflict()))
        assertEquals(true, isFreePlanDeleteFailure(AppError.Server(message = "Free plan cannot be deleted")))
    }
}
