package com.vitran.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.brand_hero_card_a11y

/** shop.app brand hero-card border (`#EFEFEF`). */
private val BrandHeroBorder = Color(0xFFEFEFEF)

/** Outer radius ≈ shop.app `rounded-[2em]` at hero brand-card scale. */
private val BrandHeroCardShape = RoundedCornerShape(VitranRadius.extraLarge)

private val BrandHeroDefaultWidth = 125.dp
private val BrandHeroMaxWidth = 180.dp

/** Logo width fraction ≈ shop.app `w-[70%]`. */
private const val BrandLogoWidthFraction = 0.70f

/**
 * Floating brand card used in the desktop Home hero collage.
 * Visual match for shop.app square brand `hero-card` — static for this phase
 * (no network image, no 3D shadow / idle / mouse parallax yet).
 */
@Composable
fun BrandHeroCard(
    brandName: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    background: @Composable BoxScope.() -> Unit = {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
    },
    logo: @Composable BoxScope.() -> Unit = {
        DefaultBrandLogoPlaceholder(brandName = brandName)
    },
) {
    val a11y = stringResource(Res.string.brand_hero_card_a11y, brandName)
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .widthIn(max = BrandHeroMaxWidth)
            .aspectRatio(1f)
            .shadow(
                elevation = VitranElevation.medium,
                shape = BrandHeroCardShape,
                clip = false,
            )
            .clip(BrandHeroCardShape)
            .border(
                width = VitranSize.borderHairline,
                color = BrandHeroBorder,
                shape = BrandHeroCardShape,
            )
            .then(clickModifier)
            .semantics { contentDescription = a11y },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(BrandHeroCardShape),
            content = background,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(BrandLogoWidthFraction),
            contentAlignment = Alignment.Center,
            content = logo,
        )
    }
}

@Composable
private fun BoxScope.DefaultBrandLogoPlaceholder(brandName: String) {
    Text(
        text = brandName,
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center)
            .padding(VitranSpacing.xs),
        color = Color.White,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center,
        maxLines = 2,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB)
@Composable
private fun BrandHeroCardPreview() {
    VitranTheme {
        Box(modifier = Modifier.padding(VitranSpacing.xl)) {
            BrandHeroCard(
                brandName = "موجی",
                modifier = Modifier.width(BrandHeroDefaultWidth),
                background = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color(0xFF2C2C2C)),
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB)
@Composable
private fun BrandHeroCardLightPreview() {
    VitranTheme {
        Box(modifier = Modifier.padding(VitranSpacing.xl)) {
            BrandHeroCard(
                brandName = "کاراوی",
                modifier = Modifier.width(BrandHeroDefaultWidth),
                background = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color(0xFFD4C4A8)),
                    )
                },
                logo = {
                    Text(
                        text = "Caraway",
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                    )
                },
            )
        }
    }
}
