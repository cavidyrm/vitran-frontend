package com.vitran.shop.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.vitran.shop.core.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE parentSlug IS NULL ORDER BY sortIndex ASC")
    fun observeRoots(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY sortIndex ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY sortIndex ASC")
    suspend fun getAll(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    /** Full taxonomy replace in one transaction. */
    @Transaction
    suspend fun replaceAllTree(categories: List<CategoryEntity>) {
        deleteAll()
        insertAll(categories)
    }

    /** Alias kept for repository call sites. */
    suspend fun replaceAll(categories: List<CategoryEntity>) {
        replaceAllTree(categories)
    }
}
