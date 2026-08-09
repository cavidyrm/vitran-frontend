package com.vitran.shop.ui.sections.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.theme.VitranShapes
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.auth_continue
import vitranshop.shared.generated.resources.auth_email_placeholder
import vitranshop.shared.generated.resources.auth_legal_after
import vitranshop.shared.generated.resources.auth_legal_before
import vitranshop.shared.generated.resources.auth_legal_mid
import vitranshop.shared.generated.resources.auth_logo_a11y
import vitranshop.shared.generated.resources.auth_or_create_account
import vitranshop.shared.generated.resources.auth_sign_in_title
import vitranshop.shared.generated.resources.ic_shop_logo
import vitranshop.shared.generated.resources.site_footer_link_privacy
import vitranshop.shared.generated.resources.site_footer_link_terms

/**
 * shop.app `/accounts/login` form body: logo, title, email pill, Continue, legal.
 *
 * Column width: full (− shell pad) on compact; capped at [AuthTokens.FormMaxWidth] on md+.
 */
@Composable
fun AuthLoginForm(
    modifier: Modifier = Modifier,
    onContinue: (String) -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }

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
            Image(
                painter = painterResource(Res.drawable.ic_shop_logo),
                contentDescription = stringResource(Res.string.auth_logo_a11y),
                modifier = Modifier.size(AuthTokens.LogoSize),
            )

            Text(
                text = stringResource(Res.string.auth_sign_in_title),
                modifier = Modifier.padding(top = VitranSpacing.xxl),
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
                text = stringResource(Res.string.auth_or_create_account),
                modifier = Modifier.padding(top = VitranSpacing.sm),
                color = AuthTokens.Muted,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                ),
                textAlign = TextAlign.Center,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = VitranSpacing.xxl),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            ) {
                AuthEmailField(
                    value = email,
                    onValueChange = { email = it },
                    onSubmit = { onContinue(email) },
                )

                AuthContinueButton(
                    onClick = { onContinue(email) },
                )

                AuthLegalText(
                    onTermsClick = onTermsClick,
                    onPrivacyClick = onPrivacyClick,
                )
            }
        }
    }
}

@Composable
private fun AuthEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val placeholder = stringResource(Res.string.auth_email_placeholder)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(AuthTokens.ControlHeight)
                .clip(VitranShapes.pill)
                .background(AuthTokens.FieldFill, VitranShapes.pill)
                .border(1.dp, AuthTokens.FieldFill, VitranShapes.pill)
                .padding(horizontal = VitranSpacing.lg),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                letterSpacing = (-0.5).sp,
                lineHeight = 22.sp,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Go,
            ),
            keyboardActions = KeyboardActions(onGo = { onSubmit() }),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            modifier = Modifier.align(Alignment.CenterEnd),
                            color = AuthTokens.Muted,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                letterSpacing = (-0.5).sp,
                                lineHeight = 22.sp,
                            ),
                            textAlign = TextAlign.End,
                            maxLines = 1,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart),
                    ) {
                        innerTextField()
                    }
                }
            },
        )
    }
}

@Composable
private fun AuthContinueButton(
    onClick: () -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val label = stringResource(Res.string.auth_continue)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AuthTokens.ControlHeight)
            .shadow(
                elevation = AuthTokens.ContinueShadowElevation,
                shape = VitranShapes.pill,
                ambientColor = primary.copy(alpha = 0.12f),
                spotColor = primary.copy(alpha = 0.24f),
            )
            .clip(VitranShapes.pill)
            .background(primary, VitranShapes.pill)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.5).sp,
                lineHeight = 22.sp,
            ),
        )
    }
}

@Composable
private fun AuthLegalText(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
) {
    val before = stringResource(Res.string.auth_legal_before)
    val mid = stringResource(Res.string.auth_legal_mid)
    val after = stringResource(Res.string.auth_legal_after)
    val terms = stringResource(Res.string.site_footer_link_terms)
    val privacy = stringResource(Res.string.site_footer_link_privacy)
    val linkStyle = TextLinkStyles(
        style = SpanStyle(
            color = AuthTokens.Muted,
            textDecoration = TextDecoration.Underline,
        ),
    )
    val annotated = buildAnnotatedString {
        append(before)
        withLink(
            LinkAnnotation.Clickable(
                tag = "terms",
                styles = linkStyle,
                linkInteractionListener = { onTermsClick() },
            ),
        ) {
            append(terms)
        }
        append(mid)
        withLink(
            LinkAnnotation.Clickable(
                tag = "privacy",
                styles = linkStyle,
                linkInteractionListener = { onPrivacyClick() },
            ),
        ) {
            append(privacy)
        }
        append(after)
    }
    Text(
        text = annotated,
        modifier = Modifier.fillMaxWidth(),
        color = AuthTokens.Muted,
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
        ),
        textAlign = TextAlign.Center,
    )
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AuthLoginFormPreview() {
    VitranTheme {
        AuthLoginForm(modifier = Modifier.padding(horizontal = VitranSpacing.xxl))
    }
}
