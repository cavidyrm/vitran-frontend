package com.vitran.shop.core.database.entity

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "cities",
    indices = [Index(value = ["slug"], unique = true)],
)
data class CityEntity(
    @PrimaryKey val id: Long,
    val slug: String,
    val name: String,
    val fetchedAt: Long,
)
