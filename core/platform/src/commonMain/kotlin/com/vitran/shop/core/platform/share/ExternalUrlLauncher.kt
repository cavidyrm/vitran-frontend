package com.vitran.shop.core.platform.share

/**
 * Opens a user-facing URL in the platform browser / handler.
 * Presentation owns when to launch; Domain/Data never call this.
 *
 * @return true when the platform accepted the URL, false when it cannot open it.
 */
interface ExternalUrlLauncher {
    fun open(url: String): Boolean
}
