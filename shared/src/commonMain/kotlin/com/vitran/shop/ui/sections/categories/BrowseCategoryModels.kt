package com.vitran.shop.ui.sections.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.categories_browse_pet
import vitranshop.shared.generated.resources.categories_browse_sporting
import vitranshop.shared.generated.resources.categories_browse_toys
import vitranshop.shared.generated.resources.home_category_baby
import vitranshop.shared.generated.resources.home_category_beauty
import vitranshop.shared.generated.resources.home_category_fitness
import vitranshop.shared.generated.resources.home_category_food
import vitranshop.shared.generated.resources.home_category_home
import vitranshop.shared.generated.resources.home_category_men
import vitranshop.shared.generated.resources.home_category_women

/**
 * One L1 browse card on the Categories “Browse categories” grid
 * (shop.app `/categories`).
 */
@Immutable
data class BrowseCategory(
    val id: String,
    val title: String,
    val backgroundColor: Color,
    val imageUrl1: String,
    val imageUrl2: String,
)

private const val ShopCategoryAssets =
    "https://cdn.shopify.com/shop-assets/static_uploads/shop-categories"

private fun browseAsset(fileName: String): String =
    "$ShopCategoryAssets/$fileName?width=500"

/**
 * Mock browse categories matching shop.app order, colors, and CDN assets.
 */
@Composable
fun rememberMockBrowseCategories(): List<BrowseCategory> {
    val beauty = stringResource(Res.string.home_category_beauty)
    val women = stringResource(Res.string.home_category_women)
    val men = stringResource(Res.string.home_category_men)
    val home = stringResource(Res.string.home_category_home)
    val fitness = stringResource(Res.string.home_category_fitness)
    val baby = stringResource(Res.string.home_category_baby)
    val sporting = stringResource(Res.string.categories_browse_sporting)
    val food = stringResource(Res.string.home_category_food)
    val toys = stringResource(Res.string.categories_browse_toys)
    val pet = stringResource(Res.string.categories_browse_pet)

    return remember(
        beauty, women, men, home, fitness, baby, sporting, food, toys, pet,
    ) {
        listOf(
            BrowseCategory(
                id = "beauty",
                title = beauty,
                backgroundColor = Color(0xFFBA405A),
                imageUrl1 = browseAsset("beauty-1.jpg"),
                imageUrl2 = browseAsset("beauty-2.png"),
            ),
            BrowseCategory(
                id = "women",
                title = women,
                backgroundColor = Color(0xFF9EA6AC),
                imageUrl1 = browseAsset("women-1.jpg"),
                imageUrl2 = browseAsset("women-2.jpg"),
            ),
            BrowseCategory(
                id = "men",
                title = men,
                backgroundColor = Color(0xFF003988),
                imageUrl1 = browseAsset("men-1.jpg"),
                imageUrl2 = browseAsset("men-2.jpg"),
            ),
            BrowseCategory(
                id = "home",
                title = home,
                backgroundColor = Color(0xFFCE5F01),
                imageUrl1 = browseAsset("home-1.jpg"),
                imageUrl2 = browseAsset("home-2.jpg"),
            ),
            BrowseCategory(
                id = "fitness",
                title = fitness,
                backgroundColor = Color(0xFF9DB798),
                imageUrl1 = browseAsset("fitness-1.png"),
                imageUrl2 = browseAsset("fitness-2.png"),
            ),
            BrowseCategory(
                id = "baby",
                title = baby,
                backgroundColor = Color(0xFF91A49B),
                imageUrl1 = browseAsset("baby-toddler-1.png"),
                imageUrl2 = browseAsset("baby-toddler-2.png"),
            ),
            BrowseCategory(
                id = "sporting",
                title = sporting,
                backgroundColor = Color(0xFF5F4DA0),
                imageUrl1 = browseAsset("sporting-goods-1.png"),
                imageUrl2 = browseAsset("sporting-goods-2.png"),
            ),
            BrowseCategory(
                id = "food",
                title = food,
                backgroundColor = Color(0xFFAD1521),
                imageUrl1 = browseAsset("food-drinks-bg-1.png"),
                imageUrl2 = browseAsset("food-drinks-bg-2.png"),
            ),
            BrowseCategory(
                id = "toys",
                title = toys,
                backgroundColor = Color(0xFF016531),
                imageUrl1 = browseAsset("toys-games-1.png"),
                imageUrl2 = browseAsset("toys-games-bg-2.png"),
            ),
            BrowseCategory(
                id = "pet",
                title = pet,
                backgroundColor = Color(0xFFA27570),
                imageUrl1 = browseAsset("pet-supplies-1.png"),
                imageUrl2 = browseAsset("pet-supplies-2.png"),
            ),
        )
    }
}
