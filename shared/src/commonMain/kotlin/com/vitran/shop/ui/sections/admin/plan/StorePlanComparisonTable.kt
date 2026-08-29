package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_check
import vitranshop.shared.generated.resources.store_plan_col_features
import vitranshop.shared.generated.resources.store_plan_col_growth
import vitranshop.shared.generated.resources.store_plan_col_professional
import vitranshop.shared.generated.resources.store_plan_col_start
import vitranshop.shared.generated.resources.store_plan_empty_dash

@Composable
fun StorePlanComparisonTable(
    rows: List<StorePlanComparisonRow>,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(StorePlanTokens.CardRadius)
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, StorePlanTokens.CardBorder, shape),
    ) {
        val desktop = maxWidth >= VitranSize.mdBreakpoint
        if (desktop) {
            ComparisonGrid(rows = rows, modifier = Modifier.fillMaxWidth())
        } else {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = VitranSpacing.xs),
            ) {
                ComparisonGrid(
                    rows = rows,
                    modifier = Modifier.width(640.dp),
                )
            }
        }
    }
}

@Composable
private fun ComparisonGrid(
    rows: List<StorePlanComparisonRow>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AdminTokens.NestedPanel)
                .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeaderCell(
                text = stringResource(Res.string.store_plan_col_features),
                modifier = Modifier.weight(1.2f),
                alignStart = true,
            )
            HeaderCell(
                text = stringResource(Res.string.store_plan_col_start),
                modifier = Modifier.weight(1f),
            )
            HeaderCell(
                text = stringResource(Res.string.store_plan_col_growth),
                modifier = Modifier.weight(1f),
            )
            HeaderCell(
                text = stringResource(Res.string.store_plan_col_professional),
                modifier = Modifier.weight(1f),
            )
        }
        rows.forEachIndexed { index, row ->
            if (index > 0) {
                HorizontalDivider(color = StorePlanTokens.CardBorder)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = row.featureLabel,
                    modifier = Modifier.weight(1.2f),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    ),
                )
                ComparisonCellView(cell = row.start, modifier = Modifier.weight(1f))
                ComparisonCellView(cell = row.growth, modifier = Modifier.weight(1f))
                ComparisonCellView(cell = row.professional, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun HeaderCell(
    text: String,
    modifier: Modifier = Modifier,
    alignStart: Boolean = false,
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        ),
        textAlign = if (alignStart) TextAlign.Start else TextAlign.Center,
    )
}

@Composable
private fun ComparisonCellView(
    cell: StorePlanComparisonCell,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when {
            cell.checked -> {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_check),
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                    tint = AdminTokens.Brand,
                )
            }
            cell.empty || cell.text.isNullOrBlank() -> {
                Text(
                    text = stringResource(Res.string.store_plan_empty_dash),
                    color = AdminTokens.Helper,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    textAlign = TextAlign.Center,
                )
            }
            else -> {
                Text(
                    text = cell.text.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
