package com.vitran.shop.ui.sections.home

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * One brand slot in the desktop hero collage (square card).
 * [backgroundImageUrl] / [logoImageUrl] are hardcoded shop.app CDN URLs (mock phase).
 */
@Immutable
data class HeroCollageBrand(
    val name: String,
    val backgroundColor: Color,
    val backgroundImageUrl: String,
    val logoLabel: String = name,
    val logoImageUrl: String? = null,
)

/**
 * One product slot in the desktop hero collage.
 * [imageUrl] is a hardcoded CDN URL; [imageColor] is the loading / error placeholder.
 */
@Immutable
data class HeroCollageProduct(
    val title: String,
    val rating: Float,
    val reviewCountLabel: String,
    val imageColor: Color,
    val imageUrl: String,
)

/**
 * A single collage scene: center media + 2 brands + 2 products.
 * Slot order matches shop.app delays: brands[0]=delay 3 (right), brands[1]=delay 7 (left);
 * products[0]=delay 1 (right), products[1]=delay 6 (left).
 */
@Immutable
data class HeroCollageScene(
    val id: String,
    val centerMediaColor: Color,
    val brands: List<HeroCollageBrand>,
    val products: List<HeroCollageProduct>,
) {
    init {
        require(brands.size == 2) { "Hero collage expects exactly 2 brand slots" }
        require(products.size == 2) { "Hero collage expects exactly 2 product slots" }
    }
}

private const val ShopifyCloudAssets =
    "https://shopify-assets.shopifycdn.com/shopifycloud/shop-client/production/assets"

/** Mock home-themed scene matching shop.app collage structure. */
val MockHeroCollageHomeScene = HeroCollageScene(
    id = "home",
    centerMediaColor = Color(0xFFE5E0D8),
    brands = listOf(
        HeroCollageBrand(
            name = "راگبل",
            backgroundColor = Color(0xFF5C6B73),
            backgroundImageUrl = "$ShopifyCloudAssets/ruggable-background-5TGUcV7x.webp",
            logoLabel = "Ruggable",
            // shop.app serves a data-URI logo — keep text fallback.
            logoImageUrl = null,
        ),
        HeroCollageBrand(
            name = "موجی",
            backgroundColor = Color(0xFF2C2C2C),
            backgroundImageUrl = "$ShopifyCloudAssets/muji-CWPumesk.webp",
            logoLabel = "MUJI",
            logoImageUrl = null,
        ),
    ),
    products = listOf(
        HeroCollageProduct(
            title = "لیوان Era Flip ۴۰ اونس | Mocha Dot",
            rating = 5f,
            reviewCountLabel = "(۱۲)",
            imageColor = Color(0xFFC4A484),
            imageUrl = "https://cdn.shopify.com/s/files/1/1114/2308/files/Era-Flip-40-MochaDot.png?v=1784148747&width=480",
        ),
        HeroCollageProduct(
            title = "لیوان Era ۴۰ اونس | Wildflower Whisper",
            rating = 5f,
            reviewCountLabel = "(۴۰)",
            imageColor = Color(0xFFE8E0D8),
            imageUrl = "https://cdn.shopify.com/s/files/1/1114/2308/files/Era-40-WildflowerWhisper.png?v=1782340164&width=480",
        ),
    ),
)

val MockHeroCollageFashionScene = HeroCollageScene(
    id = "fashion",
    centerMediaColor = Color(0xFFD9C9BE),
    brands = listOf(
        HeroCollageBrand(
            name = "بگو",
            backgroundColor = Color(0xFFE85D4C),
            backgroundImageUrl = "$ShopifyCloudAssets/baggu-background-BqbUua-o.webp",
            logoLabel = "BAGGU",
            logoImageUrl = "$ShopifyCloudAssets/baggu-logo-B94g3XXc.webp",
        ),
        HeroCollageBrand(
            name = "استیو مدن",
            backgroundColor = Color(0xFF1A1A1A),
            backgroundImageUrl = "$ShopifyCloudAssets/stevemadden-background--YiGumEm.webp",
            logoLabel = "Steve Madden",
            logoImageUrl = "$ShopifyCloudAssets/stevemadden-logo-BOIPC7s1.webp",
        ),
    ),
    products = listOf(
        HeroCollageProduct(
            title = "کفش کلئو مشکی",
            rating = 5f,
            reviewCountLabel = "(۵۴)",
            imageColor = Color(0xFF3D3D3D),
            imageUrl = "https://cdn.shopify.com/s/files/1/0082/8339/0014/files/VEHLA12290.jpg?v=1774562636&width=480",
        ),
        HeroCollageProduct(
            title = "صندل جون شکلاتی",
            rating = 4f,
            reviewCountLabel = "(۷)",
            imageColor = Color(0xFF6B4E3D),
            imageUrl = "https://cdn.shopify.com/s/files/1/0082/8339/0014/files/VEHLA12391B.jpg?v=1776236528&width=480",
        ),
    ),
)

val MockHeroCollageBeautyScene = HeroCollageScene(
    id = "beauty",
    centerMediaColor = Color(0xFFE8D5D8),
    brands = listOf(
        HeroCollageBrand(
            name = "سالت‌استون",
            backgroundColor = Color(0xFF8B9A7D),
            backgroundImageUrl = "$ShopifyCloudAssets/saltstone-background-CoaMrUwA.webp",
            logoLabel = "Saltstone",
            logoImageUrl = "$ShopifyCloudAssets/saltstone-logo-p1evxBed.webp",
        ),
        HeroCollageBrand(
            name = "فنتی",
            backgroundColor = Color(0xFF2A1215),
            backgroundImageUrl = "$ShopifyCloudAssets/fenty-background-DwZg3Veu.webp",
            logoLabel = "Fenty",
            logoImageUrl = "$ShopifyCloudAssets/fenty-logo-DCifHRWr.webp",
        ),
    ),
    products = listOf(
        HeroCollageProduct(
            title = "روغن لب براق",
            rating = 5f,
            reviewCountLabel = "(۷۷۹)",
            imageColor = Color(0xFFC97B84),
            imageUrl = "https://cdn.shopify.com/s/files/1/0131/5409/1065/files/WLO_PDP_HeroVessel_SLUSHIE_5.2025_22261aba-2760-4cec-9881-bd49aa1f463e.jpg?v=1749741937&width=480",
        ),
        HeroCollageProduct(
            title = "سایه چشم سول‌گیزر",
            rating = 5f,
            reviewCountLabel = "(۷۸۳)",
            imageColor = Color(0xFF5A4A6A),
            imageUrl = "https://cdn.shopify.com/s/files/1/0131/5409/1065/files/Soulgazer_Hypnotize_Smudge.jpg?v=1718652679&width=480",
        ),
    ),
)

val MockHeroCollageFoodScene = HeroCollageScene(
    id = "food",
    centerMediaColor = Color(0xFFE8DFC8),
    brands = listOf(
        HeroCollageBrand(
            name = "تراف",
            backgroundColor = Color(0xFF3B1F1A),
            backgroundImageUrl = "$ShopifyCloudAssets/truff-background-DAS6Epbi.webp",
            logoLabel = "TRUFF",
            logoImageUrl = null,
        ),
        HeroCollageBrand(
            name = "اوالا",
            backgroundColor = Color(0xFF4A90A4),
            backgroundImageUrl = "$ShopifyCloudAssets/owala-background-sE_ttWeX.webp",
            logoLabel = "Owala",
            logoImageUrl = "$ShopifyCloudAssets/owala-logo-C4QoZeFE.webp",
        ),
    ),
    products = listOf(
        HeroCollageProduct(
            title = "کیت اسموتی توت‌فرنگی",
            rating = 5f,
            reviewCountLabel = "(۱۵۰)",
            imageColor = Color(0xFFE07A7A),
            imageUrl = "https://cdn.shopify.com/s/files/1/0550/8559/6810/files/STRAWBERRY_SIN_GLAZE_HERO_SHOPIFY.png?v=1784223566&width=480",
        ),
        HeroCollageProduct(
            title = "چیپس پلنتین نمکی",
            rating = 4f,
            reviewCountLabel = "(۱۴)",
            imageColor = Color(0xFFD4A84B),
            imageUrl = "https://cdn.shopify.com/s/files/1/0550/8559/6810/files/Untitled_design_-_2024-08-20T155117.981.png?v=1724194285&width=480",
        ),
    ),
)

/** Cycling mock catalog used by [HeroCollage] (≈ shop.app theme loop). */
val MockHeroCollageScenes: List<HeroCollageScene> = listOf(
    MockHeroCollageHomeScene,
    MockHeroCollageFashionScene,
    MockHeroCollageBeautyScene,
    MockHeroCollageFoodScene,
)
