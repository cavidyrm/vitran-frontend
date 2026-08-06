package com.vitran.shop.ui.sections.product

import androidx.compose.runtime.Immutable
import com.vitran.shop.ui.sections.categories.allMockCategoriesProducts
import com.vitran.shop.ui.sections.home.allMockHomeProductPeeks

/**
 * Product media for the PDP gallery carousel (shop.app main-product-carousel).
 */
@Immutable
data class ProductDetailMedia(
    val imageUrls: List<String>,
) {
    init {
        require(imageUrls.isNotEmpty()) { "Product media needs at least one image" }
    }
}

/**
 * Mock product detail used by [ProductDetailScreen] in the UI/mock phase.
 */
@Immutable
data class ProductDetailMock(
    val id: String,
    val slug: String,
    val title: String,
    val media: ProductDetailMedia,
)

/** shop.app-style handle from an English mock title. */
fun productSlug(title: String): String =
    title
        .lowercase()
        .replace('&', ' ')
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
        .ifEmpty { "product" }

/**
 * In-memory product catalog keyed by list/card ids from Home and Categories mocks.
 */
object MockProductCatalog {
    private val richOverrides: Map<String, ProductDetailMock> = mapOf(
        // Our Place — Ceramic Nonstick Perfect Pot
        "home-1" to ProductDetailMock(
            id = "home-1",
            slug = "ceramic-nonstick-perfect-pot-6-5-qt",
            title = "Ceramic Nonstick Perfect Pot 6.5 qt.",
            media = ProductDetailMedia(
                imageUrls = listOf(
                    "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=1200",
                    "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/sprucesteamer.jpg?v=1704912440&width=1200",
                    "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=1200",
                    "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/sprucesteamer.jpg?v=1704912440&width=1200",
                ),
            ),
        ),
        // Brooklinen — Mulberry Silk Pillowcase (shop.app multi-image)
        "home-5" to ProductDetailMock(
            id = "home-5",
            slug = "mulberry-silk-pillowcase",
            title = "Mulberry Silk Pillowcase",
            media = ProductDetailMedia(
                imageUrls = listOf(
                    "https://cdn.shopify.com/s/files/1/0951/7126/products/ivory-silk-pillowcase_silo.jpg?v=1717181292&width=1200",
                    "https://cdn.shopify.com/s/files/1/0951/7126/products/BKL_20_Silk_PC_Cerulean_1xWOgrey.jpg?v=1661810318&width=1200",
                    "https://cdn.shopify.com/s/files/1/0951/7126/products/ivory-silk-pillowcase_detail.jpg?v=1619195966&width=1200",
                    "https://cdn.shopify.com/s/files/1/0951/7126/products/BKL_20-11_Accessories_Silk_IvoryLifestyle_Shot1_1x-copy.jpg?v=1715704023&width=1200",
                    "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=1200",
                    "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Abyss_Pillowcase_2x_WOgrey.jpg?v=1727366051&width=1200",
                ),
            ),
        ),
        // Branch — Ergonomic Chair
        "home-8" to ProductDetailMock(
            id = "home-8",
            slug = "ergonomic-chair",
            title = "Ergonomic Chair",
            media = ProductDetailMedia(
                imageUrls = listOf(
                    "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=1200",
                    "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=1200",
                    "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=1200",
                    "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=1200",
                ),
            ),
        ),
    )

    private val byId: Map<String, ProductDetailMock> by lazy {
        buildMap {
            putAll(richOverrides)
            for (product in allMockCategoriesProducts()) {
                if (containsKey(product.id)) continue
                put(
                    product.id,
                    ProductDetailMock(
                        id = product.id,
                        slug = productSlug(product.title),
                        title = product.title,
                        media = ProductDetailMedia(
                            imageUrls = listOf(
                                product.imageUrl,
                                product.imageUrl,
                                product.imageUrl,
                            ),
                        ),
                    ),
                )
            }
            for (peek in allMockHomeProductPeeks()) {
                if (containsKey(peek.id)) continue
                put(
                    peek.id,
                    ProductDetailMock(
                        id = peek.id,
                        slug = "product-${peek.id}",
                        title = "محصول",
                        media = ProductDetailMedia(
                            imageUrls = listOf(peek.imageUrl, peek.imageUrl),
                        ),
                    ),
                )
            }
        }
    }

    fun byId(id: String): ProductDetailMock? = byId[id]

    /**
     * Resolves a catalog entry for navigation. Prefer [byId]; otherwise synthesize
     * from list-card fields so every wired click can open a PDP.
     */
    fun resolve(
        id: String,
        title: String,
        imageUrl: String,
    ): ProductDetailMock =
        byId(id) ?: ProductDetailMock(
            id = id,
            slug = productSlug(title),
            title = title,
            media = ProductDetailMedia(imageUrls = listOf(imageUrl)),
        )
}
