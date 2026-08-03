package com.vitran.shop.ui.sections.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.vitran.shop.ui.components.BrandHeroCard
import com.vitran.shop.ui.components.ProductHeroCard
import com.vitran.shop.ui.media.LoopingNetworkVideo
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.hero_collage_a11y
import kotlin.math.absoluteValue
import kotlin.math.pow

/** shop.app hero band ≈ `lg:h-[40vh]` (measured ~360px at 900vh). */
private val HeroCollageHeight = 360.dp

/** Extra top inset so collage clears the overlay download banner. */
private val HeroCollageTopInset = 48.dp

private val HeroCollageMaxBrandWidth = 180.dp
private val HeroCollageMaxProductWidth = 240.dp

/** shop.app `w-[9vw]` / `w-[12vw]`. */
private const val BrandWidthFraction = 0.09f
private const val ProductWidthFraction = 0.12f

/** shop.app `lg:max-w-[40%]` center video. */
private const val CenterMediaWidthFraction = 0.40f
private const val CenterMediaAspectRatio = 16f / 9f

/**
 * Physical top-left. Do NOT use [BiasAlignment] / [Alignment.TopStart] —
 * those flip on RTL and push shop.app % anchors off-screen.
 */
private val TopLeft = AbsoluteAlignment.TopLeft

/** shop.app `--delay` units on each slot. */
private const val DelayProductRight = 1
private const val DelayBrandRight = 3
private const val DelayProductLeft = 6
private const val DelayBrandLeft = 7

/**
 * shop.app wrapper `left` / `top` percentages (element center after translate -50%).
 * [yOffset] mirrors the CSS `calc(% ± Npx)` extras.
 */
private data class HeroSlotAnchor(
    val xFraction: Float,
    val yFraction: Float,
    val yOffset: Dp = 0.dp,
)

private val BrandRightAnchor = HeroSlotAnchor(
    xFraction = 0.869232f,
    yFraction = 0.496156f,
    yOffset = 20.dp,
)
private val BrandLeftAnchor = HeroSlotAnchor(
    xFraction = 0.255981f,
    yFraction = 0.333054f,
)
private val ProductRightAnchor = HeroSlotAnchor(
    xFraction = 0.749446f,
    yFraction = 0.337307f,
)
private val ProductLeftAnchor = HeroSlotAnchor(
    xFraction = 0.133475f,
    yFraction = 0.489815f,
    yOffset = (-50).dp,
)

/** Per-slot motion derived from shop.app `--progress` + `--delay` pow curve. */
private data class HeroSlotMotion(
    val opacity: Float,
    val progressInOut: Float,
    val progress: Float,
)

/**
 * Desktop Home hero collage with theme cycling and idle float.
 * Motion matches shop.app shared `--progress` (-1→1) + delayed pow flip.
 */
@Composable
fun HeroCollage(
    scenes: List<HeroCollageScene>,
    modifier: Modifier = Modifier,
    onBrandClick: ((HeroCollageBrand) -> Unit)? = null,
    onProductClick: ((HeroCollageProduct) -> Unit)? = null,
) {
    require(scenes.isNotEmpty()) { "HeroCollage requires at least one scene" }

    val a11y = stringResource(Res.string.hero_collage_a11y)
    var sceneIndex by remember(scenes) { mutableIntStateOf(0) }
    val cycleProgress = remember { Animatable(0f) }
    val scene = scenes[sceneIndex]

    LaunchedEffect(scenes) {
        // Start fully on stage, then match shop.app's -1→1 sweep in two halves.
        cycleProgress.snapTo(0f)
        if (scenes.size <= 1) return@LaunchedEffect
        val halfCycleMs = VitranAnimation.HeroCollage.CYCLE_MS / 2
        while (isActive) {
            cycleProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = halfCycleMs, easing = LinearEasing),
            )
            sceneIndex = (sceneIndex + 1) % scenes.size
            cycleProgress.snapTo(-1f)
            cycleProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = halfCycleMs, easing = LinearEasing),
            )
        }
    }

    val progress = cycleProgress.value

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = HeroCollageTopInset)
            .height(HeroCollageHeight)
            .background(MaterialTheme.colorScheme.background)
            .graphicsLayer { clip = false }
            .semantics { contentDescription = a11y },
    ) {
        val collageWidth = maxWidth
        val collageHeight = maxHeight
        val brandWidth = (collageWidth * BrandWidthFraction)
            .coerceAtMost(HeroCollageMaxBrandWidth)
            .coerceAtLeast(72.dp)
        val productWidth = (collageWidth * ProductWidthFraction)
            .coerceAtMost(HeroCollageMaxProductWidth)
            .coerceAtLeast(96.dp)
        val density = LocalDensity.current.density

        CenterMedia(
            scene = scene,
            progress = progress,
            delayUnits = 0,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(CenterMediaWidthFraction)
                // shop.app `lg:top-[-8%]`
                .offset(y = collageHeight * -0.08f)
                .zIndex(0f),
        )

        AnchoredBrandCard(
            brand = scene.brands[0],
            anchor = BrandRightAnchor,
            cardWidth = brandWidth,
            collageWidth = collageWidth,
            collageHeight = collageHeight,
            progress = progress,
            delayUnits = DelayBrandRight,
            cameraDensity = density,
            onClick = onBrandClick?.let { handler -> { handler(scene.brands[0]) } },
        )
        AnchoredBrandCard(
            brand = scene.brands[1],
            anchor = BrandLeftAnchor,
            cardWidth = brandWidth,
            collageWidth = collageWidth,
            collageHeight = collageHeight,
            progress = progress,
            delayUnits = DelayBrandLeft,
            cameraDensity = density,
            onClick = onBrandClick?.let { handler -> { handler(scene.brands[1]) } },
        )
        AnchoredProductCard(
            product = scene.products[0],
            anchor = ProductRightAnchor,
            cardWidth = productWidth,
            collageWidth = collageWidth,
            collageHeight = collageHeight,
            progress = progress,
            delayUnits = DelayProductRight,
            cameraDensity = density,
            onClick = onProductClick?.let { handler -> { handler(scene.products[0]) } },
        )
        AnchoredProductCard(
            product = scene.products[1],
            anchor = ProductLeftAnchor,
            cardWidth = productWidth,
            collageWidth = collageWidth,
            collageHeight = collageHeight,
            progress = progress,
            delayUnits = DelayProductLeft,
            cameraDensity = density,
            onClick = onProductClick?.let { handler -> { handler(scene.products[1]) } },
        )
    }
}

@Composable
private fun CenterMedia(
    scene: HeroCollageScene,
    progress: Float,
    delayUnits: Int,
    modifier: Modifier = Modifier,
) {
    val motion = rememberHeroSlotMotion(progress = progress, delayUnits = delayUnits)
    Box(
        modifier = modifier
            .aspectRatio(CenterMediaAspectRatio)
            .graphicsLayer {
                alpha = motion.opacity
                // shop.app video: scale(.8 + cos * .2) + lateral swing.
                val s = 0.8f + 0.2f * motion.opacity
                scaleX = s
                scaleY = s
                translationX = motion.progressInOut * (-size.width * 0.5f) +
                    motion.progress * (-size.width * 0.1f)
            }
            .clip(RoundedCornerShape(VitranRadius.xl))
            .background(scene.centerMediaColor),
        contentAlignment = Alignment.Center,
    ) {
        LoopingNetworkVideo(
            url = scene.centerMediaVideoUrl,
            posterUrl = scene.centerMediaPosterUrl,
            modifier = Modifier.fillMaxSize(),
            contentDescription = null,
        )
    }
}

@Composable
private fun BoxScope.AnchoredBrandCard(
    brand: HeroCollageBrand,
    anchor: HeroSlotAnchor,
    cardWidth: Dp,
    collageWidth: Dp,
    collageHeight: Dp,
    progress: Float,
    delayUnits: Int,
    cameraDensity: Float,
    onClick: (() -> Unit)?,
) {
    val motion = rememberHeroSlotMotion(progress = progress, delayUnits = delayUnits)
    val idleOffsetY = rememberHeroIdleOffsetY(
        amplitudePx = with(LocalDensity.current) {
            (cardWidth * VitranAnimation.HeroCollage.IDLE_TRANSLATE_FRACTION).toPx()
        },
    )
    val density = LocalDensity.current
    val xPx = with(density) {
        (collageWidth * anchor.xFraction - cardWidth / 2).toPx()
    }
    val yPx = with(density) {
        (collageHeight * anchor.yFraction + anchor.yOffset - cardWidth / 2).toPx()
    }
    BrandHeroCard(
        brandName = brand.name,
        modifier = Modifier
            .zIndex(1f)
            .align(TopLeft)
            .width(cardWidth)
            .heroStageLayer(
                motion = motion,
                cameraDensity = cameraDensity,
                xPx = xPx,
                yPx = yPx,
                idleOffsetY = idleOffsetY,
            ),
        onClick = onClick,
        background = {
            val placeholder = ColorPainter(brand.backgroundColor)
            AsyncImage(
                model = resolveNetworkImageUrl(brand.backgroundImageUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder,
            )
        },
        logo = {
            val logoUrl = brand.logoImageUrl
            if (logoUrl != null) {
                AsyncImage(
                    model = resolveNetworkImageUrl(logoUrl),
                    contentDescription = brand.logoLabel,
                    modifier = Modifier
                        .fillMaxWidth(0.70f)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit,
                )
            } else {
                Text(
                    text = brand.logoLabel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }
        },
    )
}

@Composable
private fun BoxScope.AnchoredProductCard(
    product: HeroCollageProduct,
    anchor: HeroSlotAnchor,
    cardWidth: Dp,
    collageWidth: Dp,
    collageHeight: Dp,
    progress: Float,
    delayUnits: Int,
    cameraDensity: Float,
    onClick: (() -> Unit)?,
) {
    val motion = rememberHeroSlotMotion(progress = progress, delayUnits = delayUnits)
    // Approximate shop.app product card height (~220 / 182).
    val cardHeight = cardWidth * 1.21f
    val idleOffsetY = rememberHeroIdleOffsetY(
        amplitudePx = with(LocalDensity.current) {
            (cardHeight * VitranAnimation.HeroCollage.IDLE_TRANSLATE_FRACTION).toPx()
        },
    )
    val density = LocalDensity.current
    val xPx = with(density) {
        (collageWidth * anchor.xFraction - cardWidth / 2).toPx()
    }
    val yPx = with(density) {
        (collageHeight * anchor.yFraction + anchor.yOffset - cardHeight / 2).toPx()
    }
    ProductHeroCard(
        title = product.title,
        rating = product.rating,
        reviewCountLabel = product.reviewCountLabel,
        modifier = Modifier
            .zIndex(2f)
            .align(TopLeft)
            .width(cardWidth)
            .heroStageLayer(
                motion = motion,
                cameraDensity = cameraDensity,
                xPx = xPx,
                yPx = yPx,
                idleOffsetY = idleOffsetY,
            ),
        onClick = onClick,
        image = {
            val placeholder = ColorPainter(product.imageColor)
            AsyncImage(
                model = resolveNetworkImageUrl(product.imageUrl),
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder,
            )
        },
    )
}

/**
 * shop.app:
 * `--delayed-progress = progress * (1 + delay / 25)`
 * `--progress-in-out = clamp(-1, pow(delayed, 23), 1)`
 * opacity = `1 - pow(delayed, 22)`
 */
private fun heroSlotMotion(progress: Float, delayUnits: Int): HeroSlotMotion {
    val delayed = progress *
        (1f + delayUnits / VitranAnimation.HeroCollage.DELAY_FACTOR)
    val absDelayed = delayed.absoluteValue
    val signedPow = when {
        delayed < 0f -> -absDelayed.pow(VitranAnimation.HeroCollage.POW)
        else -> absDelayed.pow(VitranAnimation.HeroCollage.POW)
    }
    val progressInOut = signedPow.coerceIn(-1f, 1f)
    val opacity = (1f - absDelayed.pow(VitranAnimation.HeroCollage.POW - 1))
        .coerceIn(0f, 1f)
    return HeroSlotMotion(
        opacity = opacity,
        progressInOut = progressInOut,
        progress = progress,
    )
}

@Composable
private fun rememberHeroSlotMotion(progress: Float, delayUnits: Int): HeroSlotMotion =
    remember(progress, delayUnits) { heroSlotMotion(progress, delayUnits) }

/** shop.app `card-idle` — translateY to +5% of card size, alternate. */
@Composable
private fun rememberHeroIdleOffsetY(amplitudePx: Float): Float {
    val transition = rememberInfiniteTransition(label = "heroCardIdle")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = amplitudePx,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = VitranAnimation.HeroCollage.IDLE_DURATION_MS,
                easing = EaseInOut,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "heroIdleY",
    )
    return offset
}

/**
 * Stage transform: absolute center position + shop.app Y-flip / lateral swing + idle.
 *
 * CSS equivalent:
 * `translate3d(-50% + progressInOut*-100% + progress*-20%, -50%, z) rotateY(progressInOut*-90deg)`
 */
private fun Modifier.heroStageLayer(
    motion: HeroSlotMotion,
    cameraDensity: Float,
    xPx: Float,
    yPx: Float,
    idleOffsetY: Float,
): Modifier = graphicsLayer {
    val swing = motion.progressInOut * (-size.width) + motion.progress * (-size.width * 0.20f)
    translationX = xPx + swing
    translationY = yPx + idleOffsetY * motion.opacity
    alpha = motion.opacity
    rotationY = motion.progressInOut * -90f
    cameraDistance = 18f * cameraDensity
    transformOrigin = TransformOrigin(0.5f, 0.5f)
    clip = false
}

@Preview(showBackground = true, widthDp = 1200, heightDp = 420)
@Composable
private fun HeroCollagePreview() {
    VitranTheme {
        HeroCollage(scenes = MockHeroCollageScenes)
    }
}
