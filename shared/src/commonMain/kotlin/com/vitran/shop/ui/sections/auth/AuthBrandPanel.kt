package com.vitran.shop.ui.sections.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.auth_brand_body
import vitranshop.shared.generated.resources.auth_brand_headline
import vitranshop.shared.generated.resources.auth_brand_illustration_a11y
import vitranshop.shared.generated.resources.auth_logo_a11y
import vitranshop.shared.generated.resources.auth_shopping_bags
import vitranshop.shared.generated.resources.ic_shop_logo
import vitranshop.shared.generated.resources.ic_vitran_wordmark

private val BrandCopyMaxWidth = 300.dp
private val BrandIllustrationSize = 260.dp
private val BrandPanelPadding = 52.dp

/**
 * Brand panel: gradient atmosphere, logo, shopping-bags illustration, pitch.
 * Shown from [VitranSize.mdBreakpoint] up via [AuthSplitShell].
 */
@Composable
fun AuthBrandPanel(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6B4EFF),
                        AuthTokens.BrandGradientStart,
                        AuthTokens.BrandGradientEnd,
                    ),
                ),
            ),
    ) {
        AuthBrandAtmosphere(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(BrandPanelPadding),
        ) {
            BrandTopLogo()

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart,
            ) {
                Column(
                    modifier = Modifier.widthIn(max = BrandCopyMaxWidth),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.auth_shopping_bags),
                        contentDescription = stringResource(Res.string.auth_brand_illustration_a11y),
                        modifier = Modifier.size(BrandIllustrationSize),
                        contentScale = ContentScale.Fit,
                    )
                    Text(
                        text = stringResource(Res.string.auth_brand_headline),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 48.sp,
                            letterSpacing = (-0.4).sp,
                        ),
                    )
                    Text(
                        text = stringResource(Res.string.auth_brand_body),
                        color = Color.White.copy(alpha = 0.84f),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun BrandTopLogo() {
    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
        Image(
            painter = painterResource(Res.drawable.ic_shop_logo),
            contentDescription = stringResource(Res.string.auth_logo_a11y),
            modifier = Modifier.size(36.dp),
            colorFilter = ColorFilter.tint(Color.White),
        )
        Image(
            painter = painterResource(Res.drawable.ic_vitran_wordmark),
            contentDescription = null,
            modifier = Modifier.height(22.dp),
            colorFilter = ColorFilter.tint(Color.White),
        )
    }
}

@Composable
private fun AuthBrandAtmosphere(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.08f), Color.Transparent),
                center = Offset(w * 0.85f, h * 0.12f),
                radius = w * 0.55f,
            ),
            radius = w * 0.55f,
            center = Offset(w * 0.85f, h * 0.12f),
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent),
                center = Offset(w * 0.05f, h * 0.75f),
                radius = w * 0.45f,
            ),
            radius = w * 0.45f,
            center = Offset(w * 0.05f, h * 0.75f),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.03f),
            radius = w * 0.18f,
            center = Offset(w * 0.72f, h * 0.62f),
        )
    }
}
