package com.vitran.shop.ui.media

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

/**
 * Desktop JVM: poster only for this mock phase (no bundled native video engine).
 * [ContentScale.Fit] matches shop.app `object-fit: contain`.
 */
@Composable
actual fun LoopingNetworkVideo(
    url: String,
    posterUrl: String,
    modifier: Modifier,
    contentDescription: String?,
) {
    @Suppress("UNUSED_VARIABLE")
    val videoUrl = url
    AsyncImage(
        model = resolveNetworkImageUrl(posterUrl),
        contentDescription = contentDescription,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
    )
}
