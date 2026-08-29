package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.auth.presentation.reset.ResetPasswordUiEffect
import com.vitran.shop.feature.auth.presentation.reset.ResetPasswordViewModel
import com.vitran.shop.ui.sections.auth.AuthResetPasswordForm
import com.vitran.shop.ui.sections.auth.AuthSplitShell
import com.vitran.shop.di.vitranKoinViewModel

@Composable
fun ResetPasswordScreen(
    phone: String,
    modifier: Modifier = Modifier,
    onChangeMobile: () -> Unit = {},
    onResetComplete: () -> Unit = {},
    viewModel: ResetPasswordViewModel = vitranKoinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ResetPasswordUiEffect.ResetSucceeded -> onResetComplete()
                ResetPasswordUiEffect.ContextMissing -> onChangeMobile()
            }
        }
    }

    AuthSplitShell(modifier = modifier) {
        AuthResetPasswordForm(
            destination = phone,
            onChangeDestination = onChangeMobile,
            liveReset = true,
            isSubmitting = uiState.isSubmitting,
            resetError = uiState.generalError,
            onSubmit = viewModel::submit,
            debugOtpHint = uiState.debugOtpCode,
        )
    }
}
