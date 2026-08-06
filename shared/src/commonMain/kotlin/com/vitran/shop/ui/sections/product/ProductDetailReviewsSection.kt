package com.vitran.shop.ui.sections.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlin.math.abs
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_star_filled
import vitranshop.shared.generated.resources.product_detail_read_more_reviews
import vitranshop.shared.generated.resources.product_detail_reviews
import vitranshop.shared.generated.resources.product_detail_reviews_histogram_a11y

/**
 * PDP Reviews card (shop.app summary + histogram + carousel + CTA).
 * Lives in the buy/info column under Description; gap above comes from the column
 * `spacedBy(24)` — do not add an extra top margin.
 */
@Composable
fun ProductDetailReviewsSection(
    reviews: ProductReviewsMock,
    modifier: Modifier = Modifier,
) {
    var sheetOpen by remember { mutableStateOf(false) }
    val title = stringResource(Res.string.product_detail_reviews)
    val readMore = stringResource(Res.string.product_detail_read_more_reviews)
    val histogramA11y = stringResource(Res.string.product_detail_reviews_histogram_a11y)
    val cardShape = RoundedCornerShape(VitranRadius.extraLarge)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shopShadowS(VitranRadius.extraLarge)
            .border(VitranSize.borderHairline, CardBorder, cardShape)
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surface, cardShape)
            .padding(top = CardPad),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        ReviewsSummaryHeader(
            title = title,
            reviews = reviews,
            histogramA11y = histogramA11y,
            modifier = Modifier.padding(horizontal = CardPad),
        )

        ReviewCardsRow(
            reviews = reviews.reviews,
            onReviewClick = { sheetOpen = true },
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = CardPad, end = CardPad, bottom = CardFooterPad)
                .height(CtaHeight)
                .clip(RoundedCornerShape(percent = 50))
                .background(CtaFill)
                .clickable(role = Role.Button, onClick = { sheetOpen = true }),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = readMore,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }

    if (sheetOpen) {
        ProductDetailReviewsSheet(
            reviews = reviews,
            onDismiss = { sheetOpen = false },
        )
    }
}

@Composable
private fun ReviewsSummaryHeader(
    title: String,
    reviews: ProductReviewsMock,
    histogramA11y: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.width(ScoreColWidth),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
            )
            Text(
                text = reviews.averageLabel,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 32.sp,
                ),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(5) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_star_filled),
                        contentDescription = null,
                        size = SummaryStarSize,
                        tint = VitranTheme.extraColors.star,
                    )
                }
            }
            Text(
                text = reviews.ratingsCountLabel,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                ),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = VitranSpacing.xxl)
                .semantics { contentDescription = histogramA11y },
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            for (stars in 5 downTo 1) {
                HistogramRow(
                    stars = stars,
                    fraction = reviews.histogram.fractionFor(stars),
                )
            }
        }
    }
}

@Composable
private fun HistogramRow(
    stars: Int,
    fraction: Float,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = persianDigit(stars),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            ),
            modifier = Modifier.width(12.dp),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(HistogramBarHeight)
                .clip(RoundedCornerShape(VitranRadius.small))
                .background(HistogramTrack),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(VitranRadius.small))
                    .background(HistogramFill),
            )
        }
    }
}

/**
 * Horizontal review carousel (shop.app Swiper free-mode).
 *
 * On Wasm/Desktop, built-in scrollables ignore mouse drag and only take vertical
 * wheel (which the page [LazyColumn] consumes). Drive [ScrollState] ourselves via
 * [draggable] + wheel→horizontal mapping; keep [horizontalScroll] for offset only.
 */
@Composable
private fun ReviewCardsRow(
    reviews: List<ProductReviewItem>,
    onReviewClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    // Persian RTL: invert drag/wheel so content moves with reading direction.
    val scrollSign = if (isRtl) 1f else -1f
    val dragState = rememberDraggableState { delta ->
        scope.launch {
            scrollState.scrollBy(scrollSign * delta)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ReviewCardMinHeight)
            // Gestures disabled — drag/wheel handled below (CMP web/desktop).
            .horizontalScroll(state = scrollState, enabled = false)
            .draggable(
                state = dragState,
                orientation = Orientation.Horizontal,
            )
            .pointerInput(scrollState, isRtl) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        val change = event.changes.firstOrNull() ?: continue
                        val dx = change.scrollDelta.x
                        val dy = change.scrollDelta.y
                        if (dx == 0f && dy == 0f) continue
                        // Prefer native horizontal delta; else map vertical wheel.
                        val amount = if (abs(dx) > abs(dy)) dx else dy
                        val consumed = scrollState.dispatchRawDelta(scrollSign * amount)
                        if (abs(consumed) > 0.5f) {
                            change.consume()
                        }
                    }
                }
            },
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        verticalAlignment = Alignment.Top,
    ) {
        Spacer(modifier = Modifier.width(CardPad))
        reviews.forEach { item ->
            ReviewCard(
                item = item,
                onClick = onReviewClick,
            )
        }
        Spacer(modifier = Modifier.width(CardPad))
    }
}

@Composable
private fun ReviewCard(
    item: ProductReviewItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(ReviewCardWidth)
            .height(ReviewCardMinHeight)
            .border(
                width = VitranSize.borderHairline,
                color = ReviewCardBorder,
                shape = RoundedCornerShape(VitranRadius.xl),
            )
            .clip(RoundedCornerShape(VitranRadius.xl))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(VitranSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(item.rating.coerceIn(0, 5)) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_star_filled),
                        contentDescription = null,
                        size = CardStarSize,
                        tint = VitranTheme.extraColors.star,
                    )
                }
            }
            Text(
                text = item.body,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(AvatarSize)
                    .clip(CircleShape)
                    .background(Color(item.avatarColorArgb)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.authorInitial,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    ),
                )
            }
            Text(
                text = "${item.authorName} · ${item.dateLabel}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * shop.app `shadow-s` for the reviews outer card.
 */
private fun Modifier.shopShadowS(corner: Dp): Modifier = drawBehind {
    val radiusPx = corner.toPx()
    val yOffset = 2.dp.toPx()
    for (i in 1..5) {
        val spread = i * 1.6.dp.toPx()
        val alpha = 0.016f * (1f - (i - 1) / 5f)
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = -spread * 0.35f,
                    top = yOffset - spread * 0.1f,
                    right = size.width + spread * 0.35f,
                    bottom = size.height + yOffset + spread * 0.9f,
                    cornerRadius = CornerRadius(radiusPx + spread * 0.2f),
                ),
            )
        }
        drawPath(path = path, color = Color.Black.copy(alpha = alpha), style = Fill)
    }
}

private fun persianDigit(n: Int): String =
    when (n) {
        1 -> "۱"
        2 -> "۲"
        3 -> "۳"
        4 -> "۴"
        5 -> "۵"
        else -> n.toString()
    }

private val CardPad = 20.dp
private val CardFooterPad = 20.dp
private val ScoreColWidth = 108.dp
private val SummaryStarSize = 20.dp
private val CardStarSize = 14.dp
private val HistogramBarHeight = 8.dp
private val ReviewCardWidth = 280.dp
private val ReviewCardMinHeight = 140.dp
private val AvatarSize = 28.dp
private val CtaHeight = 44.dp
private val CardBorder = Color(0x0F183B4E)
private val ReviewCardBorder = Color(0x1A000000)
private val HistogramTrack = Color(0x0F183B4E)
private val HistogramFill = Color(0xFF121212)
private val CtaFill = Color(0xFFF2F4F5)
