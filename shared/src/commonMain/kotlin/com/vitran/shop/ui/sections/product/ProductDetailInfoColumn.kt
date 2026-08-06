package com.vitran.shop.ui.sections.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranSpacing

/**
 * Buy-side info column for Product Detail: optional in-column merchant,
 * buy box, description, reviews, and merchant Follow strip
 * (shop.app `md:w-[29em]` column content).
 *
 * Compact horizontal padding matches shop.app `px-space-16`; pass
 * [contentHorizontalPadding] = false when the parent already pads the column.
 * Vertical `spacedBy(24)` matches shop.app Description → Reviews `mt-space-24`
 * (Follow uses the same 24 gap).
 */
@Composable
fun ProductDetailInfoColumn(
    product: ProductDetailMock,
    modifier: Modifier = Modifier,
    showMerchantHeader: Boolean = false,
    contentHorizontalPadding: Boolean = true,
) {
    val coverUrl = product.merchantCoverImageUrl ?: product.media.imageUrls.first()

    Column(
        modifier = modifier
            .then(
                if (contentHorizontalPadding) {
                    Modifier.padding(horizontal = VitranSpacing.lg)
                } else {
                    Modifier
                },
            ),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xxl),
    ) {
        if (showMerchantHeader) {
            ProductDetailMerchantHeader(
                merchant = product.merchant,
                showVisitStore = false,
                showMoreMenu = true,
                logoSize = 32.dp,
            )
        }
        ProductDetailBuyBox(product = product)
        ProductDetailDescription(description = product.description)
        product.reviews?.let { reviews ->
            ProductDetailReviewsSection(reviews = reviews)
        }
        ProductDetailMerchantFollowStrip(
            merchant = product.merchant,
            coverImageUrl = coverUrl,
        )
    }
}
