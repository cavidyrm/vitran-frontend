package com.vitran.shop.core.database.entity

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "static_pages",
    indices = [Index(value = ["slug"], unique = true)],
)
data class StaticPageEntity(
    @PrimaryKey val id: Long,
    val slug: String,
    val title: String,
    val bodyHtml: String,
    val active: Boolean,
    val sortOrder: Int,
    val fetchedAt: Long,
)
