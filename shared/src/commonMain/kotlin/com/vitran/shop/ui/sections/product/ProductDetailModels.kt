package com.vitran.shop.ui.sections.product

import androidx.compose.runtime.Immutable
import com.vitran.shop.ui.sections.categories.CategoriesMerchantGridSection
import com.vitran.shop.ui.sections.categories.CategoriesProduct
import com.vitran.shop.ui.sections.categories.CategoriesProductRowSection
import com.vitran.shop.ui.sections.categories.allMockCategoriesMerchantShops
import com.vitran.shop.ui.sections.categories.allMockCategoriesProducts
import com.vitran.shop.ui.sections.home.allMockHomeShopCards

/**
 * Product media for the PDP gallery carousel (shop.app main-product-carousel).
 */
@Immutable
data class ProductDetailMedia(
    val imageUrls: List<String>,
    /**
     * How the main preview fills its rail (shop.app uses `object-contain`).
     * [ProductImageMode.Fit] and [ProductImageMode.Crop] both map to Fit on
     * desktop so wide frames do not over-zoom; Crop kept for API clarity.
     */
    val imageMode: ProductImageMode = ProductImageMode.Fit,
) {
    init {
        require(imageUrls.isNotEmpty()) { "Product media needs at least one image" }
    }
}

/**
 * Main PDP preview scaling — avoids a too-wide Fit box letterboxing portrait shots.
 */
enum class ProductImageMode {
    Fit,
    Crop,
}

/**
 * Merchant / store row on the PDP (logo, name, rating).
 *
 * [shopId] maps to [com.vitran.shop.ui.navigation.Route.Store] when Visit store is tapped.
 */
@Immutable
data class ProductDetailMerchant(
    val name: String,
    val logoUrl: String,
    /** e.g. "۴.۵" */
    val ratingLabel: String,
    /** e.g. "(۱۴۱.۴K)" */
    val reviewCountLabel: String,
    /** Home / store handle for navigation; null when unknown in mocks. */
    val shopId: String? = null,
)

/**
 * One selectable value inside a dynamic product option axis.
 */
@Immutable
data class ProductOptionValue(
    val id: String,
    val label: String,
    val swatchImageUrl: String? = null,
    /** ARGB when rendering a solid color swatch (no image). */
    val swatchColorArgb: Long? = null,
)

/**
 * Free-form option axis (Color / Size / Style / …) — product-specific, not category schema.
 */
@Immutable
data class ProductOption(
    val id: String,
    val name: String,
    val values: List<ProductOptionValue>,
    val selectedIndex: Int = 0,
) {
    init {
        require(values.isNotEmpty()) { "Option needs at least one value" }
        require(selectedIndex in values.indices) { "selectedIndex out of range" }
    }

    val usesSwatches: Boolean
        get() = values.any { it.swatchImageUrl != null || it.swatchColorArgb != null }
}

/**
 * Star-level share of ratings for the PDP reviews histogram (5★ → 1★), each 0f…1f.
 */
@Immutable
data class ProductReviewHistogram(
    val five: Float,
    val four: Float,
    val three: Float,
    val two: Float,
    val one: Float,
) {
    fun fractionFor(stars: Int): Float =
        when (stars) {
            5 -> five
            4 -> four
            3 -> three
            2 -> two
            1 -> one
            else -> 0f
        }
}

/**
 * One customer review card in the PDP reviews carousel / sheet list.
 */
@Immutable
data class ProductReviewItem(
    val rating: Int,
    val body: String,
    val authorName: String,
    val authorInitial: String,
    /** ARGB for the circular letter avatar. */
    val avatarColorArgb: Long,
    val dateLabel: String,
    /** Bold headline in the reviews sheet card (shop.app review title). */
    val title: String? = null,
    /** e.g. "آسیاب‌شده" / "۳T-۴T / آبی" — gray subtitle under the title. */
    val variantLabel: String? = null,
    val showAuthorMeta: Boolean = true,
)

/**
 * Reviews mock for the PDP card + side sheet.
 */
@Immutable
data class ProductReviewsMock(
    val averageLabel: String,
    /** e.g. "۶۴۱ امتیاز" */
    val ratingsCountLabel: String,
    val histogram: ProductReviewHistogram,
    val reviews: List<ProductReviewItem>,
    /** False when API does not provide aggregate/histogram fields. */
    val showSummaryMetrics: Boolean = true,
)

/** Default PDP reviews card so every mock product has a filled Reviews section. */
fun defaultProductReviews(
    averageLabel: String = "۴.۸",
    ratingsCountLabel: String = "۱۲۸ امتیاز",
): ProductReviewsMock =
    ProductReviewsMock(
        averageLabel = averageLabel,
        ratingsCountLabel = ratingsCountLabel,
        histogram = ProductReviewHistogram(
            five = 0.82f,
            four = 0.10f,
            three = 0.04f,
            two = 0.02f,
            one = 0.02f,
        ),
        reviews = listOf(
            ProductReviewItem(
                rating = 5,
                title = "کیفیت عالی",
                body = "کیفیت عالی بود؛ دقیقاً همان چیزی که انتظار داشتم.",
                authorName = "سارا",
                authorInitial = "س",
                avatarColorArgb = 0xFFE8A0BFL,
                dateLabel = "۱۲ تیر ۱۴۰۴",
                variantLabel = "استاندارد",
            ),
            ProductReviewItem(
                rating = 5,
                title = "ارسال سریع",
                body = "ارسال سریع و بسته‌بندی مرتب. حتماً دوباره می‌خرم.",
                authorName = "امیر",
                authorInitial = "ا",
                avatarColorArgb = 0xFF7EB6D9L,
                dateLabel = "۳ خرداد ۱۴۰۴",
                variantLabel = "استاندارد",
            ),
            ProductReviewItem(
                rating = 4,
                title = "خوب بود",
                body = "خوب بود، فقط کمی دیرتر از موعد رسید.",
                authorName = "مینا",
                authorInitial = "م",
                avatarColorArgb = 0xFFB5A0E8L,
                dateLabel = "۲۸ اردیبهشت ۱۴۰۴",
                variantLabel = "استاندارد",
            ),
            ProductReviewItem(
                rating = 5,
                title = "مطابق عکس",
                body = "از خرید راضی‌ام؛ رنگ و کیفیت با عکس یکی بود.",
                authorName = "نیما",
                authorInitial = "ن",
                avatarColorArgb = 0xFF6BC4A6L,
                dateLabel = "۱۵ اردیبهشت ۱۴۰۴",
                variantLabel = "استاندارد",
            ),
            ProductReviewItem(
                rating = 5,
                title = "بسته‌بندی محکم",
                body = "بسته‌بندی محکم و محصول تمیز رسید. پیشنهاد می‌کنم.",
                authorName = "هستی",
                authorInitial = "ه",
                avatarColorArgb = 0xFFE8B86DL,
                dateLabel = "۲ فروردین ۱۴۰۴",
            ),
            ProductReviewItem(
                rating = 5,
                title = "هدیه عالی",
                body = "برای هدیه عالی بود؛ گیرنده خیلی خوشحال شد.",
                authorName = "رضا",
                authorInitial = "ر",
                avatarColorArgb = 0xFF9BB7E8L,
                dateLabel = "۱۸ اسفند ۱۴۰۳",
                variantLabel = "استاندارد",
            ),
            ProductReviewItem(
                rating = 3,
                title = "متوسط",
                body = "متوسط بود؛ انتظار کیفیت بالاتری داشتم.",
                authorName = "لیلا",
                authorInitial = "ل",
                avatarColorArgb = 0xFFD4A5A5L,
                dateLabel = "۵ اسفند ۱۴۰۳",
            ),
            ProductReviewItem(
                rating = 5,
                title = "خرید دوباره",
                body = "دومین خریدم از این فروشگاه؛ مثل همیشه عالی.",
                authorName = "پویا",
                authorInitial = "پ",
                avatarColorArgb = 0xFF8FCB8FL,
                dateLabel = "۲۲ بهمن ۱۴۰۳",
                variantLabel = "استاندارد",
            ),
            ProductReviewItem(
                rating = 4,
                title = "قیمت مناسب",
                body = "قیمت مناسب و ظاهر شیک. راضی هستم.",
                authorName = "نازنین",
                authorInitial = "ن",
                avatarColorArgb = 0xFFC9A0DCL,
                dateLabel = "۱۰ بهمن ۱۴۰۳",
            ),
            ProductReviewItem(
                rating = 5,
                title = "پیشنهاد می‌کنم",
                body = "توصیه می‌کنم؛ ارزش خرید دارد.",
                authorName = "بهرام",
                authorInitial = "ب",
                avatarColorArgb = 0xFF7AA2C4L,
                dateLabel = "۲۸ دی ۱۴۰۳",
                variantLabel = "استاندارد",
            ),
        ),
    )

private fun blueberryReviews(): ProductReviewsMock =
    ProductReviewsMock(
        averageLabel = "۴.۹",
        ratingsCountLabel = "۶۴۱ امتیاز",
        histogram = ProductReviewHistogram(
            five = 0.94f,
            four = 0.04f,
            three = 0.01f,
            two = 0.005f,
            one = 0.005f,
        ),
        reviews = listOf(
            ProductReviewItem(
                rating = 5,
                title = "مورد علاقه پسرم",
                body = "همیشه بلوبری بلاست رو دوست داشتم؛ مورد علاقه پسرم هست.",
                authorName = "لین",
                authorInitial = "ل",
                avatarColorArgb = 0xFFE8A0BFL,
                dateLabel = "۲۳ تیر ۱۴۰۴",
                variantLabel = "آسیاب‌شده",
            ),
            ProductReviewItem(
                rating = 5,
                title = "عطر کیک بلوبری",
                body = "عطر کیک بلوبری واقعاً مشخصه؛ برای دم‌آوری دستی عالی بود.",
                authorName = "الیزابت",
                authorInitial = "ا",
                avatarColorArgb = 0xFF7EB6D9L,
                dateLabel = "۱۴ تیر ۱۴۰۴",
                variantLabel = "آسیاب‌شده",
            ),
            ProductReviewItem(
                rating = 5,
                title = "مورد علاقه همسرم",
                body = "مورد علاقه همسرم. بسته‌بندی تمیز و طعم یکدست.",
                authorName = "مریم",
                authorInitial = "م",
                avatarColorArgb = 0xFFB5A0E8L,
                dateLabel = "۵ خرداد ۱۴۰۴",
                variantLabel = "دانه کامل",
            ),
            ProductReviewItem(
                rating = 5,
                title = "عالی برای فرنچ‌پرس",
                body = "طعم عالی و تازه؛ برای فرنچ‌پرس هم خوب جواب داد.",
                authorName = "کاوه",
                authorInitial = "ک",
                avatarColorArgb = 0xFF6BC4A6L,
                dateLabel = "۲۰ اردیبهشت ۱۴۰۴",
                variantLabel = "آسیاب‌شده",
            ),
            ProductReviewItem(
                rating = 4,
                title = "عطر قوی",
                body = "عطر قوی دارد؛ کمی شیرین‌تر از انتظارم بود.",
                authorName = "نرگس",
                authorInitial = "ن",
                avatarColorArgb = 0xFFE8B86DL,
                dateLabel = "۸ فروردین ۱۴۰۴",
                variantLabel = "آسیاب‌شده",
            ),
            ProductReviewItem(
                rating = 5,
                title = "مثل پنکیک بلوبری",
                body = "مثل پنکیک بلوبری می‌مونه؛ با خامه زده عالی است.",
                authorName = "دین",
                authorInitial = "د",
                avatarColorArgb = 0xFF9BB7E8L,
                dateLabel = "۶ تیر ۱۴۰۴",
                variantLabel = "دانه کامل",
            ),
            ProductReviewItem(
                rating = 5,
                title = "بهترین قهوه",
                body = "بهترین قهوه‌ای که تا حالا خریدم.",
                authorName = "تینا",
                authorInitial = "ت",
                avatarColorArgb = 0xFFD4A5A5L,
                dateLabel = "۱ تیر ۱۴۰۴",
                variantLabel = "آسیاب‌شده",
            ),
            ProductReviewItem(
                rating = 5,
                title = "دوباره یام",
                body = "دوباره یام. همیشه همین را می‌خرم.",
                authorName = "جوئل",
                authorInitial = "ج",
                avatarColorArgb = 0xFF8FCB8FL,
                dateLabel = "۱۵ خرداد ۱۴۰۴",
                variantLabel = "آسیاب‌شده",
            ),
            ProductReviewItem(
                rating = 2,
                title = "برای هدیه بهتر است",
                body = "من بلوبری دوست ندارم؛ برای همسرم هدیه خوبی بود ولی خودم نه.",
                authorName = "تسا",
                authorInitial = "ت",
                avatarColorArgb = 0xFFC9A0DCL,
                dateLabel = "۳۰ اردیبهشت ۱۴۰۴",
                variantLabel = "آسیاب‌شده",
            ),
            ProductReviewItem(
                rating = 5,
                title = "عطر و طعم یکدست",
                body = "عطر و طعم یکدست؛ پیشنهاد می‌کنم.",
                authorName = "جان",
                authorInitial = "ج",
                avatarColorArgb = 0xFF7AA2C4L,
                dateLabel = "۲ روز پیش",
                variantLabel = "آسیاب‌شده",
            ),
        ),
    )

/**
 * Full-bleed recommendation stack under the PDP media|info row
 * (More from / Related brand rows / Discover top brands).
 */
@Immutable
data class ProductDetailRecommendations(
    val moreFrom: CategoriesProductRowSection,
    val relatedRows: List<CategoriesProductRowSection>,
    val discoverBrands: CategoriesMerchantGridSection,
)

/**
 * Mock product detail used by [ProductDetailScreen] in the UI/mock phase.
 */
@Immutable
data class ProductDetailMock(
    val id: String,
    val slug: String,
    val title: String,
    val media: ProductDetailMedia,
    val merchant: ProductDetailMerchant,
    val productRating: Float? = null,
    /** e.g. "۱.۶K امتیاز" */
    val productRatingCountLabel: String? = null,
    /** e.g. "۵۰۰+ خرید در ماه گذشته" */
    val socialProofLabel: String? = null,
    /** e.g. "فقط ۱ عدد مانده" — shop.app scarcity pill */
    val inventoryLabel: String? = null,
    val priceLabel: String,
    val compareAtPriceLabel: String? = null,
    val options: List<ProductOption> = emptyList(),
    val description: String,
    val reviews: ProductReviewsMock? = defaultProductReviews(),
    /**
     * Cover image for the merchant Follow strip (shop.app 120px banner).
     * Falls back to the first media image when null.
     */
    val merchantCoverImageUrl: String? = null,
)

/** shop.app-style handle from an English mock title. */
fun productSlug(title: String): String =
    title
        .lowercase()
        .replace('&', ' ')
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
        .ifEmpty { "product" }

private fun placeholderMerchant(name: String, logoUrl: String? = null): ProductDetailMerchant =
    ProductDetailMerchant(
        name = name.ifBlank { "برند منتخب" },
        logoUrl = logoUrl
            ?: "https://cdn.shopify.com/s/files/1/0533/2089/files/placeholder-images-image_large.png?width=88",
        ratingLabel = "۴.۸",
        reviewCountLabel = "(۱۲.۴K)",
    )

private fun merchantFromHomeShop(shop: com.vitran.shop.ui.sections.home.HomeShopCard): ProductDetailMerchant {
    val normalized = shop.ratingLabel.replace('٫', '.')
    val match = Regex("""([\d.]+)\s*\((.+)\)""").find(normalized)
    val rating = match?.groupValues?.getOrNull(1)?.replace('.', '٫') ?: "۴.۸"
    val count = match?.groupValues?.getOrNull(2)?.let { "($it)" } ?: "(۱۰K)"
    return ProductDetailMerchant(
        name = shop.name,
        logoUrl = shop.logoUrl.ifBlank {
            "https://cdn.shopify.com/s/files/1/0533/2089/files/placeholder-images-image_large.png?width=88"
        },
        ratingLabel = rating,
        reviewCountLabel = count,
        shopId = shop.id,
    )
}

private fun descriptionFor(title: String, storeName: String): String =
    "$title از $storeName با طراحی دقیق و متریال مرغوب. " +
        "مناسب استفاده روزانه؛ نگهداری آسان و دوام بالا. " +
        "جزئیات بیشتر شامل راهنمای سایز و مراقبت روی همین صفحه آمده است."

private fun fromCategoriesProduct(
    product: CategoriesProduct,
    mediaUrls: List<String> = listOf(product.imageUrl, product.imageUrl, product.imageUrl),
    options: List<ProductOption> = emptyList(),
    description: String = descriptionFor(product.title, product.storeName),
    merchantLogoUrl: String? = null,
    merchantRatingLabel: String = "۴.۵",
    merchantReviewCountLabel: String = product.reviewCountLabel ?: "(۱.۲K)",
    productRating: Float? = product.rating ?: 5f,
    productRatingCountLabel: String? =
        product.reviewCountLabel?.removePrefix("(")?.removeSuffix(")")?.let { "$it امتیاز" }
            ?: "۱۲۸ امتیاز",
    socialProofLabel: String? = "۲۰۰+ خرید در ماه گذشته",
    inventoryLabel: String? = null,
    reviews: ProductReviewsMock? = defaultProductReviews(
        ratingsCountLabel = product.reviewCountLabel
            ?.removePrefix("(")?.removeSuffix(")")
            ?.let { "$it امتیاز" }
            ?: "۱۲۸ امتیاز",
    ),
): ProductDetailMock =
    ProductDetailMock(
        id = product.id,
        slug = productSlug(product.title),
        title = product.title,
        media = ProductDetailMedia(imageUrls = mediaUrls),
        merchant = ProductDetailMerchant(
            name = product.storeName,
            logoUrl = merchantLogoUrl
                ?: "https://cdn.shopify.com/s/files/1/0533/2089/files/placeholder-images-image_large.png?width=88",
            ratingLabel = merchantRatingLabel,
            reviewCountLabel = merchantReviewCountLabel,
        ),
        productRating = productRating,
        productRatingCountLabel = productRatingCountLabel,
        socialProofLabel = socialProofLabel,
        inventoryLabel = inventoryLabel,
        priceLabel = product.priceLabel,
        compareAtPriceLabel = product.compareAtPriceLabel,
        options = options,
        description = description,
        reviews = reviews,
    )

/**
 * In-memory product catalog keyed by list/card ids from Home and Categories mocks.
 */
object MockProductCatalog {
    private val richOverrides: Map<String, ProductDetailMock> = mapOf(
        // Our Place — Ceramic Nonstick Perfect Pot (Color + Style)
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
                imageMode = ProductImageMode.Fit,
            ),
            merchant = ProductDetailMerchant(
                name = "Our Place",
                logoUrl = "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/OP_Logo.png?width=88",
                ratingLabel = "۴.۷",
                reviewCountLabel = "(۸.۹K)",
            ),
            productRating = 5f,
            productRatingCountLabel = "۸.۹K امتیاز",
            socialProofLabel = "۱٬۰۰۰+ خرید در ماه گذشته",
            priceLabel = "۶٬۲۶۰٬۰۰۰ تومان",
            options = listOf(
                ProductOption(
                    id = "color",
                    name = "رنگ",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(
                            id = "black",
                            label = "مشکی",
                            swatchColorArgb = 0xFF1A1A1AL,
                        ),
                        ProductOptionValue(
                            id = "cream",
                            label = "کرم",
                            swatchColorArgb = 0xFFF5E6D3L,
                        ),
                        ProductOptionValue(
                            id = "blue",
                            label = "آبی",
                            swatchColorArgb = 0xFF4A6FA5L,
                        ),
                    ),
                ),
                ProductOption(
                    id = "style",
                    name = "استایل",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(id = "pot", label = "قابلمه"),
                        ProductOptionValue(id = "set", label = "ست کامل"),
                    ),
                ),
            ),
            description =
                "قابلمه سرامیکی نچسب Our Place با ظرفیت ۶٫۵ کوارت برای پخت‌وپز روزمره. " +
                    "روکش سرامیکی بدون مواد مضر، مناسب گاز و فر، و قابل شستشو در ماشین ظرفشویی. " +
                    "طراحی مینیمال با درب شیشه‌ای برای کنترل راحت فرآیند پخت.",
        ),
        // Brooklinen — Mulberry Silk Pillowcase (Color swatches + Size pills)
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
            merchant = ProductDetailMerchant(
                name = "Brooklinen",
                logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/brooklinen2.myshopify.com/1785188968/logo.png?format=webp&width=88",
                ratingLabel = "۴.۵",
                reviewCountLabel = "(۱۴۱.۴K)",
            ),
            productRating = 5f,
            productRatingCountLabel = "۱.۶K امتیاز",
            socialProofLabel = "۵۰۰+ خرید در ماه گذشته",
            inventoryLabel = "فقط ۳ عدد مانده",
            priceLabel = "۲٬۹۰۰٬۰۰۰ تومان",
            options = listOf(
                ProductOption(
                    id = "color",
                    name = "رنگ",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(
                            id = "ivory",
                            label = "عاجی",
                            swatchImageUrl =
                                "https://cdn.shopify.com/s/files/1/0951/7126/products/ivory-silk-pillowcase_silo.jpg?v=1717181292&width=96",
                        ),
                        ProductOptionValue(
                            id = "cerulean",
                            label = "آبی روشن",
                            swatchImageUrl =
                                "https://cdn.shopify.com/s/files/1/0951/7126/products/BKL_20_Silk_PC_Cerulean_1xWOgrey.jpg?v=1661810318&width=96",
                        ),
                        ProductOptionValue(
                            id = "driftwood",
                            label = "چوب خشک",
                            swatchImageUrl =
                                "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=96",
                        ),
                        ProductOptionValue(
                            id = "abyss",
                            label = "آبی تیره",
                            swatchImageUrl =
                                "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Abyss_Pillowcase_2x_WOgrey.jpg?v=1727366051&width=96",
                        ),
                    ),
                ),
                ProductOption(
                    id = "size",
                    name = "سایز",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(id = "standard", label = "استاندارد"),
                        ProductOptionValue(id = "king", label = "کینگ"),
                    ),
                ),
            ),
            description =
                "خوابیدن روی ابریشم توت سفید لوکس ما از پوست و مو در برابر آسیب اصطکاک محافظت می‌کند. " +
                    "ابریشم مولبری ما بسیار نرم، تنفس‌پذیر و مناسب پوست‌های حساس است. " +
                    "قابل شستشو در ماشین با برنامه ملایم؛ برای ماندگاری بیشتر در سایه خشک کنید.",
        ),
        // Branch — Ergonomic Chair (Color + Fabric)
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
            merchant = ProductDetailMerchant(
                name = "Branch",
                logoUrl = "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/Branch_Logo.png?width=88",
                ratingLabel = "۴.۸",
                reviewCountLabel = "(۶.۵K)",
            ),
            productRating = 5f,
            productRatingCountLabel = "۶.۵K امتیاز",
            socialProofLabel = "۲۰۰+ خرید در ماه گذشته",
            priceLabel = "۱۵٬۰۸۰٬۰۰۰ تومان",
            options = listOf(
                ProductOption(
                    id = "color",
                    name = "رنگ",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(
                            id = "black",
                            label = "مشکی",
                            swatchColorArgb = 0xFF1A1A1AL,
                        ),
                        ProductOptionValue(
                            id = "grey",
                            label = "خاکستری",
                            swatchColorArgb = 0xFF9E9E9EL,
                        ),
                        ProductOptionValue(
                            id = "navy",
                            label = "سرمه‌ای",
                            swatchColorArgb = 0xFF1B2A4AL,
                        ),
                    ),
                ),
                ProductOption(
                    id = "fabric",
                    name = "پارچه",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(id = "mesh", label = "مش"),
                        ProductOptionValue(id = "knit", label = "بافت"),
                    ),
                ),
            ),
            description =
                "صندلی ارگونومیک Branch با پشتیبانی کمر قابل تنظیم، نشیمن تنفس‌پذیر و دسته‌های چندجهته. " +
                    "طراحی‌شده برای نشستن طولانی پشت میز کار؛ مونتاژ آسان و گارانتی سازنده.",
        ),
        // BruMate — Era tumbler (Color swatches + Size)
        "home-3" to ProductDetailMock(
            id = "home-3",
            slug = "era-40oz-lilac-dusk",
            title = "Era 40oz | Lilac Dusk",
            media = ProductDetailMedia(
                imageUrls = listOf(
                    "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=1200",
                    "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=1200",
                    "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=1200",
                ),
            ),
            merchant = ProductDetailMerchant(
                name = "BruMate",
                logoUrl = "https://cdn.shopify.com/s/files/1/1114/2308/files/brumate-logo.png?width=88",
                ratingLabel = "۴.۶",
                reviewCountLabel = "(۱.۳K)",
            ),
            productRating = 5f,
            productRatingCountLabel = "۱.۳K امتیاز",
            socialProofLabel = "۳۰۰+ خرید در ماه گذشته",
            priceLabel = "۱٬۶۸۰٬۰۰۰ تومان",
            options = listOf(
                ProductOption(
                    id = "color",
                    name = "رنگ",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(
                            id = "lilac",
                            label = "یاسی",
                            swatchColorArgb = 0xFFC5A3D4L,
                        ),
                        ProductOptionValue(
                            id = "black",
                            label = "مشکی",
                            swatchColorArgb = 0xFF1A1A1AL,
                        ),
                        ProductOptionValue(
                            id = "sand",
                            label = "شنی",
                            swatchColorArgb = 0xFFD4C4A8L,
                        ),
                        ProductOptionValue(
                            id = "sage",
                            label = "سبز مریم",
                            swatchColorArgb = 0xFF9CAF88L,
                        ),
                    ),
                ),
                ProductOption(
                    id = "size",
                    name = "سایز",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(id = "era-40", label = "۴۰oz"),
                        ProductOptionValue(id = "era-30", label = "۳۰oz"),
                        ProductOptionValue(id = "era-20", label = "۲۰oz"),
                    ),
                ),
            ),
            description =
                "ماگ عایق BruMate Era با ظرفیت ۴۰ اونس، درب ضد نشت و نگه داشتن دمای نوشیدنی برای ساعت‌ها. " +
                    "بدنه استیل ضدزنگ؛ مناسب سفر، باشگاه و میز کار.",
        ),
        // Caraway — Baking Sheet Duo (Color)
        "home-0" to ProductDetailMock(
            id = "home-0",
            slug = "baking-sheet-duo",
            title = "Baking Sheet Duo",
            media = ProductDetailMedia(
                imageUrls = listOf(
                    "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=1200",
                    "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=1200",
                    "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=1200",
                ),
            ),
            merchant = ProductDetailMerchant(
                name = "Caraway",
                logoUrl = "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/caraway-logo.png?width=88",
                ratingLabel = "۴.۶",
                reviewCountLabel = "(۲.۱K)",
            ),
            productRating = 5f,
            productRatingCountLabel = "۲.۱K امتیاز",
            socialProofLabel = "۴۰۰+ خرید در ماه گذشته",
            priceLabel = "۴٬۴۳۰٬۰۰۰ تومان",
            options = listOf(
                ProductOption(
                    id = "color",
                    name = "رنگ",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(
                            id = "cream",
                            label = "کرم",
                            swatchColorArgb = 0xFFF2E8DCL,
                        ),
                        ProductOptionValue(
                            id = "navy",
                            label = "سرمه‌ای",
                            swatchColorArgb = 0xFF1B2A4AL,
                        ),
                        ProductOptionValue(
                            id = "sage",
                            label = "سبز مریم",
                            swatchColorArgb = 0xFF9CAF88L,
                        ),
                    ),
                ),
            ),
            description =
                "ست دو تایی سینی فر Caraway با روکش سرامیکی نچسب. مناسب شیرینی‌پزی و برشته‌کاری؛ " +
                    "قابل شستشو در ماشین ظرفشویی و مقاوم در فر تا دمای بالا.",
        ),
        // Thuma — Classic Headboard (Size + Finish)
        "home-9" to ProductDetailMock(
            id = "home-9",
            slug = "classic-headboard",
            title = "Classic Headboard",
            media = ProductDetailMedia(
                imageUrls = listOf(
                    "https://cdn.shopify.com/s/files/1/2448/0687/products/220919_The-Headboard_Walnut_2_PDP.jpg?v=1664384103&width=1200",
                    "https://cdn.shopify.com/s/files/1/2448/0687/products/220919_The-Headboard_Walnut_2_PDP.jpg?v=1664384103&width=1200",
                    "https://cdn.shopify.com/s/files/1/2448/0687/products/220919_The-Headboard_Walnut_2_PDP.jpg?v=1664384103&width=1200",
                ),
            ),
            merchant = ProductDetailMerchant(
                name = "Thuma",
                logoUrl = "https://cdn.shopify.com/s/files/1/2448/0687/files/thuma-logo.png?width=88",
                ratingLabel = "۴.۷",
                reviewCountLabel = "(۲۲۵)",
            ),
            productRating = 5f,
            productRatingCountLabel = "۲۲۵ امتیاز",
            socialProofLabel = "۱۵۰+ خرید در ماه گذشته",
            priceLabel = "۲۷٬۱۰۰٬۰۰۰ تومان",
            options = listOf(
                ProductOption(
                    id = "size",
                    name = "سایز",
                    selectedIndex = 1,
                    values = listOf(
                        ProductOptionValue(id = "twin", label = "تک‌نفره"),
                        ProductOptionValue(id = "queen", label = "کوئین"),
                        ProductOptionValue(id = "king", label = "کینگ"),
                    ),
                ),
                ProductOption(
                    id = "finish",
                    name = "روکش",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(
                            id = "walnut",
                            label = "گردو",
                            swatchColorArgb = 0xFF5C4033L,
                        ),
                        ProductOptionValue(
                            id = "oak",
                            label = "بلوط",
                            swatchColorArgb = 0xFFC4A574L,
                        ),
                        ProductOptionValue(
                            id = "black",
                            label = "مشکی",
                            swatchColorArgb = 0xFF1A1A1AL,
                        ),
                    ),
                ),
            ),
            description =
                "تخت‌خواب‌سری کلاسیک Thuma با مونتاژ بدون ابزار و چوب مهندسی‌شده. " +
                    "طراحی مینیمال برای اتاق خواب؛ سازگار با قاب تخت Thuma.",
        ),
        // Kyte Baby — toddler PJs (Size + Color like shop.app pajama PDP)
        "14982433407087" to ProductDetailMock(
            id = "14982433407087",
            slug = "monster-truck-ls-toddler-pjs",
            title = "Monster Truck LS Toddler PJs",
            media = ProductDetailMedia(
                imageUrls = listOf(
                    "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Monster_Truck_LS_Toddler_PJs_01.jpg?width=1200",
                    "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Monster_Truck_LS_Toddler_PJs_01.jpg?width=1200",
                    "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Monster_Truck_LS_Toddler_PJs_01.jpg?width=1200",
                    "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Monster_Truck_LS_Toddler_PJs_01.jpg?width=1200",
                ),
                imageMode = ProductImageMode.Crop,
            ),
            merchant = ProductDetailMerchant(
                name = "Kyte Baby",
                logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/kyte-baby-co.myshopify.com/1725505717/KB_logo_horizontal_white.png?width=88",
                ratingLabel = "۴.۹",
                reviewCountLabel = "(۵۳۷.۴K)",
            ),
            productRating = 5f,
            productRatingCountLabel = "۳۴ امتیاز",
            socialProofLabel = "۱۰۰+ خرید در ماه گذشته",
            inventoryLabel = "فقط ۱ عدد مانده",
            priceLabel = "۱٬۸۹۰٬۰۰۰ تومان",
            options = listOf(
                ProductOption(
                    id = "size",
                    name = "سایز",
                    selectedIndex = 1,
                    values = listOf(
                        ProductOptionValue(id = "2t", label = "۲T"),
                        ProductOptionValue(id = "3t-4t", label = "۳T-۴T"),
                        ProductOptionValue(id = "5t-6t", label = "۵T-۶T"),
                        ProductOptionValue(id = "7y-8y", label = "۷Y-۸Y"),
                        ProductOptionValue(id = "10y-12y", label = "۱۰Y-۱۲Y"),
                    ),
                ),
                ProductOption(
                    id = "color",
                    name = "رنگ",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(
                            id = "monster-truck",
                            label = "مانستر تراک",
                            swatchImageUrl =
                                "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Monster_Truck_LS_Toddler_PJs_01.jpg?width=96",
                        ),
                        ProductOptionValue(
                            id = "navy",
                            label = "سرمه‌ای",
                            swatchColorArgb = 0xFF1B2A4AL,
                        ),
                    ),
                ),
            ),
            description =
                "ست لباس خواب آستین‌بلند کودک با طرح مانستر تراک از Kyte Baby. " +
                    "پارچه بامبو بسیار نرم و تنفس‌پذیر؛ مناسب خواب راحت شبانه. " +
                    "قابل شستشو در ماشین با برنامه ملایم؛ از خشک‌کن با حرارت بالا پرهیز کنید.",
        ),
        // Bones Coffee — Blueberry Blast Cake 12oz
        "7229323378740" to ProductDetailMock(
            id = "7229323378740",
            slug = "blueberry-blast-cake-12oz",
            title = "بلوبری بلاست کیک | ۱۲ اونس",
            media = ProductDetailMedia(
                imageUrls = listOf(
                    "https://cdn.shopify.com/s/files/1/1475/5488/files/BBCBagFront.jpg?width=1200",
                    "https://cdn.shopify.com/s/files/1/1475/5488/files/BBCBagFront.jpg?width=1200",
                    "https://cdn.shopify.com/s/files/1/1475/5488/files/BBCBagFront.jpg?width=1200",
                ),
                imageMode = ProductImageMode.Fit,
            ),
            merchant = ProductDetailMerchant(
                name = "Bones Coffee Company",
                logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/bones-coffee-company.myshopify.com/1730940233/bonescoffeelogo.png?width=88",
                ratingLabel = "۴.۸",
                reviewCountLabel = "(۶۸.۶K)",
            ),
            productRating = 4.9f,
            productRatingCountLabel = "۶۴۱ امتیاز",
            socialProofLabel = "۱۰۰۰+ خرید در ماه گذشته",
            priceLabel = "۱٬۲۵۰٬۰۰۰ تومان",
            options = listOf(
                ProductOption(
                    id = "grind",
                    name = "آسیاب",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(id = "ground", label = "آسیاب‌شده"),
                        ProductOptionValue(id = "whole", label = "دانه کامل"),
                    ),
                ),
            ),
            description =
                "قهوه بلوبری بلاست کیک از Bones Coffee با رایحه شیرین کیک بلوبری. " +
                    "مناسب اسپرسو و دم‌آوری دستی؛ در بسته‌بندی ۱۲ اونسی.",
            reviews = blueberryReviews(),
        ),
        // Kyte Baby — Long Sleeve Pajamas in Spider-Man (shop.app /products/15358858133615)
        "15358858133615" to ProductDetailMock(
            id = "15358858133615",
            slug = "long-sleeve-pajamas-in-spider-man",
            title = "پیژامه آستین‌بلند اسپایدرمن",
            media = ProductDetailMedia(
                imageUrls = listOf(
                    "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/1701MVSM_01.jpg?width=1200",
                    "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Spider_Man_LS_Toddler_PJs_01.jpg?width=1200",
                    "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Spider_Man_LS_Toddler_PJs_02.jpg?width=1200",
                    "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Spider_Man_LS_Toddler_PJs_03.jpg?width=1200",
                ),
                imageMode = ProductImageMode.Crop,
            ),
            merchant = ProductDetailMerchant(
                name = "Kyte Baby",
                logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/kyte-baby-co.myshopify.com/1725505717/KB_logo_horizontal_white.png?width=88",
                ratingLabel = "۴.۹",
                reviewCountLabel = "(۵۳۷.۴K)",
            ),
            productRating = 5f,
            productRatingCountLabel = "۳۳ امتیاز",
            socialProofLabel = "۱۰۰۰+ خرید در ماه گذشته",
            priceLabel = "۱٬۶۸۰٬۰۰۰ تومان",
            options = listOf(
                ProductOption(
                    id = "size",
                    name = "سایز",
                    selectedIndex = 1,
                    values = listOf(
                        ProductOptionValue(id = "18-24m", label = "۱۸-۲۴ ماه"),
                        ProductOptionValue(id = "2t", label = "۲T"),
                        ProductOptionValue(id = "3t", label = "۳T"),
                        ProductOptionValue(id = "4t", label = "۴T"),
                        ProductOptionValue(id = "5t", label = "۵T"),
                        ProductOptionValue(id = "6t", label = "۶T"),
                        ProductOptionValue(id = "7", label = "۷"),
                        ProductOptionValue(id = "8", label = "۸"),
                        ProductOptionValue(id = "10", label = "۱۰"),
                        ProductOptionValue(id = "12", label = "۱۲"),
                    ),
                ),
                ProductOption(
                    id = "color",
                    name = "رنگ",
                    selectedIndex = 0,
                    values = listOf(
                        ProductOptionValue(
                            id = "spider-man",
                            label = "اسپایدرمن",
                            swatchImageUrl =
                                "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/1701MVSM_01.jpg?width=96",
                        ),
                        ProductOptionValue(
                            id = "oat",
                            label = "جو دوسر",
                            swatchImageUrl =
                                "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/MVSM_01.jpg?width=96",
                        ),
                    ),
                ),
            ),
            description =
                "پیژامه آستین‌بلند کودک با طرح اسپایدرمن از مجموعه Kyte Baby × Marvel. " +
                    "پارچه بامبو نرم و تنفس‌پذیر؛ مناسب بازی و خواب. " +
                    "قابل شستشو در ماشین با برنامه ملایم.",
        ),
    )

    private val byId: Map<String, ProductDetailMock> by lazy {
        buildMap {
            putAll(richOverrides)
            for (product in allMockCategoriesProducts()) {
                if (containsKey(product.id)) continue
                put(
                    product.id,
                    fromCategoriesProduct(
                        product = product,
                        merchantLogoUrl = product.imageUrl,
                    ),
                )
            }
            for (shop in allMockHomeShopCards()) {
                val merchant = merchantFromHomeShop(shop)
                for (peek in shop.products) {
                    if (containsKey(peek.id)) continue
                    put(
                        peek.id,
                        ProductDetailMock(
                            id = peek.id,
                            slug = productSlug(peek.title),
                            title = peek.title,
                            media = ProductDetailMedia(
                                imageUrls = listOf(peek.imageUrl, peek.imageUrl, peek.imageUrl),
                            ),
                            merchant = merchant,
                            productRating = 5f,
                            productRatingCountLabel = "۸۶ امتیاز",
                            socialProofLabel = "۱۰۰+ خرید در ماه گذشته",
                            priceLabel = peek.priceLabel,
                            description = descriptionFor(peek.title, shop.name),
                        ),
                    )
                }
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
        storeName: String = "برند منتخب",
        priceLabel: String = "۱٬۵۰۰٬۰۰۰ تومان",
    ): ProductDetailMock {
        byId(id)?.let { return it }
        val safeTitle = title.ifBlank { "کالای منتخب" }
        val safeStore = storeName.ifBlank { "برند منتخب" }
        val safePrice = priceLabel.ifBlank { "۱٬۵۰۰٬۰۰۰ تومان" }
        return ProductDetailMock(
            id = id,
            slug = productSlug(safeTitle),
            title = safeTitle,
            media = ProductDetailMedia(imageUrls = listOf(imageUrl, imageUrl)),
            merchant = placeholderMerchant(safeStore, logoUrl = imageUrl),
            productRating = 5f,
            productRatingCountLabel = "۴۲ امتیاز",
            socialProofLabel = "۵۰+ خرید در ماه گذشته",
            priceLabel = safePrice,
            description = descriptionFor(safeTitle, safeStore),
        )
    }
}

/**
 * Builds More from / Related / Discover mocks for [product] from shared Categories catalogs.
 */
fun buildProductDetailRecommendations(
    product: ProductDetailMock,
    moreFromTitle: String,
    discoverTitle: String,
): ProductDetailRecommendations {
    val catalog = allMockCategoriesProducts()
    val merchantName = product.merchant.name

    val sameStore = catalog
        .filter { it.storeName.equals(merchantName, ignoreCase = true) && it.id != product.id }
        .distinctBy { it.id }
    val moreFromProducts = when {
        sameStore.size >= 4 -> sameStore.take(12)
        else -> (sameStore + catalog.filter { it.id != product.id })
            .distinctBy { it.id }
            .take(12)
    }.ifEmpty {
        catalog.filter { it.id != product.id }.take(8)
    }

    val relatedRows = catalog
        .filter { !it.storeName.equals(merchantName, ignoreCase = true) }
        .groupBy { it.storeName }
        .entries
        .sortedByDescending { it.value.size }
        .take(4)
        .mapIndexed { index, (store, products) ->
            CategoriesProductRowSection(
                id = "pdp-related-$index-${product.id}",
                title = store,
                products = products.distinctBy { it.id }.take(12),
            )
        }

    val discoverShops = allMockCategoriesMerchantShops()
        .filter { !it.name.equals(merchantName, ignoreCase = true) }
        .distinctBy { it.id }
        .take(10)
        .ifEmpty { allMockCategoriesMerchantShops().take(8) }

    return ProductDetailRecommendations(
        moreFrom = CategoriesProductRowSection(
            id = "pdp-more-from-${product.id}",
            title = moreFromTitle,
            products = moreFromProducts,
        ),
        relatedRows = relatedRows,
        discoverBrands = CategoriesMerchantGridSection(
            id = "pdp-discover-${product.id}",
            title = discoverTitle,
            shops = discoverShops,
        ),
    )
}
