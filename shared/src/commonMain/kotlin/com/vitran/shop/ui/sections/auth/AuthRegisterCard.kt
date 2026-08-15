package com.vitran.shop.ui.sections.auth

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme

/**
 * Register credentials card — hosted inside [AuthSplitShell] by [com.vitran.shop.ui.screens.RegisterScreen].
 * Kept as a named entry for previews / reuse.
 */
@Composable
fun AuthRegisterCard(
    modifier: Modifier = Modifier,
    onSubmit: (AuthCredentials) -> Unit = {},
    onSignIn: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
) {
    AuthCredentialsForm(
        mode = AuthMode.Register,
        onModeChange = { if (it == AuthMode.Login) onSignIn() },
        onSubmit = onSubmit,
        onTermsClick = onTermsClick,
        onPrivacyClick = onPrivacyClick,
        modifier = modifier,
    )
}

@Preview(showBackground = true, widthDp = 420)
@Composable
private fun AuthRegisterCardPreview() {
    VitranTheme {
        AuthRegisterCard(modifier = Modifier.padding(VitranSpacing.lg))
    }
}
