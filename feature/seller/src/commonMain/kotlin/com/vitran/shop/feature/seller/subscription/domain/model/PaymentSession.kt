package com.vitran.shop.feature.seller.subscription.domain.model

import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import kotlin.jvm.JvmInline

@JvmInline
value class PaymentId(val value: Long)

/**
 * Payment initiation metadata. Does **not** prove subscription entitlement.
 */
data class PaymentSession(
    val paymentId: PaymentId,
    /** Opaque provider identifier — do not parse or log unnecessarily. */
    val authority: String,
    /** Server-provided handoff URL — use unchanged after scheme validation. */
    val paymentUrl: String,
)

data class PurchasePlanCommand(
    val shopId: com.vitran.shop.feature.marketplace.shop.domain.model.ShopId,
    val planId: PlanId,
)
