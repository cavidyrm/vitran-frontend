package com.vitran.shop.core.database.di

import com.vitran.shop.core.database.DatabaseFactory
import com.vitran.shop.core.database.VitranDatabase
import org.koin.dsl.module

/**
 * Common database bindings.
 *
 * Platform modules must provide [DatabaseFactory] (Android / iOS / JVM / Wasm / JS).
 * This module turns that factory into a [VitranDatabase] singleton.
 */
val databaseModule = module {
    single<VitranDatabase> { get<DatabaseFactory>().create() }
}
