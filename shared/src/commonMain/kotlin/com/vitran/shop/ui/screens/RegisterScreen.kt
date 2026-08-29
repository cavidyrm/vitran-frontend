package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.auth.presentation.register.RegisterUiEffect
import com.vitran.shop.feature.auth.presentation.register.RegisterViewModel
import com.vitran.shop.ui.sections.auth.AuthCredentialsForm
import com.vitran.shop.ui.sections.auth.AuthMode
import com.vitran.shop.ui.sections.auth.AuthSplitShell
import com.vitran.shop.di.vitranKoinViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onContinue: (String) -> Unit = {},
    onSignIn: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    viewModel: RegisterViewModel = vitranKoinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RegisterUiEffect.NavigateToVerification -> onContinue(effect.phone)
            }
        }
    }

    AuthSplitShell(modifier = modifier) {
        AuthCredentialsForm(
            mode = AuthMode.Register,
            onModeChange = { mode ->
                if (mode == AuthMode.Login) onSignIn()
            },
            onSubmit = { credentials ->
                viewModel.submit(
                    phone = credentials.mobile,
                    password = credentials.password,
                    referralCode = credentials.inviteCode.ifBlank { null },
                )
            },
            onTermsClick = onTermsClick,
            onPrivacyClick = onPrivacyClick,
            isSubmitting = uiState.isSubmitting,
            submitError = uiState.generalError,
        )
    }
}
