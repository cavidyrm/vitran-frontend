package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.sections.auth.AuthCredentials
import com.vitran.shop.ui.sections.auth.AuthCredentialsForm
import com.vitran.shop.ui.sections.auth.AuthMode
import com.vitran.shop.ui.sections.auth.AuthSplitShell

/**
 * Register step 1 — route `/account/register`.
 * Mobile + password; mode toggle navigates to login; submit → verify OTP.
 */
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onContinue: (AuthCredentials) -> Unit = {},
    onSignIn: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
) {
    AuthSplitShell(modifier = modifier) {
        AuthCredentialsForm(
            mode = AuthMode.Register,
            onModeChange = { mode ->
                if (mode == AuthMode.Login) onSignIn()
            },
            onSubmit = onContinue,
            onTermsClick = onTermsClick,
            onPrivacyClick = onPrivacyClick,
        )
    }
}
