package com.vitran.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_star_filled
import vitranshop.shared.generated.resources.product_hero_card_a11y
import kotlin.math.roundToInt

/** shop.app product-hero-card border (`#EFEFEF`). */
private val ProductHeroBorder = Color(0xFFEFEFEF)

/** Outer card radius ≈ shop.app `rounded-[2em]` at hero card scale. */
private val ProductHeroCardShape = RoundedCornerShape(VitranRadius.extraLarge)

/** Image radius ≈ shop.app `rounded-[1em]`. */
private val ProductHeroImageShape = RoundedCornerShape(VitranRadius.large)

private val ProductHeroStarSize = 12.dp
private val ProductHeroDefaultWidth = 180.dp
private val ProductHeroMaxWidth = 240.dp
private val ProductHeroContentPadding = 10.dp
private val ProductHeroItemGap = 3.dp

/**
 * Floating product card used in the desktop Home hero collage.
 * Visual match for shop.app `product-hero-card` — static for this phase
 * (no network image, no 3D shadow / idle motion yet).
 */
@Composable
fun ProductHeroCard(
    title: String,
    rating: Float,
    reviewCountLabel: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    image: @Composable BoxScope.() -> Unit = {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
    },
) {
    val filledStars = rating.coerceIn(0f, 5f).roundToInt().coerceIn(0, 5)
    val a11y = stringResource(
        Res.string.product_hero_card_a11y,
        title,
        filledStars.toString(),
        reviewCountLabel,
    )
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .widthIn(max = ProductHeroMaxWidth)
            .shadow(
                elevation = VitranElevation.medium,
                shape = ProductHeroCardShape,
                clip = false,
            )
            .clip(ProductHeroCardShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = VitranSize.borderHairline,
                color = ProductHeroBorder,
                shape = ProductHeroCardShape,
            )
            .then(clickModifier)
            .semantics { contentDescription = a11y }
            .padding(ProductHeroContentPadding),
        verticalArrangement = Arrangement.spacedBy(ProductHeroItemGap),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(ProductHeroImageShape),
            content = image,
        )

        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = VitranSpacing.xs / 2),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = VitranSpacing.xs / 2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(filledStars) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_star_filled),
                        contentDescription = null,
                        size = ProductHeroStarSize,
                        tint = VitranTheme.extraColors.star,
                    )
                }
            }
            Text(
                text = reviewCountLabel,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB)
@Composable
private fun ProductHeroCardPreview() {
    VitranTheme {
        Box(modifier = Modifier.padding(VitranSpacing.xl)) {
            ProductHeroCard(
                title = "ست ملحفه بامبو",
                rating = 5f,
                reviewCountLabel = "(۱۲٫۲ هزار)",
                modifier = Modifier.width(ProductHeroDefaultWidth),
                image = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color(0xFFE8E0D8)),
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB)
@Composable
private fun ProductHeroCardShortReviewsPreview() {
    VitranTheme {
        Box(modifier = Modifier.padding(VitranSpacing.xl)) {
            ProductHeroCard(
                title = "لیوان ارگونومیک ۴۰ اونس",
                rating = 4f,
                reviewCountLabel = "(۱۲)",
                modifier = Modifier.width(ProductHeroDefaultWidth),
            )
        }
    }
}
