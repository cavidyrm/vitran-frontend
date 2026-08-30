package com.vitran.shop.core.platform.share

/**
 * UIActivityViewController requires a presenting UIViewController, which is not
 * available from this module. iOS share is limited to opening a provided URL.
 */
class IosShareManager(
    private val urlLauncher: ExternalUrlLauncher,
) : ShareManager {
    override fun share(text: String, url: String?): Boolean {
        val target = url ?: return false
        return urlLauncher.open(target)
    }
}
