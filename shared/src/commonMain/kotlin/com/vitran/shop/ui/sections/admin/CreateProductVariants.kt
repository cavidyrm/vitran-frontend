package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.components.admin.AdminTagChip
import com.vitran.shop.ui.components.admin.AdminTextButton
import com.vitran.shop.ui.components.admin.AdminTextField
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.components.admin.adminFieldTextStyle
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_product_card_variants
import vitranshop.shared.generated.resources.admin_product_variant_add_another
import vitranshop.shared.generated.resources.admin_product_variant_add_value
import vitranshop.shared.generated.resources.admin_product_variant_col_price
import vitranshop.shared.generated.resources.admin_product_variant_col_qty
import vitranshop.shared.generated.resources.admin_product_variant_col_sku
import vitranshop.shared.generated.resources.admin_product_variant_matrix_empty
import vitranshop.shared.generated.resources.admin_product_variant_option_n
import vitranshop.shared.generated.resources.admin_product_variant_option_name
import vitranshop.shared.generated.resources.admin_product_variant_option_placeholder
import vitranshop.shared.generated.resources.admin_product_variant_remove
import vitranshop.shared.generated.resources.admin_product_variant_remove_confirm
import vitranshop.shared.generated.resources.admin_product_variant_remove_no
import vitranshop.shared.generated.resources.admin_product_variant_remove_yes
import vitranshop.shared.generated.resources.admin_product_variant_seed_notice
import vitranshop.shared.generated.resources.admin_product_variant_table_title
import vitranshop.shared.generated.resources.admin_product_variant_value_placeholder
import vitranshop.shared.generated.resources.admin_product_variants_enable
import vitranshop.shared.generated.resources.admin_product_variants_enable_action
import vitranshop.shared.generated.resources.ic_delete
import vitranshop.shared.generated.resources.ic_more_horiz

@Composable
fun CreateProductVariantsCard(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
) {
    AdminFormCard(title = stringResource(Res.string.admin_product_card_variants)) {
        if (!state.variantsEnabled) {
            Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
                Text(
                    text = stringResource(Res.string.admin_product_variants_enable),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                )
                AdminPrimaryButton(
                    label = stringResource(Res.string.admin_product_variants_enable_action),
                    onClick = { onStateChange(state.enableVariants()) },
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap)) {
                state.options.forEachIndexed { index, option ->
                    VariantOptionCard(
                        index = index,
                        option = option,
                        pendingRemove = state.pendingRemoveOptionId == option.id,
                        onChange = { next -> onStateChange(state.updateOption(option.id) { next }) },
                        onAddValue = { onStateChange(state.commitOptionValue(option.id)) },
                        onRemoveValue = { value ->
                            onStateChange(
                                state.updateOption(option.id) {
                                    it.copy(values = it.values.filterNot { item -> item == value })
                                },
                            )
                        },
                        onRequestRemove = { onStateChange(state.requestRemoveOption(option.id)) },
                        onConfirmRemove = { onStateChange(state.removeOption(option.id)) },
                        onCancelRemove = { onStateChange(state.copy(pendingRemoveOptionId = null)) },
                    )
                }
                if (state.options.size < CreateProductMocks.MaxOptions) {
                    AdminTextButton(
                        label = stringResource(Res.string.admin_product_variant_add_another),
                        onClick = { onStateChange(state.addOption()) },
                    )
                }
                VariantMatrix(state, onStateChange)
            }
        }
    }
}

@Composable
private fun VariantOptionCard(
    index: Int,
    option: CreateProductOptionDraft,
    pendingRemove: Boolean,
    onChange: (CreateProductOptionDraft) -> Unit,
    onAddValue: () -> Unit,
    onRemoveValue: (String) -> Unit,
    onRequestRemove: () -> Unit,
    onConfirmRemove: () -> Unit,
    onCancelRemove: () -> Unit,
) {
    val heading = option.name.ifBlank {
        stringResource(Res.string.admin_product_variant_option_n, index + 1)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AdminTokens.FieldRadius))
            .border(1.dp, AdminTokens.CardBorder, RoundedCornerShape(AdminTokens.FieldRadius))
            .padding(VitranSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_more_horiz),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = AdminTokens.Helper,
            )
            Text(
                text = heading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = VitranSpacing.sm),
            )
            AdminTextButton(
                label = stringResource(Res.string.admin_product_variant_remove),
                onClick = onRequestRemove,
                leadingIcon = painterResource(Res.drawable.ic_delete),
                destructive = true,
            )
        }
        if (pendingRemove) {
            Text(
                text = stringResource(Res.string.admin_product_variant_remove_confirm),
                color = AdminTokens.Destructive,
                fontSize = 13.sp,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                AdminTextButton(
                    label = stringResource(Res.string.admin_product_variant_remove_yes),
                    onClick = onConfirmRemove,
                    destructive = true,
                )
                AdminTextButton(
                    label = stringResource(Res.string.admin_product_variant_remove_no),
                    onClick = onCancelRemove,
                )
            }
        }
        if (option.name.isBlank() || option.values.isEmpty()) {
            AdminTextField(
                label = stringResource(Res.string.admin_product_variant_option_name),
                value = option.name,
                onValueChange = { onChange(option.copy(name = it)) },
                placeholder = stringResource(Res.string.admin_product_variant_option_placeholder),
            )
        }
        VariantValueChips(
            values = option.values,
            input = option.valueInput,
            onInputChange = { onChange(option.copy(valueInput = it)) },
            onAdd = onAddValue,
            onRemove = onRemoveValue,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VariantValueChips(
    values: List<String>,
    input: String,
    onInputChange: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
) {
    var adding by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(adding) {
        if (adding) focusRequester.requestFocus()
    }
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        values.forEach { value ->
            AdminTagChip(label = value, onRemove = { onRemove(value) })
        }
        if (adding || input.isNotEmpty()) {
            VariantChipInput(
                value = input,
                onValueChange = onInputChange,
                onAdd = {
                    if (input.isNotBlank()) onAdd()
                    else adding = false
                },
                focusRequester = focusRequester,
            )
        } else {
            AdminTextButton(
                label = stringResource(Res.string.admin_product_variant_add_value),
                onClick = { adding = true },
            )
        }
    }
}

@Composable
private fun VariantChipInput(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
    focusRequester: FocusRequester,
) {
    val shape = RoundedCornerShape(percent = 50)
    var focused by remember { mutableStateOf(false) }
    var hadFocus by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .height(32.dp)
            .widthIn(min = 120.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                if (focused) AdminTokens.FieldBorderFocused else AdminTokens.FieldBorder,
                shape,
            )
            .padding(horizontal = VitranSpacing.md),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focus ->
                    focused = focus.isFocused
                    if (focus.isFocused) {
                        hadFocus = true
                    } else if (hadFocus) {
                        onAdd()
                    }
                },
            singleLine = true,
            textStyle = adminFieldTextStyle().copy(fontSize = 13.sp),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onAdd() }),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.admin_product_variant_value_placeholder),
                        color = AdminTokens.Placeholder,
                        fontSize = 13.sp,
                    )
                }
                inner()
            },
        )
    }
}

@Composable
private fun VariantMatrix(
    state: CreateProductFormState,
    onStateChange: (CreateProductFormState) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AdminTokens.FieldRadius))
            .background(AdminTokens.NestedPanel)
            .padding(VitranSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        if (state.variants.isEmpty()) {
            Text(
                text = stringResource(Res.string.admin_product_variant_matrix_empty),
                color = AdminTokens.Helper,
                fontSize = 13.sp,
            )
        } else {
            Text(
                text = stringResource(Res.string.admin_product_variant_table_title, state.variants.size),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
            if (state.variantSeedNotice) {
                Text(
                    text = stringResource(Res.string.admin_product_variant_seed_notice),
                    color = AdminTokens.Helper,
                    fontSize = 13.sp,
                )
            }
            state.variants.forEach { row ->
                VariantCombinationCard(
                    row = row,
                    onChange = { next -> onStateChange(state.updateVariant(row.id) { next }) },
                )
            }
        }
    }
}

@Composable
private fun VariantCombinationCard(
    row: ProductVariantRow,
    onChange: (ProductVariantRow) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AdminTokens.FieldRadius))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, AdminTokens.CardBorder, RoundedCornerShape(AdminTokens.FieldRadius))
            .padding(VitranSpacing.md),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = row.label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )
        AdminTextField(
            label = stringResource(Res.string.admin_product_variant_col_price),
            value = row.price,
            onValueChange = { onChange(row.copy(price = formatMoneyInput(it))) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        AdminTextField(
            label = stringResource(Res.string.admin_product_variant_col_qty),
            value = row.quantity,
            onValueChange = {
                onChange(row.copy(quantity = it.filter { ch -> ch.isDigit() || ch in '۰'..'۹' }))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        AdminTextField(
            label = stringResource(Res.string.admin_product_variant_col_sku),
            value = row.sku,
            onValueChange = { onChange(row.copy(sku = it)) },
            ltr = true,
        )
    }
}
