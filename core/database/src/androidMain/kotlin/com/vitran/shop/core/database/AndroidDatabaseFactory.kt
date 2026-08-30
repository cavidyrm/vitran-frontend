package com.vitran.shop.core.database

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class AndroidDatabaseFactory(
    private val context: Context,
) : DatabaseFactory {
    override fun create(): VitranDatabase {
        val appContext = context.applicationContext
        return Room.databaseBuilder<VitranDatabase>(
            context = appContext,
            name = appContext.getDatabasePath(VITRAN_DATABASE_NAME).absolutePath,
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
