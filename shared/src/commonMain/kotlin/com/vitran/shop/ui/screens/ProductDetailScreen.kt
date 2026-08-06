package com.vitran.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.sections.product.MockProductCatalog
import com.vitran.shop.ui.sections.product.ProductDetailInfoColumn
import com.vitran.shop.ui.sections.product.ProductDetailMediaSection
import com.vitran.shop.ui.sections.product.ProductDetailMerchantHeader
import com.vitran.shop.ui.shell.LocalShellViewportWidth
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing

/**
 * Product detail (shop.app `/products/{id}/{slug}`).
 *
 * Layout (measured on live shop.app LTR, mirrored via [VitranTheme] RTL):
 * - Compact (`< md`): merchant (+ Visit store) → media → info
 * - Mid+ (`≥ md`): one row — gallery `weight(1)` | buy column `md:w-[29em]`
 *   In RTL the Row Start edge is on the right, so DOM order
 *   `[gallery][info]` paints as `[info | gallery]` — the real mirror of shop.
 * - Large (`≥ lg`): merchant moves into the buy column (no Visit store)
 *
 * Page pad `md:px-space-16`, row `md:mt-space-24` + `md:gap-space-40`.
 * No Add to cart / Buy now (mock phase — no purchase flow).
 */
@Composable
fun ProductDetailScreen(
    productId: String,
    modifier: Modifier = Modifier,
) {
    val product = MockProductCatalog.byId(productId)
    val viewportWidth = LocalShellViewportWidth.current
    val isMdUp = viewportWidth >= VitranSize.mdBreakpoint
    val isLgUp = viewportWidth >= VitranSize.desktopBreakpoint

    if (product == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart,
        ) {
            Text(
                text = "محصول پیدا نشد",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(VitranSpacing.lg),
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = VitranSpacing.xxxl),
    ) {
        if (!isLgUp) {
            stickyHeader(key = "merchant") {
                ProductDetailMerchantHeader(
                    merchant = product.merchant,
                    showVisitStore = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(
                            horizontal = VitranSpacing.lg,
                            vertical = VitranSpacing.md,
                        ),
                )
            }
        }

        if (isMdUp) {
            item(key = "media-info") {
                // shop.app: `md:flex-row md:gap-space-40`
                //   children: media `flex-1` | buy `md:w-[29em]` (464dp).
                // VitranTheme forces RTL, so Start is on the right — same child
                // order yields buy | gallery, matching an LTR mirror without
                // swapping DOM or fighting layout with absolute offsets.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = VitranSpacing.xxxl)
                        .padding(top = DesktopRowTopMargin),
                    horizontalArrangement = Arrangement.spacedBy(DesktopColumnGap),
                    verticalAlignment = Alignment.Top,
                ) {
                    ProductDetailMediaSection(
                        media = product.media,
                        applyHorizontalInset = false,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    )
                    ProductDetailInfoColumn(
                        product = product,
                        showMerchantHeader = isLgUp,
                        contentHorizontalPadding = false,
                        modifier = Modifier
                            .widthIn(min = BuyColumnMinWidth, max = BuyColumnMaxWidth)
                            .width(BuyColumnWidth)
                            .padding(horizontal = InfoSideInset),
                    )
                }
            }
        } else {
            item(key = "media") {
                ProductDetailMediaSection(
                    media = product.media,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item(key = "info") {
                ProductDetailInfoColumn(
                    product = product,
                    showMerchantHeader = false,
                    contentHorizontalPadding = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = VitranSpacing.lg),
                )
            }
        }
    }
}

/** shop.app `md:w-[29em]` at 16px root. */
private val BuyColumnWidth = 464.dp

/** Floor so long titles do not crush into a stub column. */
private val BuyColumnMinWidth = 360.dp

/** Cap aligned with shop buy-column comfort width. */
private val BuyColumnMaxWidth = 520.dp

/** shop.app `md:gap-space-40` between gallery and buy column. */
private val DesktopColumnGap = 40.dp

/** Horizontal inset inside the buy column so info breathes from both sides. */
private val InfoSideInset = VitranSpacing.xxxl

/** shop.app `md:mt-space-24` on the media|info row. */
private val DesktopRowTopMargin = VitranSpacing.xxl
