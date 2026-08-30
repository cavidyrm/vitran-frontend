package com.vitran.shop.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "home_snapshots")
data class HomeSnapshotEntity(
    /** Use empty string when city is null (anonymous global snapshot). */
    @PrimaryKey val cityKey: String,
    val payloadJson: String,
    val fetchedAt: Long,
)
