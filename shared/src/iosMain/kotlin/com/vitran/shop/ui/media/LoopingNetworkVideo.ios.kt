package com.vitran.shop.ui.media

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.UIKitView
import coil3.compose.AsyncImage
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.AVFoundation.AVLayerVideoGravityResizeAspect
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.muted
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.CoreGraphics.CGRectZero
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.UIKit.UIView
import platform.darwin.NSObjectProtocol

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun LoopingNetworkVideo(
    url: String,
    posterUrl: String,
    modifier: Modifier,
    contentDescription: String?,
) {
    val player = remember(url) {
        val item = NSURL.URLWithString(url)?.let { AVPlayerItem(uRL = it) }
        AVPlayer(playerItem = item).apply {
            muted = true
            play()
        }
    }

    DisposableEffect(player) {
        val center = NSNotificationCenter.defaultCenter
        val observer: NSObjectProtocol = center.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = null,
        ) { _ ->
            player.seekToTime(CMTimeMake(value = 0, timescale = 1))
            player.play()
        }
        onDispose {
            center.removeObserver(observer)
            player.pause()
            player.replaceCurrentItemWithPlayerItem(null)
        }
    }

    Box(modifier = modifier) {
        AsyncImage(
            model = resolveNetworkImageUrl(posterUrl),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
        UIKitView(
            factory = { HeroVideoUIView(player) },
            modifier = Modifier.fillMaxSize(),
            update = { view -> view.bind(player) },
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private class HeroVideoUIView(
    player: AVPlayer,
) : UIView(frame = CGRectZero.readValue()) {
    private val playerLayer: AVPlayerLayer =
        AVPlayerLayer.playerLayerWithPlayer(player).also {
            it.videoGravity = AVLayerVideoGravityResizeAspect
            layer.addSublayer(it)
        }

    init {
        clipsToBounds = true
    }

    fun bind(player: AVPlayer) {
        if (playerLayer.player !== player) {
            playerLayer.player = player
        }
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        playerLayer.frame = bounds
    }
}
