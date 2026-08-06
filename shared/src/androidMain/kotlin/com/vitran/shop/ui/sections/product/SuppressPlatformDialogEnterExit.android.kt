package com.vitran.shop.ui.sections.product

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider

@Composable
actual fun SuppressPlatformDialogEnterExit() {
    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.parent as? DialogWindowProvider)?.window
        val previousDim = window?.attributes?.dimAmount
        window?.setWindowAnimations(0)
        window?.setDimAmount(0f)
        // Keep dialog above content without system dim (we draw our own scrim).
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        onDispose {
            if (previousDim != null) {
                window.setDimAmount(previousDim)
            }
        }
    }
}
