package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.auth.presentation.forgot.ForgotPasswordUiEffect
import com.vitran.shop.feature.auth.presentation.forgot.ForgotPasswordViewModel
import com.vitran.shop.ui.sections.auth.AuthForgotPasswordForm
import com.vitran.shop.ui.sections.auth.AuthSplitShell
import com.vitran.shop.di.vitranKoinViewModel

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onSendCode: (String) -> Unit = {},
    onBackToLogin: () -> Unit = {},
    viewModel: ForgotPasswordViewModel = vitranKoinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ForgotPasswordUiEffect.NavigateToReset -> onSendCode(effect.phone)
            }
        }
    }

    AuthSplitShell(modifier = modifier) {
        AuthForgotPasswordForm(
            onSendCode = viewModel::submit,
            onBackToLogin = onBackToLogin,
            isSubmitting = uiState.isSubmitting,
            submitError = uiState.generalError,
        )
    }
}
