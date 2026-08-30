package com.vitran.shop.core.platform.share

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosExternalUrlLauncher : ExternalUrlLauncher {
    override fun open(url: String): Boolean {
        val nsUrl = NSURL.URLWithString(url) ?: return false
        val application = UIApplication.sharedApplication
        if (!application.canOpenURL(nsUrl)) return false
        application.openURL(nsUrl)
        return true
    }
}
