package com.vitran.shop.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.vitran.shop.core.database.entity.HomeSnapshotEntity

@Dao
interface HomeSnapshotDao {
    @Query("SELECT * FROM home_snapshots WHERE cityKey = :cityKey LIMIT 1")
    suspend fun get(cityKey: String): HomeSnapshotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: HomeSnapshotEntity)

    @Query("DELETE FROM home_snapshots")
    suspend fun deleteAll()
}
