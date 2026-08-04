package com.vitran.shop.ui.media

import android.view.TextureView
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import coil3.compose.AsyncImage

private const val BrowserUserAgent =
    "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"

private const val DefaultVideoAspect = 16f / 9f

/**
 * Android hero video: muted loop + poster. Sized with contain (`object-fit: contain`)
 * so letterboxing matches shop.app rather than cropping.
 */
@Composable
actual fun LoopingNetworkVideo(
    url: String,
    posterUrl: String,
    modifier: Modifier,
    contentDescription: String?,
) {
    val context = LocalContext.current
    val player = remember(url) {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(BrowserUserAgent)
            .setAllowCrossProtocolRedirects(true)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(url))
        ExoPlayer.Builder(context).build().apply {
            setMediaSource(mediaSource)
            repeatMode = Player.REPEAT_MODE_ONE
            volume = 0f
            playWhenReady = true
            prepare()
        }
    }

    var videoAspect by remember(url) { mutableFloatStateOf(DefaultVideoAspect) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.height > 0) {
                    videoAspect = (videoSize.width * videoSize.pixelWidthHeightRatio) /
                        videoSize.height.toFloat()
                }
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.setVideoTextureView(null)
            player.release()
        }
    }

    val semanticsModifier = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
    } else {
        Modifier
    }

    BoxWithConstraints(
        modifier = modifier.then(semanticsModifier),
        contentAlignment = Alignment.Center,
    ) {
        val density = LocalDensity.current
        val maxW = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val maxH = constraints.maxHeight.toFloat().coerceAtLeast(1f)
        val fitted = containSize(maxW, maxH, videoAspect)
        val fittedW = with(density) { fitted.first.toDp() }
        val fittedH = with(density) { fitted.second.toDp() }

        AsyncImage(
            model = posterUrl,
            contentDescription = null,
            modifier = Modifier
                .size(fittedW, fittedH)
                .align(Alignment.Center),
            contentScale = ContentScale.Fit,
        )
        AndroidView(
            factory = { ctx ->
                TextureView(ctx).also { textureView ->
                    player.setVideoTextureView(textureView)
                }
            },
            update = { textureView ->
                player.setVideoTextureView(textureView)
            },
            modifier = Modifier
                .size(fittedW, fittedH)
                .align(Alignment.Center),
        )
    }
}

/** Largest size inside [maxW]×[maxH] that keeps [aspect] (width/height). */
private fun containSize(maxW: Float, maxH: Float, aspect: Float): Pair<Float, Float> {
    val safeAspect = aspect.coerceAtLeast(0.01f)
    return if (maxW / maxH > safeAspect) {
        val h = maxH
        h * safeAspect to h
    } else {
        val w = maxW
        w to w / safeAspect
    }
}
