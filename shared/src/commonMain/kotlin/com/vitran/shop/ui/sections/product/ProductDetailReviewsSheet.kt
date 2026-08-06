package com.vitran.shop.ui.sections.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_bold_right_chevron
import vitranshop.shared.generated.resources.ic_close
import vitranshop.shared.generated.resources.ic_more_horiz
import vitranshop.shared.generated.resources.ic_search
import vitranshop.shared.generated.resources.ic_star_filled
import vitranshop.shared.generated.resources.ic_star_outline
import vitranshop.shared.generated.resources.ic_thumb_up
import vitranshop.shared.generated.resources.product_detail_reviews
import vitranshop.shared.generated.resources.product_detail_reviews_close_a11y
import vitranshop.shared.generated.resources.product_detail_reviews_filter_done
import vitranshop.shared.generated.resources.product_detail_reviews_filter_reset
import vitranshop.shared.generated.resources.product_detail_reviews_helpful
import vitranshop.shared.generated.resources.product_detail_reviews_histogram_a11y
import vitranshop.shared.generated.resources.product_detail_reviews_more_a11y
import vitranshop.shared.generated.resources.product_detail_reviews_rating_filter
import vitranshop.shared.generated.resources.product_detail_reviews_search_placeholder
import vitranshop.shared.generated.resources.product_detail_reviews_sort
import vitranshop.shared.generated.resources.product_detail_reviews_sort_newest
import vitranshop.shared.generated.resources.product_detail_reviews_sort_oldest
import vitranshop.shared.generated.resources.product_detail_reviews_sort_rating_asc
import vitranshop.shared.generated.resources.product_detail_reviews_sort_rating_desc
import vitranshop.shared.generated.resources.product_detail_reviews_sort_relevant

/**
 * shop.app Reviews side sheet — opens from review cards / “Read more”.
 * Shell + edge slide: [ProductDetailSideSheet] (mirrors shop.app Description sheet).
 */
@Composable
fun ProductDetailReviewsSheet(
    reviews: ProductReviewsMock,
    onDismiss: () -> Unit,
) {
    val title = stringResource(Res.string.product_detail_reviews)
    val closeA11y = stringResource(Res.string.product_detail_reviews_close_a11y)
    val histogramA11y = stringResource(Res.string.product_detail_reviews_histogram_a11y)
    val helpfulLabel = stringResource(Res.string.product_detail_reviews_helpful)
    val searchPlaceholder = stringResource(Res.string.product_detail_reviews_search_placeholder)
    val sortLabel = stringResource(Res.string.product_detail_reviews_sort)
    val ratingFilterLabel = stringResource(Res.string.product_detail_reviews_rating_filter)
    val moreA11y = stringResource(Res.string.product_detail_reviews_more_a11y)

    ProductDetailSideSheet(onDismiss = onDismiss) { onClose ->
        ReviewsSheetPanel(
            reviews = reviews,
            title = title,
            closeA11y = closeA11y,
            histogramA11y = histogramA11y,
            helpfulLabel = helpfulLabel,
            searchPlaceholder = searchPlaceholder,
            sortLabel = sortLabel,
            ratingFilterLabel = ratingFilterLabel,
            moreA11y = moreA11y,
            onClose = onClose,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ReviewsSheetPanel(
    reviews: ProductReviewsMock,
    title: String,
    closeA11y: String,
    histogramA11y: String,
    helpfulLabel: String,
    searchPlaceholder: String,
    sortLabel: String,
    ratingFilterLabel: String,
    moreA11y: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var sortExpanded by remember { mutableStateOf(false) }
    var ratingExpanded by remember { mutableStateOf(false) }
    // Applied filters (after Done)
    var sortIndex by remember { mutableIntStateOf(0) }
    var ratingStars by remember { mutableStateOf(emptySet<Int>()) }
    // Draft while popover open
    var draftSort by remember { mutableIntStateOf(0) }
    var draftRatings by remember { mutableStateOf(emptySet<Int>()) }

    val sortOptions = listOf(
        stringResource(Res.string.product_detail_reviews_sort_relevant),
        stringResource(Res.string.product_detail_reviews_sort_newest),
        stringResource(Res.string.product_detail_reviews_sort_oldest),
        stringResource(Res.string.product_detail_reviews_sort_rating_asc),
        stringResource(Res.string.product_detail_reviews_sort_rating_desc),
    )
    val resetLabel = stringResource(Res.string.product_detail_reviews_filter_reset)
    val doneLabel = stringResource(Res.string.product_detail_reviews_filter_done)

    val displayedReviews = remember(reviews.reviews, query, sortIndex, ratingStars) {
        var list = reviews.reviews
        if (ratingStars.isNotEmpty()) {
            list = list.filter { it.rating in ratingStars }
        }
        val q = query.trim()
        if (q.isNotEmpty()) {
            list = list.filter { item ->
                item.body.contains(q, ignoreCase = true) ||
                    item.title?.contains(q, ignoreCase = true) == true ||
                    item.authorName.contains(q, ignoreCase = true) ||
                    item.variantLabel?.contains(q, ignoreCase = true) == true
            }
        }
        when (sortIndex) {
            1 -> list // newest — mock already newest-first
            2 -> list.asReversed()
            3 -> list.sortedBy { it.rating }
            4 -> list.sortedByDescending { it.rating }
            else -> list // most relevant — keep mock order
        }
    }

    Column(
        modifier = modifier.padding(SheetPad),
    ) {
        Box(
            modifier = Modifier
                .size(CloseButtonSize)
                .clip(CircleShape)
                .background(CloseButtonFill)
                .clickable(role = Role.Button, onClick = onClose)
                .semantics { contentDescription = closeA11y },
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = null,
                size = CloseGlyphSize,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.height(VitranSpacing.md))

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                lineHeight = 40.sp,
            ),
            modifier = Modifier.padding(bottom = VitranSpacing.md),
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            contentPadding = PaddingValues(bottom = VitranSpacing.xxxl),
        ) {
            item(key = "summary") {
                SheetSummaryHeader(
                    reviews = reviews,
                    histogramA11y = histogramA11y,
                )
            }

            item(key = "search") {
                SheetSearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = searchPlaceholder,
                )
            }

            item(key = "filters") {
                val density = LocalDensity.current
                var filtersHeightPx by remember { mutableIntStateOf(0) }
                val popoverGapPx = with(density) { VitranSpacing.sm.roundToPx() }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { filtersHeightPx = it.height }
                        .zIndex(if (sortExpanded || ratingExpanded) 8f else 0f),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FilterDropdownChip(
                            label = sortLabel,
                            expanded = sortExpanded,
                            active = sortExpanded || sortIndex != 0,
                            onClick = {
                                if (!sortExpanded) {
                                    draftSort = sortIndex
                                    sortExpanded = true
                                    ratingExpanded = false
                                } else {
                                    sortExpanded = false
                                }
                            },
                        )
                        FilterDropdownChip(
                            label = ratingFilterLabel,
                            expanded = ratingExpanded,
                            active = ratingExpanded || ratingStars.isNotEmpty(),
                            onClick = {
                                if (!ratingExpanded) {
                                    draftRatings = ratingStars
                                    ratingExpanded = true
                                    sortExpanded = false
                                } else {
                                    ratingExpanded = false
                                }
                            },
                        )
                    }

                    if (sortExpanded) {
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(0, filtersHeightPx + popoverGapPx),
                            onDismissRequest = { sortExpanded = false },
                            properties = PopupProperties(
                                focusable = true,
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                            ),
                        ) {
                            SortFilterPopover(
                                options = sortOptions,
                                selectedIndex = draftSort,
                                onSelect = { draftSort = it },
                                resetLabel = resetLabel,
                                doneLabel = doneLabel,
                                onReset = { draftSort = 0 },
                                onDone = {
                                    sortIndex = draftSort
                                    sortExpanded = false
                                },
                            )
                        }
                    }

                    if (ratingExpanded) {
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(0, filtersHeightPx + popoverGapPx),
                            onDismissRequest = { ratingExpanded = false },
                            properties = PopupProperties(
                                focusable = true,
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                            ),
                        ) {
                            RatingFilterPopover(
                                selectedStars = draftRatings,
                                onToggle = { star ->
                                    draftRatings = if (star in draftRatings) {
                                        draftRatings - star
                                    } else {
                                        draftRatings + star
                                    }
                                },
                                resetLabel = resetLabel,
                                doneLabel = doneLabel,
                                onReset = { draftRatings = emptySet() },
                                onDone = {
                                    ratingStars = draftRatings
                                    ratingExpanded = false
                                },
                            )
                        }
                    }
                }
            }

            itemsIndexed(
                items = displayedReviews,
                key = { index, item -> "${item.authorName}-${item.dateLabel}-$index" },
            ) { _, item ->
                SheetReviewCard(
                    item = item,
                    helpfulLabel = helpfulLabel,
                    moreA11y = moreA11y,
                )
            }
        }
    }
}

@Composable
private fun SheetSummaryHeader(
    reviews: ProductReviewsMock,
    histogramA11y: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.width(ScoreColWidth),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
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
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
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
                SheetHistogramRow(
                    stars = stars,
                    fraction = reviews.histogram.fractionFor(stars),
                )
            }
        }
    }
}

@Composable
private fun SheetHistogramRow(
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

@Composable
private fun SheetSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SearchHeight)
            .clip(RoundedCornerShape(percent = 50))
            .background(SearchFill)
            .padding(horizontal = VitranSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = null,
            size = 20.dp,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun FilterDropdownChip(
    label: String,
    expanded: Boolean,
    active: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (active) ChipActiveFill else ChipIdleFill
    val fg = if (active) Color.White else ReviewTextDark
    Row(
        modifier = Modifier
            .then(
                if (active) {
                    Modifier
                } else {
                    Modifier.shadow(
                        elevation = ChipIdleShadow,
                        shape = RoundedCornerShape(percent = 50),
                        spotColor = ChipIdleShadowColor,
                        ambientColor = ChipIdleShadowColor,
                    )
                },
            )
            .clip(RoundedCornerShape(percent = 50))
            .background(bg)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            ),
        )
        VitranIcon(
            painter = painterResource(Res.drawable.ic_bold_right_chevron),
            contentDescription = null,
            size = 20.dp,
            tint = fg,
            modifier = Modifier.graphicsLayer {
                rotationZ = if (expanded) -90f else 90f
            },
        )
    }
}

@Composable
private fun SortFilterPopover(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    resetLabel: String,
    doneLabel: String,
    onReset: () -> Unit,
    onDone: () -> Unit,
) {
    FilterPopoverShell {
        options.forEachIndexed { index, option ->
            FilterOptionRow(onClick = { onSelect(index) }) {
                Text(
                    text = option,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                RadioMark(selected = index == selectedIndex)
            }
        }
        FilterFooterButtons(
            resetLabel = resetLabel,
            doneLabel = doneLabel,
            onReset = onReset,
            onDone = onDone,
        )
    }
}

@Composable
private fun RatingFilterPopover(
    selectedStars: Set<Int>,
    onToggle: (Int) -> Unit,
    resetLabel: String,
    doneLabel: String,
    onReset: () -> Unit,
    onDone: () -> Unit,
) {
    FilterPopoverShell {
        for (stars in 5 downTo 1) {
            FilterOptionRow(onClick = { onToggle(stars) }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                    modifier = Modifier.weight(1f),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(5) { i ->
                            val filled = i < stars
                            VitranIcon(
                                painter = painterResource(
                                    if (filled) Res.drawable.ic_star_filled
                                    else Res.drawable.ic_star_outline,
                                ),
                                contentDescription = null,
                                size = PopoverStarSize,
                                tint = if (filled) {
                                    VitranTheme.extraColors.star
                                } else {
                                    StarEmptyTint
                                },
                            )
                        }
                    }
                    Text(
                        text = persianDigit(stars),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                        ),
                    )
                }
                CheckboxMark(checked = stars in selectedStars)
            }
        }
        FilterFooterButtons(
            resetLabel = resetLabel,
            doneLabel = doneLabel,
            onReset = onReset,
            onDone = onDone,
        )
    }
}

@Composable
private fun FilterPopoverShell(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(PopoverWidth)
            .shadow(
                elevation = PopoverShadow,
                shape = RoundedCornerShape(PopoverRadius),
                spotColor = PopoverShadowColor,
                ambientColor = PopoverShadowColor,
            )
            .clip(RoundedCornerShape(PopoverRadius))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = VitranSize.borderHairline,
                color = PopoverOutline,
                shape = RoundedCornerShape(PopoverRadius),
            )
            .padding(
                top = PopoverPadTop,
                start = PopoverPadHorizontal,
                end = PopoverPadHorizontal,
                bottom = PopoverPadBottom,
            ),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        content()
    }
}

@Composable
private fun FilterOptionRow(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(FilterRowHeight)
            .clip(RoundedCornerShape(FilterRowRadius))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = FilterRowPadStart, end = FilterRowPadEnd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FilterRowGap),
        content = content,
    )
}

@Composable
private fun FilterFooterButtons(
    resetLabel: String,
    doneLabel: String,
    onReset: () -> Unit,
    onDone: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = FilterFooterTopPad),
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(FooterBtnHeight)
                .clip(RoundedCornerShape(percent = 50))
                .background(ChipFill)
                .clickable(role = Role.Button, onClick = onReset),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = resetLabel,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                ),
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(FooterBtnHeight)
                .clip(RoundedCornerShape(percent = 50))
                .background(DoneFill)
                .clickable(role = Role.Button, onClick = onDone),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = doneLabel,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                ),
            )
        }
    }
}

/** shop.app radio: 20dp, idle thin gray ring; selected black disc + white inset ring + black dot. */
@Composable
private fun RadioMark(selected: Boolean) {
    if (selected) {
        Box(
            modifier = Modifier
                .size(ControlSize)
                .clip(CircleShape)
                .background(DoneFill)
                .border(width = 1.dp, color = DoneFill, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(ControlSize - RadioInsetRing * 2)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(RadioDotSize)
                        .clip(CircleShape)
                        .background(DoneFill),
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .size(ControlSize)
                .border(
                    width = 1.dp,
                    color = ControlIdleBorder,
                    shape = CircleShape,
                ),
        )
    }
}

@Composable
private fun CheckboxMark(checked: Boolean) {
    Box(
        modifier = Modifier
            .size(ControlSize)
            .clip(RoundedCornerShape(CheckboxRadius))
            .border(
                width = 1.dp,
                color = if (checked) DoneFill else ControlIdleBorder,
                shape = RoundedCornerShape(CheckboxRadius),
            )
            .background(if (checked) DoneFill else Color.Transparent),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Text(
                text = "✓",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                ),
            )
        }
    }
}

@Composable
private fun SheetReviewCard(
    item: ProductReviewItem,
    helpfulLabel: String,
    moreA11y: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ReviewCardRadius))
            .background(ReviewCardBg)
            .padding(ReviewCardPad),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(item.rating.coerceIn(0, 5)) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_star_filled),
                    contentDescription = null,
                    size = ReviewCardStarSize,
                    tint = ReviewStarGold,
                )
            }
        }

        item.title?.let { headline ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = headline,
                color = ReviewTextDark,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
            )
        }

        item.variantLabel?.let { label ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = ReviewTextGray,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                ),
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = item.body,
            color = ReviewBodyText,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 21.sp,
            ),
        )

        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(ReviewAvatarSize)
                    .clip(CircleShape)
                    .background(Color(item.avatarColorArgb)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.authorInitial,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                    ),
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${item.authorName} · ${item.dateLabel}",
                color = ReviewTextGray,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable(role = Role.Button, onClick = {}),
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_thumb_up),
                    contentDescription = null,
                    size = 17.dp,
                    tint = ReviewIconGray,
                )
                Text(
                    text = helpfulLabel,
                    color = ReviewIconGray,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Box(
                modifier = Modifier
                    .clickable(role = Role.Button, onClick = {})
                    .semantics { contentDescription = moreA11y },
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_more_horiz),
                    contentDescription = null,
                    size = 20.dp,
                    tint = ReviewIconGray,
                )
            }
        }
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

private val SheetPad = 24.dp
private val CloseButtonSize = 44.dp
private val CloseGlyphSize = 20.dp
private val CloseButtonFill = Color(0xFFF2F4F5)
private val ScoreColWidth = 108.dp
private val SummaryStarSize = 20.dp
private val HistogramBarHeight = 8.dp
private val HistogramTrack = Color(0x0F183B4E)
private val HistogramFill = Color(0xFF121212)
private val SearchHeight = 48.dp
private val SearchFill = Color(0xFFF2F4F5)
private val ChipFill = Color(0xFFF2F4F5)
private val ChipIdleFill = Color.White
private val ChipIdleShadow = 3.dp
private val ChipIdleShadowColor = Color.Black.copy(alpha = 0.08f)
private val ChipActiveFill = Color(0xFF121212)
private val DoneFill = Color(0xFF121212)
private val ControlIdleBorder = Color.Black.copy(alpha = 0.2f)
private val StarEmptyTint = Color(0xFFC8CDD0)
private val ReviewCardBg = Color(0xFFFBFAF8)
private val ReviewCardRadius = 14.dp
private val ReviewCardPad = 20.dp
private val ReviewCardStarSize = 22.dp
private val ReviewStarGold = Color(0xFFC6A12B)
private val ReviewTextDark = Color(0xFF141414)
private val ReviewTextGray = Color(0xFFA3A3A3)
private val ReviewBodyText = Color(0xFF2E2E2E)
private val ReviewIconGray = Color(0xFF757575)
private val ReviewAvatarSize = 26.dp
/** shop.app filter dropdown — fixed ~300px, not sheet-width. */
private val PopoverWidth = 300.dp
private val PopoverRadius = 28.dp
private val PopoverShadow = 4.dp
private val PopoverShadowColor = Color.Black.copy(alpha = 0.06f)
private val PopoverOutline = Color.White
private val PopoverPadTop = 8.dp
private val PopoverPadHorizontal = 12.dp
private val PopoverPadBottom = 12.dp
private val FilterRowHeight = 44.dp
private val FilterRowRadius = 16.dp
private val FilterRowPadStart = 8.dp
private val FilterRowPadEnd = 6.dp
private val FilterRowGap = 8.dp
private val FilterFooterTopPad = 4.dp
private val ControlSize = 20.dp
private val RadioInsetRing = 4.dp
private val RadioDotSize = 10.dp
private val CheckboxRadius = 6.dp
private val PopoverStarSize = 16.dp
private val FooterBtnHeight = 44.dp
