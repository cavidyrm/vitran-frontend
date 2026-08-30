package com.vitran.shop.feature.seller.plan.data.repository

import com.vitran.shop.core.database.VitranDatabase
import com.vitran.shop.core.database.entity.PlanEntity
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.seller.plan.data.mapper.PlanCapabilitiesMapper
import com.vitran.shop.feature.seller.plan.data.mapper.toDomain
import com.vitran.shop.feature.seller.plan.data.remote.PlanApi
import com.vitran.shop.feature.seller.plan.data.remote.dto.PublicPlanDetailDto
import com.vitran.shop.feature.seller.plan.data.remote.dto.PublicPlanListItemDto
import com.vitran.shop.feature.seller.plan.domain.model.PlanDetails
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.plan.domain.model.PlanLimits
import com.vitran.shop.feature.seller.plan.domain.model.PlanSlug
import com.vitran.shop.feature.seller.plan.domain.model.PlanSummary
import com.vitran.shop.feature.seller.plan.domain.repository.PlanRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.time.Clock

/**
 * Public plan catalog cache. Not user-scoped — survives logout.
 */
internal class DefaultPlanRepository(
    private val planApi: PlanApi,
    private val database: VitranDatabase,
) : PlanRepository {

    private val planDao get() = database.planDao()

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getPlans(forceRefresh: Boolean): AppResult<List<PlanSummary>> {
        if (!forceRefresh) {
            val cached = planDao.getAll()
            if (cached.isNotEmpty()) {
                return AppResult.Success(cached.map { it.toSummary(json) })
            }
        }
        return fetchAndCachePlans()
    }

    override suspend fun refreshPlans(): AppResult<List<PlanSummary>> =
        getPlans(forceRefresh = true)

    override suspend fun getPlan(planId: PlanId, forceRefresh: Boolean): AppResult<PlanDetails> {
        if (!forceRefresh) {
            planDao.getById(planId.value)?.let { entity ->
                return AppResult.Success(entity.toDetails(json))
            }
        }
        return when (val result = planApi.getPlan(planId)) {
            is AppResult.Success -> {
                val dto = result.value.plan
                val details = dto.toDomain()
                val now = Clock.System.now().toEpochMilliseconds()
                planDao.insertAll(listOf(dto.toEntity(now, json)))
                AppResult.Success(details)
            }
            is AppResult.Failure -> {
                val cached = planDao.getById(planId.value)
                if (cached != null) {
                    AppResult.Success(cached.toDetails(json))
                } else {
                    AppResult.Failure(result.error)
                }
            }
        }
    }

    private suspend fun fetchAndCachePlans(): AppResult<List<PlanSummary>> =
        when (val result = planApi.getPlans()) {
            is AppResult.Success -> {
                // Preserve server order (sort_order); do not re-sort locally.
                val dtos = result.value.plans
                val plans = dtos.map { it.toDomain() }
                val now = Clock.System.now().toEpochMilliseconds()
                planDao.replaceAll(dtos.map { it.toEntity(now, json) })
                AppResult.Success(plans)
            }
            is AppResult.Failure -> {
                val cached = planDao.getAll()
                if (cached.isNotEmpty()) {
                    AppResult.Success(cached.map { it.toSummary(json) })
                } else {
                    AppResult.Failure(result.error)
                }
            }
        }

    /** Test helper: whether list cache is populated. */
    internal suspend fun hasCachedPlans(): Boolean =
        planDao.getAll().isNotEmpty()
}

private fun PublicPlanListItemDto.toEntity(fetchedAt: Long, json: Json): PlanEntity =
    PlanEntity(
        id = id,
        slug = slug,
        title = title,
        description = null,
        priceAmount = priceAmount,
        durationDays = durationDays,
        maxProducts = maxProducts,
        maxImages = maxImages,
        maxShops = maxShops,
        featuresJson = json.encodeToString(JsonObject.serializer(), features),
        sortOrder = sortOrder,
        active = active,
        fetchedAt = fetchedAt,
    )

private fun PublicPlanDetailDto.toEntity(fetchedAt: Long, json: Json): PlanEntity =
    PlanEntity(
        id = id,
        slug = slug,
        title = title,
        description = description,
        priceAmount = priceAmount,
        durationDays = durationDays,
        maxProducts = maxProducts,
        maxImages = maxImages,
        maxShops = maxShops,
        featuresJson = json.encodeToString(JsonObject.serializer(), features),
        sortOrder = sortOrder,
        active = active,
        fetchedAt = fetchedAt,
    )

private fun PlanEntity.toSummary(json: Json): PlanSummary {
    val features = json.decodeFromString(JsonObject.serializer(), featuresJson)
    return PlanSummary(
        id = PlanId(id),
        slug = PlanSlug.of(slug),
        title = title,
        priceAmount = priceAmount,
        durationDays = durationDays,
        limits = PlanLimits(
            maxProducts = maxProducts ?: 0,
            maxImages = maxImages ?: 0,
            maxShops = maxShops ?: 0,
        ),
        capabilities = PlanCapabilitiesMapper.map(features),
        sortOrder = sortOrder,
        active = active,
    )
}

private fun PlanEntity.toDetails(json: Json): PlanDetails {
    val features = json.decodeFromString(JsonObject.serializer(), featuresJson)
    return PlanDetails(
        id = PlanId(id),
        slug = PlanSlug.of(slug),
        title = title,
        description = description,
        priceAmount = priceAmount,
        durationDays = durationDays,
        limits = PlanLimits(
            maxProducts = maxProducts ?: 0,
            maxImages = maxImages ?: 0,
            maxShops = maxShops ?: 0,
        ),
        capabilities = PlanCapabilitiesMapper.map(features),
        sortOrder = sortOrder,
        active = active,
    )
}
