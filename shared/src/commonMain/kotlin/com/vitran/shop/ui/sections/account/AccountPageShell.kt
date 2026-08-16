package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.FloatingSearchFab
import com.vitran.shop.ui.components.FloatingSearchOmnibox
import com.vitran.shop.ui.components.OmniboxMobileSearchSheet
import com.vitran.shop.ui.components.OmniboxResult
import com.vitran.shop.ui.components.SiteFooter
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_back_a11y
import vitranshop.shared.generated.resources.account_nav_profile
import vitranshop.shared.generated.resources.ic_chevron_right

private val MobileOmniboxBackdropBlur = 10.dp

@Composable
internal fun AccountPageShell(
    dest: AccountDest,
    onDestClick: (AccountDest) -> Unit,
    onSavedClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    backTitle: String? = null,
    showSearch: Boolean = true,
    contentMaxWidth: Dp = AccountTokens.ContentMaxWidth,
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val isDesktop = LocalDesktopLayout.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val pagePad = if (isDesktop) AccountTokens.PagePadDesktop else AccountTokens.PagePadCompact

    var query by remember { mutableStateOf("") }
    var omniboxExpanded by remember { mutableStateOf(false) }
    var omniboxBoundsInRoot by remember { mutableStateOf(Rect.Zero) }
    var screenOriginInRoot by remember { mutableStateOf(Offset.Zero) }
    val showFloatingSearch = showSearch && (isDesktop || !omniboxExpanded)

    fun dismissOmnibox() {
        query = ""
        omniboxExpanded = false
        focusManager.clearFocus()
    }

    val expandedState = rememberUpdatedState(omniboxExpanded)
    val boundsState = rememberUpdatedState(omniboxBoundsInRoot)
    val originState = rememberUpdatedState(screenOriginInRoot)
    val desktopState = rememberUpdatedState(isDesktop)
    val onDismissOmnibox by rememberUpdatedState(newValue = { dismissOmnibox() })

    LaunchedEffect(isDesktop, omniboxExpanded, scrollState) {
        if (!isDesktop || !omniboxExpanded) return@LaunchedEffect
        snapshotFlow { scrollState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { scrolling -> scrolling }
            .collect { onDismissOmnibox() }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                screenOriginInRoot = coords.positionInRoot()
            }
            .then(
                if (isDesktop) {
                    Modifier.pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                if (!desktopState.value || !expandedState.value) continue
                                val change = event.changes.firstOrNull() ?: continue
                                val isDown = change.pressed && !change.previousPressed
                                if (!isDown) continue
                                val rootPos = change.position + originState.value
                                if (!boundsState.value.contains(rootPos)) {
                                    dismissOmnibox()
                                }
                            }
                        }
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (!isDesktop && omniboxExpanded) {
                        Modifier.blur(MobileOmniboxBackdropBlur)
                    } else {
                        Modifier
                    },
                )
                .verticalScroll(
                    state = scrollState,
                    enabled = isDesktop || !omniboxExpanded,
                ),
        ) {
            if (!isDesktop && onBack != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.Button, onClick = onBack)
                        .padding(
                            horizontal = pagePad,
                            vertical = VitranSpacing.md,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                ) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_chevron_right),
                        contentDescription = stringResource(Res.string.account_back_a11y),
                        size = VitranSize.iconMedium,
                        modifier = Modifier.graphicsLayer { scaleX = if (isRtl) 1f else -1f },
                    )
                    VitranText(
                        text = backTitle ?: stringResource(Res.string.account_nav_profile),
                        style = VitranTextStyle.Title,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            val clusterMaxWidth =
                AccountTokens.SubNavWidth + AccountTokens.NavContentGap + contentMaxWidth
            if (isDesktop) {
                // shop.app: sub-nav + content cluster centered together in the pane.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = pagePad,
                            end = pagePad,
                            top = VitranSpacing.xxl,
                        ),
                ) {
                    Row(
                        modifier = Modifier
                            .widthIn(max = clusterMaxWidth)
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.spacedBy(AccountTokens.NavContentGap),
                    ) {
                        AccountSubNav(
                            dest = dest,
                            onDestClick = onDestClick,
                            onSavedClick = onSavedClick,
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(max = contentMaxWidth),
                            verticalArrangement = Arrangement.spacedBy(AccountTokens.SectionGap),
                            content = content,
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = pagePad,
                            end = pagePad,
                            top = VitranSpacing.lg,
                        ),
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = contentMaxWidth)
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        verticalArrangement = Arrangement.spacedBy(AccountTokens.SectionGap),
                        content = content,
                    )
                }
            }
            SiteFooter(
                onLinkClick = { /* mock — footer destinations not wired yet */ },
                onLanguageClick = { /* mock — language settings not wired yet */ },
                onDownloadClick = { /* mock — store deep link not wired yet */ },
                modifier = Modifier.fillMaxWidth(),
            )
            if (bottomBar != null) {
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.height(88.dp),
                )
            }
        }

        if (isDesktop) {
            FloatingSearchOmnibox(
                visible = showFloatingSearch,
                query = query,
                onQueryChange = { query = it },
                expanded = omniboxExpanded,
                onExpandedChange = { omniboxExpanded = it },
                onSubmit = { /* mock */ },
                onDismiss = { dismissOmnibox() },
                onBoundsInRoot = { omniboxBoundsInRoot = it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(30f),
            )
        } else {
            FloatingSearchFab(
                visible = showFloatingSearch,
                onClick = { omniboxExpanded = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .zIndex(100f),
            )
        }

        if (!isDesktop && omniboxExpanded) {
            OmniboxMobileSearchSheet(
                query = query,
                onQueryChange = { query = it },
                onSubmit = { /* mock */ },
                onDismiss = { dismissOmnibox() },
                onResultClick = { result ->
                    query = when (result) {
                        is OmniboxResult.Shop -> result.name
                        is OmniboxResult.Keyword -> result.fullText
                    }
                    dismissOmnibox()
                },
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(30f),
            )
        }

        if (bottomBar != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .zIndex(40f),
            ) {
                bottomBar()
            }
        }
    }
}
