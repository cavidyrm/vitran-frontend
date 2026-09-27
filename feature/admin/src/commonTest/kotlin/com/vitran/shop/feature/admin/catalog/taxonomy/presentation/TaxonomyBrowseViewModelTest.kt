package com.vitran.shop.feature.admin.catalog.taxonomy.presentation

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.admin.catalog.taxonomy.domain.AdminTaxonomyRepository
import com.vitran.shop.feature.admin.state.AdminSessionStateStore
import com.vitran.shop.feature.taxonomy.domain.model.AttributeSlug
import com.vitran.shop.feature.taxonomy.domain.model.AttributeValueSlug
import com.vitran.shop.feature.taxonomy.domain.model.CategoryDetails
import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.feature.taxonomy.domain.repository.TaxonomyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TaxonomyBrowseViewModelTest {
    @Test
    fun open_movesOneLevel_andCrumbReturnsToRoots() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val taxonomy = FakeTaxonomyRepository(sampleTree())
            val viewModel = viewModel(taxonomy)
            advanceUntilIdle()

            assertEquals(listOf("aa", "bt"), viewModel.uiState.value.levelNodes.map { it.slug.value })

            viewModel.open(CategorySlug("aa"))
            assertEquals(listOf("aa"), viewModel.uiState.value.path.map { it.value })
            assertEquals(listOf("aa-1", "aa-8"), viewModel.uiState.value.levelNodes.map { it.slug.value })

            viewModel.open(CategorySlug("aa-1"))
            assertEquals(listOf("aa-1-4"), viewModel.uiState.value.levelNodes.map { it.slug.value })

            viewModel.open(CategorySlug("aa-1-4"))
            assertEquals(listOf("aa", "aa-1"), viewModel.uiState.value.path.map { it.value })

            viewModel.openCrumb(0)
            assertEquals(emptyList(), viewModel.uiState.value.path)
            assertEquals(listOf("aa", "bt"), viewModel.uiState.value.levelNodes.map { it.slug.value })
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun open_branchWithoutChildren_marksUnlistedLevel() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val taxonomy = FakeTaxonomyRepository(
                listOf(
                    node(slug = "aa", title = "Apparel", leaf = false),
                ),
            )
            val viewModel = viewModel(taxonomy)
            advanceUntilIdle()

            viewModel.open(CategorySlug("aa"))

            assertTrue(viewModel.uiState.value.unlistedChildren)
            assertEquals(emptyList(), viewModel.uiState.value.levelNodes)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun saveName_refreshesTree_andKeepsTheOpenLevel() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val taxonomy = FakeTaxonomyRepository(sampleTree())
            val admin = FakeAdminTaxonomyRepository(taxonomy)
            val viewModel = viewModel(taxonomy, admin)
            advanceUntilIdle()

            viewModel.open(CategorySlug("aa"))
            viewModel.beginEdit(CategorySlug("aa-1"))
            advanceUntilIdle()
            viewModel.setEditName("لباس")
            viewModel.saveName()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(CategorySlug("aa-1") to "لباس", admin.lastRename)
            assertEquals(listOf("aa"), state.path)
            assertEquals("لباس", state.levelNodes.first { it.slug.value == "aa-1" }.displayName)
            assertEquals("لباس", state.editName)
            assertTrue(state.nameSaved)
            assertNull(state.editError)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun viewModel(
        taxonomy: FakeTaxonomyRepository,
        admin: AdminTaxonomyRepository = FakeAdminTaxonomyRepository(taxonomy),
    ) = TaxonomyBrowseViewModel(
        taxonomyRepository = taxonomy,
        adminRepository = admin,
        sessionStateStore = AdminSessionStateStore(mutableListOf()),
    )

    private fun sampleTree(): List<CategoryNode> {
        val dresses = node(slug = "aa-1-4", title = "Dresses", leaf = true)
        val clothing = node(
            slug = "aa-1",
            title = "Clothing",
            name = "پوشاک",
            children = listOf(dresses),
        )
        val shoes = node(slug = "aa-8", title = "Shoes", leaf = true)
        val apparel = node(
            slug = "aa",
            title = "Apparel & Accessories",
            name = "پوشاک و اکسسوری",
            children = listOf(clothing, shoes),
        )
        val baby = node(slug = "bt", title = "Baby & Toddler", name = "نوزاد", leaf = true)
        return listOf(apparel, baby)
    }

    private fun node(
        slug: String,
        title: String,
        name: String? = null,
        leaf: Boolean = false,
        children: List<CategoryNode> = emptyList(),
    ) = CategoryNode(
        slug = CategorySlug(slug),
        sourceTitle = title,
        localizedName = name,
        isLeaf = leaf,
        children = children,
    )
}

private class FakeTaxonomyRepository(
    var tree: List<CategoryNode>,
) : TaxonomyRepository {
    val details = mutableMapOf<String, CategoryDetails>()

    init {
        fun walk(nodes: List<CategoryNode>) {
            nodes.forEach { node ->
                details[node.slug.value] = CategoryDetails(
                    slug = node.slug,
                    sourceTitle = node.sourceTitle,
                    localizedName = node.localizedName,
                    fullName = node.sourceTitle,
                    isLeaf = node.isLeaf,
                    iconUrl = null,
                    children = node.children,
                )
                walk(node.children)
            }
        }
        walk(tree)
    }

    override suspend fun getCategoryTree(forceRefresh: Boolean): AppResult<List<CategoryNode>> =
        AppResult.Success(tree)

    override suspend fun getCategory(
        slug: CategorySlug,
        forceRefresh: Boolean,
    ): AppResult<CategoryDetails> =
        details[slug.value]?.let { AppResult.Success(it) }
            ?: AppResult.Failure(AppError.Validation(message = "missing"))

    override suspend fun invalidateTaxonomy() = Unit

    fun rename(slug: CategorySlug, name: String) {
        fun walk(nodes: List<CategoryNode>): List<CategoryNode> =
            nodes.map { node ->
                val next = if (node.slug == slug) node.copy(localizedName = name) else node
                next.copy(children = walk(next.children))
            }
        tree = walk(tree)
        details[slug.value]?.let { current ->
            details[slug.value] = current.copy(localizedName = name)
        }
    }
}

private class FakeAdminTaxonomyRepository(
    private val taxonomy: FakeTaxonomyRepository,
) : AdminTaxonomyRepository {
    var lastRename: Pair<CategorySlug, String>? = null

    override suspend fun importTaxonomy(
        categories: SelectedFile,
        attributes: SelectedFile,
    ): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun renameCategory(slug: CategorySlug, name: String): AppResult<Unit> {
        lastRename = slug to name
        taxonomy.rename(slug, name)
        return AppResult.Success(Unit)
    }

    override suspend fun uploadCategoryIcon(slug: CategorySlug, image: SelectedFile): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun renameAttribute(slug: AttributeSlug, name: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun renameValue(slug: AttributeValueSlug, name: String): AppResult<Unit> =
        AppResult.Success(Unit)
}
