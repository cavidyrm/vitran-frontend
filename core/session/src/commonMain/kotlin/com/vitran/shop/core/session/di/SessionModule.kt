package com.vitran.shop.core.session.di

import com.vitran.shop.core.session.EmptySessionReader
import com.vitran.shop.core.session.SessionReader
import org.koin.dsl.module

val sessionModule = module {
    single<SessionReader> { EmptySessionReader() }
}
