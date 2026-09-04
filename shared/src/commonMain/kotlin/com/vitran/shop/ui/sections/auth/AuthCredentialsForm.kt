package com.vitran.shop.ui.sections.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.theme.ShopPurpleDark
import com.vitran.shop.ui.theme.SurfaceWhite
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranShapes
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.auth_benefit
import vitranshop.shared.generated.resources.auth_create_account_cta
import vitranshop.shared.generated.resources.auth_forgot_password
import vitranshop.shared.generated.resources.auth_invite_code_label
import vitranshop.shared.generated.resources.auth_invite_code_placeholder
import vitranshop.shared.generated.resources.auth_invite_code_toggle
import vitranshop.shared.generated.resources.auth_legal_after
import vitranshop.shared.generated.resources.auth_legal_before
import vitranshop.shared.generated.resources.auth_legal_mid
import vitranshop.shared.generated.resources.auth_logo_a11y
import vitranshop.shared.generated.resources.auth_register_subtitle
import vitranshop.shared.generated.resources.auth_register_title
import vitranshop.shared.generated.resources.auth_sign_in_cta
import vitranshop.shared.generated.resources.auth_sign_in_title
import vitranshop.shared.generated.resources.auth_tab_login
import vitranshop.shared.generated.resources.auth_tab_register
import vitranshop.shared.generated.resources.ic_shop_logo
import vitranshop.shared.generated.resources.site_footer_link_privacy
import vitranshop.shared.generated.resources.site_footer_link_terms

private const val AuthTabAnimMs = 180

data class AuthCredentials(
    val mobile: String,
    val password: String,
    /** Optional invite / referral code — register only; ignored on login. */
    val inviteCode: String = "",
)

fun AuthCredentials.isValidForSubmit(): Boolean =
    isValidIranMobile(mobile) && isValidAuthPassword(password)

/**
 * Dense credentials card: mode toggle, brand header, +98 mobile, password strength, CTA.
 */
@Composable
fun AuthCredentialsForm(
    mode: AuthMode,
    modifier: Modifier = Modifier,
    onModeChange: (AuthMode) -> Unit = {},
    onSubmit: (AuthCredentials) -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    isSubmitting: Boolean = false,
    submitError: String? = null,
    phoneError: String? = null,
    passwordError: String? = null,
    referralError: String? = null,
    onClearFieldError: (String) -> Unit = {},
) {
    var nationalMobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    var inviteExpanded by remember { mutableStateOf(false) }
    val fullMobile = normalizeIranMobile(nationalMobile)
    val credentials = AuthCredentials(
        mobile = fullMobile,
        password = password,
        inviteCode = if (mode == AuthMode.Register && inviteExpanded) {
            inviteCode.trim()
        } else {
            ""
        },
    )
    val canSubmit = credentials.isValidForSubmit()

    LaunchedEffect(referralError) {
        if (!referralError.isNullOrBlank()) inviteExpanded = true
    }

    val title = when (mode) {
        AuthMode.Login -> stringResource(Res.string.auth_sign_in_title)
        AuthMode.Register -> stringResource(Res.string.auth_register_title)
    }
    val subtitle = when (mode) {
        AuthMode.Login -> stringResource(Res.string.auth_benefit)
        AuthMode.Register -> stringResource(Res.string.auth_register_subtitle)
    }
    val primaryLabel = when (mode) {
        AuthMode.Login -> stringResource(Res.string.auth_sign_in_cta)
        AuthMode.Register -> stringResource(Res.string.auth_create_account_cta)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = AuthTokens.CardShadowElevation,
                shape = AuthCardShape,
                ambientColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.06f),
                spotColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.1f),
            )
            .clip(AuthCardShape)
            .background(SurfaceWhite, AuthCardShape)
            .padding(
                horizontal = AuthTokens.CardPaddingHorizontal,
                vertical = AuthTokens.CardPaddingVertical,
            ),
        horizontalAlignment = Alignment.Start,
    ) {
        AuthModeToggle(
            mode = mode,
            onModeChange = onModeChange,
        )

        Image(
            painter = painterResource(Res.drawable.ic_shop_logo),
            contentDescription = stringResource(Res.string.auth_logo_a11y),
            modifier = Modifier
                .padding(top = VitranSpacing.xl)
                .size(AuthTokens.LogoSize),
        )

        Text(
            text = title,
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
            text = subtitle,
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
                .padding(top = VitranSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            AuthMobileField(
                value = nationalMobile,
                onValueChange = {
                    nationalMobile = it
                    onClearFieldError("phone")
                },
                errorMessage = phoneError,
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                AuthPasswordField(
                    value = password,
                    onValueChange = {
                        password = it
                        onClearFieldError("password")
                    },
                    imeAction = if (mode == AuthMode.Register && inviteExpanded) {
                        ImeAction.Next
                    } else {
                        ImeAction.Go
                    },
                    onSubmit = { if (canSubmit && !isSubmitting) onSubmit(credentials) },
                    errorMessage = passwordError,
                )
                if (mode == AuthMode.Login) {
                    Text(
                        text = stringResource(Res.string.auth_forgot_password),
                        modifier = Modifier
                            .padding(top = VitranSpacing.xs)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onForgotPassword,
                            ),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 18.sp,
                        ),
                    )
                }
            }

            if (mode == AuthMode.Register) {
                AuthInviteCodeSection(
                    expanded = inviteExpanded,
                    onExpandedChange = { inviteExpanded = it },
                    inviteCode = inviteCode,
                    onInviteCodeChange = {
                        inviteCode = it.take(32)
                        onClearFieldError("referral_code")
                    },
                    onSubmit = { if (canSubmit && !isSubmitting) onSubmit(credentials) },
                    errorMessage = referralError,
                )
            }

            if (submitError != null) {
                Text(
                    text = submitError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = VitranSpacing.sm),
                    color = AuthTokens.OtpError,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            AuthPrimaryButton(
                label = primaryLabel,
                enabled = canSubmit,
                loading = isSubmitting,
                onClick = { onSubmit(credentials) },
                modifier = Modifier.padding(top = VitranSpacing.sm),
            )

            Spacer(modifier = Modifier.height(VitranSpacing.sm))

            AuthLegalText(
                onTermsClick = onTermsClick,
                onPrivacyClick = onPrivacyClick,
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
private fun AuthModeToggle(
    mode: AuthMode,
    onModeChange: (AuthMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackShape = VitranShapes.pill
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(trackShape)
            .background(AuthTokens.FieldFill, trackShape)
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AuthModeTab(
            label = stringResource(Res.string.auth_tab_login),
            selected = mode == AuthMode.Login,
            onClick = { onModeChange(AuthMode.Login) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
        AuthModeTab(
            label = stringResource(Res.string.auth_tab_register),
            selected = mode == AuthMode.Register,
            onClick = { onModeChange(AuthMode.Register) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
    }
}

@Composable
private fun AuthModeTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = VitranShapes.pill
    val elevation by animateDpAsState(
        targetValue = if (selected) 3.dp else 0.dp,
        animationSpec = tween(AuthTabAnimMs),
        label = "authTabElevation",
    )
    val borderAlpha by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
        } else {
            androidx.compose.ui.graphics.Color.Transparent
        },
        animationSpec = tween(AuthTabAnimMs),
        label = "authTabBorder",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onSurface
        } else {
            AuthTokens.Muted
        },
        animationSpec = tween(AuthTabAnimMs),
        label = "authTabLabel",
    )
    Box(
        modifier = modifier
            .then(
                if (selected) {
                    Modifier
                        .shadow(
                            elevation = elevation,
                            shape = shape,
                            clip = false,
                            ambientColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.06f),
                            spotColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.12f),
                        )
                        .background(SurfaceWhite, shape)
                        .border(1.dp, borderAlpha, shape)
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = labelColor,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    lineHeight = 16.sp,
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Box(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .width(18.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(
                        if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            androidx.compose.ui.graphics.Color.Transparent
                        },
                    ),
            )
        }
    }
}

@Composable
private fun AuthInviteCodeSection(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    inviteCode: String,
    onInviteCodeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    errorMessage: String? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (!expanded) {
            Text(
                text = stringResource(Res.string.auth_invite_code_toggle),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onExpandedChange(true) },
                ),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp,
                ),
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(AuthTabAnimMs)) + expandVertically(tween(AuthTabAnimMs)),
            exit = fadeOut(tween(AuthTabAnimMs)) + shrinkVertically(tween(AuthTabAnimMs)),
        ) {
            AuthInviteCodeField(
                value = inviteCode,
                onValueChange = onInviteCodeChange,
                onSubmit = onSubmit,
                errorMessage = errorMessage,
            )
        }
    }
}

@Composable
private fun AuthInviteCodeField(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    errorMessage: String? = null,
) {
    var focused by remember { mutableStateOf(false) }
    val isError = !errorMessage.isNullOrBlank()
    val borderColor = when {
        isError -> AuthTokens.OtpError
        focused -> AuthTokens.FieldBorderFocus
        else -> AuthTokens.FieldBorder
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        Text(
            text = stringResource(Res.string.auth_invite_code_label),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp,
            ),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AuthTokens.LabeledFieldHeight)
                .clip(AuthFieldShape)
                .background(SurfaceWhite, AuthFieldShape)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = AuthFieldShape,
                )
                .padding(horizontal = VitranSpacing.md),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Go,
                ),
                keyboardActions = KeyboardActions(onGo = { onSubmit() }),
                decorationBox = { inner ->
                    AuthFieldPlaceholder(
                        empty = value.isEmpty(),
                        placeholder = stringResource(Res.string.auth_invite_code_placeholder),
                        inner = inner,
                    )
                },
            )
        }
        if (isError) {
            Text(
                text = errorMessage.orEmpty(),
                color = AuthTokens.OtpError,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp,
                ),
            )
        }
    }
}

@Composable
internal fun AuthPrimaryButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val hovered by interaction.collectIsHoveredAsState()
    val shape = RoundedCornerShape(VitranRadius.small)
    val interactive = enabled && !loading
    val targetBg = when {
        loading -> MaterialTheme.colorScheme.primary
        !enabled -> AuthTokens.DisabledCtaFill
        pressed -> ShopPurpleDark
        hovered -> ShopPurpleDark.copy(alpha = 0.92f)
        else -> MaterialTheme.colorScheme.primary
    }
    val background by animateColorAsState(
        targetValue = targetBg,
        animationSpec = tween(140),
        label = "authCtaBg",
    )
    val elevation by animateDpAsState(
        targetValue = when {
            !interactive -> 0.dp
            pressed -> 2.dp
            hovered -> 10.dp
            else -> 8.dp
        },
        animationSpec = tween(140),
        label = "authCtaElevation",
    )
    val labelColor = if (enabled) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        AuthTokens.DisabledCtaLabel
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AuthTokens.ControlHeight)
            .then(
                if (interactive) {
                    Modifier.shadow(
                        elevation = elevation,
                        shape = shape,
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
                    )
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .background(background, shape)
            .clickable(
                enabled = interactive,
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(VitranSize.iconSmall),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            Text(
                text = label,
                color = labelColor,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp,
                    lineHeight = 20.sp,
                ),
            )
        }
    }
}

@Composable
internal fun AuthLegalText(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    val before = stringResource(Res.string.auth_legal_before)
    val mid = stringResource(Res.string.auth_legal_mid)
    val after = stringResource(Res.string.auth_legal_after)
    val terms = stringResource(Res.string.site_footer_link_terms)
    val privacy = stringResource(Res.string.site_footer_link_privacy)
    val linkStyle = TextLinkStyles(
        style = SpanStyle(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.SemiBold,
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
        modifier = modifier.fillMaxWidth(),
        color = AuthTokens.LegalMuted,
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = textAlign,
        ),
        textAlign = textAlign,
    )
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun AuthCredentialsFormLoginPreview() {
    VitranTheme {
        AuthCredentialsForm(
            mode = AuthMode.Login,
            modifier = Modifier.padding(VitranSpacing.lg),
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun AuthCredentialsFormRegisterPreview() {
    VitranTheme {
        AuthCredentialsForm(
            mode = AuthMode.Register,
            modifier = Modifier.padding(VitranSpacing.lg),
        )
    }
}
