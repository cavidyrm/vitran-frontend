package com.vitran.shop.ui.sections.home

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * One brand slot in the desktop hero collage (square card).
 * Image slots stay composable so network media can plug in later.
 */
@Immutable
data class HeroCollageBrand(
    val name: String,
    val backgroundColor: Color,
    val logoLabel: String = name,
)

/**
 * One product slot in the desktop hero collage.
 */
@Immutable
data class HeroCollageProduct(
    val title: String,
    val rating: Float,
    val reviewCountLabel: String,
    val imageColor: Color,
)

/**
 * A single collage scene: center media + 2 brands + 2 products.
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

/** Mock home-themed scene matching shop.app collage structure. */
val MockHeroCollageHomeScene = HeroCollageScene(
    id = "home",
    centerMediaColor = Color(0xFFE5E0D8),
    brands = listOf(
        HeroCollageBrand(
            name = "راگبل",
            backgroundColor = Color(0xFF5C6B73),
            logoLabel = "Ruggable",
        ),
        HeroCollageBrand(
            name = "موجی",
            backgroundColor = Color(0xFF2C2C2C),
            logoLabel = "MUJI",
        ),
    ),
    products = listOf(
        HeroCollageProduct(
            title = "لیوان ارگونومیک ۴۰ اونس",
            rating = 5f,
            reviewCountLabel = "(۱۲)",
            imageColor = Color(0xFFC4A484),
        ),
        HeroCollageProduct(
            title = "ست ملحفه بامبو",
            rating = 5f,
            reviewCountLabel = "(۱۲٫۲ هزار)",
            imageColor = Color(0xFFE8E0D8),
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
            logoLabel = "BAGGU",
        ),
        HeroCollageBrand(
            name = "استیو مدن",
            backgroundColor = Color(0xFF1A1A1A),
            logoLabel = "Steve Madden",
        ),
    ),
    products = listOf(
        HeroCollageProduct(
            title = "کفش کلئو مشکی",
            rating = 5f,
            reviewCountLabel = "(۵۴)",
            imageColor = Color(0xFF3D3D3D),
        ),
        HeroCollageProduct(
            title = "صندل جون شکلاتی",
            rating = 4f,
            reviewCountLabel = "(۷)",
            imageColor = Color(0xFF6B4E3D),
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
            logoLabel = "Saltstone",
        ),
        HeroCollageBrand(
            name = "فنتی",
            backgroundColor = Color(0xFF2A1215),
            logoLabel = "Fenty",
        ),
    ),
    products = listOf(
        HeroCollageProduct(
            title = "روغن لب براق",
            rating = 5f,
            reviewCountLabel = "(۷۷۹)",
            imageColor = Color(0xFFC97B84),
        ),
        HeroCollageProduct(
            title = "سایه چشم سول‌گیزر",
            rating = 5f,
            reviewCountLabel = "(۷۷۸)",
            imageColor = Color(0xFF5A4A6A),
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
            logoLabel = "TRUFF",
        ),
        HeroCollageBrand(
            name = "اوالا",
            backgroundColor = Color(0xFF4A90A4),
            logoLabel = "Owala",
        ),
    ),
    products = listOf(
        HeroCollageProduct(
            title = "کیت اسموتی توت‌فرنگی",
            rating = 5f,
            reviewCountLabel = "(۱۵۰)",
            imageColor = Color(0xFFE07A7A),
        ),
        HeroCollageProduct(
            title = "چیپس پلنتین نمکی",
            rating = 4f,
            reviewCountLabel = "(۱۴)",
            imageColor = Color(0xFFD4A84B),
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
