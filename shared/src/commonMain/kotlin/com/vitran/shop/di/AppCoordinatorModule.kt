package com.vitran.shop.di

import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.ui.sections.auth.isValidAuthPassword
import com.vitran.shop.ui.sections.auth.isValidIranMobile
import com.vitran.shop.ui.sections.auth.resetPasswordRulesOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appCoordinatorModule = module {
    single { AppSessionCoordinator(get(), get()) }

    single(named("validatePhone")) { { mobile: String -> isValidIranMobile(mobile) } }
    single(named("validateAuthPassword")) { { password: String -> isValidAuthPassword(password) } }
    single(named("validateResetPassword")) { { password: String -> resetPasswordRulesOf(password).allMet } }
}

class AppSessionCoordinator(
    private val sessionRepository: SessionRepository,
    private val accountRepository: AccountRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob()),
) {
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Restoring)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private var started = false

    fun start() {
        if (started) return
        started = true
        scope.launch {
            sessionRepository.restore()
        }
        scope.launch {
            sessionRepository.sessionState.collect { state ->
                _sessionState.value = state
                if (state == SessionState.Authenticated) {
                    accountRepository.refreshCurrentUser()
                }
            }
        }
    }
}
