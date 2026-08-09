package com.vitran.shop.ui.sections.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.auth_change_email
import vitranshop.shared.generated.resources.auth_otp_a11y
import vitranshop.shared.generated.resources.auth_verify_illustration_a11y
import vitranshop.shared.generated.resources.auth_verify_subtitle
import vitranshop.shared.generated.resources.auth_verify_title

private val OtpCellShape = RoundedCornerShape(AuthTokens.OtpCellRadius)
private val OtpBadgeRed = Color(0xFFFF383C)
private val OtpPhoneDark = Color(0xFF1A1A1A)
private val OtpEnvelopeFill = Color(0xFFF7F5F8)
private val OtpPurpleDark = Color(0xFF301B92)

/**
 * shop.app email-verify OTP step: illustration, title, 6-cell code entry, change email.
 */
@Composable
fun AuthOtpForm(
    email: String,
    modifier: Modifier = Modifier,
    onChangeEmail: () -> Unit = {},
    onCodeComplete: (String) -> Unit = {},
) {
    var code by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val otpA11y = stringResource(Res.string.auth_otp_a11y)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isCompact = maxWidth < VitranSize.mdBreakpoint
        val titleSize = if (isCompact) 28.sp else 32.sp

        Column(
            modifier = Modifier
                .widthIn(max = AuthTokens.FormMaxWidth)
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AuthVerifyIllustration(
                contentDescription = stringResource(Res.string.auth_verify_illustration_a11y),
            )

            Text(
                text = stringResource(Res.string.auth_verify_title),
                modifier = Modifier.padding(top = VitranSpacing.xl),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = titleSize,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    lineHeight = titleSize * 1.1f,
                    textAlign = TextAlign.Center,
                ),
                textAlign = TextAlign.Center,
            )

            Text(
                text = stringResource(Res.string.auth_verify_subtitle),
                modifier = Modifier.padding(top = VitranSpacing.sm),
                color = AuthTokens.Muted,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                ),
                textAlign = TextAlign.Center,
            )

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = email,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }

            AuthOtpCodeField(
                code = code,
                onCodeChange = { next ->
                    val digits = next.filter { it.isDigit() }.take(6)
                    code = digits
                    if (digits.length == 6) onCodeComplete(digits)
                },
                focusRequester = focusRequester,
                a11y = otpA11y,
                modifier = Modifier.padding(top = VitranSpacing.xxl),
            )

            Text(
                text = stringResource(Res.string.auth_change_email),
                modifier = Modifier
                    .padding(top = VitranSpacing.lg)
                    .fillMaxWidth()
                    .height(42.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onChangeEmail,
                    )
                    .padding(vertical = VitranSpacing.md),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun AuthOtpCodeField(
    code: String,
    onCodeChange: (String) -> Unit,
    focusRequester: FocusRequester,
    a11y: String,
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
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(3) { index ->
                    OtpDigitCell(
                        digit = code.getOrNull(index)?.toString().orEmpty(),
                        focused = code.length == index || (index == 0 && code.isEmpty()),
                    )
                    if (index < 2) Spacer(modifier = Modifier.width(AuthTokens.OtpCellGap))
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = ((AuthTokens.OtpGroupGap - AuthTokens.OtpSeparatorSize) / 2))
                        .size(AuthTokens.OtpSeparatorSize)
                        .clip(CircleShape)
                        .background(AuthTokens.FieldFill),
                )
                repeat(3) { index ->
                    val cellIndex = index + 3
                    OtpDigitCell(
                        digit = code.getOrNull(cellIndex)?.toString().orEmpty(),
                        focused = code.length == cellIndex,
                    )
                    if (index < 2) Spacer(modifier = Modifier.width(AuthTokens.OtpCellGap))
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
) {
    val borderColor = if (focused) {
        MaterialTheme.colorScheme.onSurface
    } else {
        AuthTokens.OtpIdleBorder
    }
    Box(
        modifier = Modifier
            .width(AuthTokens.OtpCellWidth)
            .height(AuthTokens.OtpCellHeight)
            .then(
                if (focused) {
                    Modifier.shadow(
                        elevation = 6.dp,
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

/**
 * Approximates shop.app verify illustration: purple phone, open envelope, red “1” badge.
 */
@Composable
private fun AuthVerifyIllustration(
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .size(AuthTokens.VerifyIllustrationSize)
            .semantics {
                if (contentDescription != null) this.contentDescription = contentDescription
            },
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val phoneW = w * 0.39f
            val phoneH = h * 0.81f
            val phoneLeft = (w - phoneW) / 2f
            val phoneTop = h * 0.096f
            val phoneRadius = phoneW * 0.16f
            drawRoundRect(
                color = OtpPhoneDark,
                topLeft = Offset(phoneLeft, phoneTop),
                size = Size(phoneW, phoneH),
                cornerRadius = CornerRadius(phoneRadius, phoneRadius),
            )
            val inset = phoneW * 0.044f
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(primary, OtpPurpleDark)),
                topLeft = Offset(phoneLeft + inset, phoneTop + inset),
                size = Size(phoneW - inset * 2, phoneH - inset * 2),
                cornerRadius = CornerRadius(phoneRadius * 0.79f, phoneRadius * 0.79f),
            )
            val envW = w * 0.24f
            val envH = h * 0.15f
            val envLeft = (w - envW) / 2f
            val envTop = h * 0.405f
            drawRoundRect(
                color = OtpEnvelopeFill,
                topLeft = Offset(envLeft, envTop),
                size = Size(envW, envH),
                cornerRadius = CornerRadius(w * 0.017f, w * 0.017f),
            )
            val flap = Path().apply {
                moveTo(envLeft, envTop)
                lineTo(envLeft + envW / 2f, envTop + envH * 0.45f)
                lineTo(envLeft + envW, envTop)
                close()
            }
            drawPath(flap, color = Color.White.copy(alpha = 0.85f))
            drawPath(flap, color = Color(0xFFCDCBCC), style = Stroke(width = w * 0.008f))
            val badgeR = w * 0.065f
            val badgeCenter = Offset(w * 0.66f, h * 0.41f)
            drawCircle(color = OtpBadgeRed, radius = badgeR, center = badgeCenter)
        }
        Text(
            text = "1",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 28.dp, bottom = 10.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AuthOtpFormPreview() {
    VitranTheme {
        AuthOtpForm(
            email = "tohid.mahmoudvand@gmail.com",
            modifier = Modifier.padding(horizontal = VitranSpacing.xxl),
        )
    }
}
