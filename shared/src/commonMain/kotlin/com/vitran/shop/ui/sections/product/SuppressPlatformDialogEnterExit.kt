package com.vitran.shop.ui.sections.product

import androidx.compose.runtime.Composable

/**
 * Suppresses the platform [androidx.compose.ui.window.Dialog] enter/exit
 * (scale/fade from center) so shop.app-style edge `translateX` owns the motion.
 */
@Composable
expect fun SuppressPlatformDialogEnterExit()
