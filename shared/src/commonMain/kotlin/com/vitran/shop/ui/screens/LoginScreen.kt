package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.sections.auth.AuthLoginForm
import com.vitran.shop.ui.sections.auth.AuthOtpForm
import com.vitran.shop.ui.sections.auth.AuthShell

private enum class AuthLoginStep {
    Email,
    Otp,
}

/**
 * Sign-in screen — route `/account/login` ↔ shop.app `/accounts/login`.
 *
 * Mock phase: email → OTP step (same URL); Continue / Change email only swap UI.
 * Completing OTP does not flip auth state.
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
) {
    var step by remember { mutableStateOf(AuthLoginStep.Email) }
    var email by remember { mutableStateOf("") }

    AuthShell(modifier = modifier) {
        when (step) {
            AuthLoginStep.Email -> AuthLoginForm(
                onContinue = { value ->
                    email = value.trim()
                    if (email.isNotEmpty()) {
                        step = AuthLoginStep.Otp
                    }
                },
                onTermsClick = onTermsClick,
                onPrivacyClick = onPrivacyClick,
            )
            AuthLoginStep.Otp -> AuthOtpForm(
                email = email,
                onChangeEmail = { step = AuthLoginStep.Email },
                onCodeComplete = {
                    // Mock phase — no auth state change.
                },
            )
        }
    }
}
