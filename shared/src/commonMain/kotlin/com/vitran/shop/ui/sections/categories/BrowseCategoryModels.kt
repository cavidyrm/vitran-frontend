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

/**
 * Mock browse categories matching shop.app order, colors, and CDN assets.
 * Titles come from string resources; visuals reuse [browseCategoryVisualFallbacks].
 */
@Composable
fun rememberMockBrowseCategories(): List<BrowseCategory> {
    val titles = listOf(
        stringResource(Res.string.home_category_beauty),
        stringResource(Res.string.home_category_women),
        stringResource(Res.string.home_category_men),
        stringResource(Res.string.home_category_home),
        stringResource(Res.string.home_category_fitness),
        stringResource(Res.string.home_category_baby),
        stringResource(Res.string.categories_browse_sporting),
        stringResource(Res.string.home_category_food),
        stringResource(Res.string.categories_browse_toys),
        stringResource(Res.string.categories_browse_pet),
    )

    return remember(titles) {
        titles.mapIndexed { index, title ->
            val visuals = browseCategoryVisualFallbacks.getOrElse(index) {
                browseCategoryVisualFallbacks.first()
            }
            BrowseCategory(
                id = listOf(
                    "beauty", "women", "men", "home", "fitness",
                    "baby", "sporting", "food", "toys", "pet",
                )[index],
                title = title,
                backgroundColor = visuals.backgroundColor,
                imageUrl1 = visuals.imageUrl1,
                imageUrl2 = visuals.imageUrl2,
            )
        }
    }
}
