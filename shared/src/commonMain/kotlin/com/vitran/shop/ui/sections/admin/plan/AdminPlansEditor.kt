package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.components.admin.AdminSelectField
import com.vitran.shop.ui.components.admin.AdminTextField
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_plans_delete
import vitranshop.shared.generated.resources.admin_plans_editor_title
import vitranshop.shared.generated.resources.admin_plans_feature_custom_reports
import vitranshop.shared.generated.resources.admin_plans_feature_higher_list
import vitranshop.shared.generated.resources.admin_plans_feature_priority_support
import vitranshop.shared.generated.resources.admin_plans_field_analytics
import vitranshop.shared.generated.resources.admin_plans_field_monthly
import vitranshop.shared.generated.resources.admin_plans_field_name
import vitranshop.shared.generated.resources.admin_plans_field_products
import vitranshop.shared.generated.resources.admin_plans_field_slug
import vitranshop.shared.generated.resources.admin_plans_field_slots
import vitranshop.shared.generated.resources.admin_plans_field_status
import vitranshop.shared.generated.resources.admin_plans_field_team
import vitranshop.shared.generated.resources.admin_plans_field_yearly
import vitranshop.shared.generated.resources.admin_plans_save
import vitranshop.shared.generated.resources.admin_plans_yearly_discount
import vitranshop.shared.generated.resources.ic_delete

@Composable
fun AdminPlansEditorCard(
    form: AdminPlanFormState,
    onFormChange: (AdminPlanFormState) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val discount = form.yearlyDiscountPercent()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(StorePlanTokens.CardRadius))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, StorePlanTokens.CardBorder, RoundedCornerShape(StorePlanTokens.CardRadius))
            .padding(VitranSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        Text(
            text = stringResource(Res.string.admin_plans_editor_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        AdminTextField(
            label = stringResource(Res.string.admin_plans_field_name),
            value = form.name,
            onValueChange = { onFormChange(form.copy(name = it)) },
            required = true,
        )
        AdminTextField(
            label = stringResource(Res.string.admin_plans_field_slug),
            value = form.slug,
            onValueChange = { onFormChange(form.copy(slug = it)) },
            ltr = true,
            required = true,
        )
        FormPair(
            compact = compact,
            first = {
                AdminTextField(
                    label = stringResource(Res.string.admin_plans_field_monthly),
                    value = form.monthlyPrice,
                    onValueChange = { onFormChange(form.copy(monthlyPrice = it.filter { ch -> ch.isDigit() })) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = it,
                )
            },
            second = {
                Column(modifier = it) {
                    AdminTextField(
                        label = stringResource(Res.string.admin_plans_field_yearly),
                        value = form.yearlyPrice,
                        onValueChange = { onFormChange(form.copy(yearlyPrice = it.filter { ch -> ch.isDigit() })) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    if (discount != null) {
                        Spacer(modifier = Modifier.height(VitranSpacing.xs))
                        Text(
                            text = stringResource(
                                Res.string.admin_plans_yearly_discount,
                                toPersianDigits(discount),
                            ),
                            color = AdminPlansTokens.SuccessText,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        )
                    }
                }
            },
        )
        FormPair(
            compact = compact,
            first = {
                AdminTextField(
                    label = stringResource(Res.string.admin_plans_field_products),
                    value = form.productLimit,
                    onValueChange = { onFormChange(form.copy(productLimit = it.filter { ch -> ch.isDigit() })) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = it,
                )
            },
            second = {
                AdminTextField(
                    label = stringResource(Res.string.admin_plans_field_slots),
                    value = form.specialSlots,
                    onValueChange = { onFormChange(form.copy(specialSlots = it.filter { ch -> ch.isDigit() })) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = it,
                )
            },
        )
        FormPair(
            compact = compact,
            first = {
                AdminSelectField(
                    label = stringResource(Res.string.admin_plans_field_status),
                    valueId = form.statusId,
                    options = AdminPlanStatusOptions,
                    onSelect = { onFormChange(form.copy(statusId = it.id)) },
                    modifier = it,
                )
            },
            second = {
                AdminSelectField(
                    label = stringResource(Res.string.admin_plans_field_analytics),
                    valueId = form.analyticsId,
                    options = AdminPlanAnalyticsOptions,
                    onSelect = { onFormChange(form.copy(analyticsId = it.id)) },
                    modifier = it,
                )
            },
        )
        AdminTextField(
            label = stringResource(Res.string.admin_plans_field_team),
            value = form.teamMembers,
            onValueChange = { onFormChange(form.copy(teamMembers = it.filter { ch -> ch.isDigit() })) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        FeatureToggle(
            label = stringResource(Res.string.admin_plans_feature_higher_list),
            checked = form.higherInList,
            onCheckedChange = { onFormChange(form.copy(higherInList = it)) },
        )
        FeatureToggle(
            label = stringResource(Res.string.admin_plans_feature_custom_reports),
            checked = form.customReports,
            onCheckedChange = { onFormChange(form.copy(customReports = it)) },
        )
        FeatureToggle(
            label = stringResource(Res.string.admin_plans_feature_priority_support),
            checked = form.prioritySupport,
            onCheckedChange = { onFormChange(form.copy(prioritySupport = it)) },
        )
        Spacer(modifier = Modifier.height(VitranSpacing.xs))
        if (compact) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            ) {
                AdminPrimaryButton(
                    label = stringResource(Res.string.admin_plans_save),
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                    fillMaxWidth = true,
                )
                if (!form.isNew) {
                    DeletePlanButton(onClick = onDelete, fill = true)
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!form.isNew) {
                    DeletePlanButton(onClick = onDelete, fill = false)
                }
                AdminPrimaryButton(
                    label = stringResource(Res.string.admin_plans_save),
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    fillMaxWidth = true,
                )
            }
        }
    }
}

@Composable
private fun FormPair(
    compact: Boolean,
    first: @Composable (Modifier: Modifier) -> Unit,
    second: @Composable (Modifier: Modifier) -> Unit,
) {
    if (compact) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            first(Modifier.fillMaxWidth())
            second(Modifier.fillMaxWidth())
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            first(Modifier.weight(1f))
            second(Modifier.weight(1f))
        }
    }
}

@Composable
private fun DeletePlanButton(
    onClick: () -> Unit,
    fill: Boolean,
) {
    Row(
        modifier = Modifier
            .then(if (fill) Modifier.fillMaxWidth() else Modifier)
            .clip(RoundedCornerShape(VitranRadius.medium))
            .border(1.dp, StorePlanTokens.FailedBadgeText, RoundedCornerShape(VitranRadius.medium))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm, Alignment.CenterHorizontally),
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_delete),
            contentDescription = null,
            tint = StorePlanTokens.FailedBadgeText,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = stringResource(Res.string.admin_plans_delete),
            color = StorePlanTokens.FailedBadgeText,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun FeatureToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(VitranRadius.medium))
            .border(1.dp, StorePlanTokens.CardBorder, RoundedCornerShape(VitranRadius.medium))
            .clickable(role = Role.Button) { onCheckedChange(!checked) }
            .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = AdminTokens.Brand,
            ),
        )
    }
}
