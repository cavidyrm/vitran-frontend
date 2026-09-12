package com.vitran.shop.feature.account.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.data.mapper.toDomain
import com.vitran.shop.feature.account.data.remote.ProductMatchApi
import com.vitran.shop.feature.account.domain.model.ProductMatch
import com.vitran.shop.feature.account.domain.repository.ProductMatchRepository

internal class DefaultProductMatchRepository(
    private val api: ProductMatchApi,
) : ProductMatchRepository {
    override suspend fun listMatches(limit: Int): AppResult<List<ProductMatch>> =
        api.listMatches(limit).mapSuccess { it.matches.map { match -> match.toDomain() } }
}

private inline fun <T, R> AppResult<T>.mapSuccess(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(value))
    is AppResult.Failure -> this
}
