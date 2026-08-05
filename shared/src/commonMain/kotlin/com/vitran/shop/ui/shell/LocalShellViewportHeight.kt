package com.vitran.shop.ui.shell

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp

/**
 * Viewport width from [AppShell]'s root [androidx.compose.foundation.layout.BoxWithConstraints].
 * Prefer this over content-pane width for CSS-like media breakpoints (`md` / `xl`).
 */
val LocalShellViewportWidth = compositionLocalOf { 400.dp }

/**
 * Viewport height from [AppShell]'s root [androidx.compose.foundation.layout.BoxWithConstraints].
 * Used for shop.app-style typeahead `max-h: calc(45dvh - 88px)`.
 */
val LocalShellViewportHeight = compositionLocalOf { 900.dp }
