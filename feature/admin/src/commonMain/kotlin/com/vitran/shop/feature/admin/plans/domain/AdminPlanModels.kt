package com.vitran.shop.feature.admin.plans.domain

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug
import kotlinx.serialization.json.JsonObject

data class AdminPlan(
    val id: PlanId,
    val slug: PlanSlug,
    val title: String,
    val description: String?,
    val priceAmount: Long,
    val durationDays: Int?,
    val maxProducts: Int,
    val maxImages: Int,
    val maxShops: Int,
    val features: JsonObject,
    val active: Boolean,
    val sortOrder: Int,
)

data class CreatePlanCommand(
    val slug: PlanSlug,
    val title: String,
    val description: String? = null,
    val priceAmount: Long,
    val durationDays: Int?,
    val maxProducts: Int,
    val maxImages: Int,
    val maxShops: Int,
    val features: JsonObject,
    val active: Boolean = true,
    val sortOrder: Int = 0,
)

data class UpdatePlanCommand(
    val id: PlanId,
    val slug: PlanSlug? = null,
    val title: String? = null,
    val description: String? = null,
    val priceAmount: Long? = null,
    val durationDays: Int? = null,
    val maxProducts: Int? = null,
    val maxImages: Int? = null,
    val maxShops: Int? = null,
    val features: JsonObject? = null,
    val featuresUpdated: Boolean = false,
    val active: Boolean? = null,
    val sortOrder: Int? = null,
)

sealed interface DeleteAdminPlanResult {
    data object Success : DeleteAdminPlanResult
    data object FreePlanCannotBeDeleted : DeleteAdminPlanResult
    data class Failure(val error: AppError) : DeleteAdminPlanResult
}
