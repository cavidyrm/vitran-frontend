package com.vitran.shop.core.database

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import java.io.File

class JvmDatabaseFactory(
    private val dataDir: File = File(System.getProperty("user.home"), ".vitranshop/db"),
) : DatabaseFactory {
    override fun create(): VitranDatabase {
        if (!dataDir.exists()) dataDir.mkdirs()
        val dbFile = File(dataDir, VITRAN_DATABASE_NAME)
        return Room.databaseBuilder<VitranDatabase>(
            name = dbFile.absolutePath,
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
