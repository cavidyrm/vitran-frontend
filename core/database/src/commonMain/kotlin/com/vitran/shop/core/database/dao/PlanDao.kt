package com.vitran.shop.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.vitran.shop.core.database.entity.PlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Query("SELECT * FROM plans ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plans ORDER BY sortOrder ASC")
    suspend fun getAll(): List<PlanEntity>

    @Query("SELECT * FROM plans WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PlanEntity?

    @Query("SELECT * FROM plans WHERE slug = :slug LIMIT 1")
    suspend fun getBySlug(slug: String): PlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<PlanEntity>)

    @Query("DELETE FROM plans")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(plans: List<PlanEntity>) {
        deleteAll()
        insertAll(plans)
    }
}
