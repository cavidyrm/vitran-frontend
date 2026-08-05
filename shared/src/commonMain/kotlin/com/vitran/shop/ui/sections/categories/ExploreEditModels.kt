package com.vitran.shop.ui.sections.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.categories_explore_edit_fitness_subtitle
import vitranshop.shared.generated.resources.categories_explore_edit_fitness_title
import vitranshop.shared.generated.resources.categories_explore_edit_hair_subtitle
import vitranshop.shared.generated.resources.categories_explore_edit_hair_title
import vitranshop.shared.generated.resources.categories_explore_edit_kitchen_subtitle
import vitranshop.shared.generated.resources.categories_explore_edit_kitchen_title
import vitranshop.shared.generated.resources.categories_explore_edit_living_subtitle
import vitranshop.shared.generated.resources.categories_explore_edit_living_title
import vitranshop.shared.generated.resources.categories_explore_edit_mindful_subtitle
import vitranshop.shared.generated.resources.categories_explore_edit_mindful_title

/**
 * One editorial “edit” card on the Categories Explore featured carousel
 * (shop.app `/categories` top row).
 */
@Immutable
data class ExploreEdit(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val placeholderColor: Color,
)

private const val ShopEventsAssets =
    "https://cdn.shopify.com/b/shop-shopping-events-assets-production"

/**
 * Mock Explore edits matching shop.app featured curations (+ extras so carousel can scroll).
 */
@Composable
fun rememberMockExploreEdits(): List<ExploreEdit> {
    val mindfulTitle = stringResource(Res.string.categories_explore_edit_mindful_title)
    val mindfulSubtitle = stringResource(Res.string.categories_explore_edit_mindful_subtitle)
    val livingTitle = stringResource(Res.string.categories_explore_edit_living_title)
    val livingSubtitle = stringResource(Res.string.categories_explore_edit_living_subtitle)
    val hairTitle = stringResource(Res.string.categories_explore_edit_hair_title)
    val hairSubtitle = stringResource(Res.string.categories_explore_edit_hair_subtitle)
    val kitchenTitle = stringResource(Res.string.categories_explore_edit_kitchen_title)
    val kitchenSubtitle = stringResource(Res.string.categories_explore_edit_kitchen_subtitle)
    val fitnessTitle = stringResource(Res.string.categories_explore_edit_fitness_title)
    val fitnessSubtitle = stringResource(Res.string.categories_explore_edit_fitness_subtitle)

    return remember(
        mindfulTitle,
        mindfulSubtitle,
        livingTitle,
        livingSubtitle,
        hairTitle,
        hairSubtitle,
        kitchenTitle,
        kitchenSubtitle,
        fitnessTitle,
        fitnessSubtitle,
    ) {
        listOf(
            ExploreEdit(
                id = "mindful",
                title = mindfulTitle,
                subtitle = mindfulSubtitle,
                imageUrl = "$ShopEventsAssets/hocpr3iv675k2tj02nfc47taj0qe.jpg",
                placeholderColor = Color(0xFFC8B8A8),
            ),
            ExploreEdit(
                id = "living",
                title = livingTitle,
                subtitle = livingSubtitle,
                imageUrl = "$ShopEventsAssets/5o5js7m1wk7w03qp2wkfop1cybmp.jpg",
                placeholderColor = Color(0xFFD4CFC6),
            ),
            ExploreEdit(
                id = "hair",
                title = hairTitle,
                subtitle = hairSubtitle,
                imageUrl = "$ShopEventsAssets/qbwj0xu1ry3kz7x6fvzb6f74k56z.jpg",
                placeholderColor = Color(0xFFE8D4D0),
            ),
            ExploreEdit(
                id = "kitchen",
                title = kitchenTitle,
                subtitle = kitchenSubtitle,
                // Reuse living-room CDN asset as kitchen stand-in (mock phase).
                imageUrl = "$ShopEventsAssets/5o5js7m1wk7w03qp2wkfop1cybmp.jpg",
                placeholderColor = Color(0xFFB8C4C0),
            ),
            ExploreEdit(
                id = "fitness",
                title = fitnessTitle,
                subtitle = fitnessSubtitle,
                imageUrl = "$ShopEventsAssets/hocpr3iv675k2tj02nfc47taj0qe.jpg",
                placeholderColor = Color(0xFFA8B4A0),
            ),
        )
    }
}
