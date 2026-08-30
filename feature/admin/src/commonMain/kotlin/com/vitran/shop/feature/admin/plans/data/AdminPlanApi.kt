package com.vitran.shop.feature.admin.plans.data

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.admin.plans.domain.CreatePlanCommand
import com.vitran.shop.feature.admin.plans.domain.UpdatePlanCommand
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class AdminPlanApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getPlans(): AppResult<AdminPlansDataDto> = executor.execute {
        client.get(environment.apiUrl("/admin/plans")) { authMode(AuthMode.Required) }
    }

    suspend fun create(command: CreatePlanCommand): AppResult<AdminPlanDataDto> = executor.execute {
        client.post(environment.apiUrl("/admin/plans")) {
            authMode(AuthMode.Required)
            contentType(ContentType.Application.Json)
            setBody(command.toRequest())
        }
    }

    suspend fun update(command: UpdatePlanCommand): AppResult<AdminPlanDataDto> = executor.execute {
        client.patch(environment.apiUrl("/admin/plans/${command.id.value}")) {
            authMode(AuthMode.Required)
            contentType(ContentType.Application.Json)
            setBody(command.toPatchBody())
        }
    }

    suspend fun delete(id: PlanId): AppResult<Unit> = executor.executeEmpty {
        client.delete(environment.apiUrl("/admin/plans/${id.value}")) { authMode(AuthMode.Required) }
    }
}

private fun CreatePlanCommand.toRequest() = CreatePlanRequestDto(
    slug = slug.rawValue, title = title, description = description, priceAmount = priceAmount,
    durationDays = durationDays, maxProducts = maxProducts, maxImages = maxImages, maxShops = maxShops,
    features = features, active = active, sortOrder = sortOrder,
)

internal fun UpdatePlanCommand.toPatchBody() = buildJsonObject {
    slug?.let { put("slug", it.rawValue) }
    title?.let { put("title", it) }
    description?.let { put("description", it) }
    priceAmount?.let { put("price_amount", it) }
    if (durationDays != null) put("duration_days", durationDays) 
    maxProducts?.let { put("max_products", it) }
    maxImages?.let { put("max_images", it) }
    maxShops?.let { put("max_shops", it) }
    if (featuresUpdated) put("features", features ?: JsonNull)
    active?.let { put("active", it) }
    sortOrder?.let { put("sort_order", it) }
}
