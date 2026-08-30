package com.vitran.shop.feature.seller.referral.domain

import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug

/**
 * Centralized Free/Starter eligibility for referral credit apply (backend contract).
 * Do not spread slug checks through UI.
 */
object ReferralCreditEligibility {
    fun isEligiblePlanSlug(slug: PlanSlug): Boolean =
        slug.isFree || slug.isStarter

    fun isEligibleOrUnknown(slug: PlanSlug): Boolean =
        when {
            slug.isFree || slug.isStarter -> true
            slug.isGrowth || slug.isBusiness -> false
            else -> false // unknown future plans — do not assume eligible
        }
}
