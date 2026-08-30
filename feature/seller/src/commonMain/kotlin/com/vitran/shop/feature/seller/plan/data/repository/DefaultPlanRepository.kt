package com.vitran.shop.feature.seller.plan.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.seller.plan.data.mapper.toDomain
import com.vitran.shop.feature.seller.plan.data.remote.PlanApi
import com.vitran.shop.feature.seller.plan.domain.model.PlanDetails
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanSummary
import com.vitran.shop.feature.seller.plan.domain.repository.PlanRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Public plan catalog cache. Not user-scoped — survives logout.
 */
internal class DefaultPlanRepository(
    private val planApi: PlanApi,
) : PlanRepository {

    private val mutex = Mutex()
    private var cachedPlans: List<PlanSummary>? = null
    private val cachedDetails = mutableMapOf<PlanId, PlanDetails>()

    override suspend fun getPlans(forceRefresh: Boolean): AppResult<List<PlanSummary>> {
        if (!forceRefresh) {
            mutex.withLock {
                cachedPlans?.let { return AppResult.Success(it) }
            }
        }
        return fetchAndCachePlans()
    }

    override suspend fun refreshPlans(): AppResult<List<PlanSummary>> =
        getPlans(forceRefresh = true)

    override suspend fun getPlan(planId: PlanId, forceRefresh: Boolean): AppResult<PlanDetails> {
        if (!forceRefresh) {
            mutex.withLock {
                cachedDetails[planId]?.let { return AppResult.Success(it) }
            }
        }
        return when (val result = planApi.getPlan(planId)) {
            is AppResult.Success -> {
                val details = result.value.plan.toDomain()
                mutex.withLock { cachedDetails[planId] = details }
                AppResult.Success(details)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }

    private suspend fun fetchAndCachePlans(): AppResult<List<PlanSummary>> =
        when (val result = planApi.getPlans()) {
            is AppResult.Success -> {
                // Preserve server order (sort_order); do not re-sort locally.
                val plans = result.value.plans.map { it.toDomain() }
                mutex.withLock {
                    cachedPlans = plans
                    plans.forEach { summary ->
                        // List projection lacks description — only fill detail cache if absent.
                    }
                }
                AppResult.Success(plans)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    /** Test helper: whether list cache is populated. */
    internal suspend fun hasCachedPlans(): Boolean =
        mutex.withLock { cachedPlans != null }
}
