package com.vitran.shop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun VitranTheme(
    content: @Composable () -> Unit,
) {

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalVitranExtraColors provides DefaultVitranExtraColors,
    ) {
        MaterialTheme(
            colorScheme = VitranLightColorScheme,
            typography = vitranTypography(),
            shapes = VitranShapes.material,
            content = content,
        )
    }
}

private val DefaultVitranExtraColors = VitranExtraColors()

object VitranTheme {
    val extraColors: VitranExtraColors
        @Composable
        @ReadOnlyComposable
        get() = LocalVitranExtraColors.current
}
