package com.vitran.shop.feature.auth.di

import com.vitran.shop.feature.auth.data.flow.DefaultAuthFlowStateHolder
import com.vitran.shop.feature.auth.data.remote.AuthApi
import com.vitran.shop.feature.auth.data.repository.DefaultAuthRepository
import com.vitran.shop.feature.auth.domain.flow.AuthFlowStateHolder
import com.vitran.shop.feature.auth.domain.repository.AuthRepository
import com.vitran.shop.feature.auth.domain.usecase.LoginUseCase
import com.vitran.shop.feature.auth.domain.usecase.LogoutUseCase
import com.vitran.shop.feature.auth.domain.usecase.RegisterUseCase
import com.vitran.shop.feature.auth.domain.usecase.RequestPasswordResetUseCase
import com.vitran.shop.feature.auth.domain.usecase.ResendOtpUseCase
import com.vitran.shop.feature.auth.domain.usecase.ResetPasswordUseCase
import com.vitran.shop.feature.auth.domain.usecase.VerifyPhoneUseCase
import com.vitran.shop.feature.auth.presentation.forgot.ForgotPasswordViewModel
import com.vitran.shop.feature.auth.presentation.login.LoginViewModel
import com.vitran.shop.feature.auth.presentation.register.RegisterViewModel
import com.vitran.shop.feature.auth.presentation.reset.ResetPasswordViewModel
import com.vitran.shop.feature.auth.presentation.verify.RegisterVerifyViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
    single<AuthFlowStateHolder> { DefaultAuthFlowStateHolder() }
    single { AuthApi(get(), get(), get()) }
    single<AuthRepository> { DefaultAuthRepository(get(), get(), get()) }

    factory { RegisterUseCase(get(), get()) }
    factory { LoginUseCase(get(), get()) }
    factory { VerifyPhoneUseCase(get(), get()) }
    factory { ResendOtpUseCase(get(), get()) }
    factory { RequestPasswordResetUseCase(get(), get()) }
    factory { ResetPasswordUseCase(get(), get()) }
    factory { LogoutUseCase(get(), get()) }

    viewModel {
        LoginViewModel(
            loginUseCase = get(),
            validatePhone = get(named("validatePhone")),
            validatePassword = get(named("validateAuthPassword")),
        )
    }
    viewModel {
        RegisterViewModel(
            registerUseCase = get(),
            validatePhone = get(named("validatePhone")),
            validatePassword = get(named("validateAuthPassword")),
        )
    }
    viewModel { RegisterVerifyViewModel(get(), get(), get(), get()) }
    viewModel { ForgotPasswordViewModel(get(), get(named("validatePhone"))) }
    viewModel {
        ResetPasswordViewModel(
            resetPasswordUseCase = get(),
            authFlowStateHolder = get(),
            apiEnvironment = get(),
        )
    }
}
