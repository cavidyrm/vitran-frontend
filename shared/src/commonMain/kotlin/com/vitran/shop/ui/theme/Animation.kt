package com.vitran.shop.ui.theme

/**
 * Motion timings for recurring UI chrome.
 * Prefer these over hard-coded millis in tooltip / hover affordances.
 */
object VitranAnimation {
    object Tooltip {
        const val DURATION_MS = 150
        const val DISMISS_DELAY_MS = 80L
    }

    /**
     * Desktop Home hero collage — matched to shop.app CSS:
     * shared `--progress` sweeps -1→1 over ~7s; per-slot `--delay` feeds a pow curve.
     */
    object HeroCollage {
        /** Full theme cycle (`--progress` from -1 to +1). */
        const val CYCLE_MS = 7_000

        /** shop.app `.hero { --pow: 23; --delay-factor: 25 }`. */
        const val POW = 23
        const val DELAY_FACTOR = 25f

        /** shop.app `card-idle`: 4s ease-in-out infinite alternate, translateY 5%. */
        const val IDLE_DURATION_MS = 4_000
        const val IDLE_TRANSLATE_FRACTION = 0.05f
    }
}
