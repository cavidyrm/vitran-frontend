package com.vitran.shop.core.database.entity

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "shop_details",
    indices = [Index(value = ["slug"])],
)
data class ShopDetailEntity(
    @PrimaryKey val id: Long,
    val slug: String,
    val payloadJson: String,
    val unavailable: Boolean = false,
    val fetchedAt: Long,
)
