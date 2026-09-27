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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Clock

internal class DefaultTaxonomyRepository(
    private val taxonomyApi: TaxonomyApi,
    private val database: VitranDatabase,
) : TaxonomyRepository {

    private val categoryDao get() = database.categoryDao()
    private val categoryDetailDao get() = database.categoryDetailDao()

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Cache writes must not block the caller. On web, Room's worker can suspend
     * forever when OPFS/COOP is unavailable, which kept admin taxonomy on the spinner
     * and prevented the category request from being issued.
     */
    private val cacheScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override suspend fun getCategoryTree(forceRefresh: Boolean): AppResult<List<CategoryNode>> {
        if (!forceRefresh) {
            cachedTreeOrNull()?.let { return AppResult.Success(it) }
        }

        return when (val result = taxonomyApi.getCategoryTree()) {
            is AppResult.Success -> {
                val tree = result.value.categories.map { it.toDomain() }
                persistTree(tree, await = !forceRefresh)
                AppResult.Success(tree)
            }
            is AppResult.Failure -> {
                if (!forceRefresh) {
                    cachedTreeOrNull()?.let { return AppResult.Success(it) }
                }
                AppResult.Failure(result.error)
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
                persistDetails(slug, dto, await = !forceRefresh)
                AppResult.Success(details)
            }
            is AppResult.Failure -> {
                if (!forceRefresh) {
                    cachedDetailsOrNull(slug)?.let { return AppResult.Success(it) }
                }
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun invalidateTaxonomy() {
        cacheScope.launch {
            ignoreCacheFailure {
                categoryDao.deleteAll()
                categoryDetailDao.deleteAll()
            }
        }
    }

    private suspend fun cachedTreeOrNull(): List<CategoryNode>? =
        ignoreCacheFailure { categoryDao.getAll().takeIf { it.isNotEmpty() }?.toTree() }

    private suspend fun cachedDetailsOrNull(slug: CategorySlug): CategoryDetails? =
        ignoreCacheFailure { categoryDetailDao.getBySlug(slug.value)?.toDomain(json) }

    private suspend fun persistTree(tree: List<CategoryNode>, await: Boolean) {
        val rows = flattenCategoryTree(
            nodes = tree,
            parentSlug = null,
            fetchedAt = Clock.System.now().toEpochMilliseconds(),
        )
        if (await) {
            ignoreCacheFailure { categoryDao.replaceAll(rows) }
        } else {
            cacheScope.launch { ignoreCacheFailure { categoryDao.replaceAll(rows) } }
        }
    }

    private suspend fun persistDetails(
        slug: CategorySlug,
        dto: CategoryDetailsDto,
        await: Boolean,
    ) {
        val entity = CategoryDetailEntity(
            slug = slug.value,
            payloadJson = json.encodeToString(CategoryDetailsDto.serializer(), dto),
            fetchedAt = Clock.System.now().toEpochMilliseconds(),
        )
        if (await) {
            ignoreCacheFailure { categoryDetailDao.upsert(entity) }
        } else {
            cacheScope.launch { ignoreCacheFailure { categoryDetailDao.upsert(entity) } }
        }
    }
}

private suspend fun <T> ignoreCacheFailure(block: suspend () -> T): T? =
    try {
        block()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (_: Throwable) {
        null
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
