package com.vitran.shop.ui.sections.account.stores

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.LayoutDirection
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.model.ShopPublicationState
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountCard
import com.vitran.shop.ui.sections.account.AccountPrimaryButton
import com.vitran.shop.ui.sections.account.AccountTextLink
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_seller_create
import vitranshop.shared.generated.resources.account_stores_empty
import vitranshop.shared.generated.resources.account_stores_error
import vitranshop.shared.generated.resources.account_stores_loading
import vitranshop.shared.generated.resources.account_stores_loading_more
import vitranshop.shared.generated.resources.account_stores_retry
import vitranshop.shared.generated.resources.account_stores_status_hidden
import vitranshop.shared.generated.resources.account_stores_status_live
import vitranshop.shared.generated.resources.account_stores_status_pending
import vitranshop.shared.generated.resources.account_stores_status_unknown
import vitranshop.shared.generated.resources.ic_chevron_right

@Composable
internal fun AccountStoresLoading(
    modifier: Modifier = Modifier,
    message: String = stringResource(Res.string.account_stores_loading),
) {
    VitranText(
        text = message,
        style = VitranTextStyle.Body,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

@Composable
internal fun AccountStoresEmpty(
    onCreateStore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        VitranText(
            text = stringResource(Res.string.account_stores_empty),
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        AccountPrimaryButton(
            label = stringResource(Res.string.account_seller_create),
            onClick = onCreateStore,
        )
    }
}

@Composable
internal fun AccountStoresError(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        VitranText(
            text = message?.takeIf { it.isNotBlank() }
                ?: stringResource(Res.string.account_stores_error),
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        AccountTextLink(
            label = stringResource(Res.string.account_stores_retry),
            onClick = onRetry,
        )
    }
}

@Composable
internal fun AccountStoresList(
    shops: List<SellerShopSummary>,
    isLoadingMore: Boolean,
    paginationError: String?,
    onShopClick: (SellerShopSummary) -> Unit,
    onRetryPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        AccountCard {
            shops.forEachIndexed { index, shop ->
                AccountStoreRow(
                    shop = shop,
                    onClick = { onShopClick(shop) },
                )
                if (index < shops.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                        thickness = VitranSize.borderHairline,
                        color = AccountTokens.FieldDivider,
                    )
                }
            }
        }
        if (isLoadingMore) {
            VitranText(
                text = stringResource(Res.string.account_stores_loading_more),
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (!paginationError.isNullOrBlank()) {
            AccountStoresError(message = paginationError, onRetry = onRetryPage)
        }
    }
}

@Composable
private fun AccountStoreRow(
    shop: SellerShopSummary,
    onClick: (() -> Unit)?,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            VitranText(
                text = shop.title,
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
            VitranText(
                text = shopPublicationLabel(shop.publicationState),
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
        if (onClick != null) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
            )
        }
    }
}

@Composable
internal fun shopPublicationLabel(state: ShopPublicationState): String =
    stringResource(
        when (state) {
            ShopPublicationState.PendingApproval -> Res.string.account_stores_status_pending
            ShopPublicationState.Live -> Res.string.account_stores_status_live
            ShopPublicationState.ApprovedHidden -> Res.string.account_stores_status_hidden
            ShopPublicationState.Inconsistent -> Res.string.account_stores_status_unknown
        },
    )
