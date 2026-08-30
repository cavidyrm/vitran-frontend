package com.vitran.shop.core.database.entity

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "plans",
    indices = [Index(value = ["slug"], unique = true)],
)
data class PlanEntity(
    @PrimaryKey val id: Long,
    val slug: String,
    val title: String,
    val description: String?,
    val priceAmount: Long,
    val durationDays: Int?,
    val maxProducts: Int?,
    val maxImages: Int?,
    val maxShops: Int?,
    val featuresJson: String,
    val sortOrder: Int,
    val active: Boolean,
    val fetchedAt: Long,
)
