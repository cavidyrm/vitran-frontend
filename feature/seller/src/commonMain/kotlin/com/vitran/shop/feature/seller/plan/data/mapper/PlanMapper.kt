package com.vitran.shop.feature.seller.plan.data.mapper

import com.vitran.shop.feature.seller.plan.data.remote.dto.PublicPlanDetailDto
import com.vitran.shop.feature.seller.plan.data.remote.dto.PublicPlanListItemDto
import com.vitran.shop.feature.seller.plan.domain.model.PlanDetails
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanLimits
import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug
import com.vitran.shop.feature.seller.plan.domain.model.PlanSummary

internal fun PublicPlanListItemDto.toDomain(): PlanSummary =
    PlanSummary(
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
        capabilities = PlanCapabilitiesMapper.map(features),
        sortOrder = sortOrder,
        active = active,
    )

internal fun PublicPlanDetailDto.toDomain(): PlanDetails =
    PlanDetails(
        id = PlanId(id),
        slug = PlanSlug.of(slug),
        title = title,
        description = description,
        priceAmount = priceAmount,
        durationDays = durationDays,
        limits = PlanLimits(
            maxProducts = maxProducts,
            maxImages = maxImages,
            maxShops = maxShops,
        ),
        capabilities = PlanCapabilitiesMapper.map(features),
        sortOrder = sortOrder,
        active = active,
    )
