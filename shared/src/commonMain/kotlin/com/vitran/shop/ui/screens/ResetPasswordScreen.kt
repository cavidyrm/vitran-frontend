package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.sections.auth.AuthResetPasswordForm
import com.vitran.shop.ui.sections.auth.AuthSplitShell

/**
 * Reset password — route `/account/forgot/reset`.
 * OTP + new password; mock phase does not flip auth state.
 */
@Composable
fun ResetPasswordScreen(
    phone: String,
    modifier: Modifier = Modifier,
    onChangeMobile: () -> Unit = {},
    onResetComplete: () -> Unit = {},
) {
    AuthSplitShell(modifier = modifier) {
        AuthResetPasswordForm(
            destination = phone,
            onChangeDestination = onChangeMobile,
            onResetComplete = onResetComplete,
        )
    }
}
