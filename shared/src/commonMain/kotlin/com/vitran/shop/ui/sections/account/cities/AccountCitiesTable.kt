package com.vitran.shop.ui.sections.account.cities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountCard
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_cities_actions_a11y
import vitranshop.shared.generated.resources.account_cities_col_actions
import vitranshop.shared.generated.resources.account_cities_col_city
import vitranshop.shared.generated.resources.account_cities_col_id
import vitranshop.shared.generated.resources.account_cities_col_slug
import vitranshop.shared.generated.resources.account_cities_col_status
import vitranshop.shared.generated.resources.account_cities_empty
import vitranshop.shared.generated.resources.account_cities_status_active
import vitranshop.shared.generated.resources.account_cities_status_inactive
import vitranshop.shared.generated.resources.ic_city
import vitranshop.shared.generated.resources.ic_more_horiz

private const val ColCity = 1.6f
private const val ColSlug = 1.2f
private const val ColId = 0.7f
private const val ColStatus = 0.85f
private const val ColActions = 0.5f

@Composable
internal fun AccountCitiesTable(
    cities: List<AccountCity>,
    onCityClick: (AccountCity) -> Unit,
    onActiveChange: (AccountCity, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    AccountCard(modifier = modifier) {
        if (cities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(VitranSpacing.xxxl),
                contentAlignment = Alignment.Center,
            ) {
                VitranText(
                    text = stringResource(Res.string.account_cities_empty),
                    style = VitranTextStyle.Body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else if (isDesktop) {
            AccountCitiesTableHeader()
            cities.forEach { city ->
                HorizontalDivider(
                    thickness = VitranSize.borderHairline,
                    color = AccountTokens.FieldDivider,
                )
                AccountCitiesTableRow(
                    city = city,
                    onCityClick = onCityClick,
                    onActiveChange = onActiveChange,
                )
            }
        } else {
            cities.forEachIndexed { index, city ->
                AccountCitiesCompactRow(
                    city = city,
                    onCityClick = onCityClick,
                    onActiveChange = onActiveChange,
                )
                if (index != cities.lastIndex) {
                    HorizontalDivider(
                        thickness = VitranSize.borderHairline,
                        color = AccountTokens.FieldDivider,
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountCitiesTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AccountTokens.ImagePlaceholder)
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AccountCitiesHeaderCell(
            label = stringResource(Res.string.account_cities_col_city),
            modifier = Modifier.weight(ColCity),
        )
        AccountCitiesHeaderCell(
            label = stringResource(Res.string.account_cities_col_slug),
            modifier = Modifier.weight(ColSlug),
        )
        AccountCitiesHeaderCell(
            label = stringResource(Res.string.account_cities_col_id),
            modifier = Modifier.weight(ColId),
        )
        AccountCitiesHeaderCell(
            label = stringResource(Res.string.account_cities_col_status),
            modifier = Modifier.weight(ColStatus),
        )
        AccountCitiesHeaderCell(
            label = stringResource(Res.string.account_cities_col_actions),
            modifier = Modifier.weight(ColActions),
        )
    }
}

@Composable
private fun AccountCitiesHeaderCell(
    label: String,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.Text(
        text = label,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        ),
        modifier = modifier,
        maxLines = 1,
    )
}

@Composable
private fun AccountCitiesTableRow(
    city: AccountCity,
    onCityClick: (AccountCity) -> Unit,
    onActiveChange: (AccountCity, Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { onCityClick(city) }
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AccountCitiesIdentity(
            city = city,
            modifier = Modifier.weight(ColCity),
        )
        VitranText(
            text = city.slug,
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            modifier = Modifier.weight(ColSlug),
        )
        VitranText(
            text = toPersianDigits(city.id),
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            modifier = Modifier.weight(ColId),
        )
        Box(
            modifier = Modifier.weight(ColStatus),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                AccountCityActiveSwitch(
                    checked = city.isActive,
                    onCheckedChange = { onActiveChange(city, it) },
                )
                VitranText(
                    text = stringResource(
                        if (city.isActive) {
                            Res.string.account_cities_status_active
                        } else {
                            Res.string.account_cities_status_inactive
                        },
                    ),
                    style = VitranTextStyle.Label,
                    color = if (city.isActive) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    maxLines = 1,
                )
            }
        }
        Box(
            modifier = Modifier.weight(ColActions),
            contentAlignment = Alignment.Center,
        ) {
            AccountCitiesOverflowButton(onClick = { onCityClick(city) })
        }
    }
}

@Composable
private fun AccountCitiesCompactRow(
    city: AccountCity,
    onCityClick: (AccountCity) -> Unit,
    onActiveChange: (AccountCity, Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) { onCityClick(city) }
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        AccountCityGlyph()
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            VitranText(
                text = city.name,
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
            VitranText(
                text = city.slug,
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
            VitranText(
                text = stringResource(Res.string.account_cities_col_id) +
                    " " + toPersianDigits(city.id),
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
        AccountCityActiveSwitch(
            checked = city.isActive,
            onCheckedChange = { onActiveChange(city, it) },
        )
        VitranText(
            text = stringResource(
                if (city.isActive) {
                    Res.string.account_cities_status_active
                } else {
                    Res.string.account_cities_status_inactive
                },
            ),
            style = VitranTextStyle.Label,
            color = if (city.isActive) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 1,
        )
        AccountCitiesOverflowButton(onClick = { onCityClick(city) })
    }
}

@Composable
private fun AccountCitiesIdentity(
    city: AccountCity,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        AccountCityGlyph()
        VitranText(
            text = city.name,
            style = VitranTextStyle.Title,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
    }
}

@Composable
private fun AccountCityGlyph() {
    Box(
        modifier = Modifier
            .size(AccountTokens.UserAvatar)
            .clip(RoundedCornerShape(VitranRadius.small))
            .background(AccountTokens.SoftIconBg),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_city),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun AccountCitiesOverflowButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(VitranSize.touchTarget)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_more_horiz),
            contentDescription = stringResource(Res.string.account_cities_actions_a11y),
            size = VitranSize.iconMedium,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
