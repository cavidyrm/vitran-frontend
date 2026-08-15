package com.vitran.shop.ui.sections.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.SurfaceWhite
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.auth_change_mobile
import vitranshop.shared.generated.resources.auth_otp_a11y
import vitranshop.shared.generated.resources.auth_otp_error
import vitranshop.shared.generated.resources.auth_otp_resend
import vitranshop.shared.generated.resources.auth_otp_resend_countdown
import vitranshop.shared.generated.resources.auth_otp_success
import vitranshop.shared.generated.resources.auth_otp_verify_cta
import vitranshop.shared.generated.resources.auth_otp_verifying
import vitranshop.shared.generated.resources.auth_verify_subtitle
import vitranshop.shared.generated.resources.auth_verify_title
import vitranshop.shared.generated.resources.ic_check
import vitranshop.shared.generated.resources.ic_edit

private val OtpCellShape = RoundedCornerShape(AuthTokens.OtpCellRadius)
private val OtpCardShape = RoundedCornerShape(VitranRadius.large)

private enum class OtpUiPhase {
    Idle,
    Verifying,
    Error,
    Success,
}

/**
 * OTP verify card: masked phone, 6-cell entry (paste-friendly), timer, verify CTA, error/success.
 *
 * Mock: code [AuthTokens.MockInvalidOtp] fails; any other 6-digit code succeeds after a short delay.
 */
@Composable
fun AuthOtpForm(
    destination: String,
    modifier: Modifier = Modifier,
    onChangeDestination: () -> Unit = {},
    onCodeComplete: (String) -> Unit = {},
) {
    var code by remember { mutableStateOf("") }
    var phase by remember { mutableStateOf(OtpUiPhase.Idle) }
    var secondsLeft by remember { mutableIntStateOf(AuthTokens.OtpResendSeconds) }
    var resendGeneration by remember { mutableIntStateOf(0) }
    var verifyRequestId by remember { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    val otpA11y = stringResource(Res.string.auth_otp_a11y)
    val masked = maskIranMobile(destination)

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

    LaunchedEffect(verifyRequestId) {
        if (verifyRequestId == 0) return@LaunchedEffect
        val digits = code
        if (digits.length != 6) return@LaunchedEffect
        phase = OtpUiPhase.Verifying
        delay(650)
        if (digits == AuthTokens.MockInvalidOtp) {
            phase = OtpUiPhase.Error
        } else {
            phase = OtpUiPhase.Success
            delay(700)
            onCodeComplete(digits)
        }
    }

    // Auto-submit when 6 digits entered (from typing or paste).
    LaunchedEffect(code) {
        if (code.length == 6 && phase == OtpUiPhase.Idle) {
            verifyRequestId++
        }
    }

    Column(
        modifier = modifier
            .widthIn(max = AuthTokens.OtpCardMaxWidth)
            .fillMaxWidth()
            .shadow(
                elevation = AuthTokens.CardShadowElevation,
                shape = OtpCardShape,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.1f),
            )
            .clip(OtpCardShape)
            .background(SurfaceWhite, OtpCardShape)
            .padding(
                horizontal = AuthTokens.CardPaddingHorizontal,
                vertical = AuthTokens.CardPaddingVertical,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.auth_verify_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center,
            ),
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(Res.string.auth_verify_subtitle),
            modifier = Modifier.padding(top = VitranSpacing.md),
            color = AuthTokens.Muted,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
            ),
            textAlign = TextAlign.Center,
        )

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Text(
                text = masked,
                modifier = Modifier.padding(top = VitranSpacing.xs),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                ),
                textAlign = TextAlign.Center,
            )
        }

        Row(
            modifier = Modifier
                .padding(top = VitranSpacing.sm)
                .clickable(
                    enabled = phase != OtpUiPhase.Verifying,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onChangeDestination,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
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

        AuthOtpCodeField(
            code = code,
            onCodeChange = { next ->
                if (phase == OtpUiPhase.Verifying || phase == OtpUiPhase.Success) return@AuthOtpCodeField
                val digits = next.mapNotNull { ch ->
                    when (ch) {
                        in '0'..'9' -> ch
                        in '۰'..'۹' -> ('0'.code + (ch - '۰')).toChar()
                        in '٠'..'٩' -> ('0'.code + (ch - '٠')).toChar()
                        else -> null
                    }
                }.joinToString(separator = "").take(6)
                code = digits
                if (phase == OtpUiPhase.Error) phase = OtpUiPhase.Idle
            },
            focusRequester = focusRequester,
            a11y = otpA11y,
            isError = phase == OtpUiPhase.Error,
            modifier = Modifier.padding(top = VitranSpacing.xl),
        )

        when (phase) {
            OtpUiPhase.Error -> {
                Text(
                    text = stringResource(Res.string.auth_otp_error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = VitranSpacing.sm),
                    color = AuthTokens.OtpError,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                    ),
                    textAlign = TextAlign.Center,
                )
            }
            OtpUiPhase.Success -> {
                Row(
                    modifier = Modifier.padding(top = VitranSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_check),
                        contentDescription = null,
                        size = VitranSize.iconSmall,
                        tint = AuthTokens.OtpSuccess,
                    )
                    Spacer(modifier = Modifier.width(VitranSpacing.xs))
                    Text(
                        text = stringResource(Res.string.auth_otp_success),
                        color = AuthTokens.OtpSuccess,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 18.sp,
                        ),
                    )
                }
            }
            else -> Unit
        }

        val verifyLabel = if (phase == OtpUiPhase.Verifying) {
            stringResource(Res.string.auth_otp_verifying)
        } else {
            stringResource(Res.string.auth_otp_verify_cta)
        }
        AuthPrimaryButton(
            label = verifyLabel,
            enabled = code.length == 6 && phase != OtpUiPhase.Success && phase != OtpUiPhase.Verifying,
            loading = phase == OtpUiPhase.Verifying,
            onClick = { verifyRequestId++ },
            modifier = Modifier.padding(top = VitranSpacing.xl),
        )

        val resendEnabled = secondsLeft == 0 && phase != OtpUiPhase.Verifying
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
                        phase = OtpUiPhase.Idle
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
                textAlign = TextAlign.Center,
                textDecoration = if (resendEnabled) TextDecoration.Underline else TextDecoration.None,
            ),
            textAlign = TextAlign.Center,
        )
    }
}

private fun formatOtpCountdown(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}

@Composable
private fun AuthOtpCodeField(
    code: String,
    onCodeChange: (String) -> Unit,
    focusRequester: FocusRequester,
    a11y: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .semantics { contentDescription = a11y }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { focusRequester.requestFocus() },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AuthTokens.OtpCellGap),
                modifier = Modifier,
            ) {
                repeat(6) { index ->
                    OtpDigitCell(
                        digit = code.getOrNull(index)?.toString().orEmpty(),
                        focused = code.length == index || (index == 0 && code.isEmpty()),
                        isError = isError,
                    )
                }
            }

            BasicTextField(
                value = code,
                onValueChange = onCodeChange,
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.01f)
                    .focusRequester(focusRequester),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Transparent),
                cursorBrush = SolidColor(Color.Transparent),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            )
        }
    }
}

@Composable
private fun OtpDigitCell(
    digit: String,
    focused: Boolean,
    isError: Boolean,
) {
    val borderColor = when {
        isError -> AuthTokens.OtpError
        focused -> MaterialTheme.colorScheme.primary
        else -> AuthTokens.OtpIdleBorder
    }
    Box(
        modifier = Modifier
            .width(AuthTokens.OtpCellWidth)
            .height(AuthTokens.OtpCellHeight)
            .then(
                if (focused && !isError) {
                    Modifier.shadow(
                        elevation = 4.dp,
                        shape = OtpCellShape,
                        ambientColor = Color.Black.copy(alpha = 0.02f),
                        spotColor = Color.Black.copy(alpha = 0.04f),
                    )
                } else {
                    Modifier
                },
            )
            .clip(OtpCellShape)
            .background(AuthTokens.FieldFill, OtpCellShape)
            .border(1.dp, borderColor, OtpCellShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = digit,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun AuthOtpFormPreview() {
    VitranTheme {
        AuthOtpForm(
            destination = "09123456789",
            modifier = Modifier.padding(VitranSpacing.lg),
        )
    }
}
