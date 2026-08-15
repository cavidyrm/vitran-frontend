package com.vitran.shop.ui.sections.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vitran.shop.ui.theme.VitranTheme

/**
 * Legacy alias — prefer [AuthSplitShell] (two-column brand panel, no language row).
 */
@Composable
fun AuthShell(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AuthSplitShell(modifier = modifier, content = content)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AuthShellPreview() {
    VitranTheme {
        AuthShell {
            AuthLoginForm()
        }
    }
}
