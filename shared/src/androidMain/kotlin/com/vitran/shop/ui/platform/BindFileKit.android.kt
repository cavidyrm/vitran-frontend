package com.vitran.shop.ui.platform

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

@Composable
actual fun BindFileKit() {
    val context = LocalContext.current
    DisposableEffect(context) {
        val activity = context as? ComponentActivity
        if (activity != null) {
            FileKit.init(activity)
        }
        onDispose { }
    }
}
