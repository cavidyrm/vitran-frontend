package com.vitran.shop.core.database

import androidx.room3.Room
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.w3c.dom.Worker

/** JS browser lib — WebWorker SQLite (same hosting requirements as Wasm). */
class JsDatabaseFactory(
    private val workerScriptUrl: String = "./sqlite3.worker.js",
) : DatabaseFactory {
    override fun create(): VitranDatabase {
        val worker = Worker(workerScriptUrl)
        return Room.databaseBuilder<VitranDatabase>(name = VITRAN_DATABASE_NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .setDriver(WebWorkerSQLiteDriver(worker))
            .setQueryCoroutineContext(Dispatchers.Default)
            .build()
    }
}
