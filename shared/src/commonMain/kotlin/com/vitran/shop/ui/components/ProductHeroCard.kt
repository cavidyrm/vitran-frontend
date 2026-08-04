package com.vitran.shop.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_star_filled
import vitranshop.shared.generated.resources.product_hero_card_a11y
import kotlin.math.min
import kotlin.math.roundToInt

/** shop.app product-hero-card border (`#EFEFEF`). */
private val ProductHeroBorder = Color(0xFFEFEFEF)

/** shop.app `--icon-stars` on hero product cards. */
private val ProductHeroStarColor = Color(0xFFE3BE2B)

/** shop.app caption / card copy color (pure black). */
private val ProductHeroTextColor = Color(0xFF000000)

/**
 * shop.app `.hero-card > * { font-size: min(6.6cqw, 12px) }`.
 * Container width ≈ card width, so em tracks card width with a 12.dp cap.
 */
private const val ProductHeroEmFraction = 0.066f
private val ProductHeroEmMax = 12.dp

private val ProductHeroDefaultWidth = 180.dp
private val ProductHeroMaxWidth = 240.dp

/** shop.app `.text-captionMedium { letter-spacing: -0.2px }`. */
private val ProductHeroLetterSpacing = (-0.2).sp

/**
 * shop.app default `--mouse-x/-y: -1` so the card face rests with a visible 3D tilt
 * (`rotateX(10deg) rotateY(-10deg)`).
 */
private const val ProductHeroMouseRest = -1f

/**
 * Soft shadow fill — slightly lighter than shop.app `opacity: .1`.
 * Kept at a fixed strength in every interaction / stage state.
 */
private const val ProductHeroShadowAlpha = 0.06f

/**
 * Floating product card used in the desktop Home hero collage.
 *
 * Soft shadow matches shop.app `.hero-card-shadow:after` (`blur 1.5em`, low black
 * opacity). Shadow stays flat and at a **constant** alpha — stage opacity / hover
 * scale / press scale apply only to the card face.
 */
@Composable
fun ProductHeroCard(
    title: String,
    rating: Float,
    reviewCountLabel: String,
    modifier: Modifier = Modifier,
    /** Collage Y-flip degrees applied to the face only (not the soft shadow). */
    stageRotationY: Float = 0f,
    /** Collage fade applied to the face only (shadow strength stays constant). */
    stageOpacity: Float = 1f,
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
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    val interactionScale by animateFloatAsState(
        targetValue = when {
            pressed -> VitranAnimation.HeroCollage.CARD_PRESS_SCALE
            hovered -> VitranAnimation.HeroCollage.CARD_HOVER_SCALE
            else -> 1f
        },
        animationSpec = tween(
            durationMillis = VitranAnimation.HeroCollage.CARD_INTERACTION_MS,
            easing = VitranAnimation.HeroCollage.CardInteractionEasing,
        ),
        label = "productHeroInteractionScale",
    )

    var mouseX by remember { mutableFloatStateOf(ProductHeroMouseRest) }
    var mouseY by remember { mutableFloatStateOf(ProductHeroMouseRest) }
    val faceOpacity = stageOpacity.coerceIn(0f, 1f)

    BoxWithConstraints(modifier = modifier.widthIn(max = ProductHeroMaxWidth)) {
        val em = minOf(maxWidth * ProductHeroEmFraction, ProductHeroEmMax)
        val cardShape = RoundedCornerShape(em * 2)
        val imageShape = RoundedCornerShape(em)
        val blurRadius = em * 1.5f
        val density = LocalDensity.current
        val emPx = with(density) { em.toPx() }
        val blurPx = with(density) { blurRadius.toPx() }
        val cameraDistancePx = 18f * density.density
        val captionStyle = rememberProductHeroCaptionStyle(em)
        val tiltX = mouseY * -VitranAnimation.HeroCollage.CARD_TILT_DEG
        val tiltY = mouseX * VitranAnimation.HeroCollage.CARD_TILT_DEG
        // Rest-pose shadow offset (`mouse = -1` → translate +3em, +3em). Constant.
        val shadowShiftX =
            ProductHeroMouseRest * -VitranAnimation.HeroCollage.CARD_SHADOW_TRANSLATE_EM * emPx
        val shadowShiftY =
            ProductHeroMouseRest * -VitranAnimation.HeroCollage.CARD_SHADOW_TRANSLATE_EM * emPx

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .hoverable(interactionSource = interactionSource)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            when (event.type) {
                                PointerEventType.Move,
                                PointerEventType.Enter,
                                -> {
                                    val pos = event.changes.firstOrNull()?.position ?: continue
                                    val w = size.width.coerceAtLeast(1).toFloat()
                                    val h = size.height.coerceAtLeast(1).toFloat()
                                    mouseX = ((pos.x / w) - 0.5f) * 2f
                                    mouseY = ((pos.y / h) - 0.5f) * 2f
                                }
                                PointerEventType.Exit -> {
                                    mouseX = ProductHeroMouseRest
                                    mouseY = ProductHeroMouseRest
                                }
                            }
                        }
                    }
                },
        ) {
            // Fixed soft shadow — same strength in every state (no scale / stage fade).
            if (faceOpacity > 0f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            translationX = shadowShiftX
                            translationY = shadowShiftY
                            clip = false
                        }
                        .productHeroShadowBlurRoom(blurPx)
                        .blur(blurRadius)
                        .padding(blurRadius)
                        .background(Color.Black.copy(alpha = ProductHeroShadowAlpha), cardShape),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = interactionScale
                        scaleY = interactionScale
                        alpha = faceOpacity
                        rotationX = tiltX
                        rotationY = tiltY + stageRotationY
                        cameraDistance = cameraDistancePx
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                        clip = false
                    }
                    .clip(cardShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = VitranSize.borderHairline,
                        color = ProductHeroBorder,
                        shape = cardShape,
                    )
                    .then(
                        if (onClick != null) {
                            Modifier.clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onClick,
                            )
                        } else {
                            Modifier
                        },
                    )
                    .semantics { contentDescription = a11y }
                    .padding(em),
                verticalArrangement = Arrangement.spacedBy(em * 0.5f),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(imageShape),
                ) {
                    image()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationX = mouseX *
                                    size.width *
                                    VitranAnimation.HeroCollage.CARD_GRADIENT_TRANSLATE_FRACTION
                                translationY = mouseY *
                                    size.height *
                                    VitranAnimation.HeroCollage.CARD_GRADIENT_TRANSLATE_FRACTION
                            }
                            .drawBehind {
                                val radius = min(size.width, size.height)
                                drawRect(
                                    brush = Brush.radialGradient(
                                        colorStops = arrayOf(
                                            0f to Color.White.copy(alpha = 0.075f),
                                            0.7f to Color.Transparent,
                                        ),
                                        center = Offset(size.width / 2f, size.height / 2f),
                                        radius = radius,
                                    ),
                                    blendMode = BlendMode.Plus,
                                )
                            },
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = em * 0.125f),
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        color = ProductHeroTextColor,
                        style = captionStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(em * 0.25f),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            repeat(filledStars) {
                                VitranIcon(
                                    painter = painterResource(Res.drawable.ic_star_filled),
                                    contentDescription = null,
                                    size = em,
                                    tint = ProductHeroStarColor,
                                )
                            }
                        }
                        Text(
                            text = reviewCountLabel,
                            color = ProductHeroTextColor,
                            style = captionStyle,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

/** Room around the card so `Modifier.blur(1.5em)` is not clipped to the card bounds. */
private fun Modifier.productHeroShadowBlurRoom(blurPx: Float): Modifier =
    layout { measurable, constraints ->
        val blur = blurPx.roundToInt().coerceAtLeast(0)
        val placeable = measurable.measure(
            Constraints(
                minWidth = (constraints.minWidth + blur * 2).coerceAtLeast(0),
                maxWidth = if (constraints.hasBoundedWidth) {
                    constraints.maxWidth + blur * 2
                } else {
                    Constraints.Infinity
                },
                minHeight = (constraints.minHeight + blur * 2).coerceAtLeast(0),
                maxHeight = if (constraints.hasBoundedHeight) {
                    constraints.maxHeight + blur * 2
                } else {
                    Constraints.Infinity
                },
            ),
        )
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(-blur, -blur)
        }
    }

@Composable
private fun rememberProductHeroCaptionStyle(em: Dp): TextStyle {
    val density = LocalDensity.current
    val base = MaterialTheme.typography.labelMedium
    return remember(em, base, density) {
        with(density) {
            base.copy(
                fontWeight = FontWeight.Medium,
                fontSize = em.toSp(),
                lineHeight = (em * 1.333f).toSp(),
                letterSpacing = ProductHeroLetterSpacing,
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

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB)
@Composable
private fun ProductHeroCardNarrowPreview() {
    VitranTheme {
        Box(modifier = Modifier.padding(VitranSpacing.xl)) {
            ProductHeroCard(
                title = "کلاه وینتیج",
                rating = 5f,
                reviewCountLabel = "(۹۱۳)",
                modifier = Modifier.width(144.dp),
                image = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color(0xFFFFC0CB)),
                    )
                },
            )
        }
    }
}
