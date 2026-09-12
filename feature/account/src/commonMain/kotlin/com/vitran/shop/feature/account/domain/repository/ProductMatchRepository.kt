package com.vitran.shop.feature.account.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.domain.model.ProductMatch

interface ProductMatchRepository {
    suspend fun listMatches(limit: Int = 20): AppResult<List<ProductMatch>>
}
