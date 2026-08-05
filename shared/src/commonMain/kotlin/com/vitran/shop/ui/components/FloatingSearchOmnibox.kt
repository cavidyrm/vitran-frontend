package com.vitran.shop.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.shell.LocalShellViewportHeight
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.hero_omnibox_clear_a11y
import vitranshop.shared.generated.resources.hero_omnibox_placeholder
import vitranshop.shared.generated.resources.hero_omnibox_privacy
import vitranshop.shared.generated.resources.hero_omnibox_submit_a11y
import vitranshop.shared.generated.resources.hero_omnibox_suggestions_title
import kotlin.time.Duration.Companion.milliseconds

/**
 * Desktop floating search pill — shop.app
 * `fixed inset-x-0 bottom-space-36 … max-w-[600px] px-space-16`.
 *
 * Bottom-aligned in the Home overlay so expansion grows **upward**.
 * Typeahead renders above the field (list then input).
 *
 * Show/hide via `translateY(200%)` ↔ `0` (always composed).
 */
@Composable
fun FloatingSearchOmnibox(
    visible: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onBoundsInRoot: (Rect) -> Unit = {},
) {
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    val viewportHeight = LocalShellViewportHeight.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val purpleDark = VitranTheme.extraColors.purpleDark

    val placeholder = stringResource(Res.string.hero_omnibox_placeholder)
    val submitA11y = stringResource(Res.string.hero_omnibox_submit_a11y)
    val clearA11y = stringResource(Res.string.hero_omnibox_clear_a11y)
    val suggestionsTitle = stringResource(Res.string.hero_omnibox_suggestions_title)
    val privacy = stringResource(Res.string.hero_omnibox_privacy)

    val results = remember(query) { mockOmniboxResults(query) }
    val focusRequester = remember { FocusRequester() }

    var textFieldFocused by remember { mutableStateOf(false) }

    val latestOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val latestOnQueryChange by rememberUpdatedState(onQueryChange)
    val latestExpanded by rememberUpdatedState(expanded)
    val latestOnDismiss by rememberUpdatedState(onDismiss)
    val latestOnBoundsInRoot by rememberUpdatedState(onBoundsInRoot)

    LaunchedEffect(textFieldFocused) {
        if (textFieldFocused) {
            latestOnExpandedChange(true)
        } else {
            delay(VitranAnimation.Omnibox.COLLAPSE_DELAY_MS.milliseconds)
            if (!textFieldFocused && latestExpanded) {
                latestOnQueryChange("")
                latestOnExpandedChange(false)
                focusManager.clearFocus()
            }
        }
    }

    // When floating chrome becomes hidden, clear focus/query so hero stays clean.
    LaunchedEffect(visible) {
        if (!visible && latestExpanded) {
            latestOnDismiss()
        }
    }

    val hideOffsetPx = with(density) {
        OmniboxFloatingSlotHeight.toPx() * VitranAnimation.Omnibox.FLOAT_PILL_HIDE_FRACTION
    }
    val translationY by animateFloatAsState(
        targetValue = if (visible) 0f else hideOffsetPx,
        animationSpec = tween(
            durationMillis = VitranAnimation.Omnibox.FLOAT_ENTER_MS,
            easing = VitranAnimation.Omnibox.FloatEasing,
        ),
        label = "floatingSearchOmniboxY",
    )

    // shop.app typeahead ~45dvh; leave room for field + shell + bottom inset.
    val typeaheadMaxHeight = (
        viewportHeight * TypeaheadViewportFraction - TypeaheadChromeOffset
        ).coerceAtLeast(TypeaheadMinHeight)
    val panelMaxHeight = viewportHeight * TypeaheadViewportFraction + OmniboxFloatingSlotHeight

    val onResultClick: (OmniboxResult) -> Unit = { result ->
        val text = when (result) {
            is OmniboxResult.Shop -> result.name
            is OmniboxResult.Keyword -> result.fullText
        }
        latestOnQueryChange(text)
        onSubmit()
        latestOnDismiss()
    }

    // Sized to content and bottom-aligned by the HomeScreen host — expansion grows up.
    // Do not wrap in a fixed 66dp measure box; that max-height-clips the typeahead.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = VitranSpacing.lg,
                end = VitranSpacing.lg,
                bottom = OmniboxFloatingBottomInset,
            )
            .graphicsLayer { this.translationY = translationY },
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = OmniboxMaxWidth)
                .fillMaxWidth()
                .heightIn(max = panelMaxHeight)
                .wrapContentHeight(align = Alignment.Bottom)
                .omniboxGlassChrome(
                    ringWidth = OmniboxRingWidth,
                    ringColor = OmniboxRingColor,
                    corner = OmniboxCorner,
                    fill = if (expanded) OmniboxExpandedShellFill else OmniboxShellFill,
                )
                .padding(if (expanded) GlassShellPadding else 0.dp)
                .onGloballyPositioned { coords ->
                    latestOnBoundsInRoot(coords.boundsInRoot())
                },
        ) {
            if (expanded) {
                OmniboxTypeahead(
                    title = suggestionsTitle,
                    showTitle = query.isBlank(),
                    results = results,
                    privacy = privacy,
                    maxListHeight = typeaheadMaxHeight,
                    onResultClick = onResultClick,
                    resultsAbove = true,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (expanded) {
                            Modifier.omniboxDesktopSearchFieldChrome()
                        } else {
                            Modifier
                        },
                    ),
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
                    onFocusChanged = { textFieldFocused = it },
                    focusRequester = focusRequester,
                    interactionEnabled = visible,
                )
            }
        }
    }
}
