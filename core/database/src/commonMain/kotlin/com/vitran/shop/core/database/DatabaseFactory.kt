package com.vitran.shop.core.database

/**
 * Platform-specific factory that opens [VitranDatabase] with the appropriate SQLite driver
 * and filesystem path. Bound in platform DI; [com.vitran.shop.core.database.di.databaseModule]
 * exposes the resulting singleton.
 *
 * Do not store tokens or secrets in this database.
 */
interface DatabaseFactory {
    fun create(): VitranDatabase
}
