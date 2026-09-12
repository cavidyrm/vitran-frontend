package com.vitran.shop.feature.auth.presentation.register

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.auth.domain.ReferralCodeValidator
import com.vitran.shop.feature.auth.domain.flow.AuthFlowStateHolder
import com.vitran.shop.feature.auth.domain.model.LoginResult
import com.vitran.shop.feature.auth.domain.model.PasswordResetContext
import com.vitran.shop.feature.auth.domain.model.PhoneCheckResult
import com.vitran.shop.feature.auth.domain.model.RegisterCommand
import com.vitran.shop.feature.auth.domain.model.VerificationChallenge
import com.vitran.shop.feature.auth.domain.repository.AuthRepository
import com.vitran.shop.feature.auth.domain.usecase.CheckPhoneUseCase
import com.vitran.shop.feature.auth.domain.usecase.RegisterUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @Test
    fun inviteCode_debounceAndCancellation_firesOnce() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val codes = mutableListOf<String>()
            val vm = createVm(
                validator = ReferralCodeValidator { code ->
                    codes += code
                    AppResult.Success(true)
                },
            )
            vm.onInviteCodeChanged("V")
            vm.onInviteCodeChanged("V2")
            vm.onInviteCodeChanged("V21")
            advanceTimeBy(399)
            assertEquals(emptyList(), codes)
            assertIs<ReferralCodeCheckUiStatus.Idle>(vm.uiState.value.referralCheck)
            advanceTimeBy(2)
            advanceUntilIdle()
            assertEquals(listOf("V21"), codes)
            assertIs<ReferralCodeCheckUiStatus.Valid>(vm.uiState.value.referralCheck)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun inviteCode_blank_cancelsAndSkipsNetwork() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            var checks = 0
            val vm = createVm(
                validator = ReferralCodeValidator {
                    checks += 1
                    AppResult.Success(true)
                },
            )
            vm.onInviteCodeChanged("V2")
            vm.onInviteCodeChanged("  ")
            advanceUntilIdle()
            assertEquals(0, checks)
            assertIs<ReferralCodeCheckUiStatus.Idle>(vm.uiState.value.referralCheck)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun inviteCode_validFalse_isInvalidNotHttpError() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val vm = createVm(
                validator = ReferralCodeValidator { AppResult.Success(false) },
            )
            vm.onInviteCodeChanged("NOPE")
            advanceTimeBy(400)
            advanceUntilIdle()
            assertIs<ReferralCodeCheckUiStatus.Invalid>(vm.uiState.value.referralCheck)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun inviteCode_networkFailure_isSoftError() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val vm = createVm(
                validator = ReferralCodeValidator {
                    AppResult.Failure(AppError.Network.NoConnection())
                },
            )
            vm.onInviteCodeChanged("V2")
            advanceTimeBy(400)
            advanceUntilIdle()
            assertIs<ReferralCodeCheckUiStatus.Error>(vm.uiState.value.referralCheck)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun submit_stillProceedsWhenInviteCodeInvalid() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val authRepository = RecordingAuthRepository()
            val vm = createVm(
                validator = ReferralCodeValidator { AppResult.Success(false) },
                authRepository = authRepository,
            )
            vm.onInviteCodeChanged("NOPE")
            advanceTimeBy(400)
            advanceUntilIdle()
            assertIs<ReferralCodeCheckUiStatus.Invalid>(vm.uiState.value.referralCheck)

            val events = mutableListOf<RegisterUiEffect>()
            backgroundScope.launch { vm.effects.collect { events.add(it) } }
            vm.submit("09121111111", "secret12", "NOPE")
            advanceUntilIdle()
            assertEquals("NOPE", authRepository.lastCommand?.referralCode)
            assertEquals(
                listOf(RegisterUiEffect.NavigateToVerification("09121111111")),
                events,
            )
            assertNull(vm.uiState.value.generalError)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun createVm(
        validator: ReferralCodeValidator,
        authRepository: AuthRepository = RecordingAuthRepository(),
    ): RegisterViewModel =
        RegisterViewModel(
            registerUseCase = RegisterUseCase(authRepository, FakeAuthFlowStateHolder()),
            checkPhoneUseCase = CheckPhoneUseCase(authRepository),
            referralCodeValidator = validator,
            validatePhone = { true },
            validatePassword = { true },
            referralDebounceMs = 400L,
        )
}

private class RecordingAuthRepository : AuthRepository {
    var lastCommand: RegisterCommand? = null

    override suspend fun checkPhone(phone: String): AppResult<PhoneCheckResult> = unused()

    override suspend fun register(command: RegisterCommand): AppResult<VerificationChallenge> {
        lastCommand = command
        return AppResult.Success(VerificationChallenge(phone = command.phone, tempToken = "tmp"))
    }

    override suspend fun login(phone: String, password: String): AppResult<LoginResult> =
        unused()

    override suspend fun verify(tempToken: String, code: String): AppResult<Unit> = unused()

    override suspend fun resendOtp(phone: String): AppResult<String?> = unused()

    override suspend fun requestPasswordReset(phone: String): AppResult<PasswordResetContext> =
        unused()

    override suspend fun resetPassword(
        phone: String,
        code: String,
        newPassword: String,
    ): AppResult<Unit> = unused()

    override suspend fun logout(): AppResult<Unit> = unused()

    private fun unused(): Nothing = error("unused")
}

private class FakeAuthFlowStateHolder : AuthFlowStateHolder {
    private val _verificationChallenge = MutableStateFlow<VerificationChallenge?>(null)
    override val verificationChallenge: StateFlow<VerificationChallenge?> =
        _verificationChallenge.asStateFlow()

    private val _passwordResetContext = MutableStateFlow<PasswordResetContext?>(null)
    override val passwordResetContext: StateFlow<PasswordResetContext?> =
        _passwordResetContext.asStateFlow()

    override fun setVerificationChallenge(challenge: VerificationChallenge?) {
        _verificationChallenge.value = challenge
    }

    override fun setPasswordResetContext(context: PasswordResetContext?) {
        _passwordResetContext.value = context
    }

    override fun clear() {
        _verificationChallenge.value = null
        _passwordResetContext.value = null
    }
}
