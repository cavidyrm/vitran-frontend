package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.DownloadAppBanner
import com.vitran.shop.ui.sections.home.HomeHero

/**
 * Home screen host. Sections are added one at a time.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    var omniboxExpanded by remember { mutableStateOf(false) }
    var omniboxBoundsInRoot by remember { mutableStateOf(Rect.Zero) }
    var screenOriginInRoot by remember { mutableStateOf(Offset.Zero) }
    var dismissOmnibox by remember { mutableStateOf<(() -> Unit)?>(null) }

    val expandedState = rememberUpdatedState(omniboxExpanded)
    val boundsState = rememberUpdatedState(omniboxBoundsInRoot)
    val originState = rememberUpdatedState(screenOriginInRoot)
    val dismissState = rememberUpdatedState(dismissOmnibox)

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                screenOriginInRoot = coords.positionInRoot()
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        // Initial: see outside presses before children; dismiss like shop.app.
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        if (!expandedState.value) continue
                        val change = event.changes.firstOrNull() ?: continue
                        val isDown = change.pressed && !change.previousPressed
                        if (!isDown) continue
                        val rootPos = change.position + originState.value
                        if (!boundsState.value.contains(rootPos)) {
                            dismissState.value?.invoke()
                        }
                    }
                }
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = scrollState,
                    enabled = !omniboxExpanded,
                ),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                HomeHero(
                    modifier = Modifier.fillMaxWidth(),
                    onOmniboxExpandedChange = { omniboxExpanded = it },
                    onOmniboxBoundsInRoot = { omniboxBoundsInRoot = it },
                    onOmniboxDismissHandlerReady = { dismissOmnibox = it },
                )
                DownloadAppBanner(
                    onClick = {
                        if (omniboxExpanded) {
                            dismissOmnibox?.invoke()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .zIndex(10f),
                )
            }
        }
    }
}
