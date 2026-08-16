package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_seller_become_body
import vitranshop.shared.generated.resources.account_seller_become_title
import vitranshop.shared.generated.resources.account_seller_create
import vitranshop.shared.generated.resources.account_seller_illustration_a11y
import vitranshop.shared.generated.resources.ic_chevron_right
import vitranshop.shared.generated.resources.ill_seller_store

private val SellerTitle = Color(0xFF2A1A5E)
private val SellerCardBg = Color(0xFFFBFBFC)
private val CtaGradientStart = Color(0xFF7B3FF2)
private val CtaGradientEnd = Color(0xFFB07BFF)
private val Sparkle = Color(0xFF8B5CF6)

/**
 * Become-a-seller promo on the account hub — illustration + copy + gradient CTA.
 */
@Composable
internal fun AccountSellerSection(
    onCreateStore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val shape = RoundedCornerShape(AccountTokens.CardRadius)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = AccountTokens.CardElevation,
                shape = shape,
                clip = false,
                ambientColor = AccountTokens.CardShadow,
                spotColor = AccountTokens.CardShadow,
            )
            .clip(shape)
            .background(SellerCardBg, shape)
            .border(1.dp, AccountTokens.CardBorder, shape)
            .padding(VitranSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SellerCopy(modifier = Modifier.weight(1f))
                SellerIllustration(
                    modifier = Modifier
                        .width(AccountTokens.SellerIllustrationWidth)
                        .height(AccountTokens.SellerIllustrationHeight),
                )
            }
        } else {
            SellerIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .align(Alignment.CenterHorizontally),
            )
            SellerCopy(modifier = Modifier.fillMaxWidth())
        }
        SellerCreateButton(
            onClick = onCreateStore,
            isRtl = isRtl,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SellerCopy(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = stringResource(Res.string.account_seller_become_title),
                color = SellerTitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    lineHeight = 30.sp,
                ),
            )
            SellerSparkles(
                modifier = Modifier.padding(start = VitranSpacing.xs, top = 2.dp),
            )
        }
        VitranText(
            text = stringResource(Res.string.account_seller_become_body),
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SellerSparkles(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .width(10.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Sparkle),
        )
        Box(
            modifier = Modifier
                .padding(start = 6.dp)
                .width(7.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Sparkle.copy(alpha = 0.85f)),
        )
        Box(
            modifier = Modifier
                .padding(start = 2.dp)
                .width(5.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Sparkle.copy(alpha = 0.65f)),
        )
    }
}

@Composable
private fun SellerIllustration(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.ill_seller_store),
        contentDescription = stringResource(Res.string.account_seller_illustration_a11y),
        contentScale = ContentScale.Fit,
        modifier = modifier,
    )
}

@Composable
private fun SellerCreateButton(
    onClick: () -> Unit,
    isRtl: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(percent = 50)
    val brush = Brush.horizontalGradient(
        colors = listOf(CtaGradientStart, CtaGradientEnd),
    )
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(brush, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.account_seller_create),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            ),
        )
        VitranIcon(
            painter = painterResource(Res.drawable.ic_chevron_right),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = Color.White,
            modifier = Modifier
                .padding(start = VitranSpacing.sm)
                .graphicsLayer { scaleX = if (isRtl) -1f else 1f },
        )
    }
}
