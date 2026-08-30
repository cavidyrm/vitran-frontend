package com.vitran.shop.core.platform.share

import android.content.Context
import android.content.Intent
import android.net.Uri

class AndroidExternalUrlLauncher(
    private val context: Context,
) : ExternalUrlLauncher {
    override fun open(url: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
