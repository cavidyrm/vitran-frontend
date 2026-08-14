package com.vitran.shop.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_product_tag_add_a11y
import vitranshop.shared.generated.resources.admin_product_tag_remove_a11y
import vitranshop.shared.generated.resources.ic_close
import vitranshop.shared.generated.resources.ic_plus
import vitranshop.shared.generated.resources.ic_search

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminTagField(
    label: String,
    tags: List<String>,
    input: String,
    onInputChange: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    helper: String? = null,
) {
    AdminLabeledField(label = label, helper = helper, modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
            if (tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                ) {
                    tags.forEach { tag ->
                        AdminTagChip(label = tag, onRemove = { onRemove(tag) })
                    }
                }
            }
            AdminTagInputRow(
                value = input,
                onValueChange = onInputChange,
                onAdd = onAdd,
                placeholder = placeholder,
            )
        }
    }
}

@Composable
internal fun AdminTagChip(
    label: String,
    onRemove: () -> Unit,
) {
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = Modifier
            .height(32.dp)
            .clip(shape)
            .background(AdminTokens.DropdownHover)
            .border(1.dp, AdminTokens.CardBorder, shape)
            .padding(start = VitranSpacing.md, end = VitranSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
        )
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = stringResource(Res.string.admin_product_tag_remove_a11y, label),
                size = VitranSize.iconSmall,
                tint = AdminTokens.Helper,
            )
        }
    }
}

@Composable
private fun AdminTagInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
    placeholder: String?,
) {
    val shape = RoundedCornerShape(AdminTokens.FieldRadius)
    var focused by remember { mutableStateOf(false) }
    val border = if (focused) AdminTokens.FieldBorderFocused else AdminTokens.FieldBorder
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(AdminTokens.FieldHeight)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, border, shape)
            .padding(start = VitranSpacing.lg, end = VitranSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused },
                singleLine = true,
                textStyle = adminFieldTextStyle(),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAdd() }),
                decorationBox = { inner ->
                    AdminFieldDecoration(
                        value = value,
                        placeholder = placeholder,
                        innerTextField = inner,
                    )
                },
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(AdminTokens.DropdownHover)
                .clickable(enabled = value.isNotBlank(), role = Role.Button, onClick = onAdd),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_plus),
                contentDescription = stringResource(Res.string.admin_product_tag_add_a11y),
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun AdminSearchOrAddField(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    helper: String? = null,
    formatAddLabel: @Composable (String) -> String,
) {
    var query by remember(value) { mutableStateOf(value) }
    var expanded by remember { mutableStateOf(false) }
    val filtered = remember(query, options) {
        val needle = query.trim()
        if (needle.isEmpty()) options else options.filter { it.contains(needle, ignoreCase = true) }
    }
    val canAdd = query.trim().isNotEmpty() &&
        options.none { it.equals(query.trim(), ignoreCase = true) }

    AdminLabeledField(label = label, helper = helper, modifier = modifier) {
        val shape = RoundedCornerShape(AdminTokens.FieldRadius)
        val border = if (expanded) AdminTokens.FieldBorderFocused else AdminTokens.FieldBorder
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AdminTokens.FieldHeight)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface, shape)
                    .border(1.dp, border, shape)
                    .padding(horizontal = VitranSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                    tint = AdminTokens.Helper,
                )
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            expanded = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focus ->
                                expanded = focus.isFocused
                            },
                        singleLine = true,
                        textStyle = adminFieldTextStyle(),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                        decorationBox = { inner ->
                            AdminFieldDecoration(
                                value = query,
                                placeholder = placeholder,
                                innerTextField = inner,
                            )
                        },
                    )
                }
            }
            if (expanded && (filtered.isNotEmpty() || canAdd)) {
                val menuShape = RoundedCornerShape(AdminTokens.FieldRadius)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = VitranSpacing.xs)
                        .clip(menuShape)
                        .background(MaterialTheme.colorScheme.surface, menuShape)
                        .border(1.dp, AdminTokens.CardBorder, menuShape),
                ) {
                    filtered.take(8).forEach { option ->
                        Text(
                            text = option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(role = Role.Button) {
                                    query = option
                                    onSelect(option)
                                    expanded = false
                                }
                                .padding(
                                    horizontal = VitranSpacing.md,
                                    vertical = VitranSpacing.sm,
                                ),
                            style = adminFieldTextStyle(),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    if (canAdd) {
                        Text(
                            text = formatAddLabel(query.trim()),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(role = Role.Button) {
                                    val added = query.trim()
                                    onSelect(added)
                                    expanded = false
                                }
                                .padding(
                                    horizontal = VitranSpacing.md,
                                    vertical = VitranSpacing.sm,
                                ),
                            style = adminFieldTextStyle().copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}
