package com.vitran.shop.feature.admin.catalog.taxonomy.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.admin.catalog.taxonomy.data.remote.AdminTaxonomyApi
import com.vitran.shop.feature.admin.catalog.taxonomy.domain.AdminTaxonomyRepository
import com.vitran.shop.feature.taxonomy.domain.model.AttributeSlug
import com.vitran.shop.feature.taxonomy.domain.model.AttributeValueSlug
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.feature.taxonomy.domain.repository.TaxonomyRepository

internal class DefaultAdminTaxonomyRepository(
    private val api: AdminTaxonomyApi,
    private val taxonomyRepository: TaxonomyRepository,
) : AdminTaxonomyRepository {
    override suspend fun importTaxonomy(
        categories: SelectedFile,
        attributes: SelectedFile,
    ): AppResult<Unit> = invalidateAfter(api.importTaxonomy(categories, attributes))

    override suspend fun renameCategory(slug: CategorySlug, name: String): AppResult<Unit> =
        invalidateAfter(api.renameCategory(slug, name))

    override suspend fun uploadCategoryIcon(slug: CategorySlug, image: SelectedFile): AppResult<Unit> =
        invalidateAfter(api.uploadCategoryIcon(slug, image))

    override suspend fun renameAttribute(slug: AttributeSlug, name: String): AppResult<Unit> =
        invalidateAfter(api.renameAttribute(slug, name))

    override suspend fun renameValue(slug: AttributeValueSlug, name: String): AppResult<Unit> =
        invalidateAfter(api.renameValue(slug, name))

    private suspend fun invalidateAfter(result: AppResult<Unit>): AppResult<Unit> {
        if (result is AppResult.Success) taxonomyRepository.invalidateTaxonomy()
        return result
    }
}
