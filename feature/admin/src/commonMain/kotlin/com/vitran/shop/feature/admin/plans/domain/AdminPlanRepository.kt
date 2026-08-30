package com.vitran.shop.feature.admin.plans.domain

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.seller.plan.domain.model.PlanId

interface AdminPlanRepository {
    suspend fun getPlans(): AppResult<List<AdminPlan>>
    suspend fun create(command: CreatePlanCommand): AppResult<AdminPlan>
    suspend fun update(command: UpdatePlanCommand): AppResult<AdminPlan>
    suspend fun delete(plan: AdminPlan): DeleteAdminPlanResult
}
