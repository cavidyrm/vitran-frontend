package com.vitran.shop.ui.media

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

/**
 * JS Skia canvas cannot layer a DOM `<video>` under Compose floating cards.
 * Use the shop.app poster (proxied via [resolveNetworkImageUrl]) so z-order and
 * `graphicsLayer` motion stay correct. Android/iOS play the real H.264 stream.
 *
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
