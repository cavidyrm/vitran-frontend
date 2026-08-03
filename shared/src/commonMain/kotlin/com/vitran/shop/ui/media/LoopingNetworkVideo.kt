package com.vitran.shop.ui.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Muted, looping, autoplay network video for the hero collage center slot
 * (shop.app `hero-video` behavior). [posterUrl] is shown while loading / on
 * platforms without a native player (JVM) or as a web fallback.
 */
@Composable
expect fun LoopingNetworkVideo(
    url: String,
    posterUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)
