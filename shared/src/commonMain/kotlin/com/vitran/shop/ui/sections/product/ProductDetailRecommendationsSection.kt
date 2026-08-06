package com.vitran.shop.ui.sections.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.sections.categories.CategoriesMerchantGrid
import com.vitran.shop.ui.sections.categories.CategoriesProduct
import com.vitran.shop.ui.sections.categories.CategoriesProductRow
import com.vitran.shop.ui.sections.categories.CategoriesSectionGap
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.product_detail_discover_brands
import vitranshop.shared.generated.resources.product_detail_more_from
import vitranshop.shared.generated.resources.product_detail_related_to

/**
 * Full-bleed PDP recommendations under the media|info row
 * (More from → Related heading + brand rows → Discover top brands).
 *
 * Reuses [CategoriesProductRow] / [CategoriesMerchantGrid] — same card chrome
 * as Categories. Vertical rhythm: [CategoriesSectionGap] (40dp).
 */
@Composable
fun ProductDetailRecommendationsSection(
    product: ProductDetailMock,
    modifier: Modifier = Modifier,
    onProductClick: (CategoriesProduct) -> Unit = {},
) {
    val moreFromTitle = stringResource(
        Res.string.product_detail_more_from,
        product.merchant.name,
    )
    val relatedTitle = stringResource(
        Res.string.product_detail_related_to,
        product.merchant.name,
    )
    val discoverTitle = stringResource(Res.string.product_detail_discover_brands)
    val recommendations = remember(product.id, moreFromTitle, discoverTitle) {
        buildProductDetailRecommendations(
            product = product,
            moreFromTitle = moreFromTitle,
            discoverTitle = discoverTitle,
        )
    }
    val isDesktop = LocalDesktopLayout.current
    val relatedHeadingPad = if (isDesktop) {
        VitranSpacing.xxxl + VitranSpacing.lg
    } else {
        VitranSpacing.lg
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(CategoriesSectionGap),
    ) {
        CategoriesProductRow(
            section = recommendations.moreFrom,
            onProductClick = onProductClick,
            modifier = Modifier.fillMaxWidth(),
        )

        if (recommendations.relatedRows.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = relatedTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (isDesktop) 20.sp else 18.sp,
                    ),
                    modifier = Modifier
                        .padding(horizontal = relatedHeadingPad)
                        .padding(bottom = VitranSpacing.lg),
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(CategoriesSectionGap),
                ) {
                    recommendations.relatedRows.forEach { row ->
                        CategoriesProductRow(
                            section = row,
                            onProductClick = onProductClick,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }

        CategoriesMerchantGrid(
            section = recommendations.discoverBrands,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
