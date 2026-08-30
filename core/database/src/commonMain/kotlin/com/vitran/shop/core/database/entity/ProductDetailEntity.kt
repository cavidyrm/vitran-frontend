package com.vitran.shop.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "product_details")
data class ProductDetailEntity(
    @PrimaryKey val id: Long,
    val payloadJson: String,
    val unavailable: Boolean = false,
    val fetchedAt: Long,
)
