package com.vitran.shop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil3.compose.AsyncImage
import com.vitran.shop.ui.media.resolveNetworkImageUrl
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.shell.LocalShellViewportHeight
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.hero_omnibox_a11y
import vitranshop.shared.generated.resources.hero_omnibox_clear_a11y
import vitranshop.shared.generated.resources.hero_omnibox_placeholder
import vitranshop.shared.generated.resources.hero_omnibox_privacy
import vitranshop.shared.generated.resources.hero_omnibox_submit_a11y
import vitranshop.shared.generated.resources.hero_omnibox_suggestions_title
import vitranshop.shared.generated.resources.ic_arrow_right
import vitranshop.shared.generated.resources.ic_close
import vitranshop.shared.generated.resources.ic_search
import vitranshop.shared.generated.resources.ic_star_filled
import kotlin.time.Duration.Companion.milliseconds

/** shop.app omnibox `md:max-w-[600px]`. */
internal val OmniboxMaxWidth = 600.dp

/** shop.app floating pill slot `h-[66px]`. */
internal val OmniboxFloatingSlotHeight = 66.dp

/** shop.app floating pill `bottom-space-36`. */
internal val OmniboxFloatingBottomInset = 36.dp

/** shop.app `rounded-[32px]`. */
internal val OmniboxCorner = 32.dp
internal val OmniboxShape = RoundedCornerShape(OmniboxCorner)

private val OmniboxActionSize = 40.dp

/** shop.app typeahead icon render size (`width:20px`). */
private val SuggestionIconSize = 20.dp

/** shop.app rating star next to shop score. */
private val RatingStarSize = 12.dp

/**
 * shop.app expanded shell padding (`lg:p-space-8`).
 * Compact collapsed uses `p-space-4`.
 */
internal val GlassShellPadding = VitranSpacing.sm
private val CompactShellPadding = VitranSpacing.xs

/** shop.app `0 0 0 4.5px rgba(0,0,0,0.03)` — drawn outside, not as inset border. */
internal val OmniboxRingWidth = 4.5.dp
internal val OmniboxRingColor = Color.Black.copy(alpha = 0.03f)

internal val OmniboxShellFill = Color.White.copy(alpha = 0.9f)

/** Expanded desktop overlay — solid white so categories do not show through (shop.app). */
internal val OmniboxExpandedShellFill = Color.White

/**
 * shop.app desktop focused search field (`lg:focus:`):
 * `bg-overlay-fixed-light-75`, `border-border-secondary`, `shadow-s`.
 */
private val SearchFieldFocusFill = Color.White.copy(alpha = 0.75f)
private val SearchFieldFocusBorder = Color(0xFF183B4E).copy(alpha = 0.06f)
private val SearchFieldFocusShadow = Color.Black.copy(alpha = 0.06f)
private val SearchFieldFocusShadowElevation = 2.dp

private val SuggestionHover = Color.Black.copy(alpha = 0.04f)
private val ShopLogoFallback = Color.Black.copy(alpha = 0.08f)
/** shop.app shop-rating chip fill (soft gray pill behind score + star). */
private val ShopRatingPillBackground = Color.Black.copy(alpha = 0.06f)
private const val SuggestionIconAlpha = 0.6f
private const val PlaceholderAlpha = 0.6f

/**
 * shop.app typeahead: container `max-h-[45dvh]`; list `max-h: calc(45dvh - 88px)`
 * with `overflow-y: auto` and `overscroll-behavior: contain`.
 */
internal const val TypeaheadViewportFraction = 0.45f
/** Input row + shell padding; title/privacy scroll inside the list. */
internal val TypeaheadChromeOffset = 72.dp
private val ShellBottomGap = 12.dp
internal val TypeaheadMinHeight = 120.dp

/**
 * Consumes leftover vertical scroll after the typeahead list so the parent
 * page scroll does not move (shop.app `overscroll-behavior: contain`).
 */
internal val TypeaheadNestedScrollConnection = object : NestedScrollConnection {
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset = Offset(0f, available.y)

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
        Velocity(0f, available.y)
}

/**
 * Home hero search omnibox matching shop.app:
 * - Desktop: collapsed pill in layout; expanded field+typeahead is one solid Popup panel
 *   (overlay — does not push Home content).
 * - Compact: collapsed pill; focus opens [OmniboxMobileSearchSheet] above bottom nav.
 * Mock-only: [onSubmit] does not navigate yet.
 *
 * [expanded] is controlled by the parent so outside-tap (desktop) can collapse immediately.
 *
 * @param onBoundsInRoot Omnibox hit rect in root coordinates (desktop outside-tap),
 * including the expanded typeahead panel when open.
 * @param onCollapsedLayoutCoordinates Collapsed field layout coords (floating-search
 * visibility). Parent should keep the reference and re-read [LayoutCoordinates.boundsInRoot]
 * when scroll changes — [onGloballyPositioned] alone may not recompose every frame.
 */
@Composable
fun HeroOmnibox(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onBoundsInRoot: (Rect) -> Unit = {},
    onCollapsedLayoutCoordinates: (LayoutCoordinates) -> Unit = {},
) {
    val isDesktop = LocalDesktopLayout.current
    val placeholder = stringResource(Res.string.hero_omnibox_placeholder)
    val a11y = stringResource(Res.string.hero_omnibox_a11y)
    val submitA11y = stringResource(Res.string.hero_omnibox_submit_a11y)
    val clearA11y = stringResource(Res.string.hero_omnibox_clear_a11y)
    val suggestionsTitle = stringResource(Res.string.hero_omnibox_suggestions_title)
    val privacy = stringResource(Res.string.hero_omnibox_privacy)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val purpleDark = VitranTheme.extraColors.purpleDark
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val viewportHeight = LocalShellViewportHeight.current

    val results = remember(query) { mockOmniboxResults(query) }
    val anchorFocusRequester = remember { FocusRequester() }

    var textFieldFocused by remember { mutableStateOf(false) }
    // Remaining space under omnibox top → avoid clipping at the content-frame bottom.
    var maxShellHeight by remember { mutableStateOf(viewportHeight * TypeaheadViewportFraction) }
    var collapsedWidthPx by remember { mutableIntStateOf(0) }
    var collapsedHeightPx by remember { mutableIntStateOf(0) }

    val latestOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val latestOnBoundsInRoot by rememberUpdatedState(onBoundsInRoot)
    val latestOnCollapsedLayoutCoordinates by rememberUpdatedState(onCollapsedLayoutCoordinates)
    val latestOnQueryChange by rememberUpdatedState(onQueryChange)
    val latestExpanded by rememberUpdatedState(expanded)

    fun dismissToInitial() {
        latestOnQueryChange("")
        latestOnExpandedChange(false)
        focusManager.clearFocus()
    }

    LaunchedEffect(textFieldFocused, isDesktop) {
        if (textFieldFocused) {
            latestOnExpandedChange(true)
        } else if (isDesktop) {
            delay(VitranAnimation.Omnibox.COLLAPSE_DELAY_MS.milliseconds)
            // Desktop blur / outside click: collapse + reset.
            if (!textFieldFocused && latestExpanded) {
                latestOnQueryChange("")
                latestOnExpandedChange(false)
            }
        }
        // Compact: hero-field blur must not dismiss — the mobile sheet owns focus.
    }

    val onResultClick: (OmniboxResult) -> Unit = { result ->
        val text = when (result) {
            is OmniboxResult.Shop -> result.name
            is OmniboxResult.Keyword -> result.fullText
        }
        latestOnQueryChange(text)
        onSubmit()
        dismissToInitial()
    }

    val typeaheadMaxHeight = (maxShellHeight - TypeaheadChromeOffset)
        .coerceAtLeast(TypeaheadMinHeight)

    val showDesktopTypeahead = isDesktop && expanded
    val collapsedWidth = with(density) {
        if (collapsedWidthPx > 0) collapsedWidthPx.toDp() else OmniboxMaxWidth
    }
    val collapsedHeight = with(density) {
        if (collapsedHeightPx > 0) collapsedHeightPx.toDp() else VitranSize.touchTarget
    }

    fun updateShellCap(coords: LayoutCoordinates) {
        latestOnBoundsInRoot(coords.boundsInRoot())
        if (isDesktop) {
            val root = coords.findRootCoordinates()
            val top = coords.boundsInRoot().top
            val remainingPx = (root.size.height - top).coerceAtLeast(0f)
            val remainingDp = with(density) { remainingPx.toDp() } - ShellBottomGap
            val cap = viewportHeight * TypeaheadViewportFraction
            maxShellHeight = minOf(cap, remainingDp).coerceAtLeast(
                TypeaheadMinHeight + TypeaheadChromeOffset,
            )
        }
    }

    Box(
        modifier = modifier
            .widthIn(max = OmniboxMaxWidth)
            .fillMaxWidth()
            .semantics { contentDescription = a11y },
    ) {
        // Layout peg: collapsed pill size only — never grows with typeahead.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (showDesktopTypeahead) {
                        Modifier
                            .width(collapsedWidth)
                            .height(collapsedHeight)
                    } else {
                        Modifier
                    },
                )
                .then(
                    if (isDesktop) {
                        Modifier.omniboxGlassChrome(
                            ringWidth = OmniboxRingWidth,
                            ringColor = OmniboxRingColor,
                            corner = OmniboxCorner,
                            fill = OmniboxShellFill,
                        )
                    } else {
                        Modifier
                            .shadow(
                                elevation = VitranElevation.large,
                                shape = OmniboxShape,
                                clip = false,
                            )
                            .clip(OmniboxShape)
                            .background(MaterialTheme.colorScheme.surface)
                    },
                )
                .padding(if (isDesktop) PaddingValues(0.dp) else PaddingValues(CompactShellPadding))
                .graphicsLayer { alpha = if (showDesktopTypeahead) 0f else 1f }
                .onSizeChanged { size ->
                    if (!showDesktopTypeahead) {
                        collapsedWidthPx = size.width
                        collapsedHeightPx = size.height
                    }
                }
                .onGloballyPositioned { coords ->
                    // Always report collapsed peg — floating search visibility ignores Popup.
                    latestOnCollapsedLayoutCoordinates(coords)
                    if (!showDesktopTypeahead) {
                        updateShellCap(coords)
                    }
                },
        ) {
            // Real input stays in the composition tree (not inside a focusable Popup) so
            // wheel events outside the overlay still reach the Home page scroll.
            OmniboxSearchRow(
                query = query,
                onQueryChange = onQueryChange,
                onSubmit = onSubmit,
                placeholder = placeholder,
                submitA11y = submitA11y,
                clearA11y = clearA11y,
                isRtl = isRtl,
                purpleDark = purpleDark,
                onFocusChanged = { textFieldFocused = it },
                focusRequester = anchorFocusRequester,
                // Desktop: keep enabled while expanded (hidden under the Popup visual).
                interactionEnabled = isDesktop || !expanded,
            )
        }

        // Visual panel only — focusable=false so it does not steal page wheel scroll.
        if (showDesktopTypeahead) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset.Zero,
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                ),
                onDismissRequest = { dismissToInitial() },
            ) {
                Column(
                    modifier = Modifier
                        .width(collapsedWidth)
                        .widthIn(max = OmniboxMaxWidth)
                        .omniboxGlassChrome(
                            ringWidth = OmniboxRingWidth,
                            ringColor = OmniboxRingColor,
                            corner = OmniboxCorner,
                            fill = OmniboxExpandedShellFill,
                        )
                        .heightIn(max = maxShellHeight)
                        .padding(GlassShellPadding)
                        .onGloballyPositioned { updateShellCap(it) },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .omniboxDesktopSearchFieldChrome(),
                    ) {
                        OmniboxSearchRow(
                            query = query,
                            onQueryChange = onQueryChange,
                            onSubmit = onSubmit,
                            placeholder = placeholder,
                            submitA11y = submitA11y,
                            clearA11y = clearA11y,
                            isRtl = isRtl,
                            purpleDark = purpleDark,
                            // Keep keyboard focus on the in-flow field so the Popup can
                            // stay non-focusable (page wheel scroll outside still works).
                            onFocusChanged = { focused ->
                                if (focused) {
                                    anchorFocusRequester.requestFocus()
                                }
                            },
                            interactionEnabled = true,
                        )
                    }
                    OmniboxTypeahead(
                        title = suggestionsTitle,
                        showTitle = query.isBlank(),
                        results = results,
                        privacy = privacy,
                        maxListHeight = typeaheadMaxHeight,
                        onResultClick = onResultClick,
                    )
                }
            }
        }
    }
}

internal fun Modifier.omniboxGlassChrome(
    ringWidth: Dp,
    ringColor: Color,
    corner: Dp,
    fill: Color = OmniboxShellFill,
): Modifier = this
    .drawBehind {
        val ringPx = ringWidth.toPx()
        val cornerPx = corner.toPx()
        val stroke = Stroke(width = ringPx)
        val inset = ringPx / 2f
        drawRoundRect(
            color = ringColor,
            topLeft = Offset(-inset, -inset),
            size = Size(size.width + ringPx, size.height + ringPx),
            cornerRadius = CornerRadius(cornerPx + inset),
            style = stroke,
        )
    }
    .shadow(
        elevation = VitranElevation.medium,
        shape = OmniboxShape,
        clip = false,
        ambientColor = Color.Black.copy(alpha = 0.08f),
        spotColor = Color.Black.copy(alpha = 0.08f),
    )
    .clip(OmniboxShape)
    .background(fill)

/**
 * shop.app desktop focused input:
 * `rounded-radius-max` + `bg rgba(255,255,255,.75)` +
 * `border 1px rgba(24,59,78,.06)` + `shadow-s` (`0 2px 8px rgba(0,0,0,.06)`).
 */
internal fun Modifier.omniboxDesktopSearchFieldChrome(): Modifier = this
    .shadow(
        elevation = SearchFieldFocusShadowElevation,
        shape = CircleShape,
        clip = false,
        ambientColor = SearchFieldFocusShadow,
        spotColor = SearchFieldFocusShadow,
    )
    .clip(CircleShape)
    .background(SearchFieldFocusFill)
    .border(
        width = 1.dp,
        color = SearchFieldFocusBorder,
        shape = CircleShape,
    )

@Composable
internal fun OmniboxSearchRow(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    placeholder: String,
    submitA11y: String,
    clearA11y: String,
    isRtl: Boolean,
    purpleDark: Color,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    interactionEnabled: Boolean = true,
) {
    BasicTextField(
        value = query,
        onValueChange = { if (interactionEnabled) onQueryChange(it) },
        modifier = modifier
            .fillMaxWidth()
            .height(VitranSize.touchTarget)
            .then(
                if (focusRequester != null) {
                    Modifier.focusRequester(focusRequester)
                } else {
                    Modifier
                },
            )
            .onFocusChanged { onFocusChanged(it.isFocused) },
        enabled = interactionEnabled,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = if (query.isEmpty()) TextAlign.Center else TextAlign.Start,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = VitranSpacing.xl, end = VitranSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = VitranSpacing.sm),
                    contentAlignment = if (query.isEmpty()) {
                        Alignment.Center
                    } else {
                        Alignment.CenterStart
                    },
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = PlaceholderAlpha),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (query.isEmpty()) {
                            Alignment.Center
                        } else {
                            Alignment.CenterStart
                        },
                    ) {
                        innerTextField()
                    }
                }

                if (query.isNotEmpty() && interactionEnabled) {
                    Box(
                        modifier = Modifier
                            .size(OmniboxActionSize)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onQueryChange("") },
                            )
                            .semantics { contentDescription = clearA11y },
                        contentAlignment = Alignment.Center,
                    ) {
                        VitranIcon(
                            painter = painterResource(Res.drawable.ic_close),
                            contentDescription = null,
                            size = VitranSize.iconSmall,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(OmniboxActionSize)
                        .shadow(
                            elevation = VitranElevation.large,
                            shape = CircleShape,
                            clip = false,
                            ambientColor = purpleDark.copy(alpha = 0.34f),
                            spotColor = purpleDark.copy(alpha = 0.34f),
                        )
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(
                            enabled = interactionEnabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onSubmit,
                        )
                        .semantics { contentDescription = submitA11y },
                    contentAlignment = Alignment.Center,
                ) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_arrow_right),
                        contentDescription = null,
                        modifier = Modifier.graphicsLayer { scaleX = if (isRtl) -1f else 1f },
                        size = SuggestionIconSize,
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
    )
}

/**
 * Typeahead list shared by hero Popup and floating bottom omnibox.
 *
 * @param resultsAbove When true (floating pill), list sits above the field —
 * padding is mirrored so spacing still hugs the input.
 */
@Composable
internal fun OmniboxTypeahead(
    title: String,
    showTitle: Boolean,
    results: List<OmniboxResult>,
    privacy: String,
    maxListHeight: Dp,
    onResultClick: (OmniboxResult) -> Unit,
    resultsAbove: Boolean = false,
) {
    // Title + rows + privacy share one scrollport so shell max-height never crops the panel.
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = if (resultsAbove) 0.dp else VitranSpacing.xs,
                bottom = if (resultsAbove) VitranSpacing.xs else 0.dp,
            )
            .heightIn(max = maxListHeight)
            .nestedScroll(TypeaheadNestedScrollConnection)
            .padding(horizontal = VitranSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        contentPadding = PaddingValues(
            top = if (resultsAbove) VitranSpacing.xs else 0.dp,
            bottom = if (resultsAbove) 0.dp else VitranSpacing.xs,
        ),
    ) {
        if (showTitle) {
            item(key = "title") {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        horizontal = VitranSpacing.sm,
                        vertical = VitranSpacing.xs,
                    ),
                )
            }
        }
        items(
            items = results,
            key = { it.id },
        ) { result ->
            when (result) {
                is OmniboxResult.Shop -> OmniboxShopRow(
                    shop = result,
                    onClick = { onResultClick(result) },
                )
                is OmniboxResult.Keyword -> OmniboxKeywordRow(
                    keyword = result,
                    onClick = { onResultClick(result) },
                )
            }
        }
        item(key = "privacy") {
            Text(
                text = privacy,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = VitranSpacing.sm,
                    vertical = VitranSpacing.sm,
                ),
            )
        }
    }
}

@Composable
internal fun OmniboxShopRow(
    shop: OmniboxResult.Shop,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    // shop.app: [avatar] [name][rating pill] clustered at start — rating is NOT end-aligned.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(if (pressed || hovered) SuggestionHover else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(VitranSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        ShopLogoAvatar(name = shop.name, logoUrl = shop.logoUrl)
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Text(
                text = shop.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            shop.rating?.let { rating ->
                ShopRatingPill(rating = rating)
            }
        }
    }
}

/** shop.app typeahead shop rating chip: light gray pill, `4.8` + star (LTR numerals). */
@Composable
private fun ShopRatingPill(rating: Float) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(ShopRatingPillBackground)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = formatShopRating(rating),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
            VitranIcon(
                painter = painterResource(Res.drawable.ic_star_filled),
                contentDescription = null,
                size = RatingStarSize,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ShopLogoAvatar(
    name: String,
    logoUrl: String?,
) {
    val letter = name.trim().firstOrNull()?.toString().orEmpty()
    Box(
        modifier = Modifier
            .size(VitranSize.avatarMedium)
            .clip(CircleShape)
            .background(ShopLogoFallback),
        contentAlignment = Alignment.Center,
    ) {
        if (logoUrl != null) {
            AsyncImage(
                model = resolveNetworkImageUrl(logoUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(ShopLogoFallback),
                error = ColorPainter(ShopLogoFallback),
            )
        } else if (letter.isNotEmpty()) {
            Text(
                text = letter,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
internal fun OmniboxKeywordRow(
    keyword: OmniboxResult.Keyword,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val onSurface = MaterialTheme.colorScheme.onSurface
    val label = remember(keyword.matchedPrefix, keyword.completion, onSurface) {
        buildAnnotatedString {
            if (keyword.matchedPrefix.isNotEmpty()) {
                withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = onSurface)) {
                    append(keyword.matchedPrefix)
                }
            }
            if (keyword.completion.isNotEmpty()) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = onSurface)) {
                    append(keyword.completion)
                }
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(if (pressed || hovered) SuggestionHover else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(VitranSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        Box(
            modifier = Modifier.size(VitranSize.avatarMedium),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = null,
                size = SuggestionIconSize,
                tint = onSurface.copy(alpha = SuggestionIconAlpha),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(end = VitranSpacing.sm),
        )
    }
}

private fun formatShopRating(rating: Float): String {
    val tenths = (rating * 10f).toInt().coerceAtLeast(0)
    return "${tenths / 10}.${tenths % 10}"
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB, widthDp = 640)
@Composable
private fun HeroOmniboxEmptyPreview() {
    VitranTheme {
        Box(modifier = Modifier.padding(VitranSpacing.xl)) {
            HeroOmnibox(
                query = "",
                onQueryChange = {},
                onSubmit = {},
                expanded = false,
                onExpandedChange = {},
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB, widthDp = 640, heightDp = 520)
@Composable
private fun HeroOmniboxQueryExpandedPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            Box(modifier = Modifier.padding(VitranSpacing.xl)) {
                HeroOmnibox(
                    query = "زن",
                    onQueryChange = {},
                    onSubmit = {},
                    expanded = true,
                    onExpandedChange = {},
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB, widthDp = 390, heightDp = 720)
@Composable
private fun HeroOmniboxMobileSheetPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides false) {
            OmniboxMobileSearchSheet(
                query = "زن",
                onQueryChange = {},
                onSubmit = {},
                onDismiss = {},
                onResultClick = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB, widthDp = 640, heightDp = 420)
@Composable
private fun HeroOmniboxExpandedDesktopPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            Box(modifier = Modifier.padding(VitranSpacing.xl)) {
                HeroOmnibox(
                    query = "",
                    onQueryChange = {},
                    onSubmit = {},
                    expanded = true,
                    onExpandedChange = {},
                )
            }
        }
    }
}
