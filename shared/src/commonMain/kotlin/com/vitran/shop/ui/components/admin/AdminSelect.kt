package com.vitran.shop.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_search_placeholder
import vitranshop.shared.generated.resources.ic_check
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ic_search
import vitranshop.shared.generated.resources.ic_unfold

@Composable
fun AdminSelectField(
    label: String,
    valueId: String?,
    options: List<AdminSelectOption>,
    onSelect: (AdminSelectOption) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    helper: String? = null,
    enabled: Boolean = true,
) {
    val selected = options.firstOrNull { it.id == valueId }
    AdminDropdownAnchor(
        label = label,
        helper = helper,
        displayText = selected?.label,
        placeholder = placeholder,
        enabled = enabled,
        modifier = modifier,
    ) { dismiss ->
        AdminOptionList(
            options = options,
            selectedId = valueId,
            showChevron = false,
            onSelect = {
                onSelect(it)
                dismiss()
            },
        )
    }
}

@Composable
fun AdminSearchableSelect(
    label: String,
    valueId: String?,
    options: List<AdminSelectOption>,
    onSelect: (AdminSelectOption) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    helper: String? = null,
    searchPlaceholder: String? = null,
    showItemChevron: Boolean = true,
) {
    val selected = options.firstOrNull { it.id == valueId }
    val queryPlaceholder = searchPlaceholder ?: stringResource(Res.string.admin_search_placeholder)
    AdminDropdownAnchor(
        label = label,
        helper = helper,
        displayText = selected?.label,
        placeholder = placeholder,
        enabled = true,
        modifier = modifier,
    ) { dismiss ->
        var query by remember { mutableStateOf("") }
        val filtered = remember(query, options) {
            val needle = query.trim()
            if (needle.isEmpty()) {
                options
            } else {
                options.filter { it.label.contains(needle, ignoreCase = true) }
            }
        }
        Column {
            AdminDropdownSearch(
                query = query,
                onQueryChange = { query = it },
                placeholder = queryPlaceholder,
            )
            AdminOptionList(
                options = filtered,
                selectedId = valueId,
                showChevron = showItemChevron,
                onSelect = {
                    onSelect(it)
                    dismiss()
                },
            )
        }
    }
}

@Composable
internal fun AdminDropdownAnchor(
    label: String,
    helper: String?,
    displayText: String?,
    placeholder: String?,
    enabled: Boolean,
    modifier: Modifier,
    menu: @Composable (dismiss: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var anchorWidthPx by remember { mutableIntStateOf(0) }
    var anchorHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    AdminLabeledField(label = label, helper = helper, modifier = modifier) {
        val shape = RoundedCornerShape(AdminTokens.FieldRadius)
        val border = if (expanded) AdminTokens.FieldBorderFocused else AdminTokens.FieldBorder
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AdminTokens.FieldHeight)
                    .onSizeChanged {
                        anchorWidthPx = it.width
                        anchorHeightPx = it.height
                    }
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface, shape)
                    .border(1.dp, border, shape)
                    .clickable(enabled = enabled, role = Role.Button) {
                        if (enabled) expanded = !expanded
                    }
                    .padding(horizontal = VitranSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = displayText ?: placeholder.orEmpty(),
                    color = if (displayText == null) {
                        AdminTokens.Placeholder
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    style = adminFieldTextStyle(),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_unfold),
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                    tint = AdminTokens.Helper,
                )
            }
            if (expanded) {
                val menuWidth = with(density) { anchorWidthPx.toDp() }.let { measured ->
                    if (measured > 0.dp) measured else 280.dp
                }
                Popup(
                    alignment = Alignment.TopStart,
                    offset = IntOffset(0, anchorHeightPx + with(density) { 4.dp.roundToPx() }),
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(
                        focusable = true,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    ),
                ) {
                    val menuShape = RoundedCornerShape(AdminTokens.FieldRadius)
                    Box(
                        modifier = Modifier
                            .width(menuWidth)
                            .shadow(
                                elevation = VitranElevation.medium,
                                shape = menuShape,
                                ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            )
                            .clip(menuShape)
                            .background(MaterialTheme.colorScheme.surface, menuShape)
                            .border(1.dp, AdminTokens.CardBorder, menuShape),
                    ) {
                        menu { expanded = false }
                    }
                }
            }
        }
    }
}

@Composable
internal fun AdminDropdownSearch(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
) {
    val shape = RoundedCornerShape(AdminTokens.FieldRadius)
    var focused by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(VitranSpacing.sm)
            .height(AdminTokens.FieldHeight)
            .clip(shape)
            .border(
                1.dp,
                if (focused) AdminTokens.FieldBorderFocused else AdminTokens.FieldBorder,
                shape,
            )
            .padding(horizontal = VitranSpacing.sm),
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
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    color = AdminTokens.Placeholder,
                    style = adminFieldTextStyle(),
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused },
                singleLine = true,
                textStyle = adminFieldTextStyle(),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            )
        }
    }
}

@Composable
private fun AdminOptionList(
    options: List<AdminSelectOption>,
    selectedId: String?,
    showChevron: Boolean,
    onSelect: (AdminSelectOption) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = AdminTokens.DropdownMaxHeight),
    ) {
        items(options, key = { it.id }) { option ->
            val selected = option.id == selectedId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (selected) AdminTokens.DropdownHover else MaterialTheme.colorScheme.surface)
                    .clickable(role = Role.Button) { onSelect(option) }
                    .padding(
                        horizontal = VitranSpacing.md,
                        vertical = VitranSpacing.sm,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                if (selected) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_check),
                        contentDescription = null,
                        size = VitranSize.iconSmall,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.label,
                        style = adminFieldTextStyle(),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    val description = option.description
                    if (description != null) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = AdminTokens.Helper,
                        )
                    }
                }
                if (showChevron && !selected) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_chevron_right),
                        contentDescription = null,
                        size = VitranSize.iconSmall,
                        tint = AdminTokens.Helper,
                    )
                }
            }
        }
    }
}
