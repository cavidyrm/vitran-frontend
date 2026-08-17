package com.vitran.shop.ui.sections.account.cities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountPrimaryButton
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_cities_add
import vitranshop.shared.generated.resources.account_cities_subtitle
import vitranshop.shared.generated.resources.account_cities_title
import vitranshop.shared.generated.resources.ic_plus

@Composable
internal fun AccountCitiesHeader(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    if (isDesktop) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            ) {
                AccountCitiesTitleBlock()
            }
            AccountPrimaryButton(
                label = stringResource(Res.string.account_cities_add),
                onClick = onAddClick,
                icon = painterResource(Res.drawable.ic_plus),
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            AccountCitiesTitleBlock()
            AccountPrimaryButton(
                label = stringResource(Res.string.account_cities_add),
                onClick = onAddClick,
                icon = painterResource(Res.drawable.ic_plus),
            )
        }
    }
}

@Composable
private fun AccountCitiesTitleBlock() {
    VitranText(
        text = stringResource(Res.string.account_cities_title),
        style = VitranTextStyle.Headline,
        color = MaterialTheme.colorScheme.onSurface,
    )
    VitranText(
        text = stringResource(Res.string.account_cities_subtitle),
        style = VitranTextStyle.Body,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
