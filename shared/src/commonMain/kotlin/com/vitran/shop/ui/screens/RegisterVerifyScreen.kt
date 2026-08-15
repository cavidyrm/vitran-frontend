package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.sections.auth.AuthOtpForm
import com.vitran.shop.ui.sections.auth.AuthSplitShell

/**
 * Register OTP step — route `/account/register/verify`.
 * Mock phase: completing OTP does not flip auth state.
 */
@Composable
fun RegisterVerifyScreen(
    phone: String,
    modifier: Modifier = Modifier,
    onChangeMobile: () -> Unit = {},
    onVerified: (String) -> Unit = {},
) {
    AuthSplitShell(modifier = modifier) {
        AuthOtpForm(
            destination = phone,
            onChangeDestination = onChangeMobile,
            onCodeComplete = onVerified,
        )
    }
}
