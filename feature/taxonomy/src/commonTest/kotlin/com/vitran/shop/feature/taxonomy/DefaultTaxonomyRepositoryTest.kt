package com.vitran.shop.feature.taxonomy

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.taxonomy.data.mapper.toDomain
import com.vitran.shop.feature.taxonomy.data.remote.TaxonomyApi
import com.vitran.shop.feature.taxonomy.data.remote.dto.CategoryTreeNodeDto
import com.vitran.shop.feature.taxonomy.data.repository.DefaultTaxonomyRepository
import com.vitran.shop.feature.taxonomy.domain.model.CategoryDetails
import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.feature.taxonomy.domain.model.collectLeafCategories
import com.vitran.shop.feature.taxonomy.domain.model.findBySlug
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DefaultTaxonomyRepositoryTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createTaxonomyTestExecutor()

    @Test
    fun treeNode_mapsRecursively() {
        val root = CategoryTreeNodeDto(
            slug = "aa-1-1-1-1",
            title = "Root",
            name = "ریشه",
            isLeaf = false,
            children = listOf(
                CategoryTreeNodeDto(
                    slug = "aa-1-2-3-4",
                    title = "Child",
                    name = null,
                    isLeaf = true,
                ),
            ),
        ).toDomain()

        assertEquals(CategorySlug("aa-1-1-1-1"), root.slug)
        assertEquals("ریشه", root.localizedName)
        assertFalse(root.isLeaf)
        assertEquals(1, root.children.size)
        assertTrue(root.children.single().isLeaf)
        assertEquals("Child", root.children.single().displayName)
    }

    @Test
    fun isLeafPreservedWhenChildrenEmpty() {
        val node = CategoryTreeNodeDto(
            slug = "aa-9-9-9-9",
            title = "Empty Branch",
            name = null,
            isLeaf = false,
            children = emptyList(),
        ).toDomain()

        assertFalse(node.isLeaf)
        assertTrue(node.children.isEmpty())
    }

    @Test
    fun getCategoryTree_cachesSecondCall() = runTest {
        var requestCount = 0
        val repository = DefaultTaxonomyRepository(
            taxonomyApi = TaxonomyApi(
                client = createTaxonomyTestClient(
                    MockEngine {
                        requestCount++
                        jsonResponse(HttpStatusCode.OK, categoryTreeEnvelope)
                    },
                ),
                environment = environment,
                executor = executor,
            ),
        )

        repository.getCategoryTree()
        repository.getCategoryTree()

        assertEquals(1, requestCount)
    }

    @Test
    fun getCategory_detailCachedBySlug() = runTest {
        var requestCount = 0
        val repository = DefaultTaxonomyRepository(
            taxonomyApi = TaxonomyApi(
                client = createTaxonomyTestClient(
                    MockEngine {
                        requestCount++
                        jsonResponse(HttpStatusCode.OK, categoryDetailEnvelope)
                    },
                ),
                environment = environment,
                executor = executor,
            ),
        )

        val slug = CategorySlug("aa-1-2-3-4")
        repository.getCategory(slug)
        repository.getCategory(slug)

        assertEquals(1, requestCount)
    }

    @Test
    fun getCategory_mapsDetailFields() = runTest {
        val repository = DefaultTaxonomyRepository(
            taxonomyApi = TaxonomyApi(
                client = createTaxonomyTestClient(
                    MockEngine { jsonResponse(HttpStatusCode.OK, categoryDetailEnvelope) },
                ),
                environment = environment,
                executor = executor,
            ),
        )

        val result = repository.getCategory(CategorySlug("aa-1-2-3-4"))

        assertIs<AppResult.Success<CategoryDetails>>(result)
        assertEquals("T-Shirts", result.value.sourceTitle)
        assertEquals("T-Shirts", result.value.displayName)
        assertEquals(
            "Apparel & Accessories > Clothing > Shirts > T-Shirts",
            result.value.fullName,
        )
        assertEquals("https://cdn.example.com/categories/uuid.jpg", result.value.iconUrl)
    }

    @Test
    fun nestedTree_fromApiEnvelope() = runTest {
        val repository = DefaultTaxonomyRepository(
            taxonomyApi = TaxonomyApi(
                client = createTaxonomyTestClient(
                    MockEngine { jsonResponse(HttpStatusCode.OK, categoryTreeEnvelope) },
                ),
                environment = environment,
                executor = executor,
            ),
        )

        val result = repository.getCategoryTree()

        assertIs<AppResult.Success<List<CategoryNode>>>(result)
        val grandchild = result.value
            .first()
            .children
            .single()
            .children
            .single()
        assertEquals(CategorySlug("aa-1-2-3-5"), grandchild.slug)
        assertTrue(grandchild.isLeaf)
    }
}
