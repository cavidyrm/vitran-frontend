package com.vitran.shop.feature.taxonomy

import com.vitran.shop.core.database.createInMemoryVitranDatabase
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.taxonomy.data.remote.TaxonomyApi
import com.vitran.shop.feature.taxonomy.data.repository.DefaultTaxonomyRepository
import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.feature.taxonomy.domain.model.collectLeafCategories
import com.vitran.shop.feature.taxonomy.domain.model.findBySlug
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CategoryTreeHelpersTest {

    @Test
    fun findBySlug_locatesNestedNode() = runTest {
        val tree = loadSampleTree()

        val node = tree.findBySlug(CategorySlug("aa-1-2-3-5"))

        assertEquals("تی‌شرت", node?.localizedName)
    }

    @Test
    fun collectLeafCategories_includesOnlyLeaves() = runTest {
        val tree = loadSampleTree()

        val leaves = tree.collectLeafCategories()

        assertEquals(1, leaves.size)
        assertEquals("aa-1-2-3-5", leaves.single().slug.value)
    }

    private suspend fun loadSampleTree(): List<CategoryNode> {
        val repository = DefaultTaxonomyRepository(
            taxonomyApi = TaxonomyApi(
                client = createTaxonomyTestClient(
                    MockEngine { jsonResponse(HttpStatusCode.OK, categoryTreeEnvelope) },
                ),
                environment = ApiEnvironment(origin = "http://localhost:8080"),
                executor = createTaxonomyTestExecutor(),
            ),
            database = createInMemoryVitranDatabase(),
        )
        return (repository.getCategoryTree() as AppResult.Success).value
    }
}
