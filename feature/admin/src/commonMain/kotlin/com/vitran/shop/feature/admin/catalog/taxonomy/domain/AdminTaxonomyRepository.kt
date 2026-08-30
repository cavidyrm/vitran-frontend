package com.vitran.shop.feature.admin.catalog.taxonomy.domain

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.taxonomy.domain.model.AttributeSlug
import com.vitran.shop.feature.taxonomy.domain.model.AttributeValueSlug
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug

interface AdminTaxonomyRepository {
    suspend fun importTaxonomy(
        categories: SelectedFile,
        attributes: SelectedFile,
    ): AppResult<Unit>

    suspend fun renameCategory(slug: CategorySlug, name: String): AppResult<Unit>

    suspend fun uploadCategoryIcon(slug: CategorySlug, image: SelectedFile): AppResult<Unit>

    suspend fun renameAttribute(slug: AttributeSlug, name: String): AppResult<Unit>

    suspend fun renameValue(slug: AttributeValueSlug, name: String): AppResult<Unit>
}
