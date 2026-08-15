package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.sections.auth.AuthForgotPasswordForm
import com.vitran.shop.ui.sections.auth.AuthSplitShell

/**
 * Forgot password — route `/account/forgot`.
 * Mobile only; submit sends a mock code and continues to reset.
 */
@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onSendCode: (String) -> Unit = {},
    onBackToLogin: () -> Unit = {},
) {
    AuthSplitShell(modifier = modifier) {
        AuthForgotPasswordForm(
            onSendCode = onSendCode,
            onBackToLogin = onBackToLogin,
        )
    }
}
