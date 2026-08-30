package com.vitran.shop.feature.taxonomy.data.repository

import com.vitran.shop.core.database.VitranDatabase
import com.vitran.shop.core.database.entity.CategoryDetailEntity
import com.vitran.shop.core.database.entity.CategoryEntity
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.taxonomy.data.mapper.toDomain
import com.vitran.shop.feature.taxonomy.data.remote.TaxonomyApi
import com.vitran.shop.feature.taxonomy.data.remote.dto.CategoryDetailsDto
import com.vitran.shop.feature.taxonomy.domain.model.CategoryDetails
import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.feature.taxonomy.domain.repository.TaxonomyRepository
import kotlinx.serialization.json.Json
import kotlin.time.Clock

internal class DefaultTaxonomyRepository(
    private val taxonomyApi: TaxonomyApi,
    private val database: VitranDatabase,
) : TaxonomyRepository {

    private val categoryDao get() = database.categoryDao()
    private val categoryDetailDao get() = database.categoryDetailDao()

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getCategoryTree(forceRefresh: Boolean): AppResult<List<CategoryNode>> {
        if (!forceRefresh) {
            val cached = categoryDao.getAll()
            if (cached.isNotEmpty()) {
                return AppResult.Success(cached.toTree())
            }
        }

        return when (val result = taxonomyApi.getCategoryTree()) {
            is AppResult.Success -> {
                val tree = result.value.categories.map { it.toDomain() }
                val now = Clock.System.now().toEpochMilliseconds()
                categoryDao.replaceAll(flattenCategoryTree(tree, parentSlug = null, fetchedAt = now))
                AppResult.Success(tree)
            }
            is AppResult.Failure -> {
                val cached = categoryDao.getAll()
                if (cached.isNotEmpty()) {
                    AppResult.Success(cached.toTree())
                } else {
                    AppResult.Failure(result.error)
                }
            }
        }
    }

    override suspend fun getCategory(
        slug: CategorySlug,
        forceRefresh: Boolean,
    ): AppResult<CategoryDetails> {
        if (!forceRefresh) {
            categoryDetailDao.getBySlug(slug.value)?.let { entity ->
                return AppResult.Success(entity.toDomain(json))
            }
        }

        return when (val result = taxonomyApi.getCategory(slug)) {
            is AppResult.Success -> {
                val dto = result.value.category
                val details = dto.toDomain()
                val now = Clock.System.now().toEpochMilliseconds()
                categoryDetailDao.upsert(
                    CategoryDetailEntity(
                        slug = slug.value,
                        payloadJson = json.encodeToString(CategoryDetailsDto.serializer(), dto),
                        fetchedAt = now,
                    ),
                )
                AppResult.Success(details)
            }
            is AppResult.Failure -> {
                val cached = categoryDetailDao.getBySlug(slug.value)
                if (cached != null) {
                    AppResult.Success(cached.toDomain(json))
                } else {
                    AppResult.Failure(result.error)
                }
            }
        }
    }

    override suspend fun invalidateTaxonomy() {
        categoryDao.deleteAll()
        categoryDetailDao.deleteAll()
    }
}

internal fun flattenCategoryTree(
    nodes: List<CategoryNode>,
    parentSlug: String?,
    fetchedAt: Long,
): List<CategoryEntity> {
    val out = mutableListOf<CategoryEntity>()
    var sortIndex = 0
    fun walk(nodes: List<CategoryNode>, parentSlug: String?) {
        for (node in nodes) {
            out += CategoryEntity(
                slug = node.slug.value,
                parentSlug = parentSlug,
                sourceTitle = node.sourceTitle,
                localizedName = node.localizedName,
                isLeaf = node.isLeaf,
                sortIndex = sortIndex++,
                fetchedAt = fetchedAt,
            )
            walk(node.children, node.slug.value)
        }
    }
    walk(nodes, parentSlug)
    return out
}

private fun List<CategoryEntity>.toTree(): List<CategoryNode> {
    val byParent = groupBy { it.parentSlug }
    fun childrenOf(parent: String?): List<CategoryNode> =
        byParent[parent]
            .orEmpty()
            .sortedBy { it.sortIndex }
            .map { entity ->
                CategoryNode(
                    slug = CategorySlug(entity.slug),
                    sourceTitle = entity.sourceTitle,
                    localizedName = entity.localizedName,
                    isLeaf = entity.isLeaf,
                    children = childrenOf(entity.slug),
                )
            }
    return childrenOf(null)
}

private fun CategoryDetailEntity.toDomain(json: Json): CategoryDetails =
    json.decodeFromString(CategoryDetailsDto.serializer(), payloadJson).toDomain()
