package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.sections.product.MockProductCatalog
import com.vitran.shop.ui.sections.product.ProductDetailMediaSection
import com.vitran.shop.ui.theme.VitranSpacing

/**
 * Product detail (shop.app `/products/{id}/{slug}`).
 *
 * Built section-by-section — currently only the media gallery.
 * Uses [LazyColumn] (not Column + verticalScroll) so compact gallery
 * horizontal drag/fling is not eaten by nested vertical scroll on web.
 */
@Composable
fun ProductDetailScreen(
    productId: String,
    modifier: Modifier = Modifier,
) {
    val product = MockProductCatalog.byId(productId)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        if (product != null) {
            item(key = "media") {
                ProductDetailMediaSection(
                    media = product.media,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else {
            item(key = "missing") {
                Text(
                    text = "محصول پیدا نشد",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(VitranSpacing.lg),
                )
            }
        }
    }
}
