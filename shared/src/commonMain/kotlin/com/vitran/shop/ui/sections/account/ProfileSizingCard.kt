package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_field_bottom_size
import vitranshop.shared.generated.resources.account_field_shoe_size
import vitranshop.shared.generated.resources.account_field_top_size
import vitranshop.shared.generated.resources.account_section_sizes
import vitranshop.shared.generated.resources.account_section_sizes_hint
import vitranshop.shared.generated.resources.account_size_unset
import vitranshop.shared.generated.resources.ic_ruler

@Composable
internal fun ProfileSizingCard(
    profile: AccountProfile,
    onProfileChange: (AccountProfile) -> Unit,
    modifier: Modifier = Modifier,
) {
    val unset = stringResource(Res.string.account_size_unset)
    AccountCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(VitranSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            AccountSectionHeader(
                title = stringResource(Res.string.account_section_sizes),
                hint = stringResource(Res.string.account_section_sizes_hint),
                icon = painterResource(Res.drawable.ic_ruler),
            )
            AccountDropdownField(
                label = stringResource(Res.string.account_field_shoe_size),
                value = profile.shoeSize.orEmpty(),
                placeholder = unset,
                options = listOf(unset) + ShoeSizeOptions,
                onSelect = { selected ->
                    onProfileChange(
                        profile.copy(shoeSize = selected.takeUnless { it == unset }),
                    )
                },
            )
            AccountDropdownField(
                label = stringResource(Res.string.account_field_top_size),
                value = profile.topSize.orEmpty(),
                placeholder = unset,
                options = listOf(unset) + ClothingSizeOptions,
                onSelect = { selected ->
                    onProfileChange(
                        profile.copy(topSize = selected.takeUnless { it == unset }),
                    )
                },
            )
            AccountDropdownField(
                label = stringResource(Res.string.account_field_bottom_size),
                value = profile.bottomSize.orEmpty(),
                placeholder = unset,
                options = listOf(unset) + ClothingSizeOptions,
                onSelect = { selected ->
                    onProfileChange(
                        profile.copy(bottomSize = selected.takeUnless { it == unset }),
                    )
                },
            )
        }
    }
}
