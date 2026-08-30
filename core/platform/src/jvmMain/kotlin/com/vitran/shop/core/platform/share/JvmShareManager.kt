package com.vitran.shop.core.platform.share

/**
 * Desktop has no system share sheet. Copies are not implemented here —
 * opening the URL is the supported action when a URL is provided.
 */
class JvmShareManager(
    private val urlLauncher: ExternalUrlLauncher,
) : ShareManager {
    override fun share(text: String, url: String?): Boolean {
        val target = url ?: return false
        return urlLauncher.open(target)
    }
}
