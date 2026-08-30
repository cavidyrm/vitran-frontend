package com.vitran.shop.feature.seller.referral.domain

import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReferralCreditEligibilityTest {
    @Test
    fun freeAndStarter_eligible() {
        assertTrue(ReferralCreditEligibility.isEligiblePlanSlug(PlanSlug.of("free")))
        assertTrue(ReferralCreditEligibility.isEligiblePlanSlug(PlanSlug.of("starter")))
    }

    @Test
    fun growthAndBusiness_notEligible() {
        assertFalse(ReferralCreditEligibility.isEligibleOrUnknown(PlanSlug.of("growth")))
        assertFalse(ReferralCreditEligibility.isEligibleOrUnknown(PlanSlug.of("business")))
    }

    @Test
    fun unknownSlug_notAssumedEligible() {
        assertFalse(ReferralCreditEligibility.isEligibleOrUnknown(PlanSlug.of("enterprise")))
    }
}
