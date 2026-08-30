package com.vitran.shop.feature.seller.subscription.data.mapper

import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanLimits
import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug
import com.vitran.shop.feature.seller.subscription.data.remote.dto.PaymentSessionDto
import com.vitran.shop.feature.seller.subscription.data.remote.dto.ShopSubscriptionDto
import com.vitran.shop.feature.seller.subscription.data.remote.dto.SubscriptionPlanDto
import com.vitran.shop.feature.seller.subscription.domain.model.PaymentId
import com.vitran.shop.feature.seller.subscription.domain.model.PaymentSession
import com.vitran.shop.feature.seller.subscription.domain.model.ShopSubscription
import com.vitran.shop.feature.seller.subscription.domain.model.SubscriptionPlan
import com.vitran.shop.feature.seller.subscription.domain.model.SubscriptionStatus
import kotlinx.datetime.Instant

internal fun ShopSubscriptionDto.toDomain(): ShopSubscription =
    ShopSubscription(
        shopId = ShopId(shopId),
        plan = plan.toDomain(),
        status = SubscriptionStatus.parse(status),
        startedAt = Instant.parse(startedAt),
        expiresAt = expiresAt?.let { runCatching { Instant.parse(it) }.getOrNull() },
        daysRemaining = daysRemaining,
    )

internal fun SubscriptionPlanDto.toDomain(): SubscriptionPlan =
    SubscriptionPlan(
        id = PlanId(id),
        slug = PlanSlug.of(slug),
        title = title,
        priceAmount = priceAmount,
        durationDays = durationDays,
        limits = PlanLimits(
            maxProducts = maxProducts,
            maxImages = maxImages,
            maxShops = maxShops,
        ),
    )

internal fun PaymentSessionDto.toDomain(): PaymentSession =
    PaymentSession(
        paymentId = PaymentId(paymentId),
        authority = authority,
        paymentUrl = paymentUrl,
    )
