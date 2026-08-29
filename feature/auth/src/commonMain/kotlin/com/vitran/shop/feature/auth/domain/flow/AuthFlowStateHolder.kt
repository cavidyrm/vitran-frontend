package com.vitran.shop.feature.auth.domain.flow

import com.vitran.shop.feature.auth.domain.model.PasswordResetContext
import com.vitran.shop.feature.auth.domain.model.VerificationChallenge
import kotlinx.coroutines.flow.StateFlow

interface AuthFlowStateHolder {
    val verificationChallenge: StateFlow<VerificationChallenge?>
    val passwordResetContext: StateFlow<PasswordResetContext?>

    fun setVerificationChallenge(challenge: VerificationChallenge?)
    fun setPasswordResetContext(context: PasswordResetContext?)
    fun clear()
}
