package com.vitran.shop.ui.sections.account.cities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountCard
import com.vitran.shop.ui.sections.account.AccountOutlinedButton
import com.vitran.shop.ui.sections.account.AccountPrimaryButton
import com.vitran.shop.ui.sections.account.AccountStackedField
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.ErrorRed
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.ShopPurpleTint
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_cities_toggle_a11y
import vitranshop.shared.generated.resources.account_city_create_cancel
import vitranshop.shared.generated.resources.account_city_create_name_placeholder
import vitranshop.shared.generated.resources.account_city_create_preview
import vitranshop.shared.generated.resources.account_city_create_preview_empty
import vitranshop.shared.generated.resources.account_city_create_slug_hint
import vitranshop.shared.generated.resources.account_city_create_submit
import vitranshop.shared.generated.resources.account_city_detail_delete
import vitranshop.shared.generated.resources.account_city_detail_field_id
import vitranshop.shared.generated.resources.account_city_detail_field_name
import vitranshop.shared.generated.resources.account_city_detail_field_slug
import vitranshop.shared.generated.resources.account_city_detail_info
import vitranshop.shared.generated.resources.account_city_detail_reset
import vitranshop.shared.generated.resources.account_city_detail_toggle_active
import vitranshop.shared.generated.resources.account_city_detail_toggle_active_hint
import vitranshop.shared.generated.resources.account_save_changes

internal enum class AccountCityFormMode {
    Create,
    Edit,
}

@Composable
internal fun AccountCityForm(
    city: AccountCity,
    onCityChange: (AccountCity) -> Unit,
    mode: AccountCityFormMode,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null,
    nameError: String? = null,
    slugError: String? = null,
    onClearFieldError: (String) -> Unit = {},
) {
    val isDesktop = LocalDesktopLayout.current
    AccountCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xxl),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            ) {
                VitranText(
                    text = stringResource(Res.string.account_city_detail_info),
                    style = VitranTextStyle.Title,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (mode == AccountCityFormMode.Edit) {
                    if (isDesktop) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                        ) {
                            AccountStackedField(
                                label = stringResource(Res.string.account_city_detail_field_id),
                                value = toPersianDigits(city.id),
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.weight(0.7f),
                            )
                            AccountStackedField(
                                label = stringResource(Res.string.account_city_detail_field_name),
                                value = city.name,
                                onValueChange = { onCityChange(city.copy(name = it)) },
                                modifier = Modifier.weight(1.3f),
                            )
                        }
                    } else {
                        AccountStackedField(
                            label = stringResource(Res.string.account_city_detail_field_id),
                            value = toPersianDigits(city.id),
                            onValueChange = {},
                            readOnly = true,
                        )
                        AccountStackedField(
                            label = stringResource(Res.string.account_city_detail_field_name),
                            value = city.name,
                            onValueChange = { onCityChange(city.copy(name = it)) },
                        )
                    }
                    AccountStackedField(
                        label = stringResource(Res.string.account_city_detail_field_slug),
                        value = city.slug,
                        onValueChange = { onCityChange(city.copy(slug = it)) },
                    )
                    AccountCityToggleRow(
                        checked = city.isActive,
                        onCheckedChange = { onCityChange(city.copy(isActive = it)) },
                    )
                } else {
                    AccountStackedField(
                        label = stringResource(Res.string.account_city_detail_field_name),
                        value = city.name,
                        onValueChange = {
                            onClearFieldError("name")
                            onCityChange(city.copy(name = it))
                        },
                        placeholder = stringResource(Res.string.account_city_create_name_placeholder),
                        required = true,
                        error = nameError,
                    )
                    AccountStackedField(
                        label = stringResource(Res.string.account_city_detail_field_slug),
                        value = city.slug,
                        onValueChange = {
                            onClearFieldError("slug")
                            onCityChange(city.copy(slug = it))
                        },
                        required = true,
                        supportingText = stringResource(Res.string.account_city_create_slug_hint),
                        error = slugError,
                    )
                    AccountCitySlugPreview(slug = city.slug)
                    AccountCityToggleRow(
                        checked = city.isActive,
                        onCheckedChange = { onCityChange(city.copy(isActive = it)) },
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = AccountTokens.FieldDivider,
            )
            if (mode == AccountCityFormMode.Edit) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                ) {
                    AccountPrimaryButton(
                        label = stringResource(Res.string.account_save_changes),
                        onClick = onPrimary,
                    )
                    AccountOutlinedButton(
                        label = stringResource(Res.string.account_city_detail_reset),
                        onClick = onSecondary,
                        borderColor = ShopPurple,
                        contentColor = ShopPurple,
                    )
                    if (onDelete != null) {
                        Spacer(modifier = Modifier.weight(1f))
                        AccountOutlinedButton(
                            label = stringResource(Res.string.account_city_detail_delete),
                            onClick = onDelete,
                            borderColor = ErrorRed,
                            contentColor = ErrorRed,
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                ) {
                    AccountPrimaryButton(
                        label = stringResource(Res.string.account_city_create_submit),
                        onClick = onPrimary,
                    )
                    AccountOutlinedButton(
                        label = stringResource(Res.string.account_city_create_cancel),
                        onClick = onSecondary,
                    )
                }
            }
        }
    }
}

private const val CitySwitchScale = 0.62f
private val CitySwitchWidth = 32.dp
private val CitySwitchHeight = 20.dp

@Composable
internal fun AccountCityActiveSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val toggleLabel = stringResource(Res.string.account_cities_toggle_a11y)
    Box(
        modifier = modifier
            .wrapContentSize(unbounded = true)
            .requiredSize(width = CitySwitchWidth, height = CitySwitchHeight)
            .semantics { contentDescription = toggleLabel },
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    checkedBorderColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = AccountTokens.ChipBorder,
                    uncheckedBorderColor = AccountTokens.ChipBorder,
                ),
                modifier = Modifier.scale(CitySwitchScale),
            )
        }
    }
}

@Composable
private fun AccountCityToggleRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            VitranText(
                text = stringResource(Res.string.account_city_detail_toggle_active),
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
            )
            VitranText(
                text = stringResource(Res.string.account_city_detail_toggle_active_hint),
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        AccountCityActiveSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun AccountCitySlugPreview(slug: String) {
    val preview = if (slug.isBlank()) {
        stringResource(Res.string.account_city_create_preview_empty)
    } else {
        stringResource(Res.string.account_city_create_preview, slug.trim())
    }
    val shape = RoundedCornerShape(VitranRadius.small)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ShopPurpleTint, shape)
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VitranText(
            text = preview,
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
