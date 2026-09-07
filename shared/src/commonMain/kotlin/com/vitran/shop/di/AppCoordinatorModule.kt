package com.vitran.shop.di

import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.auth.domain.ReferralCodeValidator
import com.vitran.shop.ui.sections.auth.isValidAuthPassword
import com.vitran.shop.ui.sections.auth.isValidIranMobile
import com.vitran.shop.ui.sections.auth.resetPasswordRulesOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appCoordinatorModule = module {
    single { AppSessionCoordinator(get(), get()) }
    single<ReferralCodeValidator> { ReferralRepositoryCodeValidator(get()) }

    single(named("validatePhone")) { { mobile: String -> isValidIranMobile(mobile) } }
    single(named("validateAuthPassword")) { { password: String -> isValidAuthPassword(password) } }
    single(named("validateResetPassword")) { { password: String -> resetPasswordRulesOf(password).allMet } }
}

class AppSessionCoordinator(
    private val sessionRepository: SessionRepository,
    private val accountRepository: AccountRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {
    private var started = false

    fun start() {
        if (started) return
        started = true
        scope.launch {
            sessionRepository.restore()
        }
        scope.launch {
            sessionRepository.sessionState.collect { state ->
                if (state == SessionState.Authenticated) {
                    accountRepository.refreshCurrentUser()
                }
            }
        }
    }
}
