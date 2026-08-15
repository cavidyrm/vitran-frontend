package com.vitran.shop.ui.sections.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.SurfaceWhite
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.auth_mobile_country_code
import vitranshop.shared.generated.resources.auth_mobile_label
import vitranshop.shared.generated.resources.auth_mobile_placeholder
import vitranshop.shared.generated.resources.auth_password_hide_a11y
import vitranshop.shared.generated.resources.auth_password_label
import vitranshop.shared.generated.resources.auth_password_placeholder
import vitranshop.shared.generated.resources.auth_password_rule_digit
import vitranshop.shared.generated.resources.auth_password_rule_length
import vitranshop.shared.generated.resources.auth_password_rule_letter
import vitranshop.shared.generated.resources.auth_password_show_a11y
import vitranshop.shared.generated.resources.ic_check
import vitranshop.shared.generated.resources.ic_visibility
import vitranshop.shared.generated.resources.ic_visibility_off

internal val AuthCardShape = RoundedCornerShape(VitranRadius.large)
internal val AuthFieldShape = RoundedCornerShape(VitranRadius.small)

private val OtpCellShape = RoundedCornerShape(AuthTokens.OtpCellRadius)

@Composable
internal fun AuthMobileField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    onSubmit: () -> Unit = {},
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
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        Text(
            text = stringResource(Res.string.auth_mobile_label),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp,
            ),
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AuthTokens.LabeledFieldHeight)
                    .clip(AuthFieldShape)
                    .background(SurfaceWhite, AuthFieldShape)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = AuthFieldShape,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(AuthTokens.CountryCodeWidth)
                        .fillMaxHeight()
                        .background(AuthTokens.FieldFill),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.auth_mobile_country_code),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 18.sp,
                        ),
                        maxLines = 1,
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(AuthTokens.FieldBorder),
                )
                BasicTextField(
                    value = value,
                    onValueChange = { onValueChange(filterToAsciiDigits(it, 11)) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = VitranSpacing.md)
                        .onFocusChanged { focused = it.isFocused },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = imeAction,
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = { onSubmit() },
                        onNext = { },
                    ),
                    decorationBox = { inner ->
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            AuthFieldPlaceholder(
                                empty = value.isEmpty(),
                                placeholder = stringResource(Res.string.auth_mobile_placeholder),
                                inner = inner,
                            )
                        }
                    },
                )
            }
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
internal fun AuthPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(Res.string.auth_password_label),
    placeholder: String = stringResource(Res.string.auth_password_placeholder),
    imeAction: ImeAction = ImeAction.Go,
) {
    var visible by remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }
    val strength = passwordStrengthOf(value)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp,
            ),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AuthTokens.LabeledFieldHeight)
                .clip(AuthFieldShape)
                .background(SurfaceWhite, AuthFieldShape)
                .border(
                    width = 1.dp,
                    color = if (focused) AuthTokens.FieldBorderFocus else AuthTokens.FieldBorder,
                    shape = AuthFieldShape,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = VitranSpacing.md)
                    .onFocusChanged { focused = it.isFocused },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                visualTransformation = if (visible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = imeAction,
                ),
                keyboardActions = KeyboardActions(
                    onGo = { onSubmit() },
                    onNext = { },
                ),
                decorationBox = { inner ->
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        AuthFieldPlaceholder(
                            empty = value.isEmpty(),
                            placeholder = placeholder,
                            inner = inner,
                        )
                    }
                },
            )
            Box(
                modifier = Modifier
                    .size(AuthTokens.LabeledFieldHeight)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { visible = !visible },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(
                        if (visible) Res.drawable.ic_visibility_off else Res.drawable.ic_visibility,
                    ),
                    contentDescription = stringResource(
                        if (visible) {
                            Res.string.auth_password_hide_a11y
                        } else {
                            Res.string.auth_password_show_a11y
                        },
                    ),
                    size = VitranSize.iconMedium,
                    tint = AuthTokens.Muted,
                )
            }
        }
        AuthPasswordStrengthBar(strength = strength)
    }
}

@Composable
internal fun AuthPasswordStrengthBar(
    strength: PasswordStrength,
    modifier: Modifier = Modifier,
) {
    val active = when (strength) {
        PasswordStrength.Empty -> 0
        PasswordStrength.Weak -> 1
        PasswordStrength.Fair -> 2
        PasswordStrength.Good -> 3
        PasswordStrength.Strong -> 4
    }
    val color = when (strength) {
        PasswordStrength.Empty -> AuthTokens.FieldBorder
        PasswordStrength.Weak -> AuthTokens.StrengthWeak
        PasswordStrength.Fair -> AuthTokens.StrengthFair
        PasswordStrength.Good -> AuthTokens.StrengthGood
        PasswordStrength.Strong -> AuthTokens.StrengthStrong
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (index < active) color else AuthTokens.FieldFill,
                    ),
            )
        }
    }
}

@Composable
internal fun AuthFieldPlaceholder(
    empty: Boolean,
    placeholder: String,
    inner: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (empty) {
            Text(
                text = placeholder,
                color = AuthTokens.Muted,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                ),
                maxLines = 1,
            )
        }
        inner()
    }
}

@Composable
internal fun AuthOtpCodeField(
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
                onValueChange = { onCodeChange(filterToAsciiDigits(it, 6)) },
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.01f)
                    .focusRequester(focusRequester),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Transparent),
                cursorBrush = SolidColor(Color.Transparent),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

@Composable
internal fun AuthPasswordRulesChecklist(
    rules: AuthPasswordRules,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        AuthPasswordRuleRow(
            label = stringResource(Res.string.auth_password_rule_length),
            met = rules.minLength,
        )
        AuthPasswordRuleRow(
            label = stringResource(Res.string.auth_password_rule_letter),
            met = rules.hasLetter,
        )
        AuthPasswordRuleRow(
            label = stringResource(Res.string.auth_password_rule_digit),
            met = rules.hasDigit,
        )
    }
}

@Composable
private fun AuthPasswordRuleRow(
    label: String,
    met: Boolean,
) {
    val color = if (met) AuthTokens.OtpSuccess else AuthTokens.Muted
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_check),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = color,
        )
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                fontWeight = if (met) FontWeight.SemiBold else FontWeight.Medium,
                lineHeight = 16.sp,
            ),
        )
    }
}

@Composable
fun AuthInlineNotice(
    message: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(AuthFieldShape)
            .background(AuthTokens.OtpSuccess.copy(alpha = 0.12f), AuthFieldShape)
            .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_check),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = AuthTokens.OtpSuccess,
        )
        Text(
            text = message,
            color = AuthTokens.OtpSuccess,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp,
            ),
        )
    }
}
