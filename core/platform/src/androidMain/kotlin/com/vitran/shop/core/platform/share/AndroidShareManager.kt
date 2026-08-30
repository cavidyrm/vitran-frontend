package com.vitran.shop.core.platform.share

import android.content.Context
import android.content.Intent

class AndroidShareManager(
    private val context: Context,
) : ShareManager {
    override fun share(text: String, url: String?): Boolean {
        return try {
            val body = if (url.isNullOrBlank()) text else "$text\n$url"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, null))
            true
        } catch (_: Exception) {
            false
        }
    }
}
