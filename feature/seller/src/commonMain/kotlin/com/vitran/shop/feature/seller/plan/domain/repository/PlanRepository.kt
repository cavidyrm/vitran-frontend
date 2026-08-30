package com.vitran.shop.feature.seller.plan.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.seller.plan.domain.model.PlanDetails
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanSummary

interface PlanRepository {
    suspend fun getPlans(forceRefresh: Boolean = false): AppResult<List<PlanSummary>>

    suspend fun getPlan(planId: PlanId, forceRefresh: Boolean = false): AppResult<PlanDetails>

    suspend fun refreshPlans(): AppResult<List<PlanSummary>>
}
