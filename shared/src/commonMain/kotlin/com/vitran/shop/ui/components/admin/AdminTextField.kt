package com.vitran.shop.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_toolbar_bold
import vitranshop.shared.generated.resources.admin_toolbar_heading
import vitranshop.shared.generated.resources.admin_toolbar_italic
import vitranshop.shared.generated.resources.admin_toolbar_link
import vitranshop.shared.generated.resources.admin_toolbar_list
import vitranshop.shared.generated.resources.admin_toolbar_more
import vitranshop.shared.generated.resources.admin_toolbar_numbered
import vitranshop.shared.generated.resources.admin_toolbar_redo
import vitranshop.shared.generated.resources.admin_toolbar_undo

@Composable
fun AdminTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    helper: String? = null,
    prefix: String? = null,
    required: Boolean = false,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null,
    error: String? = null,
    ltr: Boolean = false,
) {
    AdminLabeledField(
        label = label,
        helper = helper,
        error = error,
        required = required,
        modifier = modifier,
    ) {
        AdminFieldBox(
            readOnly = readOnly,
            prefix = prefix,
            trailing = trailing,
            isError = error != null,
            ltr = ltr,
        ) { innerModifier ->
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = innerModifier,
                enabled = !readOnly,
                readOnly = readOnly,
                singleLine = singleLine,
                textStyle = adminFieldTextStyle(),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation,
                decorationBox = { inner ->
                    AdminFieldDecoration(
                        value = value,
                        placeholder = placeholder,
                        innerTextField = inner,
                    )
                },
            )
        }
    }
}

@Composable
fun AdminMultilineField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    helper: String? = null,
    error: String? = null,
    showToolbar: Boolean = false,
) {
    AdminLabeledField(label = label, helper = helper, error = error, modifier = modifier) {
        val shape = RoundedCornerShape(AdminTokens.FieldRadius)
        var focused by remember { mutableStateOf(false) }
        val border = when {
            error != null -> AdminTokens.Destructive
            focused -> AdminTokens.FieldBorderFocused
            else -> AdminTokens.FieldBorder
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, border, shape),
        ) {
            if (showToolbar) {
                AdminRichTextToolbar()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = AdminTokens.MultilineMinHeight)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focused = it.isFocused },
                    textStyle = adminFieldTextStyle(),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    decorationBox = { inner ->
                        AdminFieldDecoration(
                            value = value,
                            placeholder = placeholder,
                            innerTextField = inner,
                            centerVertically = false,
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun AdminLabeledField(
    label: String,
    modifier: Modifier = Modifier,
    helper: String? = null,
    error: String? = null,
    required: Boolean = false,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        if (label.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs)) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    ),
                )
                if (required) {
                    Text(
                        text = "*",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                    )
                }
            }
        }
        content()
        if (error != null) {
            Text(
                text = error,
                color = AdminTokens.Destructive,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
            )
        } else if (helper != null) {
            Text(
                text = helper,
                color = AdminTokens.Helper,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
            )
        }
    }
}

@Composable
internal fun AdminFieldBox(
    readOnly: Boolean,
    prefix: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    ltr: Boolean = false,
    content: @Composable (modifier: Modifier) -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(AdminTokens.FieldRadius)
    val border = when {
        isError -> AdminTokens.Destructive
        focused && !readOnly -> AdminTokens.FieldBorderFocused
        else -> AdminTokens.FieldBorder
    }
    val fieldModifier = Modifier
        .fillMaxWidth()
        .height(AdminTokens.FieldHeight)
        .clip(shape)
        .background(
            if (readOnly) AdminTokens.DropdownHover else MaterialTheme.colorScheme.surface,
            shape,
        )
        .border(1.dp, border, shape)
        .padding(horizontal = VitranSpacing.lg)
    val row = @Composable {
        Row(
            modifier = fieldModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            if (prefix != null) {
                Text(
                    text = prefix,
                    color = AdminTokens.Helper,
                    style = adminFieldTextStyle().copy(color = AdminTokens.Helper),
                )
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                content(
                    Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focused = it.isFocused },
                )
            }
            if (trailing != null) {
                trailing()
            }
        }
    }
    if (ltr) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            row()
        }
    } else {
        row()
    }
}

@Composable
internal fun AdminFieldDecoration(
    value: String,
    placeholder: String?,
    innerTextField: @Composable () -> Unit,
    centerVertically: Boolean = true,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        if (value.isEmpty() && placeholder != null) {
            Text(
                text = placeholder,
                color = AdminTokens.Placeholder,
                style = adminFieldTextStyle().copy(color = AdminTokens.Placeholder),
                modifier = if (centerVertically) {
                    Modifier.align(Alignment.CenterStart)
                } else {
                    Modifier
                },
            )
        }
        innerTextField()
    }
}

@Composable
internal fun adminFieldTextStyle() = MaterialTheme.typography.bodyMedium.copy(
    color = MaterialTheme.colorScheme.onSurface,
    fontSize = 14.sp,
    lineHeight = 20.sp,
)

@Composable
private fun AdminRichTextToolbar() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AdminTokens.ToolbarFill)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ToolbarMark(stringResource(Res.string.admin_toolbar_heading), FontWeight.SemiBold)
            ToolbarMark(stringResource(Res.string.admin_toolbar_bold), FontWeight.Bold)
            Text(
                text = stringResource(Res.string.admin_toolbar_italic),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            ToolbarMark(stringResource(Res.string.admin_toolbar_list))
            ToolbarMark(stringResource(Res.string.admin_toolbar_numbered))
            ToolbarMark(stringResource(Res.string.admin_toolbar_link), color = AdminTokens.Brand)
            ToolbarMark(stringResource(Res.string.admin_toolbar_undo), color = AdminTokens.Helper)
            ToolbarMark(stringResource(Res.string.admin_toolbar_redo), color = AdminTokens.Helper)
            ToolbarMark(stringResource(Res.string.admin_toolbar_more), color = AdminTokens.Helper)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AdminTokens.CardBorder),
        )
    }
}

@Composable
private fun ToolbarMark(
    label: String,
    weight: FontWeight = FontWeight.Medium,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Text(
        text = label,
        fontWeight = weight,
        fontSize = 13.sp,
        color = color,
    )
}
