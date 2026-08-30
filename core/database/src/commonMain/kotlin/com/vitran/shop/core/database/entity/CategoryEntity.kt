package com.vitran.shop.core.database.entity

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [Index(value = ["parentSlug"])],
)
data class CategoryEntity(
    @PrimaryKey val slug: String,
    val parentSlug: String?,
    val sourceTitle: String,
    val localizedName: String?,
    val isLeaf: Boolean,
    val sortIndex: Int,
    val fetchedAt: Long,
)
