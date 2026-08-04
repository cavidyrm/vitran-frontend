package com.vitran.shop.ui.sections.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.home_category_baby
import vitranshop.shared.generated.resources.home_category_beauty
import vitranshop.shared.generated.resources.home_category_fitness
import vitranshop.shared.generated.resources.home_category_food
import vitranshop.shared.generated.resources.home_category_home
import vitranshop.shared.generated.resources.home_category_men
import vitranshop.shared.generated.resources.home_category_women

/**
 * One Home category pill (shop.app L1 category chip).
 * [imageUrl] is a hardcoded shop.app CDN pill PNG (mock phase).
 */
@Immutable
data class HomeCategory(
    val id: String,
    val title: String,
    val imageUrl: String,
)

private const val ShopCategoryPillAssets =
    "https://shopify-assets.shopifycdn.com/shop-assets/static_uploads/shop-categories"

/**
 * Mock L1 categories matching shop.app home pill row order and CDN assets.
 */
@Composable
fun rememberMockHomeCategories(): List<HomeCategory> {
    val women = stringResource(Res.string.home_category_women)
    val men = stringResource(Res.string.home_category_men)
    val beauty = stringResource(Res.string.home_category_beauty)
    val home = stringResource(Res.string.home_category_home)
    val fitness = stringResource(Res.string.home_category_fitness)
    val baby = stringResource(Res.string.home_category_baby)
    val food = stringResource(Res.string.home_category_food)
    return remember(women, men, beauty, home, fitness, baby, food) {
        listOf(
            HomeCategory(
                id = "women",
                title = women,
                imageUrl = "$ShopCategoryPillAssets/20260326_1_L1_womenswear_pill.png?width=640",
            ),
            HomeCategory(
                id = "men",
                title = men,
                imageUrl = "$ShopCategoryPillAssets/20260326_2_L1_menswear_pill.png?width=640",
            ),
            HomeCategory(
                id = "beauty",
                title = beauty,
                imageUrl = "$ShopCategoryPillAssets/20260326_5_L1_beauty_pill.png?width=640",
            ),
            HomeCategory(
                id = "home",
                title = home,
                imageUrl = "$ShopCategoryPillAssets/20260326_6_L1_home_pill.png?width=640",
            ),
            HomeCategory(
                id = "fitness",
                title = fitness,
                imageUrl = "$ShopCategoryPillAssets/20260326_69_L1_fitness_nutrition_pill.png?width=640",
            ),
            HomeCategory(
                id = "baby",
                title = baby,
                imageUrl = "$ShopCategoryPillAssets/20260326_209_L1_baby_toddler_pill.png?width=640",
            ),
            HomeCategory(
                id = "food",
                title = food,
                imageUrl = "$ShopCategoryPillAssets/20260326_251_L1_food_drinks_pill.png?width=640",
            ),
        )
    }
}
