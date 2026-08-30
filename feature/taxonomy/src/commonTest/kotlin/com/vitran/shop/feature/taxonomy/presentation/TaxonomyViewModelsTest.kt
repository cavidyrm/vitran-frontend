package com.vitran.shop.feature.taxonomy.presentation

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class TaxonomyPickerViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val sampleRoot = CategoryNode(
        slug = CategorySlug("aa-1-1-1-1"),
        sourceTitle = "Apparel",
        localizedName = "پوشاک",
        isLeaf = false,
        children = emptyList(),
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialLoad_success() = runTest {
        val viewModel = TaxonomyPickerViewModel(
            FakeTaxonomyRepository(tree = listOf(sampleRoot)),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<TaxonomyPickerUiState.Content>(state)
        assertEquals(1, state.roots.size)
    }

    @Test
    fun initialLoad_empty() = runTest {
        val viewModel = TaxonomyPickerViewModel(FakeTaxonomyRepository(tree = emptyList()))

        advanceUntilIdle()

        assertIs<TaxonomyPickerUiState.Empty>(viewModel.uiState.value)
    }

    @Test
    fun initialLoad_failure_andRetry() = runTest {
        val repository = FakeTaxonomyRepository(shouldFail = true)
        val viewModel = TaxonomyPickerViewModel(repository)

        advanceUntilIdle()
        assertIs<TaxonomyPickerUiState.Error>(viewModel.uiState.value)

        repository.shouldFail = false
        repository.tree = listOf(sampleRoot)
        viewModel.retry()
        advanceUntilIdle()

        assertIs<TaxonomyPickerUiState.Content>(viewModel.uiState.value)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesBrowseViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadsRootCategories() = runTest {
        val root = CategoryNode(
            slug = CategorySlug("aa-1-1-1-1"),
            sourceTitle = "Root",
            localizedName = "ریشه",
            isLeaf = false,
            children = emptyList(),
        )
        val viewModel = CategoriesBrowseViewModel(
            FakeTaxonomyRepository(tree = listOf(root)),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<CategoriesBrowseUiState.Content>(state)
        assertEquals("ریشه", state.rootCategories.single().displayName)
    }
}

internal class FakeTaxonomyRepository(
    var tree: List<CategoryNode> = emptyList(),
    var shouldFail: Boolean = false,
) : TaxonomyRepository {
    override suspend fun getCategoryTree(forceRefresh: Boolean): AppResult<List<CategoryNode>> =
        if (shouldFail) {
            AppResult.Failure(AppError.Network.NoConnection())
        } else {
            AppResult.Success(tree)
        }

    override suspend fun getCategory(slug: CategorySlug, forceRefresh: Boolean): AppResult<CategoryDetails> =
        AppResult.Failure(AppError.Unexpected())

    override suspend fun invalidateTaxonomy() = Unit
}
