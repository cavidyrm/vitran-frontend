package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_plans_archive_link
import vitranshop.shared.generated.resources.admin_plans_col_actions
import vitranshop.shared.generated.resources.admin_plans_col_analytics
import vitranshop.shared.generated.resources.admin_plans_col_monthly
import vitranshop.shared.generated.resources.admin_plans_col_plan
import vitranshop.shared.generated.resources.admin_plans_col_products
import vitranshop.shared.generated.resources.admin_plans_col_slots
import vitranshop.shared.generated.resources.admin_plans_col_status
import vitranshop.shared.generated.resources.admin_plans_col_yearly
import vitranshop.shared.generated.resources.admin_plans_delete_a11y
import vitranshop.shared.generated.resources.admin_plans_edit_a11y
import vitranshop.shared.generated.resources.admin_plans_list_title
import vitranshop.shared.generated.resources.admin_plans_popular_badge
import vitranshop.shared.generated.resources.admin_plans_table_scroll_hint
import vitranshop.shared.generated.resources.admin_plans_view_a11y
import vitranshop.shared.generated.resources.ic_delete
import vitranshop.shared.generated.resources.ic_edit
import vitranshop.shared.generated.resources.ic_visibility

private val DesktopTableMinWidth = 980.dp
private val ScrollFadeWidth = 28.dp
private val ScrollbarTrackHeight = 6.dp

@Composable
fun AdminPlansTableCard(
    plans: List<AdminPlanDefinition>,
    selectedId: String?,
    onSelect: (AdminPlanDefinition) -> Unit,
    onEdit: (AdminPlanDefinition) -> Unit,
    onDelete: (AdminPlanDefinition) -> Unit,
    onView: (AdminPlanDefinition) -> Unit = onSelect,
    onShowArchive: () -> Unit = {},
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(StorePlanTokens.CardRadius))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, StorePlanTokens.CardBorder, RoundedCornerShape(StorePlanTokens.CardRadius))
            .padding(VitranSpacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.admin_plans_list_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.height(VitranSpacing.md))
        if (compact) {
            plans.forEachIndexed { index, plan ->
                CompactPlanRow(
                    plan = plan,
                    selected = plan.id == selectedId,
                    onSelect = { onSelect(plan) },
                    onEdit = { onEdit(plan) },
                    onDelete = { onDelete(plan) },
                    onView = { onView(plan) },
                )
                if (index != plans.lastIndex) {
                    HorizontalDivider(
                        thickness = VitranSize.borderHairline,
                        color = StorePlanTokens.CardBorder,
                        modifier = Modifier.padding(vertical = VitranSpacing.sm),
                    )
                }
            }
        } else {
            DesktopPlansScrollTable(
                plans = plans,
                selectedId = selectedId,
                onSelect = onSelect,
                onEdit = onEdit,
                onDelete = onDelete,
                onView = onView,
            )
        }
        Spacer(modifier = Modifier.height(VitranSpacing.md))
        Text(
            text = stringResource(Res.string.admin_plans_archive_link),
            color = AdminTokens.Brand,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(role = Role.Button, onClick = onShowArchive)
                .padding(vertical = VitranSpacing.xs),
        )
    }
}

@Composable
private fun DesktopPlansScrollTable(
    plans: List<AdminPlanDefinition>,
    selectedId: String?,
    onSelect: (AdminPlanDefinition) -> Unit,
    onEdit: (AdminPlanDefinition) -> Unit,
    onDelete: (AdminPlanDefinition) -> Unit,
    onView: (AdminPlanDefinition) -> Unit,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val scrollSign = if (isRtl) 1f else -1f
    val dragState = rememberDraggableState { delta ->
        scope.launch {
            scrollState.scrollBy(scrollSign * delta)
        }
    }
    val canScroll = scrollState.maxValue > 0
    val surface = MaterialTheme.colorScheme.surface
    val fadePx = with(LocalDensity.current) { ScrollFadeWidth.toPx() }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (canScroll) {
            Text(
                text = stringResource(Res.string.admin_plans_table_scroll_hint),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = VitranSpacing.sm),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(state = scrollState, enabled = false)
                .draggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                )
                .drawWithContent {
                    drawContent()
                    if (scrollState.maxValue <= 0) return@drawWithContent
                    val showStart = scrollState.canScrollBackward
                    val showEnd = scrollState.canScrollForward
                    if (showStart) {
                        val brush = Brush.horizontalGradient(
                            colors = if (isRtl) {
                                listOf(Color.Transparent, surface)
                            } else {
                                listOf(surface, Color.Transparent)
                            },
                            startX = if (isRtl) size.width - fadePx else 0f,
                            endX = if (isRtl) size.width else fadePx,
                        )
                        drawRect(
                            brush = brush,
                            topLeft = Offset(if (isRtl) size.width - fadePx else 0f, 0f),
                            size = Size(fadePx, size.height),
                        )
                    }
                    if (showEnd) {
                        val brush = Brush.horizontalGradient(
                            colors = if (isRtl) {
                                listOf(surface, Color.Transparent)
                            } else {
                                listOf(Color.Transparent, surface)
                            },
                            startX = if (isRtl) 0f else size.width - fadePx,
                            endX = if (isRtl) fadePx else size.width,
                        )
                        drawRect(
                            brush = brush,
                            topLeft = Offset(if (isRtl) 0f else size.width - fadePx, 0f),
                            size = Size(fadePx, size.height),
                        )
                    }
                },
        ) {
            Column(modifier = Modifier.width(DesktopTableMinWidth)) {
                HeaderRow()
                plans.forEach { plan ->
                    DesktopPlanRow(
                        plan = plan,
                        selected = plan.id == selectedId,
                        onSelect = { onSelect(plan) },
                        onEdit = { onEdit(plan) },
                        onDelete = { onDelete(plan) },
                        onView = { onView(plan) },
                    )
                }
            }
        }
        if (canScroll) {
            Spacer(modifier = Modifier.height(VitranSpacing.sm))
            HorizontalScrollBar(scrollState = scrollState)
        }
    }
}

@Composable
private fun HorizontalScrollBar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(ScrollbarTrackHeight)
            .clip(RoundedCornerShape(999.dp))
            .background(StorePlanTokens.CardBorder.copy(alpha = 0.7f)),
    ) {
        val trackPx = with(LocalDensity.current) { maxWidth.toPx() }
        val maxScroll = scrollState.maxValue.coerceAtLeast(1)
        val contentPx = trackPx + maxScroll
        val thumbFraction = (trackPx / contentPx).coerceIn(0.18f, 1f)
        val thumbWidthPx = trackPx * thumbFraction
        val travelPx = (trackPx - thumbWidthPx).coerceAtLeast(0f)
        val thumbOffsetPx = (scrollState.value.toFloat() / maxScroll) * travelPx

        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffsetPx.roundToInt(), 0) }
                .width(with(LocalDensity.current) { thumbWidthPx.toDp() })
                .fillMaxHeight()
                .clip(RoundedCornerShape(999.dp))
                .background(AdminTokens.Brand.copy(alpha = 0.55f)),
        )
    }
}

@Composable
private fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = VitranSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeaderCell(stringResource(Res.string.admin_plans_col_plan), Modifier.width(200.dp))
        HeaderCell(stringResource(Res.string.admin_plans_col_actions), Modifier.width(120.dp))
        HeaderCell(stringResource(Res.string.admin_plans_col_monthly), Modifier.width(120.dp))
        HeaderCell(stringResource(Res.string.admin_plans_col_yearly), Modifier.width(130.dp))
        HeaderCell(stringResource(Res.string.admin_plans_col_products), Modifier.width(80.dp))
        HeaderCell(stringResource(Res.string.admin_plans_col_slots), Modifier.width(100.dp))
        HeaderCell(stringResource(Res.string.admin_plans_col_analytics), Modifier.width(90.dp))
        HeaderCell(stringResource(Res.string.admin_plans_col_status), Modifier.width(80.dp))
    }
}

@Composable
private fun HeaderCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        modifier = modifier.padding(horizontal = VitranSpacing.xs),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun CompactPlanRow(
    plan: AdminPlanDefinition,
    selected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onView: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    val borderColor = if (selected) AdminPlansTokens.PopularBorder else StorePlanTokens.CardBorder
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (selected) AdminPlansTokens.SoftPurple.copy(alpha = 0.35f)
                else MaterialTheme.colorScheme.surface,
            )
            .border(1.dp, borderColor, shape)
            .clickable(role = Role.Button, onClick = onSelect)
            .padding(VitranSpacing.md),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AdminPlansTokens.SoftPurple),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(plan.icon),
                    contentDescription = null,
                    tint = AdminTokens.Brand,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
                ) {
                    Text(
                        text = plan.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (plan.popular) {
                        Text(
                            text = stringResource(Res.string.admin_plans_popular_badge),
                            color = AdminTokens.Brand,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(AdminPlansTokens.SoftPurple)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
                Text(
                    text = plan.tagline,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            StatusPill(plan.status)
        }
        Text(
            text = "${formatTomanAmount(plan.monthlyPriceToman)} تومان / ماه · ${formatTomanAmount(plan.yearlyPriceToman)} تومان / سال",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        )
        Text(
            text = "${stringResource(Res.string.admin_plans_col_products)}: ${productLimitLabel(plan.productLimit)} · " +
                "${stringResource(Res.string.admin_plans_col_slots)}: ${specialSlotsLabel(plan.specialSlots)} · " +
                "${stringResource(Res.string.admin_plans_col_analytics)}: ${plan.analytics.label()}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
        PlanActionsRow(onView = onView, onEdit = onEdit, onDelete = onDelete)
    }
}

@Composable
private fun DesktopPlanRow(
    plan: AdminPlanDefinition,
    selected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onView: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    val borderColor = if (selected) AdminPlansTokens.PopularBorder else StorePlanTokens.CardBorder
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(shape)
            .background(
                if (selected) AdminPlansTokens.SoftPurple.copy(alpha = 0.35f)
                else MaterialTheme.colorScheme.surface,
            )
            .border(1.dp, borderColor, shape)
            .clickable(role = Role.Button, onClick = onSelect)
            .padding(horizontal = VitranSpacing.sm, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.width(200.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AdminPlansTokens.SoftPurple),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(plan.icon),
                    contentDescription = null,
                    tint = AdminTokens.Brand,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
                ) {
                    Text(
                        text = plan.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (plan.popular) {
                        Text(
                            text = stringResource(Res.string.admin_plans_popular_badge),
                            color = AdminTokens.Brand,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(AdminPlansTokens.SoftPurple)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
                Text(
                    text = plan.tagline,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        PlanActionsRow(
            onView = onView,
            onEdit = onEdit,
            onDelete = onDelete,
            modifier = Modifier.width(120.dp),
        )
        CellText("${formatTomanAmount(plan.monthlyPriceToman)} تومان", Modifier.width(120.dp))
        CellText("${formatTomanAmount(plan.yearlyPriceToman)} تومان", Modifier.width(130.dp))
        CellText(productLimitLabel(plan.productLimit), Modifier.width(80.dp))
        CellText(specialSlotsLabel(plan.specialSlots), Modifier.width(100.dp))
        CellText(plan.analytics.label(), Modifier.width(90.dp))
        Box(modifier = Modifier.width(80.dp), contentAlignment = Alignment.CenterStart) {
            StatusPill(plan.status)
        }
    }
}

@Composable
private fun PlanActionsRow(
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ActionIcon(
            painter = Res.drawable.ic_visibility,
            tint = AdminTokens.Brand,
            a11y = stringResource(Res.string.admin_plans_view_a11y),
            onClick = onView,
        )
        ActionIcon(
            painter = Res.drawable.ic_edit,
            tint = AdminTokens.Brand,
            a11y = stringResource(Res.string.admin_plans_edit_a11y),
            onClick = onEdit,
        )
        ActionIcon(
            painter = Res.drawable.ic_delete,
            tint = StorePlanTokens.FailedBadgeText,
            a11y = stringResource(Res.string.admin_plans_delete_a11y),
            onClick = onDelete,
        )
    }
}

@Composable
private fun CellText(text: String, modifier: Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier.padding(horizontal = VitranSpacing.xs),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun StatusPill(status: AdminPlanStatus) {
    val bg = if (status == AdminPlanStatus.Active) {
        StorePlanTokens.ActiveBadgeBg
    } else {
        StorePlanTokens.CardBorder
    }
    val fg = if (status == AdminPlanStatus.Active) {
        StorePlanTokens.ActiveBadgeText
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = status.label(),
        color = fg,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
private fun ActionIcon(
    painter: DrawableResource,
    tint: androidx.compose.ui.graphics.Color,
    a11y: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(painter),
            contentDescription = a11y,
            tint = tint,
            modifier = Modifier.size(18.dp),
        )
    }
}
