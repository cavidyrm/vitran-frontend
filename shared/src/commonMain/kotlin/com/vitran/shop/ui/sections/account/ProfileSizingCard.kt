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
    upperBodySlug: String?,
    lowerBodySlug: String?,
    shoeSlug: String?,
    onUpperBodyChange: (String?) -> Unit,
    onLowerBodyChange: (String?) -> Unit,
    onShoeChange: (String?) -> Unit,
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
                value = sizeLabelForSlug(ShoeSizeOptions, shoeSlug),
                placeholder = unset,
                options = listOf(unset) + ShoeSizeOptions.map { it.first },
                onSelect = { selected ->
                    onShoeChange(
                        selected.takeUnless { it == unset }?.let { sizeSlugForLabel(ShoeSizeOptions, it) },
                    )
                },
            )
            AccountDropdownField(
                label = stringResource(Res.string.account_field_top_size),
                value = sizeLabelForSlug(UpperBodySizeOptions, upperBodySlug),
                placeholder = unset,
                options = listOf(unset) + UpperBodySizeOptions.map { it.first },
                onSelect = { selected ->
                    onUpperBodyChange(
                        selected.takeUnless { it == unset }
                            ?.let { sizeSlugForLabel(UpperBodySizeOptions, it) },
                    )
                },
            )
            AccountDropdownField(
                label = stringResource(Res.string.account_field_bottom_size),
                value = sizeLabelForSlug(LowerBodySizeOptions, lowerBodySlug),
                placeholder = unset,
                options = listOf(unset) + LowerBodySizeOptions.map { it.first },
                onSelect = { selected ->
                    onLowerBodyChange(
                        selected.takeUnless { it == unset }
                            ?.let { sizeSlugForLabel(LowerBodySizeOptions, it) },
                    )
                },
            )
        }
    }
}
