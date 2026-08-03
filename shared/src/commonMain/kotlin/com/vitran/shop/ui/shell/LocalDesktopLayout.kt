package com.vitran.shop.ui.shell

import androidx.compose.runtime.compositionLocalOf

/**
 * Whether [AppShell] is in desktop layout (viewport ≥ [com.vitran.shop.ui.theme.VitranSize.desktopBreakpoint]).
 *
 * Prefer this over measuring content width — the side rail already consumes space,
 * so content-pane width can be below the breakpoint even on desktop.
 */
val LocalDesktopLayout = compositionLocalOf { false }
