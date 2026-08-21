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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource

/**
 * Four equal feature cards in a responsive row (2×2 compact, 4-up from md).
 */
@Composable
fun AboutFeaturesSection(
    features: List<AboutFeatureItem>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val fourUp = maxWidth >= VitranSize.mdBreakpoint
        if (fourUp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            ) {
                features.forEach { item ->
                    AboutFeatureCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            ) {
                features.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                    ) {
                        rowItems.forEach { item ->
                            AboutFeatureCard(
                                item = item,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutFeatureCard(
    item: AboutFeatureItem,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = VitranElevation.small,
                shape = RoundedCornerShape(AboutTokens.CardRadius),
                ambientColor = Color.Black.copy(alpha = AboutTokens.CardShadowAlpha),
                spotColor = Color.Black.copy(alpha = AboutTokens.CardShadowAlpha),
            )
            .clip(RoundedCornerShape(AboutTokens.CardRadius))
            .background(MaterialTheme.colorScheme.surface)
            .padding(VitranSpacing.xl),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(AboutTokens.SoftBar),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(item.icon),
                contentDescription = null,
                tint = ShopPurple,
                size = VitranSize.iconMedium,
            )
        }
        Spacer(modifier = Modifier.height(VitranSpacing.md))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(VitranSpacing.sm))
        Text(
            text = item.body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
