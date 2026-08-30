package com.vitran.shop.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.vitran.shop.core.database.entity.ProductDetailEntity

@Dao
interface ProductDetailDao {
    @Query("SELECT * FROM product_details WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ProductDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ProductDetailEntity)

    @Query("UPDATE product_details SET unavailable = 1 WHERE id = :id")
    suspend fun markUnavailable(id: Long)

    @Query("DELETE FROM product_details WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM product_details")
    suspend fun deleteAll()
}
