package com.vitran.shop.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.vitran.shop.core.database.entity.CategoryDetailEntity

@Dao
interface CategoryDetailDao {
    @Query("SELECT * FROM category_details WHERE slug = :slug LIMIT 1")
    suspend fun getBySlug(slug: String): CategoryDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CategoryDetailEntity)

    @Query("DELETE FROM category_details")
    suspend fun deleteAll()
}
