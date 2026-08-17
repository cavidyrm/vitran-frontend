package com.vitran.shop.ui.sections.account.users

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountSelectMenu
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_users_page_a11y
import vitranshop.shared.generated.resources.account_users_page_next_a11y
import vitranshop.shared.generated.resources.account_users_page_prev_a11y
import vitranshop.shared.generated.resources.account_users_per_page
import vitranshop.shared.generated.resources.account_users_summary
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ic_unfold
import kotlin.math.max

private sealed interface PageItem {
    data class Number(val page: Int) : PageItem
    data object Ellipsis : PageItem
}

@Composable
internal fun AccountUsersPagination(
    totalCount: Int,
    page: Int,
    pageSize: Int,
    onPageChange: (Int) -> Unit,
    onPageSizeChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    val pageCount = max(1, (totalCount + pageSize - 1) / pageSize)
    val safePage = page.coerceIn(1, pageCount)
    val from = if (totalCount == 0) 0 else ((safePage - 1) * pageSize) + 1
    val to = if (totalCount == 0) 0 else minOf(safePage * pageSize, totalCount)
    val summary = stringResource(
        Res.string.account_users_summary,
        toPersianDigits(from),
        toPersianDigits(to),
        toPersianDigits(totalCount),
    )
    val pageSizeTen = stringResource(
        Res.string.account_users_per_page,
        toPersianDigits(10),
    )
    val pageSizeTwenty = stringResource(
        Res.string.account_users_per_page,
        toPersianDigits(20),
    )
    val pageSizeFifty = stringResource(
        Res.string.account_users_per_page,
        toPersianDigits(50),
    )
    val pageSizeOptions = listOf(
        10 to pageSizeTen,
        20 to pageSizeTwenty,
        50 to pageSizeFifty,
    )
    val selectedSizeLabel = pageSizeOptions.firstOrNull { it.first == pageSize }?.second
        ?: stringResource(Res.string.account_users_per_page, toPersianDigits(pageSize))

    if (isDesktop) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            AccountUsersPageSizeSelect(
                selectedLabel = selectedSizeLabel,
                options = pageSizeOptions.map { it.second },
                onSelect = { label ->
                    val size = pageSizeOptions.firstOrNull { it.second == label }?.first ?: pageSize
                    onPageSizeChange(size)
                },
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                VitranText(
                    text = summary,
                    style = VitranTextStyle.Body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false,
                )
            }
            AccountUsersPageButtons(
                page = safePage,
                pageCount = pageCount,
                onPageChange = onPageChange,
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VitranText(
                text = summary,
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
            )
            AccountUsersPageButtons(
                page = safePage,
                pageCount = pageCount,
                onPageChange = onPageChange,
            )
            AccountUsersPageSizeSelect(
                selectedLabel = selectedSizeLabel,
                options = pageSizeOptions.map { it.second },
                onSelect = { label ->
                    val size = pageSizeOptions.firstOrNull { it.second == label }?.first ?: pageSize
                    onPageSizeChange(size)
                },
            )
        }
    }
}

@Composable
private fun AccountUsersPageSizeSelect(
    selectedLabel: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    var fieldWidthPx by remember { mutableIntStateOf(0) }
    var fieldHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val shape = RoundedCornerShape(VitranRadius.small)
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .widthIn(min = 128.dp)
            .height(AccountTokens.StackedFieldHeight)
            .onSizeChanged { size ->
                fieldWidthPx = size.width
                fieldHeightPx = size.height
            }
            .clip(shape)
            .border(1.dp, AccountTokens.CardBorder, shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .clickable(role = Role.Button) { open = true }
            .padding(horizontal = VitranSpacing.md),
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            VitranText(
                text = selectedLabel,
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false,
            )
            VitranIcon(
                painter = painterResource(Res.drawable.ic_unfold),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        AccountSelectMenu(
            expanded = open,
            onDismiss = { open = false },
            options = options,
            selected = selectedLabel,
            onSelect = {
                onSelect(it)
                open = false
            },
            menuWidth = with(density) {
                if (fieldWidthPx > 0) fieldWidthPx.toDp() else 0.dp
            },
            anchorHeightPx = fieldHeightPx,
        )
    }
}

@Composable
private fun AccountUsersPageButtons(
    page: Int,
    pageCount: Int,
    onPageChange: (Int) -> Unit,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Row(
        modifier = Modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        AccountUsersPageArrow(
            enabled = page > 1,
            contentDescription = stringResource(Res.string.account_users_page_prev_a11y),
            flip = !isRtl,
            onClick = { onPageChange(page - 1) },
        )
        pageItems(page, pageCount).forEach { item ->
            when (item) {
                is PageItem.Ellipsis -> {
                    VitranText(
                        text = "…",
                        style = VitranTextStyle.Body,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = VitranSpacing.sm),
                    )
                }
                is PageItem.Number -> {
                    AccountUsersPageNumber(
                        page = item.page,
                        selected = item.page == page,
                        onClick = { onPageChange(item.page) },
                    )
                }
            }
        }
        AccountUsersPageArrow(
            enabled = page < pageCount,
            contentDescription = stringResource(Res.string.account_users_page_next_a11y),
            flip = isRtl,
            onClick = { onPageChange(page + 1) },
        )
    }
}

@Composable
private fun AccountUsersPageArrow(
    enabled: Boolean,
    contentDescription: String,
    flip: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(VitranRadius.small)
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(shape)
            .border(1.dp, AccountTokens.CardBorder, shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_chevron_right),
            contentDescription = contentDescription,
            size = VitranSize.iconSmall,
            tint = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.graphicsLayer { scaleX = if (flip) -1f else 1f },
        )
    }
}

@Composable
private fun AccountUsersPageNumber(
    page: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(VitranRadius.small)
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(shape)
            .then(
                if (selected) {
                    Modifier.background(bg, shape)
                } else {
                    Modifier
                        .border(1.dp, AccountTokens.CardBorder, shape)
                        .background(bg, shape)
                },
            )
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Text(
            text = toPersianDigits(page),
            color = fg,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            fontSize = 13.sp,
        )
    }
}

private fun pageItems(current: Int, total: Int): List<PageItem> {
    if (total <= 5) return (1..total).map { PageItem.Number(it) }
    val items = mutableListOf<PageItem>()
    val windowStart = max(2, current - 1)
    val windowEnd = minOf(total - 1, current + 1)
    items += PageItem.Number(1)
    if (windowStart > 2) items += PageItem.Ellipsis
    for (p in windowStart..windowEnd) {
        items += PageItem.Number(p)
    }
    if (windowEnd < total - 1) items += PageItem.Ellipsis
    items += PageItem.Number(total)
    return items
}
