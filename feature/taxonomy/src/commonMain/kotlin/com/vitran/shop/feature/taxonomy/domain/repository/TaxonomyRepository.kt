package com.vitran.shop.feature.taxonomy.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.taxonomy.domain.model.CategoryDetails
import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug

interface TaxonomyRepository {
    suspend fun getCategoryTree(forceRefresh: Boolean = false): AppResult<List<CategoryNode>>
    suspend fun getCategory(slug: CategorySlug, forceRefresh: Boolean = false): AppResult<CategoryDetails>
    suspend fun invalidateTaxonomy()
}
