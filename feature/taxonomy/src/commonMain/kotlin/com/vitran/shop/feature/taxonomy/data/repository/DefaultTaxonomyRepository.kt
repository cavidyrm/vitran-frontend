package com.vitran.shop.feature.taxonomy.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.taxonomy.data.mapper.toDomain
import com.vitran.shop.feature.taxonomy.data.remote.TaxonomyApi
import com.vitran.shop.feature.taxonomy.domain.model.CategoryDetails
import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.feature.taxonomy.domain.repository.TaxonomyRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultTaxonomyRepository(
    private val taxonomyApi: TaxonomyApi,
) : TaxonomyRepository {

    private val cacheMutex = Mutex()
    private var cachedTree: List<CategoryNode>? = null
    private val cachedDetails = mutableMapOf<CategorySlug, CategoryDetails>()

    override suspend fun getCategoryTree(forceRefresh: Boolean): AppResult<List<CategoryNode>> {
        if (!forceRefresh) {
            cacheMutex.withLock {
                cachedTree?.let { return AppResult.Success(it) }
            }
        }

        return when (val result = taxonomyApi.getCategoryTree()) {
            is AppResult.Success -> {
                val tree = result.value.categories.map { it.toDomain() }
                cacheMutex.withLock { cachedTree = tree }
                AppResult.Success(tree)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun getCategory(
        slug: CategorySlug,
        forceRefresh: Boolean,
    ): AppResult<CategoryDetails> {
        if (!forceRefresh) {
            cacheMutex.withLock {
                cachedDetails[slug]?.let { return AppResult.Success(it) }
            }
        }

        return when (val result = taxonomyApi.getCategory(slug)) {
            is AppResult.Success -> {
                val details = result.value.category.toDomain()
                cacheMutex.withLock { cachedDetails[slug] = details }
                AppResult.Success(details)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun invalidateTaxonomy() {
        cacheMutex.withLock {
            cachedTree = null
            cachedDetails.clear()
        }
    }
}
