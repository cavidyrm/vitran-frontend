package com.vitran.shop.ui.sections.store

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.vitran.shop.ui.sections.categories.CategoriesProduct

/**
 * Mock products for the Store products grid (shop.app `/m/{handle}` Products).
 *
 * Reuses [CategoriesProduct] so [com.vitran.shop.ui.components.CategoriesProductCard]
 * can render the same feed-card chrome (store name hidden on this page).
 *
 * Sale badges use `"۱۵٪"` (digits then `%`) — a leading Arabic `٪` glyph renders as
 * a broken “x” in some desktop fonts.
 */
@Immutable
data class StoreProductsMock(
    val products: List<CategoriesProduct>,
)

@Composable
fun rememberMockStoreProducts(storeName: String): StoreProductsMock {
    return remember(storeName) {
        StoreProductsMock(products = mockStoreProducts(storeName))
    }
}

/** CDN URLs reused from Categories / Home mocks so placeholders always resolve. */
private fun mockStoreProducts(storeName: String): List<CategoriesProduct> = listOf(
    item(
        id = "store-p1",
        storeName = storeName,
        title = "کریر هیپ سیت اصلی",
        imageUrl = "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
        rating = 5f,
        reviewCountLabel = "(۸٫۸K)",
        priceLabel = "۲٬۹۵۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p2",
        storeName = storeName,
        title = "اتصال هندزفری هیپ سیت",
        imageUrl = "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
        rating = 5f,
        reviewCountLabel = "(۱٫۶K)",
        priceLabel = "۳٬۲۵۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p3",
        storeName = storeName,
        title = "افزاینده کمربند",
        imageUrl = "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=384",
        rating = 5f,
        reviewCountLabel = "(۶۲۰)",
        priceLabel = "۴۶۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p4",
        storeName = storeName,
        title = "بند کراس‌بادی",
        imageUrl = "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
        rating = 4.5f,
        reviewCountLabel = "(۳۲۱)",
        priceLabel = "۱٬۴۷۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p5",
        storeName = storeName,
        title = "کوله پک",
        imageUrl = "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384",
        rating = 4.5f,
        reviewCountLabel = "(۴۰۵)",
        priceLabel = "۱٬۹۶۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p6",
        storeName = storeName,
        title = "اسلینگ لیفت‌آف کودک نوپا",
        imageUrl = "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384",
        rating = 5f,
        reviewCountLabel = "(۱۷۹)",
        priceLabel = "۲٬۸۵۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p7",
        storeName = storeName,
        title = "پاد تعویض پوشک",
        imageUrl = "https://cdn.shopify.com/s/files/1/2462/9621/files/Screenshot_2026-02-03_at_8.52.17_AM.png?v=1770130590&width=384",
        rating = 5f,
        reviewCountLabel = "(۱۴۸)",
        priceLabel = "۸۹۰٬۰۰۰ تومان",
        discountLabel = "۱۵٪",
        compareAtPriceLabel = "۱٬۰۵۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p8",
        storeName = storeName,
        title = "کریر مرواریدی",
        imageUrl = "https://cdn.shopify.com/s/files/1/0438/1069/files/CZ_Linen_Bedding_French_Blue_01_Sheet_Set_e872ba82-953b-4ecd-a0e7-93e2dfee5509.jpg?v=1776470016&width=384",
        rating = 5f,
        reviewCountLabel = "(۱۴۲)",
        priceLabel = "۴٬۲۰۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p9",
        storeName = storeName,
        title = "مینی پک",
        imageUrl = "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=384",
        rating = 4.5f,
        reviewCountLabel = "(۴۶)",
        priceLabel = "۱٬۱۲۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p10",
        storeName = storeName,
        title = "دندون‌گیر حرفی",
        imageUrl = "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Monster_Truck_LS_Toddler_PJs_01.jpg?width=384",
        rating = 5f,
        reviewCountLabel = "(۳۱)",
        priceLabel = "۳۸۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p11",
        storeName = storeName,
        title = "کریر عروسکی",
        imageUrl = "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/1701MVSM_01.jpg?width=384",
        rating = 5f,
        reviewCountLabel = "(۱۵)",
        priceLabel = "۵۲۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p12",
        storeName = storeName,
        title = "کیف میان‌وعده کودک",
        imageUrl = "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/SakuraIceCream_SSToddlerPJ_01.jpg?width=384",
        rating = 4.5f,
        reviewCountLabel = "(۹)",
        priceLabel = "۶۴۰٬۰۰۰ تومان",
        discountLabel = "۱۰٪",
        compareAtPriceLabel = "۷۱۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p13",
        storeName = storeName,
        title = "کلاه ست مادر و کودک",
        imageUrl = "https://cdn.shopify.com/s/files/1/1475/5488/files/BBCBagFront.jpg?width=384",
        rating = 4.5f,
        reviewCountLabel = "(۲۲)",
        priceLabel = "۷۸۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p14",
        storeName = storeName,
        title = "لیوان نی‌دار",
        imageUrl = "https://cdn.shopify.com/s/files/1/1475/5488/files/HIGBagFront.jpg?width=384",
        rating = 5f,
        reviewCountLabel = "(۵۴)",
        priceLabel = "۴۴۰٬۰۰۰ تومان",
        discountLabel = "۲۰٪",
        compareAtPriceLabel = "۵۵۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p15",
        storeName = storeName,
        title = "دندون‌گیر فیل",
        imageUrl = "https://cdn.shopify.com/s/files/1/1475/5488/files/cinnamon_roll_12oz_front_-_GR.jpg?width=384",
        rating = 4.5f,
        reviewCountLabel = "(۱۸)",
        priceLabel = "۳۱۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p16",
        storeName = storeName,
        title = "عروسک توشی بیر",
        imageUrl = "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
        rating = 5f,
        reviewCountLabel = "(۶۷)",
        priceLabel = "۹۵۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p17",
        storeName = storeName,
        title = "رَتِل چوبی",
        imageUrl = "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
        rating = 4.5f,
        reviewCountLabel = "(۴۱)",
        priceLabel = "۲۷۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p18",
        storeName = storeName,
        title = "کارت هدیه فروشگاه",
        imageUrl = "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
        rating = 5f,
        reviewCountLabel = "(۱۲)",
        priceLabel = "۵۰۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p19",
        storeName = storeName,
        title = "کریر حیوان خانگی",
        imageUrl = "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384",
        rating = 4.5f,
        reviewCountLabel = "(۸)",
        priceLabel = "۳٬۱۰۰٬۰۰۰ تومان",
        discountLabel = "۱۲٪",
        compareAtPriceLabel = "۳٬۵۲۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p20",
        storeName = storeName,
        title = "جعبه بسته‌بندی هدیه",
        imageUrl = "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384",
        rating = 5f,
        reviewCountLabel = "(۳۳)",
        priceLabel = "۱۸۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p21",
        storeName = storeName,
        title = "کاور عینک آفتابی",
        imageUrl = "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=384",
        rating = 4.5f,
        reviewCountLabel = "(۲۷)",
        priceLabel = "۳۶۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p22",
        storeName = storeName,
        title = "زنجیر دندون‌گیر",
        imageUrl = "https://cdn.shopify.com/s/files/1/0438/1069/files/CZ_Linen_Bedding_French_Blue_01_Sheet_Set_e872ba82-953b-4ecd-a0e7-93e2dfee5509.jpg?v=1776470016&width=384",
        rating = 5f,
        reviewCountLabel = "(۱۹)",
        priceLabel = "۲۹۰٬۰۰۰ تومان",
        discountLabel = "۲۵٪",
        compareAtPriceLabel = "۳۸۵٬۰۰۰ تومان",
    ),
    item(
        id = "store-p23",
        storeName = storeName,
        title = "استکر فانوس",
        imageUrl = "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=384",
        rating = 4.5f,
        reviewCountLabel = "(۱۴)",
        priceLabel = "۴۲۰٬۰۰۰ تومان",
    ),
    item(
        id = "store-p24",
        storeName = storeName,
        title = "خانه بازی چوبی",
        imageUrl = "https://cdn.shopify.com/s/files/1/2462/9621/files/Screenshot_2026-02-03_at_8.52.17_AM.png?v=1770130590&width=384",
        rating = 5f,
        reviewCountLabel = "(۵۶)",
        priceLabel = "۱٬۸۵۰٬۰۰۰ تومان",
    ),
)

private fun item(
    id: String,
    storeName: String,
    title: String,
    imageUrl: String,
    rating: Float,
    reviewCountLabel: String,
    priceLabel: String,
    discountLabel: String? = null,
    compareAtPriceLabel: String? = null,
) = CategoriesProduct(
    id = id,
    storeName = storeName,
    title = title,
    imageUrl = imageUrl,
    rating = rating,
    reviewCountLabel = reviewCountLabel,
    priceLabel = priceLabel,
    discountLabel = discountLabel,
    compareAtPriceLabel = compareAtPriceLabel,
)
