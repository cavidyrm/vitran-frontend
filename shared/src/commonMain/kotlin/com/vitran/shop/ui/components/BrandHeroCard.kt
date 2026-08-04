package com.vitran.shop.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.brand_hero_card_a11y
import kotlin.math.roundToInt

/** shop.app brand hero-card border (`#EFEFEF`). */
private val BrandHeroBorder = Color(0xFFEFEFEF)

/**
 * shop.app `.hero-card > * { font-size: min(6.6cqw, 12px) }` — used for `rounded-[2em]`.
 */
private const val BrandHeroEmFraction = 0.066f
private val BrandHeroEmMax = 12.dp

private val BrandHeroDefaultWidth = 125.dp
private val BrandHeroMaxWidth = 180.dp

/** Logo width fraction ≈ shop.app `w-[70%]`. */
private const val BrandLogoWidthFraction = 0.70f

/**
 * Rest pointer for hover tracking. Kept at 0 so the face stays flat — Compose
 * Web/Skia drops rounded clips under rotationX/Y, which looked like sharp crops.
 * Soft shadow still uses a fixed shop.app-style offset below.
 */
private const val BrandHeroMouseRest = 0f

/** Fixed shadow offset equivalent to shop.app `--mouse-x/-y: -1` → `+3em`. */
private const val BrandHeroShadowMouse = -1f

/**
 * Soft shadow fill — same treatment as product cards (slightly lighter than
 * shop.app `opacity: .1`). Constant strength across interaction / stage states.
 */
private const val BrandHeroShadowAlpha = 0.06f

/**
 * Floating brand / shop card used in the desktop Home hero collage.
 *
 * Matches shop.app square brand `hero-card`: soft blur shadow, hover/press scale,
 * and eased 5% background parallax. Face stays orthographic so rounded corners
 * survive on Compose Web (3D tilt crops clips under Skia).
 */
@Composable
fun BrandHeroCard(
    brandName: String,
    modifier: Modifier = Modifier,
    /**
     * Reserved for collage Y-flip. Unused on face for now — any rotationY breaks
     * rounded corners on Compose Web/Skia. Enter/exit uses [stageOpacity] only.
     */
    @Suppress("UNUSED_PARAMETER")
    stageRotationY: Float = 0f,
    /** Collage fade applied to the face only (shadow strength stays constant). */
    stageOpacity: Float = 1f,
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
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    // Soft spring ≈ shop.app `transition-transform duration-200` ease (no snap).
    val interactionScale by animateFloatAsState(
        targetValue = when {
            pressed -> VitranAnimation.HeroCollage.CARD_PRESS_SCALE
            hovered -> VitranAnimation.HeroCollage.CARD_HOVER_SCALE
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "brandHeroInteractionScale",
    )

    var pointerX by remember { mutableFloatStateOf(BrandHeroMouseRest) }
    var pointerY by remember { mutableFloatStateOf(BrandHeroMouseRest) }
    // Ease parallax toward the pointer so enter / move / exit never jump.
    val mouseX by animateFloatAsState(
        targetValue = pointerX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "brandHeroMouseX",
    )
    val mouseY by animateFloatAsState(
        targetValue = pointerY,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "brandHeroMouseY",
    )
    val faceOpacity = stageOpacity.coerceIn(0f, 1f)

    BoxWithConstraints(
        modifier = modifier
            .widthIn(max = BrandHeroMaxWidth)
            .aspectRatio(1f),
    ) {
        val em = minOf(maxWidth * BrandHeroEmFraction, BrandHeroEmMax)
        val cardShape = RoundedCornerShape(em * 2)
        val blurRadius = em * 1.5f
        val density = LocalDensity.current
        val emPx = with(density) { em.toPx() }
        val blurPx = with(density) { blurRadius.toPx() }
        // Shadow offset stays fixed (shop.app rest pose); face is not 3D-tilted on Web.
        val shadowShiftX =
            BrandHeroShadowMouse * -VitranAnimation.HeroCollage.CARD_SHADOW_TRANSLATE_EM * emPx
        val shadowShiftY =
            BrandHeroShadowMouse * -VitranAnimation.HeroCollage.CARD_SHADOW_TRANSLATE_EM * emPx
        val bgParallaxX =
            mouseX * VitranAnimation.HeroCollage.BRAND_BG_PARALLAX_FRACTION
        val bgParallaxY =
            mouseY * VitranAnimation.HeroCollage.BRAND_BG_PARALLAX_FRACTION

        Box(
            modifier = Modifier
                .fillMaxSize()
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
                                    pointerX = ((pos.x / w) - 0.5f) * 2f
                                    pointerY = ((pos.y / h) - 0.5f) * 2f
                                }
                                PointerEventType.Exit -> {
                                    pointerX = BrandHeroMouseRest
                                    pointerY = BrandHeroMouseRest
                                }
                            }
                        }
                    }
                }
                // Scale shadow + face together (shop.app transforms the whole card).
                .graphicsLayer {
                    scaleX = interactionScale
                    scaleY = interactionScale
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                    clip = false
                },
        ) {
            // shop.app `.hero-card-shadow:after` — flat soft blob; constant strength.
            if (faceOpacity > 0f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            translationX = shadowShiftX
                            translationY = shadowShiftY
                            clip = false
                        }
                        .brandHeroShadowBlurRoom(blurPx)
                        .blur(blurRadius)
                        .padding(blurRadius)
                        .background(Color.Black.copy(alpha = BrandHeroShadowAlpha), cardShape),
                )
            }

            // Face stays orthographic — Compose Web/Skia crops rounded clips under any
            // rotationX/Y, so mouse tilt and stage Y-flip are omitted; stage uses opacity.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = faceOpacity
                        // stageRotationY intentionally unused (see kdoc on parameter).
                    }
                    .clip(cardShape)
                    .border(
                        width = VitranSize.borderHairline,
                        color = BrandHeroBorder,
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
                    .semantics { contentDescription = a11y },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(1.5f)
                        .align(Alignment.Center)
                        .graphicsLayer {
                            translationX = bgParallaxX * size.width
                            translationY = bgParallaxY * size.height
                        },
                    content = background,
                )
                Box(
                    modifier = Modifier.fillMaxWidth(BrandLogoWidthFraction),
                    contentAlignment = Alignment.Center,
                    content = logo,
                )
            }
        }
    }
}

/** Room around the card so `Modifier.blur(1.5em)` is not clipped to the card bounds. */
private fun Modifier.brandHeroShadowBlurRoom(blurPx: Float): Modifier =
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
