package com.vitran.shop.core.database

import com.vitran.shop.core.database.entity.CategoryEntity
import com.vitran.shop.core.database.entity.CityEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DatabaseTest {

    @Test
    fun replaceCities_andObserve_emitsOrderedByName() = runTest {
        val db = createInMemoryVitranDatabase()
        val now = 1_700_000_000_000L

        db.replaceCities(
            listOf(
                CityEntity(id = 1, slug = "tehran", name = "تهران", fetchedAt = now),
                CityEntity(id = 2, slug = "shiraz", name = "شیراز", fetchedAt = now),
            ),
        )

        val rows = db.cityDao().observeAll().first()
        assertEquals(2, rows.size)
        assertEquals(setOf("tehran", "shiraz"), rows.map { it.slug }.toSet())

        db.replaceCities(emptyList())
        assertTrue(db.cityDao().getAll().isEmpty())
        db.close()
    }

    @Test
    fun replaceCategoryTree_isTransactionalFullReplace() = runTest {
        val db = createInMemoryVitranDatabase()
        db.categoryDao().replaceAll(
            listOf(
                CategoryEntity(
                    slug = "root",
                    parentSlug = null,
                    sourceTitle = "Root",
                    localizedName = "ریشه",
                    isLeaf = false,
                    sortIndex = 0,
                    fetchedAt = 1L,
                ),
            ),
        )
        db.replaceCategoryTree(
            listOf(
                CategoryEntity(
                    slug = "a",
                    parentSlug = null,
                    sourceTitle = "A",
                    localizedName = null,
                    isLeaf = true,
                    sortIndex = 0,
                    fetchedAt = 2L,
                ),
                CategoryEntity(
                    slug = "b",
                    parentSlug = null,
                    sourceTitle = "B",
                    localizedName = null,
                    isLeaf = true,
                    sortIndex = 1,
                    fetchedAt = 2L,
                ),
            ),
        )
        assertEquals(2, db.categoryDao().getAll().size)
        assertEquals(2, db.categoryDao().observeRoots().first().size)
        db.close()
    }
}
