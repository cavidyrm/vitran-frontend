package com.vitran.shop.core.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.vitran.shop.core.database.dao.CategoryDao
import com.vitran.shop.core.database.dao.CategoryDetailDao
import com.vitran.shop.core.database.dao.CityDao
import com.vitran.shop.core.database.dao.HomeSnapshotDao
import com.vitran.shop.core.database.dao.PlanDao
import com.vitran.shop.core.database.dao.ProductDetailDao
import com.vitran.shop.core.database.dao.ShopDetailDao
import com.vitran.shop.core.database.dao.StaticPageDao
import com.vitran.shop.core.database.entity.CategoryDetailEntity
import com.vitran.shop.core.database.entity.CategoryEntity
import com.vitran.shop.core.database.entity.CityEntity
import com.vitran.shop.core.database.entity.HomeSnapshotEntity
import com.vitran.shop.core.database.entity.PlanEntity
import com.vitran.shop.core.database.entity.ProductDetailEntity
import com.vitran.shop.core.database.entity.ShopDetailEntity
import com.vitran.shop.core.database.entity.StaticPageEntity

/**
 * Public reference / marketplace snapshot cache. Schema version 1 is cache-only.
 * Tokens and secrets must never be stored here.
 */
@Database(
    entities = [
        CityEntity::class,
        CategoryEntity::class,
        CategoryDetailEntity::class,
        PlanEntity::class,
        StaticPageEntity::class,
        HomeSnapshotEntity::class,
        ShopDetailEntity::class,
        ProductDetailEntity::class,
    ],
    version = VITRAN_DATABASE_VERSION,
    exportSchema = true,
)
@ConstructedBy(VitranDatabaseConstructor::class)
abstract class VitranDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun categoryDao(): CategoryDao
    abstract fun categoryDetailDao(): CategoryDetailDao
    abstract fun planDao(): PlanDao
    abstract fun staticPageDao(): StaticPageDao
    abstract fun homeSnapshotDao(): HomeSnapshotDao
    abstract fun shopDetailDao(): ShopDetailDao
    abstract fun productDetailDao(): ProductDetailDao

    /**
     * Database-level replace helpers.
     *
     * Room 3 no longer documents `@Transaction` on [RoomDatabase] methods the way Room 2 did;
     * nesting DAO calls inside [androidx.room3.withWriteTransaction] can also contend for the
     * writer connection. Delegating to DAO `@Transaction` methods is the supported pattern.
     */
    suspend fun replaceCities(cities: List<CityEntity>) {
        cityDao().replaceAll(cities)
    }

    suspend fun replaceCategoryTree(categories: List<CategoryEntity>) {
        categoryDao().replaceAllTree(categories)
    }

    suspend fun replacePlans(plans: List<PlanEntity>) {
        planDao().replaceAll(plans)
    }

    suspend fun replaceStaticPages(pages: List<StaticPageEntity>) {
        staticPageDao().replaceAll(pages)
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object VitranDatabaseConstructor : RoomDatabaseConstructor<VitranDatabase> {
    override fun initialize(): VitranDatabase
}

const val VITRAN_DATABASE_NAME = "vitran_cache.db"
const val VITRAN_DATABASE_VERSION = 1
