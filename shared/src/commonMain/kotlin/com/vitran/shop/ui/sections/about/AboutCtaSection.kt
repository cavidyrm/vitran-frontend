package com.vitran.shop.ui.sections.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.ShopPurpleDark
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.about_cta_button
import vitranshop.shared.generated.resources.about_cta_subtitle
import vitranshop.shared.generated.resources.about_cta_title
import vitranshop.shared.generated.resources.ic_arrow_right

/**
 * Purple join banner with Get Started CTA.
 */
@Composable
fun AboutCtaSection(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AboutTokens.CardRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(ShopPurple, ShopPurpleDark),
                ),
            )
            .padding(
                horizontal = VitranSpacing.xxl,
                vertical = VitranSpacing.xxxl,
            ),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val sideBySide = maxWidth >= VitranSize.mdBreakpoint
            if (sideBySide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AboutCtaCopy(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.padding(VitranSpacing.lg))
                    AboutCtaButton(onClick = onStartClick, isRtl = isRtl)
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AboutCtaCopy(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(VitranSpacing.xl))
                    AboutCtaButton(onClick = onStartClick, isRtl = isRtl)
                }
            }
        }
    }
}

@Composable
private fun AboutCtaCopy(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.about_cta_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 36.sp,
            ),
            color = Color.White,
        )
        Spacer(modifier = Modifier.height(VitranSpacing.sm))
        Text(
            text = stringResource(Res.string.about_cta_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f),
        )
    }
}

@Composable
private fun AboutCtaButton(
    onClick: () -> Unit,
    isRtl: Boolean,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(VitranRadius.medium))
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = VitranSpacing.xl, vertical = VitranSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.about_cta_button),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = ShopPurple,
        )
        VitranIcon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = null,
            tint = ShopPurple,
            size = VitranSize.iconSmall,
            modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
        )
    }
}
