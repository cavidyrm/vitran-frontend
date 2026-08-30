package com.vitran.shop.feature.admin.catalog.location.domain

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.feature.location.domain.model.CityId

data class CreateCityCommand(
    val slug: String,
    val name: String,
)

data class UpdateCityCommand(
    val id: CityId,
    val slug: String,
    val name: String,
)

sealed interface AdminLocationError {
    data object CityInUse : AdminLocationError

    data class RequestFailed(
        val error: AppError,
    ) : AdminLocationError
}
