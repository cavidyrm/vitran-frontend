package com.vitran.shop.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.hero_omnibox_clear_a11y
import vitranshop.shared.generated.resources.hero_omnibox_close_a11y
import vitranshop.shared.generated.resources.hero_omnibox_placeholder
import vitranshop.shared.generated.resources.hero_omnibox_privacy
import vitranshop.shared.generated.resources.hero_omnibox_submit_a11y
import vitranshop.shared.generated.resources.hero_omnibox_suggestions_title
import vitranshop.shared.generated.resources.ic_arrow_right
import vitranshop.shared.generated.resources.ic_close

/** shop.app mobile search pill `rounded-[32px]`. */
private val MobileSearchPillCorner = 32.dp
private val MobileSearchPillShape = RoundedCornerShape(MobileSearchPillCorner)

/**
 * shop.app open mobile bar height (~104px): text row + submit row stacked
 * inside the tall pill (`flex-col` + `h-space-48` × 2 + pad).
 */
private val MobileSearchPillHeight = 104.dp
private val MobileSearchFieldRowHeight = 48.dp
private val MobileSearchActionSize = 40.dp
private val MobileSearchActionIconSize = 20.dp
private val MobileCloseButtonSize = 46.dp

/** shop.app `inset-x-space-12` around the floating search pill. */
private val MobileSearchHorizontalInset = VitranSpacing.md

/**
 * shop.app frosted sheet: `bg-[rgba(255,255,255,0.9)] backdrop-blur-[10px]`,
 * full-bleed (`inset-x-0`), no corner radius.
 */
private val MobileSheetFrost = Color.White.copy(alpha = 0.9f)
private const val PlaceholderAlpha = 0.6f

/**
 * shop.app compact search overlay above bottom nav:
 * full-bleed frosted surface (no rounded card), tall text-area-like search pill
 * that slides up from below (`translateY(100px)`, 400ms ease-out).
 */
@Composable
fun OmniboxMobileSearchSheet(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    onResultClick: (OmniboxResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val placeholder = stringResource(Res.string.hero_omnibox_placeholder)
    val submitA11y = stringResource(Res.string.hero_omnibox_submit_a11y)
    val clearA11y = stringResource(Res.string.hero_omnibox_clear_a11y)
    val closeA11y = stringResource(Res.string.hero_omnibox_close_a11y)
    val suggestionsTitle = stringResource(Res.string.hero_omnibox_suggestions_title)
    val privacy = stringResource(Res.string.hero_omnibox_privacy)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val purpleDark = VitranTheme.extraColors.purpleDark
    val results = remember(query) { mockOmniboxResults(query) }
    val focusRequester = remember { FocusRequester() }
    val density = LocalDensity.current

    val overlayProgress = remember { Animatable(0f) }
    val searchOffsetPx = remember {
        Animatable(with(density) {
            VitranAnimation.Omnibox.MOBILE_SEARCH_ENTER_OFFSET_DP.dp.toPx()
        })
    }
    val searchAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        launch {
            overlayProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = VitranAnimation.Omnibox.MOBILE_OVERLAY_ENTER_MS,
                    easing = VitranAnimation.Omnibox.ExpandEasing,
                ),
            )
        }
        launch {
            searchOffsetPx.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = VitranAnimation.Omnibox.MOBILE_SEARCH_ENTER_MS,
                    easing = EaseOut,
                ),
            )
        }
        launch {
            searchAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = VitranAnimation.Omnibox.MOBILE_SEARCH_ENTER_MS,
                    easing = EaseOut,
                ),
            )
        }
    }

    val overlayEnterOffsetPx = with(density) { 12.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                val t = overlayProgress.value
                alpha = t
                translationY = (1f - t) * overlayEnterOffsetPx
            },
    ) {
        // Full-bleed frosted fill — square corners (shop.app, no rounded sheet card).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MobileSheetFrost),
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = VitranSpacing.lg,
                        end = VitranSpacing.lg,
                        top = VitranSpacing.lg,
                    ),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Box(
                    modifier = Modifier
                        .size(MobileCloseButtonSize)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            shape = CircleShape,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDismiss,
                        )
                        .semantics { contentDescription = closeA11y },
                    contentAlignment = Alignment.Center,
                ) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_close),
                        contentDescription = null,
                        size = VitranSize.iconMedium,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = VitranSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
                contentPadding = PaddingValues(
                    top = VitranSpacing.sm,
                    bottom = VitranSpacing.sm,
                ),
            ) {
                if (query.isBlank()) {
                    item(key = "title") {
                        Text(
                            text = suggestionsTitle,
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MobileSearchHorizontalInset,
                        end = MobileSearchHorizontalInset,
                        bottom = VitranSpacing.md,
                    )
                    .offset(y = with(density) { searchOffsetPx.value.toDp() })
                    .graphicsLayer { alpha = searchAlpha.value },
            ) {
                OmniboxMobileSearchField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSubmit = onSubmit,
                    placeholder = placeholder,
                    submitA11y = submitA11y,
                    clearA11y = clearA11y,
                    isRtl = isRtl,
                    purpleDark = purpleDark,
                    focusRequester = focusRequester,
                )
            }
        }
    }
}

/**
 * shop.app mobile open search control: tall pill (`rounded-[32px]`, ~104px)
 * with text on top and submit aligned end below — reads like a text area.
 */
@Composable
private fun OmniboxMobileSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    placeholder: String,
    submitA11y: String,
    clearA11y: String,
    isRtl: Boolean,
    purpleDark: Color,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(MobileSearchPillHeight)
            .shadow(
                elevation = VitranElevation.large,
                shape = MobileSearchPillShape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.14f),
                spotColor = Color.Black.copy(alpha = 0.14f),
            )
            .clip(MobileSearchPillShape)
            .background(Color.White)
            .padding(VitranSpacing.xs),
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(MobileSearchFieldRowHeight)
                .focusRequester(focusRequester)
                .padding(horizontal = VitranSpacing.lg),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = PlaceholderAlpha),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    innerTextField()
                }
            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MobileSearchFieldRowHeight)
                .padding(horizontal = VitranSpacing.xs),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(MobileSearchActionSize)
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
                    .size(MobileSearchActionSize)
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
                    size = MobileSearchActionIconSize,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}
