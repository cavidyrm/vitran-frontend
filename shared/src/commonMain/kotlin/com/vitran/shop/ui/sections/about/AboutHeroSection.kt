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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.about_breadcrumb_current
import vitranshop.shared.generated.resources.about_breadcrumb_home
import vitranshop.shared.generated.resources.about_hero_body
import vitranshop.shared.generated.resources.about_hero_cta
import vitranshop.shared.generated.resources.about_hero_image_a11y
import vitranshop.shared.generated.resources.about_hero_subtitle
import vitranshop.shared.generated.resources.about_hero_title
import vitranshop.shared.generated.resources.about_hero_trust_title
import vitranshop.shared.generated.resources.ic_arrow_right
import vitranshop.shared.generated.resources.ic_people

/**
 * About Us hero: breadcrumb, title/CTA copy, and rounded image with floating trust card.
 * Two columns from [VitranSize.mdBreakpoint]; stacked below.
 */
@Composable
fun AboutHeroSection(
    onStoryClick: () -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val twoColumn = maxWidth >= VitranSize.mdBreakpoint
        if (twoColumn) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xxxl),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AboutHeroCopy(
                    onStoryClick = onStoryClick,
                    onHomeClick = onHomeClick,
                    modifier = Modifier.weight(1f),
                )
                AboutHeroMedia(modifier = Modifier.weight(1f))
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.xxl),
            ) {
                AboutHeroCopy(
                    onStoryClick = onStoryClick,
                    onHomeClick = onHomeClick,
                    modifier = Modifier.fillMaxWidth(),
                )
                AboutHeroMedia(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun AboutHeroCopy(
    onStoryClick: () -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Column(modifier = modifier) {
        AboutBreadcrumb(onHomeClick = onHomeClick)
        Spacer(modifier = Modifier.height(VitranSpacing.xl))
        Text(
            text = stringResource(Res.string.about_hero_title),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                lineHeight = 44.sp,
            ),
            color = ShopPurple,
        )
        Spacer(modifier = Modifier.height(VitranSpacing.md))
        Text(
            text = stringResource(Res.string.about_hero_subtitle),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(VitranSpacing.md))
        Text(
            text = stringResource(Res.string.about_hero_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(VitranSpacing.xxl))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(VitranRadius.medium))
                .background(ShopPurple)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClick = onStoryClick,
                )
                .padding(horizontal = VitranSpacing.xl, vertical = VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.about_hero_cta),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White,
            )
            VitranIcon(
                painter = painterResource(Res.drawable.ic_arrow_right),
                contentDescription = null,
                tint = Color.White,
                size = VitranSize.iconSmall,
                modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
            )
        }
    }
}

@Composable
private fun AboutBreadcrumb(onHomeClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        Text(
            text = stringResource(Res.string.about_breadcrumb_home),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onHomeClick,
            ),
        )
        Text(
            text = "›",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(Res.string.about_breadcrumb_current),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun AboutHeroMedia(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        AsyncImage(
            model = AboutMockImages.Hero,
            contentDescription = stringResource(Res.string.about_hero_image_a11y),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(RoundedCornerShape(AboutTokens.ImageRadius))
                .background(AboutTokens.SoftSurface),
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = -VitranSpacing.lg, y = VitranSpacing.lg)
                .widthIn(max = 260.dp)
                .shadow(
                    elevation = VitranElevation.medium,
                    shape = RoundedCornerShape(VitranRadius.medium),
                    ambientColor = Color.Black.copy(alpha = AboutTokens.CardShadowAlpha),
                    spotColor = Color.Black.copy(alpha = AboutTokens.CardShadowAlpha),
                )
                .clip(RoundedCornerShape(VitranRadius.medium))
                .background(MaterialTheme.colorScheme.surface)
                .padding(VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AboutTokens.SoftBar),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_people),
                    contentDescription = null,
                    tint = ShopPurple,
                    size = VitranSize.iconMedium,
                )
            }
            Text(
                text = stringResource(Res.string.about_hero_trust_title),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}
