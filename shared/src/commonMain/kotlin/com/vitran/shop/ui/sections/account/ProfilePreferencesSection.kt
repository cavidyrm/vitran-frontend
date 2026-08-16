package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.VitranShapes
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_add_pref
import vitranshop.shared.generated.resources.account_field_hair_color
import vitranshop.shared.generated.resources.account_field_hair_type
import vitranshop.shared.generated.resources.account_field_skin_tone
import vitranshop.shared.generated.resources.account_field_skin_type
import vitranshop.shared.generated.resources.account_field_skin_undertone
import vitranshop.shared.generated.resources.account_pref_clothing
import vitranshop.shared.generated.resources.account_pref_hair_care
import vitranshop.shared.generated.resources.account_pref_skin_care
import vitranshop.shared.generated.resources.account_pref_tech
import vitranshop.shared.generated.resources.account_section_prefs
import vitranshop.shared.generated.resources.account_section_prefs_hint
import vitranshop.shared.generated.resources.ic_chevron_down
import vitranshop.shared.generated.resources.ic_droplet
import vitranshop.shared.generated.resources.ic_face
import vitranshop.shared.generated.resources.ic_favorite_outline
import vitranshop.shared.generated.resources.ic_hair
import vitranshop.shared.generated.resources.ic_palette

private const val PrefSkin = "skin"
private const val PrefHair = "hair"
private const val PrefClothing = "clothing"
private const val PrefTech = "tech"

@Composable
internal fun ProfilePreferencesSection(
    profile: AccountProfile,
    onProfileChange: (AccountProfile) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedPrefs by remember { mutableStateOf(setOf(PrefSkin)) }
    var expandedPref by remember { mutableStateOf<AccountPrefField?>(AccountPrefField.SkinType) }
    val skinLabel = stringResource(Res.string.account_pref_skin_care)
    val hairLabel = stringResource(Res.string.account_pref_hair_care)

    fun togglePref(key: String) {
        val next = if (key in selectedPrefs) selectedPrefs - key else selectedPrefs + key
        selectedPrefs = next
        val skinFields = setOf(
            AccountPrefField.SkinType,
            AccountPrefField.SkinUndertone,
            AccountPrefField.SkinTone,
        )
        val hairFields = setOf(AccountPrefField.HairType, AccountPrefField.HairColor)
        val current = expandedPref
        if (key == PrefSkin && PrefSkin !in next && current != null && current in skinFields) {
            expandedPref = null
        }
        if (key == PrefHair && PrefHair !in next && current != null && current in hairFields) {
            expandedPref = null
        }
    }

    AccountCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(
                start = VitranSpacing.lg,
                end = VitranSpacing.lg,
                top = VitranSpacing.lg,
                bottom = VitranSpacing.md,
            ),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            AccountSoftHeader(
                title = stringResource(Res.string.account_section_prefs),
                hint = stringResource(Res.string.account_section_prefs_hint),
                icon = painterResource(Res.drawable.ic_favorite_outline),
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                PreferenceTag(
                    label = stringResource(Res.string.account_add_pref, skinLabel),
                    selected = PrefSkin in selectedPrefs,
                    onClick = { togglePref(PrefSkin) },
                )
                PreferenceTag(
                    label = stringResource(Res.string.account_add_pref, hairLabel),
                    selected = PrefHair in selectedPrefs,
                    onClick = { togglePref(PrefHair) },
                )
                PreferenceTag(
                    label = stringResource(
                        Res.string.account_add_pref,
                        stringResource(Res.string.account_pref_clothing),
                    ),
                    selected = PrefClothing in selectedPrefs,
                    onClick = { togglePref(PrefClothing) },
                )
                PreferenceTag(
                    label = stringResource(
                        Res.string.account_add_pref,
                        stringResource(Res.string.account_pref_tech),
                    ),
                    selected = PrefTech in selectedPrefs,
                    onClick = { togglePref(PrefTech) },
                )
            }
        }
        if (PrefSkin in selectedPrefs) {
            SettingsPrefAccordion(
                label = stringResource(Res.string.account_field_skin_type),
                icon = painterResource(Res.drawable.ic_face),
                expanded = expandedPref == AccountPrefField.SkinType,
                onToggle = {
                    expandedPref = if (expandedPref == AccountPrefField.SkinType) {
                        null
                    } else {
                        AccountPrefField.SkinType
                    }
                },
                options = SkinTypeOptions,
                selected = profile.skinType,
                onSelect = { onProfileChange(profile.copy(skinType = it)) },
                showDivider = true,
            )
            SettingsPrefAccordion(
                label = stringResource(Res.string.account_field_skin_undertone),
                icon = painterResource(Res.drawable.ic_palette),
                expanded = expandedPref == AccountPrefField.SkinUndertone,
                onToggle = {
                    expandedPref = if (expandedPref == AccountPrefField.SkinUndertone) {
                        null
                    } else {
                        AccountPrefField.SkinUndertone
                    }
                },
                options = SkinUndertoneOptions,
                selected = profile.skinUndertone,
                onSelect = { onProfileChange(profile.copy(skinUndertone = it)) },
                showDivider = true,
            )
            SettingsPrefAccordion(
                label = stringResource(Res.string.account_field_skin_tone),
                icon = painterResource(Res.drawable.ic_droplet),
                expanded = expandedPref == AccountPrefField.SkinTone,
                onToggle = {
                    expandedPref = if (expandedPref == AccountPrefField.SkinTone) {
                        null
                    } else {
                        AccountPrefField.SkinTone
                    }
                },
                options = SkinToneOptions,
                selected = profile.skinTone,
                onSelect = { onProfileChange(profile.copy(skinTone = it)) },
                showDivider = PrefHair in selectedPrefs,
            )
        }
        if (PrefHair in selectedPrefs) {
            SettingsPrefAccordion(
                label = stringResource(Res.string.account_field_hair_type),
                icon = painterResource(Res.drawable.ic_hair),
                expanded = expandedPref == AccountPrefField.HairType,
                onToggle = {
                    expandedPref = if (expandedPref == AccountPrefField.HairType) {
                        null
                    } else {
                        AccountPrefField.HairType
                    }
                },
                options = HairTypeOptions,
                selected = profile.hairType,
                onSelect = { onProfileChange(profile.copy(hairType = it)) },
                showDivider = true,
            )
            SettingsPrefAccordion(
                label = stringResource(Res.string.account_field_hair_color),
                icon = painterResource(Res.drawable.ic_droplet),
                expanded = expandedPref == AccountPrefField.HairColor,
                onToggle = {
                    expandedPref = if (expandedPref == AccountPrefField.HairColor) {
                        null
                    } else {
                        AccountPrefField.HairColor
                    }
                },
                options = HairColorOptions,
                selected = profile.hairColor,
                onSelect = { onProfileChange(profile.copy(hairColor = it)) },
                showDivider = false,
            )
        }
    }
}

@Composable
private fun SettingsPrefAccordion(
    label: String,
    icon: Painter,
    expanded: Boolean,
    onToggle: () -> Unit,
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
    showDivider: Boolean,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onToggle)
                .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            AccountSoftIcon(painter = icon)
            VitranText(
                text = label,
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            VitranIcon(
                painter = painterResource(Res.drawable.ic_chevron_down),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.graphicsLayer {
                    rotationZ = if (expanded) 180f else 0f
                },
            )
        }
        if (expanded) {
            AccountChipRow(
                options = options,
                selected = selected,
                onSelect = onSelect,
                accentOutline = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = VitranSpacing.lg,
                        end = VitranSpacing.lg,
                        bottom = VitranSpacing.lg,
                    ),
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                thickness = VitranSize.borderHairline,
                color = AccountTokens.FieldDivider,
            )
        }
    }
}

@Composable
private fun PreferenceTag(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = VitranShapes.pill
    val borderColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .heightIn(min = 40.dp)
            .clip(shape)
            .background(
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape,
            )
            .border(width = 1.dp, color = borderColor, shape = shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        VitranText(
            text = label,
            style = VitranTextStyle.Body,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}
