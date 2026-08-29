package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.auth.presentation.verify.RegisterVerifyUiEffect
import com.vitran.shop.feature.auth.presentation.verify.RegisterVerifyViewModel
import com.vitran.shop.ui.sections.auth.AuthOtpForm
import com.vitran.shop.ui.sections.auth.AuthSplitShell
import com.vitran.shop.di.vitranKoinViewModel

@Composable
fun RegisterVerifyScreen(
    phone: String,
    modifier: Modifier = Modifier,
    onChangeMobile: () -> Unit = {},
    onVerified: () -> Unit = {},
    viewModel: RegisterVerifyViewModel = vitranKoinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val destination = uiState.phoneDisplay.ifBlank { phone }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                RegisterVerifyUiEffect.Verified -> onVerified()
                RegisterVerifyUiEffect.ChallengeMissing -> onChangeMobile()
            }
        }
    }

    AuthSplitShell(modifier = modifier) {
        AuthOtpForm(
            destination = destination,
            onChangeDestination = onChangeMobile,
            liveVerification = true,
            isVerifying = uiState.isSubmitting,
            verifyError = uiState.error,
            onVerify = viewModel::verifyCode,
            onResend = viewModel::resendCode,
            debugOtpHint = uiState.debugOtpCode,
        )
    }
}
