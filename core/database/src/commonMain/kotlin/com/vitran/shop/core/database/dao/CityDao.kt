package com.vitran.shop.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.vitran.shop.core.database.entity.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM cities ORDER BY name ASC")
    fun observeAll(): Flow<List<CityEntity>>

    @Query("SELECT * FROM cities ORDER BY name ASC")
    suspend fun getAll(): List<CityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cities: List<CityEntity>)

    @Query("DELETE FROM cities")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(cities: List<CityEntity>) {
        deleteAll()
        insertAll(cities)
    }
}
