package com.vitran.shop.ui.sections.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.home_category_baby
import vitranshop.shared.generated.resources.home_category_beauty
import vitranshop.shared.generated.resources.home_category_fitness
import vitranshop.shared.generated.resources.home_category_food
import vitranshop.shared.generated.resources.home_category_home
import vitranshop.shared.generated.resources.home_category_men
import vitranshop.shared.generated.resources.home_category_women
import vitranshop.shared.generated.resources.home_mosaic_anti_aging
import vitranshop.shared.generated.resources.home_mosaic_appliances
import vitranshop.shared.generated.resources.home_mosaic_blankets
import vitranshop.shared.generated.resources.home_mosaic_candy
import vitranshop.shared.generated.resources.home_mosaic_coffee
import vitranshop.shared.generated.resources.home_mosaic_diapers
import vitranshop.shared.generated.resources.home_mosaic_dresses
import vitranshop.shared.generated.resources.home_mosaic_drinks_shakes
import vitranshop.shared.generated.resources.home_mosaic_exercise
import vitranshop.shared.generated.resources.home_mosaic_formula
import vitranshop.shared.generated.resources.home_mosaic_hair_styling
import vitranshop.shared.generated.resources.home_mosaic_home_fragrances
import vitranshop.shared.generated.resources.home_mosaic_hoodies
import vitranshop.shared.generated.resources.home_mosaic_lotion
import vitranshop.shared.generated.resources.home_mosaic_outfits
import vitranshop.shared.generated.resources.home_mosaic_pants
import vitranshop.shared.generated.resources.home_mosaic_perfume
import vitranshop.shared.generated.resources.home_mosaic_rugs
import vitranshop.shared.generated.resources.home_mosaic_shirts
import vitranshop.shared.generated.resources.home_mosaic_snacks
import vitranshop.shared.generated.resources.home_mosaic_sneakers
import vitranshop.shared.generated.resources.home_mosaic_strollers
import vitranshop.shared.generated.resources.home_mosaic_supplements
import vitranshop.shared.generated.resources.home_mosaic_tea
import vitranshop.shared.generated.resources.home_mosaic_tshirts
import vitranshop.shared.generated.resources.home_mosaic_vitamins

/**
 * One L2 tile inside a Home category mosaic (shop.app 2×2 subcategory cell).
 * [imageUrl] is a hardcoded shop.app CDN PNG (colors are baked into the asset).
 * [placeholderColor] fills while loading / on error (`bg-bg-fill-secondary`).
 */
@Immutable
data class HomeCategoryMosaicTile(
    val id: String,
    val title: String,
    val imageUrl: String,
    val placeholderColor: Color = MosaicTilePlaceholder,
)

/**
 * One L1 mosaic card: title + chevron header and exactly four L2 tiles.
 */
@Immutable
data class HomeCategoryMosaic(
    val id: String,
    val title: String,
    val tiles: List<HomeCategoryMosaicTile>,
) {
    init {
        require(tiles.size == 4) { "Category mosaic expects exactly 4 tiles" }
    }
}

/** shop.app `bg-bg-fill-secondary` behind mosaic tile images. */
val MosaicTilePlaceholder = Color(0xFFF2F4F5)

private const val ShopCategoryAssets =
    "https://shopify-assets.shopifycdn.com/shop-assets/static_uploads/shop-categories"

private fun mosaicAsset(fileName: String): String =
    "$ShopCategoryAssets/$fileName?width=640"

/**
 * Mock L1 mosaics matching shop.app home carousel order and CDN assets.
 */
@Composable
fun rememberMockHomeCategoryMosaics(): List<HomeCategoryMosaic> {
    val women = stringResource(Res.string.home_category_women)
    val men = stringResource(Res.string.home_category_men)
    val beauty = stringResource(Res.string.home_category_beauty)
    val home = stringResource(Res.string.home_category_home)
    val fitness = stringResource(Res.string.home_category_fitness)
    val baby = stringResource(Res.string.home_category_baby)
    val food = stringResource(Res.string.home_category_food)

    val dresses = stringResource(Res.string.home_mosaic_dresses)
    val shirts = stringResource(Res.string.home_mosaic_shirts)
    val sneakers = stringResource(Res.string.home_mosaic_sneakers)
    val pants = stringResource(Res.string.home_mosaic_pants)
    val hoodies = stringResource(Res.string.home_mosaic_hoodies)
    val tshirts = stringResource(Res.string.home_mosaic_tshirts)
    val lotion = stringResource(Res.string.home_mosaic_lotion)
    val hairStyling = stringResource(Res.string.home_mosaic_hair_styling)
    val antiAging = stringResource(Res.string.home_mosaic_anti_aging)
    val perfume = stringResource(Res.string.home_mosaic_perfume)
    val blankets = stringResource(Res.string.home_mosaic_blankets)
    val rugs = stringResource(Res.string.home_mosaic_rugs)
    val homeFragrances = stringResource(Res.string.home_mosaic_home_fragrances)
    val appliances = stringResource(Res.string.home_mosaic_appliances)
    val exercise = stringResource(Res.string.home_mosaic_exercise)
    val supplements = stringResource(Res.string.home_mosaic_supplements)
    val vitamins = stringResource(Res.string.home_mosaic_vitamins)
    val drinksShakes = stringResource(Res.string.home_mosaic_drinks_shakes)
    val formula = stringResource(Res.string.home_mosaic_formula)
    val strollers = stringResource(Res.string.home_mosaic_strollers)
    val diapers = stringResource(Res.string.home_mosaic_diapers)
    val outfits = stringResource(Res.string.home_mosaic_outfits)
    val coffee = stringResource(Res.string.home_mosaic_coffee)
    val tea = stringResource(Res.string.home_mosaic_tea)
    val candy = stringResource(Res.string.home_mosaic_candy)
    val snacks = stringResource(Res.string.home_mosaic_snacks)

    return remember(
        women, men, beauty, home, fitness, baby, food,
        dresses, shirts, sneakers, pants, hoodies, tshirts,
        lotion, hairStyling, antiAging, perfume,
        blankets, rugs, homeFragrances, appliances,
        exercise, supplements, vitamins, drinksShakes,
        formula, strollers, diapers, outfits,
        coffee, tea, candy, snacks,
    ) {
        listOf(
            HomeCategoryMosaic(
                id = "women",
                title = women,
                tiles = listOf(
                    HomeCategoryMosaicTile(
                        id = "dresses",
                        title = dresses,
                        imageUrl = mosaicAsset("20260326_27_L2_womenswear_dresses.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "shirts",
                        title = shirts,
                        imageUrl = mosaicAsset("20260326_314_L3_womenswear_shirts_tops_shirts.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "sneakers",
                        title = sneakers,
                        imageUrl = mosaicAsset("20260326_188_L3_womenswear_shoes_sneakers.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "pants",
                        title = pants,
                        imageUrl = mosaicAsset("20260326_26_L2_womenswear_pants.png"),
                    ),
                ),
            ),
            HomeCategoryMosaic(
                id = "men",
                title = men,
                tiles = listOf(
                    HomeCategoryMosaicTile(
                        id = "hoodies",
                        title = hoodies,
                        imageUrl = mosaicAsset("20260326_318_L3_menswear_shirts_tops_hoodies.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "pants",
                        title = pants,
                        imageUrl = mosaicAsset("20260326_17_L2_menswear_pants.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "tshirts",
                        title = tshirts,
                        imageUrl = mosaicAsset("20260326_317_L3_menswear_shirts_tops_t_shirts.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "sneakers",
                        title = sneakers,
                        imageUrl = mosaicAsset("20260326_205_L3_menswear_shoes_sneakers.png"),
                    ),
                ),
            ),
            HomeCategoryMosaic(
                id = "beauty",
                title = beauty,
                tiles = listOf(
                    HomeCategoryMosaicTile(
                        id = "lotion",
                        title = lotion,
                        imageUrl = mosaicAsset("20260326_55_L3_beauty_skin_care_lotion_moisturizer.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "hair_styling",
                        title = hairStyling,
                        imageUrl = mosaicAsset("20260326_206_L3_beauty_hair_care_hair_styling_products.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "anti_aging",
                        title = antiAging,
                        imageUrl = mosaicAsset("20260326_59_L3_beauty_skin_care_anti_aging_kits.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "perfume",
                        title = perfume,
                        imageUrl = mosaicAsset("20260417_66_L2_beauty_perfume_cologne.png"),
                    ),
                ),
            ),
            HomeCategoryMosaic(
                id = "home",
                title = home,
                tiles = listOf(
                    HomeCategoryMosaicTile(
                        id = "blankets",
                        title = blankets,
                        imageUrl = mosaicAsset("20260326_90_L3_home_bedding_blankets.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "rugs",
                        title = rugs,
                        imageUrl = mosaicAsset("20260326_77_L3_home_decor_rugs.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "home_fragrances",
                        title = homeFragrances,
                        imageUrl = mosaicAsset("20260417_79_L3_home_decor_home_fragrances.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "appliances",
                        title = appliances,
                        imageUrl = mosaicAsset("20260326_95_L2_home_household_appliances.png"),
                    ),
                ),
            ),
            HomeCategoryMosaic(
                id = "fitness",
                title = fitness,
                tiles = listOf(
                    HomeCategoryMosaicTile(
                        id = "exercise",
                        title = exercise,
                        imageUrl = mosaicAsset("20260326_250_L2_fitness_nutrition_exercise_equipment.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "supplements",
                        title = supplements,
                        imageUrl = mosaicAsset(
                            "20260326_242_L3_fitness_nutrition_vitamins_supplements_supplements.png",
                        ),
                    ),
                    HomeCategoryMosaicTile(
                        id = "vitamins",
                        title = vitamins,
                        imageUrl = mosaicAsset(
                            "20260326_241_L3_fitness_nutrition_vitamins_supplements_vitamins.png",
                        ),
                    ),
                    HomeCategoryMosaicTile(
                        id = "drinks_shakes",
                        title = drinksShakes,
                        imageUrl = mosaicAsset(
                            "20260326_246_L3_fitness_nutrition_nutrition_drinks_shakes.png",
                        ),
                    ),
                ),
            ),
            HomeCategoryMosaic(
                id = "baby",
                title = baby,
                tiles = listOf(
                    HomeCategoryMosaicTile(
                        id = "formula",
                        title = formula,
                        imageUrl = mosaicAsset("20260326_219_L3_baby_toddler_nursing_feeding_formula.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "strollers",
                        title = strollers,
                        imageUrl = mosaicAsset("20260326_225_L2_baby_toddler_strollers_travel.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "diapers",
                        title = diapers,
                        imageUrl = mosaicAsset("20260326_224_L2_baby_toddler_diapers.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "outfits",
                        title = outfits,
                        imageUrl = mosaicAsset("20260326_211_L3_baby_toddler_clothing_outfits.png"),
                    ),
                ),
            ),
            HomeCategoryMosaic(
                id = "food",
                title = food,
                tiles = listOf(
                    HomeCategoryMosaicTile(
                        id = "coffee",
                        title = coffee,
                        imageUrl = mosaicAsset("20260326_252_L2_food_drinks_coffee.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "tea",
                        title = tea,
                        imageUrl = mosaicAsset("20260326_253_L2_food_drinks_tea.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "candy",
                        title = candy,
                        imageUrl = mosaicAsset("20260417_254_L2_food_drinks_candy_chocolate.png"),
                    ),
                    HomeCategoryMosaicTile(
                        id = "snacks",
                        title = snacks,
                        imageUrl = mosaicAsset("20260326_255_L2_food_drinks_snacks.png"),
                    ),
                ),
            ),
        )
    }
}
