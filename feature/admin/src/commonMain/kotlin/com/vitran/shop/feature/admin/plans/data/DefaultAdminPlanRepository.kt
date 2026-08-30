package com.vitran.shop.feature.admin.plans.data

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.admin.plans.domain.AdminPlan
import com.vitran.shop.feature.admin.plans.domain.AdminPlanRepository
import com.vitran.shop.feature.admin.plans.domain.CreatePlanCommand
import com.vitran.shop.feature.admin.plans.domain.DeleteAdminPlanResult
import com.vitran.shop.feature.admin.plans.domain.UpdatePlanCommand
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug
import com.vitran.shop.feature.seller.plan.domain.repository.PlanRepository

internal class DefaultAdminPlanRepository(
    private val api: AdminPlanApi,
    private val publicPlanRepository: PlanRepository,
) : AdminPlanRepository {
    override suspend fun getPlans() = api.getPlans().mapSuccess { it.plans.map(AdminPlanDto::toDomain) }

    override suspend fun create(command: CreatePlanCommand) =
        api.create(command).mapMutation { it.plan.toDomain() }

    override suspend fun update(command: UpdatePlanCommand) =
        api.update(command).mapMutation { it.plan.toDomain() }

    override suspend fun delete(plan: AdminPlan): DeleteAdminPlanResult {
        if (plan.isFreeDeletionProtected()) return DeleteAdminPlanResult.FreePlanCannotBeDeleted
        return when (val result = api.delete(plan.id)) {
            is AppResult.Success -> {
                publicPlanRepository.refreshPlans()
                DeleteAdminPlanResult.Success
            }
            is AppResult.Failure -> {
                if (isFreePlanDeleteFailure(result.error)) DeleteAdminPlanResult.FreePlanCannotBeDeleted
                else DeleteAdminPlanResult.Failure(result.error)
            }
        }
    }

    private suspend inline fun <T, R> AppResult<T>.mapMutation(transform: (T) -> R): AppResult<R> =
        when (this) {
            is AppResult.Success -> {
                val value = transform(value)
                publicPlanRepository.refreshPlans()
                AppResult.Success(value)
            }
            is AppResult.Failure -> this
        }
}

internal fun AdminPlan.isFreeDeletionProtected(): Boolean = slug.isFree

internal fun isFreePlanDeleteFailure(error: AppError): Boolean =
    error is AppError.Conflict || error.message.orEmpty().contains("free plan", ignoreCase = true)

private fun AdminPlanDto.toDomain() = AdminPlan(
    id = PlanId(id), slug = PlanSlug.of(slug), title = title, description = description,
    priceAmount = priceAmount, durationDays = durationDays, maxProducts = maxProducts,
    maxImages = maxImages, maxShops = maxShops, features = features, active = active, sortOrder = sortOrder,
)

private inline fun <T, R> AppResult<T>.mapSuccess(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(value))
    is AppResult.Failure -> this
}
