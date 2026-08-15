package com.vitran.shop.ui.sections.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.SurfaceWhite
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.auth_change_mobile
import vitranshop.shared.generated.resources.auth_logo_a11y
import vitranshop.shared.generated.resources.auth_new_password_label
import vitranshop.shared.generated.resources.auth_otp_a11y
import vitranshop.shared.generated.resources.auth_otp_error
import vitranshop.shared.generated.resources.auth_otp_resend
import vitranshop.shared.generated.resources.auth_otp_resend_countdown
import vitranshop.shared.generated.resources.auth_reset_cta
import vitranshop.shared.generated.resources.auth_reset_saving
import vitranshop.shared.generated.resources.auth_reset_subtitle
import vitranshop.shared.generated.resources.auth_reset_title
import vitranshop.shared.generated.resources.ic_edit
import vitranshop.shared.generated.resources.ic_shop_logo

private enum class ResetUiPhase {
    Idle,
    Saving,
    Error,
}

/**
 * Reset-password card: 6-digit OTP + new password (no confirm, no OTP auto-submit).
 *
 * Mock: code [AuthTokens.MockInvalidOtp] fails; any other 6-digit code with a valid
 * password succeeds after a short delay.
 */
@Composable
fun AuthResetPasswordForm(
    destination: String,
    modifier: Modifier = Modifier,
    onChangeDestination: () -> Unit = {},
    onResetComplete: () -> Unit = {},
) {
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phase by remember { mutableStateOf(ResetUiPhase.Idle) }
    var secondsLeft by remember { mutableIntStateOf(AuthTokens.OtpResendSeconds) }
    var resendGeneration by remember { mutableIntStateOf(0) }
    var saveRequestId by remember { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    val otpA11y = stringResource(Res.string.auth_otp_a11y)
    val masked = maskIranMobile(destination)
    val passwordRules = resetPasswordRulesOf(password)
    val canSubmit = code.length == 6 &&
        passwordRules.allMet &&
        phase != ResetUiPhase.Saving

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(resendGeneration) {
        secondsLeft = AuthTokens.OtpResendSeconds
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    LaunchedEffect(saveRequestId) {
        if (saveRequestId == 0) return@LaunchedEffect
        if (code.length != 6 || !passwordRules.allMet) return@LaunchedEffect
        phase = ResetUiPhase.Saving
        delay(650)
        if (code == AuthTokens.MockInvalidOtp) {
            phase = ResetUiPhase.Error
            focusRequester.requestFocus()
        } else {
            onResetComplete()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = AuthTokens.CardShadowElevation,
                shape = AuthCardShape,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.1f),
            )
            .clip(AuthCardShape)
            .background(SurfaceWhite, AuthCardShape)
            .padding(
                horizontal = AuthTokens.CardPaddingHorizontal,
                vertical = AuthTokens.CardPaddingVertical,
            ),
        horizontalAlignment = Alignment.Start,
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_shop_logo),
            contentDescription = stringResource(Res.string.auth_logo_a11y),
            modifier = Modifier.size(AuthTokens.LogoSize),
        )

        Text(
            text = stringResource(Res.string.auth_reset_title),
            modifier = Modifier.padding(top = VitranSpacing.md),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp,
                lineHeight = 32.sp,
            ),
        )

        Text(
            text = stringResource(Res.string.auth_reset_subtitle, masked),
            modifier = Modifier.padding(top = VitranSpacing.sm),
            color = AuthTokens.Muted,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 22.sp,
            ),
        )

        Row(
            modifier = Modifier
                .padding(top = VitranSpacing.sm)
                .clickable(
                    enabled = phase != ResetUiPhase.Saving,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onChangeDestination,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_edit),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(VitranSpacing.xs))
            Text(
                text = stringResource(Res.string.auth_change_mobile),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp,
                ),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = VitranSpacing.xl),
        ) {
            AuthOtpCodeField(
                code = code,
                onCodeChange = { next ->
                    if (phase == ResetUiPhase.Saving) return@AuthOtpCodeField
                    code = next
                    if (phase == ResetUiPhase.Error) phase = ResetUiPhase.Idle
                },
                focusRequester = focusRequester,
                a11y = otpA11y,
                isError = phase == ResetUiPhase.Error,
            )
            if (phase == ResetUiPhase.Error) {
                Text(
                    text = stringResource(Res.string.auth_otp_error),
                    modifier = Modifier.padding(top = VitranSpacing.xs),
                    color = AuthTokens.OtpError,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp,
                    ),
                )
            }
        }

        Spacer(
            modifier = Modifier
                .padding(vertical = VitranSpacing.md)
                .fillMaxWidth()
                .height(1.dp)
                .background(AuthTokens.FieldBorder.copy(alpha = 0.7f)),
        )

        AuthPasswordField(
            value = password,
            onValueChange = { password = it },
            onSubmit = { if (canSubmit) saveRequestId++ },
            label = stringResource(Res.string.auth_new_password_label),
        )

        AuthPasswordRulesChecklist(
            rules = passwordRules,
            modifier = Modifier.padding(top = VitranSpacing.sm),
        )

        val ctaLabel = if (phase == ResetUiPhase.Saving) {
            stringResource(Res.string.auth_reset_saving)
        } else {
            stringResource(Res.string.auth_reset_cta)
        }
        AuthPrimaryButton(
            label = ctaLabel,
            enabled = canSubmit,
            loading = phase == ResetUiPhase.Saving,
            onClick = { saveRequestId++ },
            modifier = Modifier.padding(top = VitranSpacing.lg),
        )

        val resendEnabled = secondsLeft == 0 && phase != ResetUiPhase.Saving
        Text(
            text = if (secondsLeft > 0) {
                stringResource(
                    Res.string.auth_otp_resend_countdown,
                    formatOtpCountdown(secondsLeft),
                )
            } else {
                stringResource(Res.string.auth_otp_resend)
            },
            modifier = Modifier
                .padding(top = VitranSpacing.md)
                .clickable(
                    enabled = resendEnabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        resendGeneration++
                        code = ""
                        phase = ResetUiPhase.Idle
                        focusRequester.requestFocus()
                    },
                ),
            color = if (resendEnabled) {
                MaterialTheme.colorScheme.primary
            } else {
                AuthTokens.Muted.copy(alpha = 0.72f)
            },
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                fontWeight = if (resendEnabled) FontWeight.SemiBold else FontWeight.Medium,
                lineHeight = 18.sp,
                textAlign = TextAlign.Start,
                textDecoration = if (resendEnabled) TextDecoration.Underline else TextDecoration.None,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun AuthResetPasswordFormPreview() {
    VitranTheme {
        AuthResetPasswordForm(
            destination = "09123456789",
            modifier = Modifier.padding(VitranSpacing.lg),
        )
    }
}
