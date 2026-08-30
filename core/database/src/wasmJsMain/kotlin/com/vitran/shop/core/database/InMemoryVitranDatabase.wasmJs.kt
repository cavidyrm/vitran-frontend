package com.vitran.shop.core.database

actual fun createInMemoryVitranDatabase(): VitranDatabase {
    error("In-memory Room helpers are unsupported on Wasm; use JVM tests or WebWorkerSQLiteDriver")
}
