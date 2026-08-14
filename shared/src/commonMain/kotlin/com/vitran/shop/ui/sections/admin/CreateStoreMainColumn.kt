package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminEmptyPromptCard
import com.vitran.shop.ui.components.admin.AdminFormCard
import com.vitran.shop.ui.components.admin.AdminMediaDropzone
import com.vitran.shop.ui.components.admin.AdminMultilineField
import com.vitran.shop.ui.components.admin.AdminSelectField
import com.vitran.shop.ui.components.admin.AdminSearchableSelect
import com.vitran.shop.ui.components.admin.AdminTextField
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_card_about_sub
import vitranshop.shared.generated.resources.admin_card_identity
import vitranshop.shared.generated.resources.admin_card_identity_sub
import vitranshop.shared.generated.resources.admin_card_location
import vitranshop.shared.generated.resources.admin_card_location_sub
import vitranshop.shared.generated.resources.admin_card_media
import vitranshop.shared.generated.resources.admin_card_media_sub
import vitranshop.shared.generated.resources.admin_card_policies
import vitranshop.shared.generated.resources.admin_card_policies_sub
import vitranshop.shared.generated.resources.admin_card_social
import vitranshop.shared.generated.resources.admin_card_social_sub
import vitranshop.shared.generated.resources.admin_card_theme
import vitranshop.shared.generated.resources.admin_card_theme_sub
import vitranshop.shared.generated.resources.admin_card_url_sub
import vitranshop.shared.generated.resources.admin_create_store_publish
import vitranshop.shared.generated.resources.admin_field_about
import vitranshop.shared.generated.resources.admin_field_about_placeholder
import vitranshop.shared.generated.resources.admin_field_address
import vitranshop.shared.generated.resources.admin_field_category_prompt
import vitranshop.shared.generated.resources.admin_field_city
import vitranshop.shared.generated.resources.admin_field_city_helper
import vitranshop.shared.generated.resources.admin_field_city_placeholder
import vitranshop.shared.generated.resources.admin_field_cover
import vitranshop.shared.generated.resources.admin_field_email
import vitranshop.shared.generated.resources.admin_field_email_channel
import vitranshop.shared.generated.resources.admin_field_email_placeholder
import vitranshop.shared.generated.resources.admin_field_icon
import vitranshop.shared.generated.resources.admin_field_instagram
import vitranshop.shared.generated.resources.admin_field_legal_name
import vitranshop.shared.generated.resources.admin_field_owner_name
import vitranshop.shared.generated.resources.admin_field_phone
import vitranshop.shared.generated.resources.admin_field_phone_placeholder
import vitranshop.shared.generated.resources.admin_field_policies
import vitranshop.shared.generated.resources.admin_field_province
import vitranshop.shared.generated.resources.admin_field_province_placeholder
import vitranshop.shared.generated.resources.admin_field_returns
import vitranshop.shared.generated.resources.admin_field_shipping
import vitranshop.shared.generated.resources.admin_field_slogan
import vitranshop.shared.generated.resources.admin_field_slogan_placeholder
import vitranshop.shared.generated.resources.admin_field_store_name
import vitranshop.shared.generated.resources.admin_field_store_name_placeholder
import vitranshop.shared.generated.resources.admin_field_telegram
import vitranshop.shared.generated.resources.admin_field_website
import vitranshop.shared.generated.resources.admin_field_whatsapp
import vitranshop.shared.generated.resources.admin_media_crop
import vitranshop.shared.generated.resources.admin_media_crop_banner
import vitranshop.shared.generated.resources.admin_media_crop_free
import vitranshop.shared.generated.resources.admin_media_crop_wide
import vitranshop.shared.generated.resources.admin_media_edit
import vitranshop.shared.generated.resources.admin_media_position
import vitranshop.shared.generated.resources.admin_media_spec_formats
import vitranshop.shared.generated.resources.admin_media_spec_max
import vitranshop.shared.generated.resources.admin_media_spec_size
import vitranshop.shared.generated.resources.admin_media_zoom
import vitranshop.shared.generated.resources.admin_policy_mode_auto
import vitranshop.shared.generated.resources.admin_policy_mode_manual
import vitranshop.shared.generated.resources.admin_policy_mode_template
import vitranshop.shared.generated.resources.admin_policy_templates_label
import vitranshop.shared.generated.resources.admin_empty_products_action
import vitranshop.shared.generated.resources.admin_empty_products_body
import vitranshop.shared.generated.resources.admin_empty_products_title
import vitranshop.shared.generated.resources.admin_preview_store_fallback
import vitranshop.shared.generated.resources.admin_preview_visit
import vitranshop.shared.generated.resources.admin_publish_check_brand
import vitranshop.shared.generated.resources.admin_publish_check_contact
import vitranshop.shared.generated.resources.admin_publish_check_info
import vitranshop.shared.generated.resources.admin_publish_check_policies
import vitranshop.shared.generated.resources.admin_publish_done
import vitranshop.shared.generated.resources.admin_publish_not_ready_title
import vitranshop.shared.generated.resources.admin_publish_ready_title
import vitranshop.shared.generated.resources.admin_publish_view
import vitranshop.shared.generated.resources.admin_social_add_new
import vitranshop.shared.generated.resources.admin_social_drag_a11y
import vitranshop.shared.generated.resources.admin_social_placeholder_email
import vitranshop.shared.generated.resources.admin_social_placeholder_instagram
import vitranshop.shared.generated.resources.admin_social_placeholder_telegram
import vitranshop.shared.generated.resources.admin_social_placeholder_website
import vitranshop.shared.generated.resources.admin_social_placeholder_whatsapp
import vitranshop.shared.generated.resources.admin_social_remove
import vitranshop.shared.generated.resources.admin_summary_status
import vitranshop.shared.generated.resources.admin_tab_policies
import vitranshop.shared.generated.resources.admin_tab_returns
import vitranshop.shared.generated.resources.admin_tab_shipping
import vitranshop.shared.generated.resources.admin_theme_include_background
import vitranshop.shared.generated.resources.admin_theme_include_button
import vitranshop.shared.generated.resources.admin_theme_include_cards
import vitranshop.shared.generated.resources.admin_theme_include_primary
import vitranshop.shared.generated.resources.admin_theme_includes
import vitranshop.shared.generated.resources.admin_theme_presets
import vitranshop.shared.generated.resources.admin_url_available
import vitranshop.shared.generated.resources.admin_url_copied
import vitranshop.shared.generated.resources.admin_url_copy
import vitranshop.shared.generated.resources.admin_url_label
import vitranshop.shared.generated.resources.admin_url_prefix
import vitranshop.shared.generated.resources.ic_check
import vitranshop.shared.generated.resources.ic_filter_sliders
import vitranshop.shared.generated.resources.ic_menu_hamburger
import vitranshop.shared.generated.resources.ic_nav_home
import vitranshop.shared.generated.resources.ic_policy_about
import vitranshop.shared.generated.resources.ic_shop_logo
import vitranshop.shared.generated.resources.ic_share
import vitranshop.shared.generated.resources.ic_social_email
import vitranshop.shared.generated.resources.ic_social_instagram
import vitranshop.shared.generated.resources.ic_social_website

@Composable
fun CreateStoreStepBody(
    step: CreateStoreStep,
    state: CreateStoreFormState,
    onStateChange: (CreateStoreFormState) -> Unit,
    onViewStore: () -> Unit = {},
    onPublish: () -> Unit = {},
    onAddProduct: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AdminTokens.CardGap),
    ) {
        when (step) {
            CreateStoreStep.Basics -> CreateStoreBasicsStep(state, onStateChange)
            CreateStoreStep.Brand -> CreateStoreBrandStep(state, onStateChange)
            CreateStoreStep.Contact -> CreateStoreContactStep(state, onStateChange)
            CreateStoreStep.Policies -> CreateStorePoliciesStep(state, onStateChange)
            CreateStoreStep.Publish -> CreateStorePublishStep(
                state = state,
                onViewStore = onViewStore,
                onPublish = onPublish,
                onAddProduct = onAddProduct,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateStoreBasicsStep(
    state: CreateStoreFormState,
    onStateChange: (CreateStoreFormState) -> Unit,
) {
    AdminFormCard(
        title = stringResource(Res.string.admin_card_identity),
        subtitle = stringResource(Res.string.admin_card_identity_sub),
        icon = painterResource(Res.drawable.ic_shop_logo),
    ) {
        AdminTextField(
            label = stringResource(Res.string.admin_field_store_name),
            value = state.storeName,
            onValueChange = { onStateChange(state.withStoreName(it)) },
            placeholder = stringResource(Res.string.admin_field_store_name_placeholder),
            required = true,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            AdminTextField(
                label = stringResource(Res.string.admin_field_legal_name),
                value = state.legalName,
                onValueChange = { onStateChange(state.copy(legalName = it, dirty = true)) },
                modifier = Modifier.weight(1f),
            )
            AdminTextField(
                label = stringResource(Res.string.admin_field_owner_name),
                value = state.ownerName,
                onValueChange = { onStateChange(state.copy(ownerName = it, dirty = true)) },
                modifier = Modifier.weight(1f),
            )
        }
        AdminTextField(
            label = stringResource(Res.string.admin_field_slogan),
            value = state.slogan,
            onValueChange = { onStateChange(state.copy(slogan = it, dirty = true)) },
            placeholder = stringResource(Res.string.admin_field_slogan_placeholder),
        )
        Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
            Text(
                text = stringResource(Res.string.admin_field_category_prompt),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                CreateStoreMocks.categories.forEach { category ->
                    val selected = state.typeId == category.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(
                                if (selected) state.theme.primary.copy(alpha = 0.12f)
                                else AdminTokens.DropdownHover,
                            )
                            .border(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) state.theme.primary else AdminTokens.FieldBorder,
                                shape = RoundedCornerShape(percent = 50),
                            )
                            .clickable(role = Role.Button) {
                                onStateChange(state.withCategory(category.id))
                            }
                            .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
                    ) {
                        Text(
                            text = "${category.emoji}  ${category.label}",
                            color = if (selected) state.theme.primary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
        }
    }
    AdminFormCard(
        title = stringResource(Res.string.admin_url_label),
        subtitle = stringResource(Res.string.admin_card_url_sub),
        icon = painterResource(Res.drawable.ic_share),
    ) {
        CreateStoreUrlBuilder(state = state, onStateChange = onStateChange)
    }
}

@Composable
private fun CreateStoreUrlBuilder(
    state: CreateStoreFormState,
    onStateChange: (CreateStoreFormState) -> Unit,
) {
    var copied by remember { mutableStateOf(false) }
    AdminTextField(
        label = stringResource(Res.string.admin_url_label),
        value = state.slug,
        onValueChange = {
            copied = false
            onStateChange(state.withSlug(it))
        },
        prefix = stringResource(Res.string.admin_url_prefix),
        required = true,
        placeholder = "aria-store",
        trailing = {
            Text(
                text = stringResource(
                    if (copied) Res.string.admin_url_copied else Res.string.admin_url_copy,
                ),
                color = if (copied) AdminTokens.Success else MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(role = Role.Button) { copied = true },
            )
        },
    )
    if (state.slug.isNotBlank()) {
        Text(
            text = stringResource(Res.string.admin_url_available),
            color = AdminTokens.Success,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateStoreBrandStep(
    state: CreateStoreFormState,
    onStateChange: (CreateStoreFormState) -> Unit,
) {
    var editingCover by remember { mutableStateOf(false) }
    var cropPreset by remember { mutableStateOf("wide") }
    AdminFormCard(
        title = stringResource(Res.string.admin_card_media),
        subtitle = stringResource(Res.string.admin_card_media_sub),
        icon = painterResource(Res.drawable.ic_nav_home),
    ) {
        AdminMediaDropzone(
            label = stringResource(Res.string.admin_field_cover),
            imageUrl = state.coverUrl,
            onPick = {
                onStateChange(
                    state.copy(
                        coverUrl = CreateStoreMocks.MockCoverUrl,
                        coverZoom = 1f,
                        coverOffsetX = 0f,
                        coverOffsetY = 0f,
                        dirty = true,
                    ),
                )
                editingCover = true
            },
            onRemove = {
                onStateChange(
                    state.copy(
                        coverUrl = null,
                        coverZoom = 1f,
                        coverOffsetX = 0f,
                        coverOffsetY = 0f,
                        dirty = true,
                    ),
                )
                editingCover = false
            },
            specs = listOf(
                stringResource(Res.string.admin_media_spec_size),
                stringResource(Res.string.admin_media_spec_max),
                stringResource(Res.string.admin_media_spec_formats),
            ),
            zoom = state.coverZoom,
            offsetX = state.coverOffsetX,
            offsetY = state.coverOffsetY,
        )
        if (state.coverUrl != null) {
            Text(
                text = stringResource(Res.string.admin_media_edit),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                modifier = Modifier.clickable(role = Role.Button) { editingCover = !editingCover },
            )
            if (editingCover) {
                CoverEditor(
                    cropPreset = cropPreset,
                    onCropPreset = { id ->
                        cropPreset = id
                        val (zoom, offsetY) = when (id) {
                            "banner" -> 1.35f to -0.2f
                            "free" -> 1.15f to 0f
                            else -> 1f to 0f
                        }
                        onStateChange(
                            state.copy(
                                coverZoom = zoom,
                                coverOffsetY = offsetY,
                                dirty = true,
                            ),
                        )
                    },
                    zoom = state.coverZoom,
                    offsetX = state.coverOffsetX,
                    onZoom = { onStateChange(state.copy(coverZoom = it, dirty = true)) },
                    onOffsetX = { onStateChange(state.copy(coverOffsetX = it, dirty = true)) },
                    brand = state.theme.primary,
                )
            }
        }
        AdminMediaDropzone(
            label = stringResource(Res.string.admin_field_icon),
            imageUrl = state.iconUrl,
            onPick = { onStateChange(state.copy(iconUrl = CreateStoreMocks.MockIconUrl, dirty = true)) },
            onRemove = { onStateChange(state.copy(iconUrl = null, dirty = true)) },
            height = AdminTokens.LogoDropzoneSize,
            circular = true,
        )
    }
    AdminFormCard(
        title = stringResource(Res.string.admin_card_theme),
        subtitle = stringResource(Res.string.admin_card_theme_sub),
        icon = painterResource(Res.drawable.ic_filter_sliders),
    ) {
        Text(
            text = stringResource(Res.string.admin_theme_presets),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            CreateStoreMocks.themes.forEach { theme ->
                val selected = theme.id == state.themeId
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(VitranRadius.medium))
                        .border(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) theme.primary else AdminTokens.CardBorder,
                            shape = RoundedCornerShape(VitranRadius.medium),
                        )
                        .background(
                            if (selected) theme.secondary.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.surface,
                        )
                        .clickable(role = Role.Button) { onStateChange(state.withTheme(theme.id)) }
                        .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(text = theme.emoji, fontSize = 20.sp)
                    Text(
                        text = theme.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    )
                }
            }
        }
        ThemeIncludesPreview(theme = state.theme)
    }
    AdminFormCard(
        title = stringResource(Res.string.admin_field_about),
        subtitle = stringResource(Res.string.admin_card_about_sub),
        icon = painterResource(Res.drawable.ic_policy_about),
    ) {
        AdminMultilineField(
            label = stringResource(Res.string.admin_field_about),
            value = state.about,
            onValueChange = { onStateChange(state.copy(about = it, dirty = true)) },
            placeholder = stringResource(Res.string.admin_field_about_placeholder),
            showToolbar = true,
        )
    }
}

@Composable
private fun ThemeIncludesPreview(theme: CreateStoreTheme) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(VitranRadius.medium))
            .background(theme.pageBackground)
            .border(1.dp, theme.primary.copy(alpha = 0.18f), RoundedCornerShape(VitranRadius.medium))
            .padding(VitranSpacing.md),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.admin_theme_includes),
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        )
        ThemeIncludeRow(
            label = stringResource(Res.string.admin_theme_include_primary),
            swatch = theme.primary,
        )
        ThemeIncludeRow(
            label = stringResource(Res.string.admin_theme_include_button),
            swatch = theme.primary,
            preview = {
                Box(
                    modifier = Modifier
                        .height(22.dp)
                        .clip(RoundedCornerShape(VitranRadius.small))
                        .background(theme.primary)
                        .padding(horizontal = VitranSpacing.sm),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.admin_preview_visit),
                        color = theme.onPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            },
        )
        ThemeIncludeRow(
            label = stringResource(Res.string.admin_theme_include_background),
            swatch = theme.pageBackground,
        )
        ThemeIncludeRow(
            label = stringResource(Res.string.admin_theme_include_cards),
            swatch = theme.cardBackground,
            preview = {
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.cardBackground)
                        .border(1.dp, theme.secondary, RoundedCornerShape(8.dp)),
                )
            },
        )
    }
}

@Composable
private fun ThemeIncludeRow(
    label: String,
    swatch: androidx.compose.ui.graphics.Color,
    preview: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = "✓  $label",
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (preview != null) {
            preview()
        } else {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(swatch)
                    .border(1.dp, AdminTokens.FieldBorder, CircleShape),
            )
        }
    }
}

@Composable
private fun CoverEditor(
    cropPreset: String,
    onCropPreset: (String) -> Unit,
    zoom: Float,
    offsetX: Float,
    onZoom: (Float) -> Unit,
    onOffsetX: (Float) -> Unit,
    brand: androidx.compose.ui.graphics.Color,
) {
    val crops = listOf(
        "wide" to stringResource(Res.string.admin_media_crop_wide),
        "banner" to stringResource(Res.string.admin_media_crop_banner),
        "free" to stringResource(Res.string.admin_media_crop_free),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(VitranRadius.medium))
            .background(AdminTokens.DropdownHover)
            .padding(VitranSpacing.md),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.admin_media_crop),
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
            crops.forEach { (id, label) ->
                val selected = cropPreset == id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(VitranRadius.small))
                        .background(if (selected) MaterialTheme.colorScheme.surface else AdminTokens.DropdownHover)
                        .border(
                            1.dp,
                            if (selected) brand else AdminTokens.FieldBorder,
                            RoundedCornerShape(VitranRadius.small),
                        )
                        .clickable(role = Role.Button) { onCropPreset(id) }
                        .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
                ) {
                    Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        CoverSlider(
            label = stringResource(Res.string.admin_media_zoom),
            value = zoom,
            valueRange = 1f..1.8f,
            onValueChange = onZoom,
            brand = brand,
        )
        CoverSlider(
            label = stringResource(Res.string.admin_media_position),
            value = offsetX,
            valueRange = -1f..1f,
            onValueChange = onOffsetX,
            brand = brand,
        )
    }
}

@Composable
private fun CoverSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    brand: androidx.compose.ui.graphics.Color,
) {
    Column {
        Text(text = label, color = AdminTokens.Helper, fontSize = 12.sp)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = brand,
                activeTrackColor = brand,
                inactiveTrackColor = AdminTokens.FieldBorder,
            ),
        )
    }
}

@Composable
private fun CreateStoreContactStep(
    state: CreateStoreFormState,
    onStateChange: (CreateStoreFormState) -> Unit,
) {
    val cityOptions = CreateStoreMocks.citiesFor(state.provinceId)
    AdminFormCard(
        title = stringResource(Res.string.admin_card_location),
        subtitle = stringResource(Res.string.admin_card_location_sub),
        icon = painterResource(Res.drawable.ic_social_email),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            AdminTextField(
                label = stringResource(Res.string.admin_field_email),
                value = state.email,
                onValueChange = { onStateChange(state.copy(email = it, dirty = true)) },
                placeholder = stringResource(Res.string.admin_field_email_placeholder),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.weight(1f),
            )
            AdminTextField(
                label = stringResource(Res.string.admin_field_phone),
                value = state.phone,
                onValueChange = { onStateChange(state.copy(phone = it, dirty = true)) },
                placeholder = stringResource(Res.string.admin_field_phone_placeholder),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.weight(1f),
            )
        }
        AdminTextField(
            label = stringResource(Res.string.admin_field_address),
            value = state.address,
            onValueChange = { onStateChange(state.copy(address = it, dirty = true)) },
            singleLine = false,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                AdminSearchableSelect(
                    label = stringResource(Res.string.admin_field_province),
                    valueId = state.provinceId,
                    options = CreateStoreMocks.provinces,
                    onSelect = { onStateChange(state.withProvince(it.id)) },
                    placeholder = stringResource(Res.string.admin_field_province_placeholder),
                    showItemChevron = false,
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                AdminSelectField(
                    label = stringResource(Res.string.admin_field_city),
                    valueId = state.cityId,
                    options = cityOptions,
                    onSelect = { onStateChange(state.copy(cityId = it.id, dirty = true)) },
                    placeholder = stringResource(Res.string.admin_field_city_placeholder),
                    helper = if (state.provinceId == null) {
                        stringResource(Res.string.admin_field_city_helper)
                    } else {
                        null
                    },
                    enabled = state.provinceId != null,
                )
            }
        }
    }
    AdminFormCard(
        title = stringResource(Res.string.admin_card_social),
        subtitle = stringResource(Res.string.admin_card_social_sub),
        icon = painterResource(Res.drawable.ic_social_instagram),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
            state.socialChannels.forEachIndexed { index, channel ->
                key(channel.id) {
                    SocialChannelRow(
                        channel = channel,
                        canMoveUp = index > 0,
                        canMoveDown = index < state.socialChannels.lastIndex,
                        onMove = { delta ->
                            onStateChange(state.moveSocial(index, index + delta))
                        },
                        onHandleChange = { handle ->
                            onStateChange(
                                state.copy(
                                    socialChannels = state.socialChannels.map {
                                        if (it.id == channel.id) it.copy(handle = handle) else it
                                    },
                                    dirty = true,
                                ),
                            )
                        },
                        onRemove = {
                            onStateChange(
                                state.copy(
                                    socialChannels = state.socialChannels.filter { it.id != channel.id },
                                    dirty = true,
                                ),
                            )
                        },
                    )
                }
            }
            val used = state.socialChannels.map { it.kind }.toSet()
            val canAdd = StoreSocialKind.entries.any { it !in used }
            if (canAdd) {
                Text(
                    text = "+ ${stringResource(Res.string.admin_social_add_new)}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable(role = Role.Button) {
                        val next = StoreSocialKind.entries.first { it !in used }
                        onStateChange(
                            state.copy(
                                socialChannels = state.socialChannels + StoreSocialChannel(
                                    id = next.name.lowercase(),
                                    kind = next,
                                ),
                                dirty = true,
                            ),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun SocialChannelRow(
    channel: StoreSocialChannel,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMove: (Int) -> Unit,
    onHandleChange: (String) -> Unit,
    onRemove: () -> Unit,
) {
    val label = when (channel.kind) {
        StoreSocialKind.Instagram -> stringResource(Res.string.admin_field_instagram)
        StoreSocialKind.Telegram -> stringResource(Res.string.admin_field_telegram)
        StoreSocialKind.WhatsApp -> stringResource(Res.string.admin_field_whatsapp)
        StoreSocialKind.Website -> stringResource(Res.string.admin_field_website)
        StoreSocialKind.Email -> stringResource(Res.string.admin_field_email_channel)
    }
    val placeholder = when (channel.kind) {
        StoreSocialKind.Instagram -> stringResource(Res.string.admin_social_placeholder_instagram)
        StoreSocialKind.Telegram -> stringResource(Res.string.admin_social_placeholder_telegram)
        StoreSocialKind.WhatsApp -> stringResource(Res.string.admin_social_placeholder_whatsapp)
        StoreSocialKind.Website -> stringResource(Res.string.admin_social_placeholder_website)
        StoreSocialKind.Email -> stringResource(Res.string.admin_social_placeholder_email)
    }
    val icon = when (channel.kind) {
        StoreSocialKind.Instagram -> Res.drawable.ic_social_instagram
        StoreSocialKind.Telegram -> Res.drawable.ic_social_website
        StoreSocialKind.WhatsApp -> Res.drawable.ic_social_email
        StoreSocialKind.Website -> Res.drawable.ic_social_website
        StoreSocialKind.Email -> Res.drawable.ic_social_email
    }
    val prefix = when (channel.kind) {
        StoreSocialKind.Instagram, StoreSocialKind.Telegram -> "@"
        else -> null
    }
    var drag by remember { mutableFloatStateOf(0f) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(0, drag.toInt()) }
            .clip(RoundedCornerShape(VitranRadius.medium))
            .border(1.dp, AdminTokens.CardBorder, RoundedCornerShape(VitranRadius.medium))
            .padding(VitranSpacing.md),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                Box(
                    modifier = Modifier
                        .size(VitranSize.touchTarget)
                        .pointerInput(canMoveUp, canMoveDown) {
                            detectVerticalDragGestures(
                                onDragEnd = { drag = 0f },
                                onDragCancel = { drag = 0f },
                                onVerticalDrag = { change, amount ->
                                    change.consume()
                                    drag += amount
                                    when {
                                        drag > 56f && canMoveDown -> {
                                            onMove(1)
                                            drag = 0f
                                        }
                                        drag < -56f && canMoveUp -> {
                                            onMove(-1)
                                            drag = 0f
                                        }
                                    }
                                },
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_menu_hamburger),
                        contentDescription = stringResource(Res.string.admin_social_drag_a11y),
                        size = VitranSize.iconSmall,
                        tint = AdminTokens.Helper,
                    )
                }
                VitranIcon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                )
                Text(text = label, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            }
            Text(
                text = stringResource(Res.string.admin_social_remove),
                color = AdminTokens.Helper,
                fontSize = 12.sp,
                modifier = Modifier.clickable(role = Role.Button, onClick = onRemove),
            )
        }
        AdminTextField(
            label = label,
            value = channel.handle,
            onValueChange = onHandleChange,
            prefix = prefix,
            placeholder = placeholder,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateStorePoliciesStep(
    state: CreateStoreFormState,
    onStateChange: (CreateStoreFormState) -> Unit,
) {
    var tab by remember { mutableStateOf(0) }
    AdminFormCard(
        title = stringResource(Res.string.admin_card_policies),
        subtitle = stringResource(Res.string.admin_card_policies_sub),
        icon = painterResource(Res.drawable.ic_policy_about),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
            PolicyModeRow(
                selected = state.policyMode,
                onSelect = { mode ->
                    when (mode) {
                        PolicyInputMode.Manual -> onStateChange(state.copy(policyMode = mode, dirty = true))
                        PolicyInputMode.Template -> onStateChange(state.copy(policyMode = mode, dirty = true))
                        PolicyInputMode.Auto -> {
                            val generated = CreateStoreMocks.autoPolicyCopy(state.storeName)
                            onStateChange(
                                state.copy(
                                    policyMode = mode,
                                    policies = generated.policies,
                                    shipping = generated.shipping,
                                    returns = generated.returns,
                                    policyTemplateId = generated.id,
                                    dirty = true,
                                ),
                            )
                        }
                    }
                },
            )
            if (state.policyMode == PolicyInputMode.Template) {
                Text(
                    text = stringResource(Res.string.admin_policy_templates_label),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                ) {
                    CreateStoreMocks.policyTemplates.forEach { template ->
                        val selected = state.policyTemplateId == template.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(VitranRadius.small))
                                .background(
                                    if (selected) state.theme.primary.copy(alpha = 0.12f)
                                    else AdminTokens.DropdownHover,
                                )
                                .border(
                                    1.dp,
                                    if (selected) state.theme.primary else AdminTokens.FieldBorder,
                                    RoundedCornerShape(VitranRadius.small),
                                )
                                .clickable(role = Role.Button) {
                                    onStateChange(
                                        state.copy(
                                            policyTemplateId = template.id,
                                            policies = template.policies,
                                            shipping = template.shipping,
                                            returns = template.returns,
                                            dirty = true,
                                        ),
                                    )
                                }
                                .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
                        ) {
                            Text(
                                text = template.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selected) state.theme.primary else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(VitranRadius.medium))
                .background(AdminTokens.DropdownHover)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            listOf(
                stringResource(Res.string.admin_tab_policies),
                stringResource(Res.string.admin_tab_shipping),
                stringResource(Res.string.admin_tab_returns),
            ).forEachIndexed { index, title ->
                val selected = tab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(VitranRadius.small))
                        .background(if (selected) MaterialTheme.colorScheme.surface else AdminTokens.DropdownHover)
                        .clickable(role = Role.Button) { tab = index },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = title,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = if (selected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            AdminTokens.Helper
                        },
                    )
                }
            }
        }
        val editorReadOnly = state.policyMode != PolicyInputMode.Manual
        when (tab) {
            0 -> AdminMultilineField(
                label = stringResource(Res.string.admin_field_policies),
                value = state.policies,
                onValueChange = {
                    if (!editorReadOnly) onStateChange(state.copy(policies = it, dirty = true))
                },
                showToolbar = state.policyMode == PolicyInputMode.Manual,
            )
            1 -> AdminMultilineField(
                label = stringResource(Res.string.admin_field_shipping),
                value = state.shipping,
                onValueChange = {
                    if (!editorReadOnly) onStateChange(state.copy(shipping = it, dirty = true))
                },
            )
            else -> AdminMultilineField(
                label = stringResource(Res.string.admin_field_returns),
                value = state.returns,
                onValueChange = {
                    if (!editorReadOnly) onStateChange(state.copy(returns = it, dirty = true))
                },
            )
        }
    }
}

@Composable
private fun PolicyModeRow(
    selected: PolicyInputMode,
    onSelect: (PolicyInputMode) -> Unit,
) {
    val options = listOf(
        PolicyInputMode.Manual to stringResource(Res.string.admin_policy_mode_manual),
        PolicyInputMode.Template to stringResource(Res.string.admin_policy_mode_template),
        PolicyInputMode.Auto to stringResource(Res.string.admin_policy_mode_auto),
    )
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        options.forEach { (mode, label) ->
            val isOn = selected == mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(VitranRadius.small))
                    .clickable(role = Role.Button) { onSelect(mode) }
                    .padding(vertical = VitranSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .border(
                            1.5.dp,
                            if (isOn) MaterialTheme.colorScheme.onSurface else AdminTokens.FieldBorder,
                            CircleShape,
                        )
                        .padding(3.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isOn) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface),
                        )
                    }
                }
                Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun CreateStorePublishStep(
    state: CreateStoreFormState,
    onViewStore: () -> Unit,
    onPublish: () -> Unit,
    onAddProduct: () -> Unit,
) {
    val ready = state.canPublish
    val name = state.storeName.ifBlank {
        stringResource(Res.string.admin_preview_store_fallback)
    }
    AdminFormCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            Text(
                text = stringResource(
                    if (ready) Res.string.admin_publish_ready_title else Res.string.admin_publish_not_ready_title,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                ),
            )
            Text(
                text = name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )
            if (state.shareUrl.isNotBlank()) {
                Text(
                    text = state.shareUrl,
                    color = AdminTokens.Helper,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Text(
                text = stringResource(Res.string.admin_summary_status),
                color = AdminTokens.Helper,
                fontSize = 12.sp,
            )
            PublishCheck(stringResource(Res.string.admin_publish_check_info), state.basicsComplete())
            PublishCheck(stringResource(Res.string.admin_publish_check_brand), state.brandComplete())
            PublishCheck(stringResource(Res.string.admin_publish_check_contact), state.contactComplete())
            PublishCheck(stringResource(Res.string.admin_publish_check_policies), state.policiesComplete())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(AdminTokens.SaveHeight)
                        .clip(RoundedCornerShape(VitranRadius.small))
                        .border(1.dp, AdminTokens.FieldBorder, RoundedCornerShape(VitranRadius.small))
                        .clickable(enabled = state.slug.isNotBlank(), role = Role.Button, onClick = onViewStore),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.admin_publish_view),
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1.15f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(VitranRadius.small))
                        .background(
                            if (ready && !state.published) state.theme.primary else AdminTokens.FieldBorder,
                        )
                        .clickable(enabled = ready && !state.published, role = Role.Button, onClick = onPublish),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(
                            if (state.published) Res.string.admin_publish_done else Res.string.admin_create_store_publish,
                        ),
                        color = if (ready && !state.published) state.theme.onPrimary else MaterialTheme.colorScheme.surface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
    AdminEmptyPromptCard(
        title = stringResource(Res.string.admin_empty_products_title),
        body = stringResource(Res.string.admin_empty_products_body),
        actionLabel = stringResource(Res.string.admin_empty_products_action),
        emoji = "📦",
        emphasized = false,
        onAction = onAddProduct,
    )
}

@Composable
private fun PublishCheck(
    label: String,
    done: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (done) AdminTokens.Success else AdminTokens.DropdownHover),
            contentAlignment = Alignment.Center,
        ) {
            if (done) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_check),
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                    tint = MaterialTheme.colorScheme.surface,
                )
            }
        }
        Text(
            text = label,
            color = if (done) MaterialTheme.colorScheme.onSurface else AdminTokens.Helper,
            fontSize = 14.sp,
        )
    }
}
