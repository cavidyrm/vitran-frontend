package com.vitran.shop.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.vitran.shop.core.database.entity.StaticPageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StaticPageDao {
    @Query("SELECT * FROM static_pages ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<StaticPageEntity>>

    @Query("SELECT * FROM static_pages ORDER BY sortOrder ASC")
    suspend fun getAll(): List<StaticPageEntity>

    @Query("SELECT * FROM static_pages WHERE slug = :slug LIMIT 1")
    suspend fun getBySlug(slug: String): StaticPageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: StaticPageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pages: List<StaticPageEntity>)

    @Query("DELETE FROM static_pages WHERE slug = :slug")
    suspend fun deleteBySlug(slug: String)

    @Query("DELETE FROM static_pages")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(pages: List<StaticPageEntity>) {
        deleteAll()
        insertAll(pages)
    }
}
