package com.vitran.shop.core.session.di

import com.vitran.shop.core.session.SessionReader
import com.vitran.shop.core.session.auth.DefaultSessionAuthCoordinator
import com.vitran.shop.core.session.auth.DefaultTokenRefreshCoordinator
import com.vitran.shop.core.session.auth.SessionAuthCoordinator
import com.vitran.shop.core.session.auth.TokenRefreshCoordinator
import com.vitran.shop.core.session.data.CredentialStore
import com.vitran.shop.core.session.data.SessionCredentialPersistence
import com.vitran.shop.core.session.repository.DefaultSessionReader
import com.vitran.shop.core.session.repository.DefaultSessionRepository
import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.core.session.repository.SessionRoleCache
import com.vitran.shop.core.session.time.AppClock
import com.vitran.shop.core.session.time.SystemAppClock
import org.koin.dsl.module

val sessionModule = module {
    single<AppClock> { SystemAppClock() }
    single { CredentialStore() }
    single { SessionRoleCache() }
    single { SessionCredentialPersistence(get()) }
    single { mutableListOf<SessionInvalidationListener>() }

    single<SessionRepository> {
        DefaultSessionRepository(
            credentialStore = get(),
            persistence = get(),
            invalidationListeners = get(),
            roleCache = get(),
        )
    }

    single<TokenRefreshCoordinator> {
        DefaultTokenRefreshCoordinator(
            sessionRepository = get(),
            refreshRemote = get(),
            clock = get(),
        )
    }

    single<SessionAuthCoordinator> {
        DefaultSessionAuthCoordinator(refreshCoordinator = get())
    }

    single<SessionReader> {
        DefaultSessionReader(
            credentialStore = get(),
            roleCache = get(),
        )
    }
}
