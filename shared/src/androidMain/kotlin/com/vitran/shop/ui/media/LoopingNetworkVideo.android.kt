package com.vitran.shop.ui.media

import android.view.TextureView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import coil3.compose.AsyncImage

private const val BrowserUserAgent =
    "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"

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

    DisposableEffect(player) {
        onDispose {
            player.setVideoTextureView(null)
            player.release()
        }
    }

    val semanticsModifier = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
    } else {
        Modifier
    }

    Box(modifier = modifier.then(semanticsModifier)) {
        AsyncImage(
            model = posterUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
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
            modifier = Modifier.fillMaxSize(),
        )
    }
}
