package com.vitran.shop.feature.home.data

/**
 * Values sent on `GET /home/screen` and `GET /home/sections/{key}`.
 * [appVersion] matches `vitran.versionName` in `gradle.properties`.
 */
data class HomeClientContext(
    val platform: String,
    val appVersion: String,
) {
    companion object {
        const val APP_VERSION: String = "1.0.0"

        fun current(): HomeClientContext = HomeClientContext(
            platform = currentHomePlatform(),
            appVersion = APP_VERSION,
        )
    }
}

/** `ios`, `android`, or `web`. Desktop uses `web`. */
expect fun currentHomePlatform(): String
