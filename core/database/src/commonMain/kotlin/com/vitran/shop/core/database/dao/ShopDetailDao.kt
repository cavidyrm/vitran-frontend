package com.vitran.shop.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.vitran.shop.core.database.entity.ShopDetailEntity

@Dao
interface ShopDetailDao {
    @Query("SELECT * FROM shop_details WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ShopDetailEntity?

    @Query("SELECT * FROM shop_details WHERE slug = :slug LIMIT 1")
    suspend fun getBySlug(slug: String): ShopDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ShopDetailEntity)

    @Query("UPDATE shop_details SET unavailable = 1 WHERE id = :id")
    suspend fun markUnavailable(id: Long)

    @Query("DELETE FROM shop_details WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM shop_details")
    suspend fun deleteAll()
}
