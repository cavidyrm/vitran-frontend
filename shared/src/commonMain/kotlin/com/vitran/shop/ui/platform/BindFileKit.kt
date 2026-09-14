package com.vitran.shop.ui.platform

import androidx.compose.runtime.Composable

/**
 * Platform setup for FileKit dialogs. Required on Android (Activity Result registry)
 * and Desktop JVM (app id). No-op on iOS, JS, and Wasm.
 */
@Composable
expect fun BindFileKit()
