package com.vitran.shop.feature.auth.domain

import com.vitran.shop.core.domain.result.AppResult

fun interface ReferralCodeValidator {
    suspend fun validate(code: String): AppResult<Boolean>
}
