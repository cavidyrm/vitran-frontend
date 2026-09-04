package com.vitran.shop.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.auth.presentation.login.LoginUiEffect
import com.vitran.shop.feature.auth.presentation.login.LoginViewModel
import com.vitran.shop.ui.sections.auth.AuthCredentialsForm
import com.vitran.shop.ui.sections.auth.AuthInlineNotice
import com.vitran.shop.ui.sections.auth.AuthMode
import com.vitran.shop.ui.sections.auth.AuthSplitShell
import com.vitran.shop.ui.theme.VitranSpacing
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import com.vitran.shop.di.vitranKoinViewModel
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.auth_reset_success

private const val PasswordResetNoticeMs = 3200L

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    showPasswordResetNotice: Boolean = false,
    onPasswordResetNoticeConsumed: () -> Unit = {},
    onCreateAccount: () -> Unit = {},
    onSignedIn: () -> Unit = {},
    onVerificationRequired: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    viewModel: LoginViewModel = vitranKoinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                LoginUiEffect.LoginSucceeded -> onSignedIn()
                LoginUiEffect.NavigateToVerification -> onVerificationRequired()
            }
        }
    }

    var noticeVisible by remember(showPasswordResetNotice) {
        mutableStateOf(showPasswordResetNotice)
    }
    LaunchedEffect(showPasswordResetNotice) {
        if (!showPasswordResetNotice) return@LaunchedEffect
        noticeVisible = true
        delay(PasswordResetNoticeMs)
        noticeVisible = false
        onPasswordResetNoticeConsumed()
    }

    AuthSplitShell(modifier = modifier) {
        Column {
            AnimatedVisibility(
                visible = noticeVisible,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                AuthInlineNotice(
                    message = stringResource(Res.string.auth_reset_success),
                    modifier = Modifier.padding(bottom = VitranSpacing.md),
                )
            }
            AuthCredentialsForm(
                mode = AuthMode.Login,
                onModeChange = { mode ->
                    if (mode == AuthMode.Register) onCreateAccount()
                },
                onSubmit = { credentials ->
                    viewModel.submit(credentials.mobile, credentials.password)
                },
                onForgotPassword = onForgotPassword,
                onTermsClick = onTermsClick,
                onPrivacyClick = onPrivacyClick,
                isSubmitting = uiState.isSubmitting,
                submitError = uiState.generalError,
                phoneError = uiState.fieldErrors["phone"],
                passwordError = uiState.fieldErrors["password"],
                onClearFieldError = viewModel::clearFieldError,
            )
        }
    }
}
