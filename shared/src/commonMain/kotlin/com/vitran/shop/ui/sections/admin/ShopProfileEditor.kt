package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.vitran.shop.feature.seller.shop.presentation.ShopHoursDraft
import com.vitran.shop.feature.seller.shop.presentation.ShopProfileDraft
import com.vitran.shop.feature.seller.shop.presentation.ShopRuleDraft
import com.vitran.shop.feature.seller.shop.presentation.ShopShippingDraft
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminMultilineField
import com.vitran.shop.ui.components.admin.AdminSelectField
import com.vitran.shop.ui.components.admin.AdminSelectOption
import com.vitran.shop.ui.components.admin.AdminTextButton
import com.vitran.shop.ui.components.admin.AdminTextField
import com.vitran.shop.ui.components.admin.AdminToggleRow
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_profile_add_hours
import vitranshop.shared.generated.resources.admin_profile_add_rule
import vitranshop.shared.generated.resources.admin_profile_add_shipping
import vitranshop.shared.generated.resources.admin_profile_card_seo
import vitranshop.shared.generated.resources.admin_profile_card_shipping
import vitranshop.shared.generated.resources.admin_profile_clear_avatar
import vitranshop.shared.generated.resources.admin_profile_clear_webhook
import vitranshop.shared.generated.resources.admin_profile_day
import vitranshop.shared.generated.resources.admin_profile_day_fri
import vitranshop.shared.generated.resources.admin_profile_day_mon
import vitranshop.shared.generated.resources.admin_profile_day_sat
import vitranshop.shared.generated.resources.admin_profile_day_sun
import vitranshop.shared.generated.resources.admin_profile_day_thu
import vitranshop.shared.generated.resources.admin_profile_day_tue
import vitranshop.shared.generated.resources.admin_profile_day_wed
import vitranshop.shared.generated.resources.admin_profile_fulfillment
import vitranshop.shared.generated.resources.admin_profile_fulfillment_api
import vitranshop.shared.generated.resources.admin_profile_fulfillment_local
import vitranshop.shared.generated.resources.admin_profile_fulfillment_manual
import vitranshop.shared.generated.resources.admin_profile_fulfillment_marketplace
import vitranshop.shared.generated.resources.admin_profile_fulfillment_redirect
import vitranshop.shared.generated.resources.admin_profile_hours_closed
import vitranshop.shared.generated.resources.admin_profile_hours_close
import vitranshop.shared.generated.resources.admin_profile_hours_open
import vitranshop.shared.generated.resources.admin_profile_noindex
import vitranshop.shared.generated.resources.admin_profile_remove
import vitranshop.shared.generated.resources.admin_profile_rule_body
import vitranshop.shared.generated.resources.admin_profile_rule_title
import vitranshop.shared.generated.resources.admin_profile_rules
import vitranshop.shared.generated.resources.admin_profile_seo_desc
import vitranshop.shared.generated.resources.admin_profile_seo_title
import vitranshop.shared.generated.resources.admin_profile_shipping_active
import vitranshop.shared.generated.resources.admin_profile_shipping_code
import vitranshop.shared.generated.resources.admin_profile_shipping_cost
import vitranshop.shared.generated.resources.admin_profile_shipping_max_days
import vitranshop.shared.generated.resources.admin_profile_shipping_min_days
import vitranshop.shared.generated.resources.admin_profile_shipping_title
import vitranshop.shared.generated.resources.admin_profile_webhook
import vitranshop.shared.generated.resources.admin_profile_webhook_hint

@Composable
fun ShopProfileEditor(
    profile: ShopProfileDraft,
    onProfileChange: (ShopProfileDraft) -> Unit,
    fieldErrors: Map<String, String> = emptyMap(),
    showClearActions: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val dayOptions = shopDayOptions()
    val fulfillmentOptions = shopFulfillmentOptions()
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        AdminFormCard(title = stringResource(Res.string.admin_profile_card_seo)) {
            AdminTextField(
                label = stringResource(Res.string.admin_profile_seo_title),
                value = profile.seoTitle,
                onValueChange = { value ->
                    onProfileChange(profile.touch("seo_title") { it.copy(seoTitle = value) })
                },
                error = fieldErrors["seo_title"],
            )
            AdminMultilineField(
                label = stringResource(Res.string.admin_profile_seo_desc),
                value = profile.seoDescription,
                onValueChange = { value ->
                    onProfileChange(profile.touch("seo_description") { it.copy(seoDescription = value) })
                },
                error = fieldErrors["seo_description"],
            )
            AdminToggleRow(
                label = stringResource(Res.string.admin_profile_noindex),
                checked = profile.noindex,
                onCheckedChange = { checked ->
                    onProfileChange(profile.touch("noindex") { it.copy(noindex = checked) })
                },
            )
        }
        AdminFormCard(title = stringResource(Res.string.admin_profile_rules)) {
            profile.rules.forEachIndexed { index, rule ->
                RuleEditor(
                    rule = rule,
                    onChange = { next ->
                        onProfileChange(
                            profile.touch("rules") { draft ->
                                draft.copy(rules = draft.rules.replaceAt(index, next))
                            },
                        )
                    },
                    onRemove = {
                        onProfileChange(
                            profile.touch("rules") { draft ->
                                draft.copy(rules = draft.rules.filterIndexed { i, _ -> i != index })
                            },
                        )
                    },
                )
            }
            AdminTextButton(
                label = stringResource(Res.string.admin_profile_add_rule),
                onClick = {
                    onProfileChange(
                        profile.touch("rules") { it.copy(rules = it.rules + ShopRuleDraft()) },
                    )
                },
            )
            fieldErrors["rules"]?.let { message ->
                AdminTextField(
                    label = stringResource(Res.string.admin_profile_rules),
                    value = profile.rules.size.toString(),
                    onValueChange = {},
                    readOnly = true,
                    error = message,
                )
            }
        }
        AdminFormCard(title = stringResource(Res.string.admin_profile_card_shipping)) {
            profile.shippingMethods.forEachIndexed { index, method ->
                ShippingEditor(
                    method = method,
                    onChange = { next ->
                        onProfileChange(
                            profile.touch("shipping_methods") { draft ->
                                draft.copy(shippingMethods = draft.shippingMethods.replaceAt(index, next))
                            },
                        )
                    },
                    onRemove = {
                        onProfileChange(
                            profile.touch("shipping_methods") { draft ->
                                draft.copy(
                                    shippingMethods = draft.shippingMethods.filterIndexed { i, _ -> i != index },
                                )
                            },
                        )
                    },
                )
            }
            AdminTextButton(
                label = stringResource(Res.string.admin_profile_add_shipping),
                onClick = {
                    onProfileChange(
                        profile.touch("shipping_methods") {
                            it.copy(shippingMethods = it.shippingMethods + ShopShippingDraft())
                        },
                    )
                },
            )
            profile.workingHours.forEachIndexed { index, hours ->
                HoursEditor(
                    hours = hours,
                    dayOptions = dayOptions,
                    onChange = { next ->
                        onProfileChange(
                            profile.touch("working_hours") { draft ->
                                draft.copy(workingHours = draft.workingHours.replaceAt(index, next))
                            },
                        )
                    },
                    onRemove = {
                        onProfileChange(
                            profile.touch("working_hours") { draft ->
                                draft.copy(workingHours = draft.workingHours.filterIndexed { i, _ -> i != index })
                            },
                        )
                    },
                )
            }
            AdminTextButton(
                label = stringResource(Res.string.admin_profile_add_hours),
                onClick = {
                    onProfileChange(
                        profile.touch("working_hours") {
                            it.copy(workingHours = it.workingHours + ShopHoursDraft())
                        },
                    )
                },
            )
            AdminSelectField(
                label = stringResource(Res.string.admin_profile_fulfillment),
                valueId = profile.fulfillmentType.takeIf { it.isNotBlank() },
                options = fulfillmentOptions,
                onSelect = { option ->
                    onProfileChange(
                        profile.touch("fulfillment_type") { it.copy(fulfillmentType = option.id) },
                    )
                },
                error = fieldErrors["fulfillment_type"],
            )
            AdminTextField(
                label = stringResource(Res.string.admin_profile_webhook),
                value = profile.webhookUrl,
                onValueChange = { value ->
                    onProfileChange(profile.touch("webhook_url") { it.copy(webhookUrl = value) })
                },
                placeholder = stringResource(Res.string.admin_profile_webhook_hint),
                error = fieldErrors["webhook_url"],
                ltr = true,
            )
            if (showClearActions) {
                AdminToggleRow(
                    label = stringResource(Res.string.admin_profile_clear_avatar),
                    checked = profile.clearAvatar,
                    onCheckedChange = { checked ->
                        onProfileChange(profile.touch("clear_avatar") { it.copy(clearAvatar = checked) })
                    },
                )
                AdminToggleRow(
                    label = stringResource(Res.string.admin_profile_clear_webhook),
                    checked = profile.clearWebhook,
                    onCheckedChange = { checked ->
                        onProfileChange(profile.touch("clear_webhook") { it.copy(clearWebhook = checked) })
                    },
                )
            }
        }
    }
}

@Composable
private fun RuleEditor(
    rule: ShopRuleDraft,
    onChange: (ShopRuleDraft) -> Unit,
    onRemove: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        AdminTextField(
            label = stringResource(Res.string.admin_profile_rule_title),
            value = rule.title,
            onValueChange = { onChange(rule.copy(title = it)) },
        )
        AdminMultilineField(
            label = stringResource(Res.string.admin_profile_rule_body),
            value = rule.body,
            onValueChange = { onChange(rule.copy(body = it)) },
        )
        AdminTextButton(
            label = stringResource(Res.string.admin_profile_remove),
            onClick = onRemove,
            destructive = true,
        )
    }
}

@Composable
private fun ShippingEditor(
    method: ShopShippingDraft,
    onChange: (ShopShippingDraft) -> Unit,
    onRemove: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            AdminTextField(
                label = stringResource(Res.string.admin_profile_shipping_code),
                value = method.code,
                onValueChange = { onChange(method.copy(code = it)) },
                ltr = true,
                modifier = Modifier.weight(1f),
            )
            AdminTextField(
                label = stringResource(Res.string.admin_profile_shipping_title),
                value = method.title,
                onValueChange = { onChange(method.copy(title = it)) },
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            AdminTextField(
                label = stringResource(Res.string.admin_profile_shipping_cost),
                value = method.cost,
                onValueChange = { onChange(method.copy(cost = it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                ltr = true,
                modifier = Modifier.weight(1f),
            )
            AdminTextField(
                label = stringResource(Res.string.admin_profile_shipping_min_days),
                value = method.minDays,
                onValueChange = { onChange(method.copy(minDays = it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                ltr = true,
                modifier = Modifier.weight(1f),
            )
            AdminTextField(
                label = stringResource(Res.string.admin_profile_shipping_max_days),
                value = method.maxDays,
                onValueChange = { onChange(method.copy(maxDays = it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                ltr = true,
                modifier = Modifier.weight(1f),
            )
        }
        AdminToggleRow(
            label = stringResource(Res.string.admin_profile_shipping_active),
            checked = method.active,
            onCheckedChange = { onChange(method.copy(active = it)) },
        )
        AdminTextButton(
            label = stringResource(Res.string.admin_profile_remove),
            onClick = onRemove,
            destructive = true,
        )
    }
}

@Composable
private fun HoursEditor(
    hours: ShopHoursDraft,
    dayOptions: List<AdminSelectOption>,
    onChange: (ShopHoursDraft) -> Unit,
    onRemove: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        AdminSelectField(
            label = stringResource(Res.string.admin_profile_day),
            valueId = hours.day,
            options = dayOptions,
            onSelect = { onChange(hours.copy(day = it.id)) },
        )
        if (!hours.closed) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                AdminTextField(
                    label = stringResource(Res.string.admin_profile_hours_open),
                    value = hours.opensAt,
                    onValueChange = { onChange(hours.copy(opensAt = it)) },
                    ltr = true,
                    modifier = Modifier.weight(1f),
                )
                AdminTextField(
                    label = stringResource(Res.string.admin_profile_hours_close),
                    value = hours.closesAt,
                    onValueChange = { onChange(hours.copy(closesAt = it)) },
                    ltr = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        AdminToggleRow(
            label = stringResource(Res.string.admin_profile_hours_closed),
            checked = hours.closed,
            onCheckedChange = { onChange(hours.copy(closed = it)) },
        )
        AdminTextButton(
            label = stringResource(Res.string.admin_profile_remove),
            onClick = onRemove,
            destructive = true,
        )
    }
}

@Composable
private fun shopDayOptions(): List<AdminSelectOption> =
    listOf(
        AdminSelectOption("sat", stringResource(Res.string.admin_profile_day_sat)),
        AdminSelectOption("sun", stringResource(Res.string.admin_profile_day_sun)),
        AdminSelectOption("mon", stringResource(Res.string.admin_profile_day_mon)),
        AdminSelectOption("tue", stringResource(Res.string.admin_profile_day_tue)),
        AdminSelectOption("wed", stringResource(Res.string.admin_profile_day_wed)),
        AdminSelectOption("thu", stringResource(Res.string.admin_profile_day_thu)),
        AdminSelectOption("fri", stringResource(Res.string.admin_profile_day_fri)),
    )

@Composable
private fun shopFulfillmentOptions(): List<AdminSelectOption> =
    listOf(
        AdminSelectOption("manual", stringResource(Res.string.admin_profile_fulfillment_manual)),
        AdminSelectOption("redirect", stringResource(Res.string.admin_profile_fulfillment_redirect)),
        AdminSelectOption("api", stringResource(Res.string.admin_profile_fulfillment_api)),
        AdminSelectOption("marketplace", stringResource(Res.string.admin_profile_fulfillment_marketplace)),
        AdminSelectOption("local", stringResource(Res.string.admin_profile_fulfillment_local)),
    )

private fun <T> List<T>.replaceAt(index: Int, value: T): List<T> =
    mapIndexed { i, item -> if (i == index) value else item }
