package com.vitran.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.download_app_banner_a11y
import vitranshop.shared.generated.resources.download_app_banner_primary
import vitranshop.shared.generated.resources.download_app_banner_secondary
import vitranshop.shared.generated.resources.ic_arrow_right
import vitranshop.shared.generated.resources.ic_shop_logo

/** shop.app `bg-bg-fill-inverse` for DownloadShopAppBanner. */
private val BannerBackground = Color(0xFF121212)
private val BannerSecondaryText = Color.White.copy(alpha = 0.65f)
private val BrandChipShape = RoundedCornerShape(6.dp)

/**
 * Black install strip matching shop.app `DownloadShopAppBanner`.
 * Mock [onClick] only — no store deep link in this phase.
 */
@Composable
fun DownloadAppBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = stringResource(Res.string.download_app_banner_primary)
    val secondary = stringResource(Res.string.download_app_banner_secondary)
    val a11y = stringResource(Res.string.download_app_banner_a11y)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(VitranSize.downloadBannerHeight)
            .background(BannerBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .semantics { contentDescription = a11y }
            .padding(horizontal = VitranSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(VitranSize.iconMedium)
                .clip(BrandChipShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_shop_logo),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = Color.White,
            )
        }

        Text(
            text = primary,
            modifier = Modifier.padding(start = VitranSpacing.sm),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
        Text(
            text = secondary,
            modifier = Modifier.padding(start = VitranSpacing.xs),
            color = BannerSecondaryText,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
        )

        VitranIcon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = null,
            modifier = Modifier
                .padding(start = VitranSpacing.sm)
                .graphicsLayer { scaleX = if (isRtl) -1f else 1f },
            size = 20.dp,
            tint = BannerSecondaryText,
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun DownloadAppBannerPreview() {
    VitranTheme {
        DownloadAppBanner(onClick = {})
    }
}
