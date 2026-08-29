package com.vitran.shop.ui.sections.categories

import androidx.compose.ui.graphics.Color

private const val ShopCategoryAssets =
    "https://cdn.shopify.com/shop-assets/static_uploads/shop-categories"

private fun browseAsset(fileName: String): String =
    "$ShopCategoryAssets/$fileName?width=500"

/**
 * Visual-only fallbacks for browse grid cards when taxonomy list nodes lack CDN assets.
 * Index-aligned with API root category order when possible.
 */
data class BrowseCategoryVisuals(
    val backgroundColor: Color,
    val imageUrl1: String,
    val imageUrl2: String,
)

val browseCategoryVisualFallbacks: List<BrowseCategoryVisuals> = listOf(
    BrowseCategoryVisuals(
        backgroundColor = Color(0xFFBA405A),
        imageUrl1 = browseAsset("beauty-1.jpg"),
        imageUrl2 = browseAsset("beauty-2.png"),
    ),
    BrowseCategoryVisuals(
        backgroundColor = Color(0xFF9EA6AC),
        imageUrl1 = browseAsset("women-1.jpg"),
        imageUrl2 = browseAsset("women-2.jpg"),
    ),
    BrowseCategoryVisuals(
        backgroundColor = Color(0xFF003988),
        imageUrl1 = browseAsset("men-1.jpg"),
        imageUrl2 = browseAsset("men-2.jpg"),
    ),
    BrowseCategoryVisuals(
        backgroundColor = Color(0xFFCE5F01),
        imageUrl1 = browseAsset("home-1.jpg"),
        imageUrl2 = browseAsset("home-2.jpg"),
    ),
    BrowseCategoryVisuals(
        backgroundColor = Color(0xFF9DB798),
        imageUrl1 = browseAsset("fitness-1.png"),
        imageUrl2 = browseAsset("fitness-2.png"),
    ),
    BrowseCategoryVisuals(
        backgroundColor = Color(0xFF91A49B),
        imageUrl1 = browseAsset("baby-toddler-1.png"),
        imageUrl2 = browseAsset("baby-toddler-2.png"),
    ),
    BrowseCategoryVisuals(
        backgroundColor = Color(0xFF5F4DA0),
        imageUrl1 = browseAsset("sporting-goods-1.png"),
        imageUrl2 = browseAsset("sporting-goods-2.png"),
    ),
    BrowseCategoryVisuals(
        backgroundColor = Color(0xFFAD1521),
        imageUrl1 = browseAsset("food-drinks-bg-1.png"),
        imageUrl2 = browseAsset("food-drinks-bg-2.png"),
    ),
    BrowseCategoryVisuals(
        backgroundColor = Color(0xFF016531),
        imageUrl1 = browseAsset("toys-games-1.png"),
        imageUrl2 = browseAsset("toys-games-bg-2.png"),
    ),
    BrowseCategoryVisuals(
        backgroundColor = Color(0xFFA27570),
        imageUrl1 = browseAsset("pet-supplies-1.png"),
        imageUrl2 = browseAsset("pet-supplies-2.png"),
    ),
)

private val defaultBrowseVisuals = BrowseCategoryVisuals(
    backgroundColor = Color(0xFF9EA6AC),
    imageUrl1 = browseAsset("women-1.jpg"),
    imageUrl2 = browseAsset("women-2.jpg"),
)

fun browseCategoryVisualsAt(index: Int): BrowseCategoryVisuals =
    browseCategoryVisualFallbacks.getOrElse(index) { defaultBrowseVisuals }
