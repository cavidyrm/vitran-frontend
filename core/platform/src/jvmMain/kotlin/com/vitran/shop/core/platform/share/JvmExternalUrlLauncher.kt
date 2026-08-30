package com.vitran.shop.core.platform.share

import java.awt.Desktop
import java.net.URI

class JvmExternalUrlLauncher : ExternalUrlLauncher {
    override fun open(url: String): Boolean {
        return try {
            if (!Desktop.isDesktopSupported()) return false
            val desktop = Desktop.getDesktop()
            if (!desktop.isSupported(Desktop.Action.BROWSE)) return false
            desktop.browse(URI(url))
            true
        } catch (_: Exception) {
            false
        }
    }
}
