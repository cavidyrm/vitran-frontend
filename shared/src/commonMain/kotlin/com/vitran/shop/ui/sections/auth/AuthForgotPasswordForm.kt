package com.vitran.shop.ui.sections.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.SurfaceWhite
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.auth_back_to_login
import vitranshop.shared.generated.resources.auth_forgot_password
import vitranshop.shared.generated.resources.auth_forgot_send_code
import vitranshop.shared.generated.resources.auth_forgot_subtitle
import vitranshop.shared.generated.resources.auth_logo_a11y
import vitranshop.shared.generated.resources.auth_mobile_invalid
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ic_shop_logo

/**
 * Forgot-password card: +98 mobile, send-code CTA, return to login.
 * Hosted inside [AuthSplitShell] by [com.vitran.shop.ui.screens.ForgotPasswordScreen].
 */
@Composable
fun AuthForgotPasswordForm(
    modifier: Modifier = Modifier,
    onSendCode: (String) -> Unit = {},
    onBackToLogin: () -> Unit = {},
    isSubmitting: Boolean = false,
    submitError: String? = null,
    phoneError: String? = null,
    onClearFieldError: (String) -> Unit = {},
) {
    var nationalMobile by remember { mutableStateOf("") }
    val fullMobile = normalizeIranMobile(nationalMobile)
    val canSubmit = isValidIranMobile(fullMobile)
    val showInvalid = nationalMobile.isNotEmpty() && !canSubmit

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
            text = stringResource(Res.string.auth_forgot_password),
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
            text = stringResource(Res.string.auth_forgot_subtitle),
            modifier = Modifier.padding(top = VitranSpacing.sm),
            color = AuthTokens.Muted,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 22.sp,
            ),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = VitranSpacing.md),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            AuthMobileField(
                value = nationalMobile,
                onValueChange = {
                    nationalMobile = it
                    onClearFieldError("phone")
                },
                imeAction = ImeAction.Go,
                onSubmit = { if (canSubmit) onSendCode(fullMobile) },
                errorMessage = when {
                    showInvalid -> stringResource(Res.string.auth_mobile_invalid)
                    else -> phoneError
                },
            )

            if (submitError != null) {
                Text(
                    text = submitError,
                    color = AuthTokens.OtpError,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = VitranSpacing.xs),
                )
            }

            AuthPrimaryButton(
                label = stringResource(Res.string.auth_forgot_send_code),
                enabled = canSubmit && !isSubmitting,
                loading = isSubmitting,
                onClick = { onSendCode(fullMobile) },
            )

            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBackToLogin,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_chevron_right),
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(Res.string.auth_back_to_login),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 18.sp,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun AuthForgotPasswordFormPreview() {
    VitranTheme {
        AuthForgotPasswordForm(
            modifier = Modifier.padding(VitranSpacing.lg),
        )
    }
}
