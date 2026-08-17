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
import com.vitran.shop.ui.sections.account.AccountTextLink
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_cities_add
import vitranshop.shared.generated.resources.account_cities_title
import vitranshop.shared.generated.resources.ic_plus

@Composable
internal fun AccountCityDetailHeader(
    title: String,
    subtitle: String,
    breadcrumbCurrent: String,
    onCitiesClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAddClick: (() -> Unit)? = null,
) {
    val isDesktop = LocalDesktopLayout.current
    if (isDesktop) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            AccountCityTitleBlock(
                title = title,
                subtitle = subtitle,
                breadcrumbCurrent = breadcrumbCurrent,
                onCitiesClick = onCitiesClick,
                modifier = Modifier.weight(1f),
            )
            if (onAddClick != null) {
                AccountPrimaryButton(
                    label = stringResource(Res.string.account_cities_add),
                    onClick = onAddClick,
                    icon = painterResource(Res.drawable.ic_plus),
                )
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            AccountCityTitleBlock(
                title = title,
                subtitle = subtitle,
                breadcrumbCurrent = breadcrumbCurrent,
                onCitiesClick = onCitiesClick,
            )
            if (onAddClick != null) {
                AccountPrimaryButton(
                    label = stringResource(Res.string.account_cities_add),
                    onClick = onAddClick,
                    icon = painterResource(Res.drawable.ic_plus),
                )
            }
        }
    }
}

@Composable
private fun AccountCityTitleBlock(
    title: String,
    subtitle: String,
    breadcrumbCurrent: String,
    onCitiesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            AccountTextLink(
                label = stringResource(Res.string.account_cities_title),
                onClick = onCitiesClick,
            )
            VitranText(
                text = "/",
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            VitranText(
                text = breadcrumbCurrent,
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        VitranText(
            text = title,
            style = VitranTextStyle.Headline,
            color = MaterialTheme.colorScheme.onSurface,
        )
        VitranText(
            text = subtitle,
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
