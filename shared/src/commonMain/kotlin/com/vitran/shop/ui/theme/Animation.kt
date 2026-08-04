package com.vitran.shop.ui.theme

import androidx.compose.animation.core.CubicBezierEasing

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

        /**
         * shop.app `.hero-card`:
         * `transition-transform duration-200` + `hover:scale-[1.025]` / `active:scale-[0.98]`.
         */
        const val CARD_INTERACTION_MS = 200
        const val CARD_HOVER_SCALE = 1.025f
        const val CARD_PRESS_SCALE = 0.98f

        /** Tailwind `ease` ≈ `cubic-bezier(0.4, 0, 0.2, 1)`. */
        val CardInteractionEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

        /** shop.app product mouse tilt: `rotateX/Y(mouse * ±10deg)`. */
        const val CARD_TILT_DEG = 10f

        /** shop.app brand mouse tilt: `rotateX/Y(mouse * ±15deg)`. */
        const val BRAND_CARD_TILT_DEG = 15f

        /** shop.app shadow chase: `translate(mouse * -3em)`. */
        const val CARD_SHADOW_TRANSLATE_EM = 3f

        /** shop.app product gradient chase: `translate(mouse * 25%)`. */
        const val CARD_GRADIENT_TRANSLATE_FRACTION = 0.25f

        /** shop.app brand bg parallax: `translate(mouse * 5%)` on 150% image. */
        const val BRAND_BG_PARALLAX_FRACTION = 0.05f
    }

    /**
     * Home hero omnibox expand/collapse — shop.app
     * `lg:duration-[320ms] lg:ease-[cubic-bezier(.2,0,0,1)]`.
     */
    object Omnibox {
        const val EXPAND_MS = 320
        val ExpandEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

        /** Brief delay so suggestion taps register before focus-loss collapse. */
        const val COLLAPSE_DELAY_MS = 150L

        /**
         * Compact sheet frosted overlay enter — shop.app
         * `_omniboxFullscreenOverlayEnter`: opacity + translateY(12px) + scale(.985).
         */
        const val MOBILE_OVERLAY_ENTER_MS = 320

        /**
         * Compact search pill enter — shop.app
         * `_slideDownAndFadeIn_omjax_1`: translateY(100px)→0, 0.4s ease-out.
         */
        const val MOBILE_SEARCH_ENTER_MS = 400
        const val MOBILE_SEARCH_ENTER_OFFSET_DP = 100
    }

    /**
     * Home category mosaic tiles — shop.app
     * `transition-transform duration-150 ease-out` + `hover:scale-110` on the image.
     */
    object CategoryMosaic {
        const val TILE_HOVER_MS = 150
        const val TILE_HOVER_SCALE = 1.1f
        /** Tailwind `ease-out` ≈ `cubic-bezier(0, 0, 0.2, 1)`. */
        val TileHoverEasing = CubicBezierEasing(0f, 0f, 0.2f, 1f)
    }
}
