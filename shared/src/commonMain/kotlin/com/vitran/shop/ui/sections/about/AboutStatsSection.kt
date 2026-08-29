package com.vitran.shop.ui.sections.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource

/**
 * Soft purple stats strip with four metrics (row on md+, 2×2 grid on compact).
 */
@Composable
fun AboutStatsSection(
    stats: List<AboutStatItem>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AboutTokens.CardRadius))
            .background(AboutTokens.SoftBar)
            .padding(
                horizontal = VitranSpacing.xl,
                vertical = VitranSpacing.xxl,
            ),
    ) {
        val fourUp = maxWidth >= VitranSize.mdBreakpoint
        if (fourUp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                stats.forEachIndexed { index, item ->
                    AboutStatCell(
                        item = item,
                        modifier = Modifier.weight(1f),
                    )
                    if (index < stats.lastIndex) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = VitranSpacing.sm)
                                .width(1.dp)
                                .height(48.dp)
                                .background(ShopPurple.copy(alpha = 0.18f)),
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.xl),
            ) {
                stats.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                    ) {
                        rowItems.forEach { item ->
                            AboutStatCell(
                                item = item,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutStatCell(
    item: AboutStatItem,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(item.icon),
                contentDescription = null,
                tint = ShopPurple,
                size = VitranSize.iconMedium,
            )
        }
        Spacer(modifier = Modifier.height(VitranSpacing.sm))
        Text(
            text = item.value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(VitranSpacing.xs))
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
