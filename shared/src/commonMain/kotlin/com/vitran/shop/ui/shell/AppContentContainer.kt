package com.vitran.shop.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing

/** shop.app content-frame border (`border-[#EBEBEB]`). */
private val ContentFrameBorder = Color(0xFFEBEBEB)

/**
 * Main page content frame matching shop.app.
 *
 * [framed] must follow the **viewport** breakpoint (same as [AppShell]), not the
 * content column width — the rail already consumes horizontal space.
 *
 * Fill is page canvas `#FBFBFB` (not card white). Cards / omnibox stay on `surface`.
 *
 * - `framed = true` (desktop): inset card, 28.dp radius, `#EBEBEB` border, soft shadow.
 * - `framed = false` (compact): full-bleed, no chrome.
 */
@Composable
fun AppContentContainer(
    framed: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val pageColor = MaterialTheme.colorScheme.background
    if (framed) {
        val shape = RoundedCornerShape(VitranRadius.extraLarge)
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = VitranSpacing.sm,
                    end = VitranSpacing.sm,
                    bottom = VitranSpacing.sm,
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = VitranElevation.medium,
                        shape = shape,
                        ambientColor = Color.Black.copy(alpha = 0.06f),
                        spotColor = Color.Black.copy(alpha = 0.06f),
                    )
                    // Do not clip children — expanded omnibox / overlays must paint past
                    // the frame edge (shop.app absolute typeahead). Shape still paints bg/border.
                    .background(pageColor, shape)
                    .border(width = 1.dp, color = ContentFrameBorder, shape = shape),
                content = content,
            )
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(pageColor),
            content = content,
        )
    }
}
