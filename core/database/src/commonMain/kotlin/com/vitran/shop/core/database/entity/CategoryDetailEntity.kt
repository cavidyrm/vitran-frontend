package com.vitran.shop.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "category_details")
data class CategoryDetailEntity(
    @PrimaryKey val slug: String,
    /** Detail snapshot as JSON string at the data boundary. */
    val payloadJson: String,
    val fetchedAt: Long,
)
