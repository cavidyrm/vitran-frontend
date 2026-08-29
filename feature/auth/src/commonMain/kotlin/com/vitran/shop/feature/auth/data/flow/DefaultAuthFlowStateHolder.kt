package com.vitran.shop.feature.auth.data.flow

import com.vitran.shop.feature.auth.domain.flow.AuthFlowStateHolder
import com.vitran.shop.feature.auth.domain.model.PasswordResetContext
import com.vitran.shop.feature.auth.domain.model.VerificationChallenge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class DefaultAuthFlowStateHolder : AuthFlowStateHolder {
    private val _verificationChallenge = MutableStateFlow<VerificationChallenge?>(null)
    override val verificationChallenge: StateFlow<VerificationChallenge?> = _verificationChallenge.asStateFlow()

    private val _passwordResetContext = MutableStateFlow<PasswordResetContext?>(null)
    override val passwordResetContext: StateFlow<PasswordResetContext?> = _passwordResetContext.asStateFlow()

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
