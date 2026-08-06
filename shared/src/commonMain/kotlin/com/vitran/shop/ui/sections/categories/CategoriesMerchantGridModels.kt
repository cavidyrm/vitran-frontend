package com.vitran.shop.ui.sections.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.home_category_baby
import vitranshop.shared.generated.resources.home_category_beauty
import vitranshop.shared.generated.resources.home_category_food
import vitranshop.shared.generated.resources.home_category_men
import vitranshop.shared.generated.resources.home_category_women

/**
 * One merchant tile in a Categories category-merchant wrap grid
 * (shop.app `/categories` `product-focused-merchant-card`).
 */
@Immutable
data class CategoriesMerchantShop(
    val id: String,
    val name: String,
    val ratingLabel: String,
    val logoUrl: String,
    /** Product images shown in the card media carousel (shop.app uses 4). */
    val imageUrls: List<String>,
)

/**
 * One category header + non-scrolling multi-row merchant grid.
 */
@Immutable
data class CategoriesMerchantGridSection(
    val id: String,
    val title: String,
    val shops: List<CategoriesMerchantShop>,
)

/**
 * Mock category merchant grids matching shop.app Explore order (first five).
 */
@Composable
fun rememberMockCategoriesMerchantGrids(): List<CategoriesMerchantGridSection> {
    val women = stringResource(Res.string.home_category_women)
    val men = stringResource(Res.string.home_category_men)
    val beauty = stringResource(Res.string.home_category_beauty)
    val food = stringResource(Res.string.home_category_food)
    val baby = stringResource(Res.string.home_category_baby)

    return remember(women, men, beauty, food, baby) {
        listOf(
            CategoriesMerchantGridSection(
                id = "merchant-women",
                title = women,
                shops = mockWomenMerchants,
            ),
            CategoriesMerchantGridSection(
                id = "merchant-men",
                title = men,
                shops = mockMenMerchants,
            ),
            CategoriesMerchantGridSection(
                id = "merchant-beauty",
                title = beauty,
                shops = mockBeautyMerchants,
            ),
            CategoriesMerchantGridSection(
                id = "merchant-food",
                title = food,
                shops = mockFoodMerchants,
            ),
            CategoriesMerchantGridSection(
                id = "merchant-baby",
                title = baby,
                shops = mockBabyMerchants,
            ),
        )
    }
}

private fun shop(
    id: String,
    name: String,
    ratingLabel: String,
    logoUrl: String,
    imageUrls: List<String>,
): CategoriesMerchantShop = CategoriesMerchantShop(
    id = id,
    name = name,
    ratingLabel = ratingLabel,
    logoUrl = logoUrl,
    imageUrls = imageUrls,
)

/** Flat list of Categories merchant tiles — used by PDP Discover top brands. */
fun allMockCategoriesMerchantShops(): List<CategoriesMerchantShop> =
    mockWomenMerchants +
        mockMenMerchants +
        mockBeautyMerchants +
        mockFoodMerchants +
        mockBabyMerchants

// CDN imagery reused from existing Categories / Home mocks (4 product shots each).

private val mockWomenMerchants = listOf(
    shop(
        id = "w-comfrt",
        name = "Comfrt",
        ratingLabel = "۴.۸",
        logoUrl = "https://cdn.shopify.com/s/files/1/0569/4029/8284/files/D_1.png?v=1655843709&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0630/4999/0366/files/1700125_01.jpg?v=1779804348&width=384",
            "https://cdn.shopify.com/s/files/1/0932/1356/files/20260128_ECOMM_SS26_20_WOMENSOFFSHOULDERTEE_WHITE_13216.jpg?v=1771343002&width=384",
            "https://cdn.shopify.com/s/files/1/0664/9036/8232/files/SGM1E38TDSMDW_9.jpg?v=1784754651&width=384",
            "https://cdn.shopify.com/s/files/1/0037/3807/5202/files/DOLCEVITA-SANDAL_JULIO_TORTOISE_VINYL-02.jpg?v=1758816127&width=384",
        ),
    ),
    shop(
        id = "w-wild-oak",
        name = "Wild Oak Boutique",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/s/files/1/0630/4999/0366/files/1700125_01.jpg?v=1779804348&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0664/9036/8232/files/SGM1E38TDSMDW_9.jpg?v=1784754651&width=384",
            "https://cdn.shopify.com/s/files/1/0630/4999/0366/files/1700125_01.jpg?v=1779804348&width=384",
            "https://cdn.shopify.com/s/files/1/0037/3807/5202/files/DOLCEVITA-SANDAL_JULIO_TORTOISE_VINYL-02.jpg?v=1758816127&width=384",
            "https://cdn.shopify.com/s/files/1/0932/1356/files/20260128_ECOMM_SS26_20_WOMENSOFFSHOULDERTEE_WHITE_13216.jpg?v=1771343002&width=384",
        ),
    ),
    shop(
        id = "w-aviator",
        name = "Aviator Nation",
        ratingLabel = "۴.۵",
        logoUrl = "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-019-BLAC-S_On-Model_Front-Crop_renee.jpg?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-019-BLAC-S_On-Model_Front-Crop_renee.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-028-BLAC-S_On-Model_Front-Crop_victoria_6b349c2b-0b94-42d2-9650-2b56f744b5a6.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/W-1734-LIWT_Side-Crop.png?width=384",
            "https://cdn.shopify.com/s/files/1/1089/2102/files/6044PIROUETTESKORTBLACK_00107-Edit.jpg?width=384",
        ),
    ),
    shop(
        id = "w-revice",
        name = "Revice",
        ratingLabel = "۴.۴",
        logoUrl = "https://cdn.shopify.com/s/files/1/0037/3807/5202/files/DOLCEVITA-SANDAL_JULIO_TORTOISE_VINYL-02.jpg?v=1758816127&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0037/3807/5202/files/DOLCEVITA-SANDAL_JULIO_TORTOISE_VINYL-02.jpg?v=1758816127&width=384",
            "https://cdn.shopify.com/s/files/1/0664/9036/8232/files/SGM1E38TDSMDW_9.jpg?v=1784754651&width=384",
            "https://cdn.shopify.com/s/files/1/0630/4999/0366/files/1700125_01.jpg?v=1779804348&width=384",
            "https://cdn.shopify.com/s/files/1/0932/1356/files/20260128_ECOMM_SS26_20_WOMENSOFFSHOULDERTEE_WHITE_13216.jpg?v=1771343002&width=384",
        ),
    ),
    shop(
        id = "w-fashion-nova",
        name = "Fashion Nova",
        ratingLabel = "۴.۳",
        logoUrl = "https://cdn.shopify.com/s/files/1/1089/2102/files/6044PIROUETTESKORTBLACK_00107-Edit.jpg?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/1089/2102/files/6044PIROUETTESKORTBLACK_00107-Edit.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/1089/2102/files/PirouetteSkortwithPockets-DigitalLavender_2844-Edited.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/1089/2102/files/F1054LONGERPIROUETTESKORTBLACK_00228-Edit.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-019-BLAC-S_On-Model_Front-Crop_renee.jpg?width=384",
        ),
    ),
    shop(
        id = "w-roller",
        name = "Roller Rabbit",
        ratingLabel = "۴.۹",
        logoUrl = "https://cdn.shopify.com/s/files/1/0630/4999/0366/files/1700125_01.jpg?v=1779804348&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0932/1356/files/20260128_ECOMM_SS26_20_WOMENSOFFSHOULDERTEE_WHITE_13216.jpg?v=1771343002&width=384",
            "https://cdn.shopify.com/s/files/1/0630/4999/0366/files/1700125_01.jpg?v=1779804348&width=384",
            "https://cdn.shopify.com/s/files/1/0664/9036/8232/files/SGM1E38TDSMDW_9.jpg?v=1784754651&width=384",
            "https://cdn.shopify.com/s/files/1/0037/3807/5202/files/DOLCEVITA-SANDAL_JULIO_TORTOISE_VINYL-02.jpg?v=1758816127&width=384",
        ),
    ),
    shop(
        id = "w-moco",
        name = "MOCO Boutique",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/s/files/1/0664/9036/8232/files/SGM1E38TDSMDW_9.jpg?v=1784754651&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0664/9036/8232/files/SGM1E38TDSMDW_9.jpg?v=1784754651&width=384",
            "https://cdn.shopify.com/s/files/1/0037/3807/5202/files/DOLCEVITA-SANDAL_JULIO_TORTOISE_VINYL-02.jpg?v=1758816127&width=384",
            "https://cdn.shopify.com/s/files/1/0932/1356/files/20260128_ECOMM_SS26_20_WOMENSOFFSHOULDERTEE_WHITE_13216.jpg?v=1771343002&width=384",
            "https://cdn.shopify.com/s/files/1/0630/4999/0366/files/1700125_01.jpg?v=1779804348&width=384",
        ),
    ),
    shop(
        id = "w-wool",
        name = "wool&",
        ratingLabel = "۴.۸",
        logoUrl = "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/W-1734-LIWT_Side-Crop.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/W-1734-LIWT_Side-Crop.png?width=384",
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-028-BLAC-S_On-Model_Front-Crop_victoria_6b349c2b-0b94-42d2-9650-2b56f744b5a6.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-019-BLAC-S_On-Model_Front-Crop_renee.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/1089/2102/files/PirouetteSkortwithPockets-DigitalLavender_2844-Edited.jpg?width=384",
        ),
    ),
    shop(
        id = "w-adanola",
        name = "Adanola",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/s/files/1/1089/2102/files/F1054LONGERPIROUETTESKORTBLACK_00228-Edit.jpg?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/1089/2102/files/F1054LONGERPIROUETTESKORTBLACK_00228-Edit.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/1089/2102/files/6044PIROUETTESKORTBLACK_00107-Edit.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-019-BLAC-S_On-Model_Front-Crop_renee.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0932/1356/files/20260128_ECOMM_SS26_20_WOMENSOFFSHOULDERTEE_WHITE_13216.jpg?v=1771343002&width=384",
        ),
    ),
    shop(
        id = "w-negative",
        name = "Negative",
        ratingLabel = "۴.۶",
        logoUrl = "https://cdn.shopify.com/s/files/1/0630/4999/0366/files/1700125_01.jpg?v=1779804348&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0630/4999/0366/files/1700125_01.jpg?v=1779804348&width=384",
            "https://cdn.shopify.com/s/files/1/0664/9036/8232/files/SGM1E38TDSMDW_9.jpg?v=1784754651&width=384",
            "https://cdn.shopify.com/s/files/1/1089/2102/files/6044PIROUETTESKORTBLACK_00107-Edit.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0037/3807/5202/files/DOLCEVITA-SANDAL_JULIO_TORTOISE_VINYL-02.jpg?v=1758816127&width=384",
        ),
    ),
)

private val mockMenMerchants = listOf(
    shop(
        id = "m-bylt",
        name = "BYLT Basics",
        ratingLabel = "۴.۸",
        logoUrl = "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
            "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=384",
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
            "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
        ),
    ),
    shop(
        id = "m-true-classic",
        name = "True Classic",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=384",
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
            "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
            "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384",
        ),
    ),
    shop(
        id = "m-taylor",
        name = "Taylor Stitch",
        ratingLabel = "۴.۶",
        logoUrl = "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
            "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
            "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=384",
            "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384",
        ),
    ),
    shop(
        id = "m-dixxon",
        name = "Dixxon Flannel Co.",
        ratingLabel = "۴.۵",
        logoUrl = "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
            "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
            "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384",
            "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=384",
        ),
    ),
    shop(
        id = "m-jack",
        name = "Jack Archer",
        ratingLabel = "۴.۴",
        logoUrl = "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384",
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
            "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384",
            "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
        ),
    ),
    shop(
        id = "m-kuiu",
        name = "KUIU",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384",
            "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=384",
            "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
        ),
    ),
    shop(
        id = "m-brunt",
        name = "BRUNT Workwear",
        ratingLabel = "۴.۶",
        logoUrl = "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
            "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384",
            "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=384",
            "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384",
        ),
    ),
    shop(
        id = "m-gymshark",
        name = "Gymshark US",
        ratingLabel = "۴.۵",
        logoUrl = "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-019-BLAC-S_On-Model_Front-Crop_renee.jpg?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-019-BLAC-S_On-Model_Front-Crop_renee.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-028-BLAC-S_On-Model_Front-Crop_victoria_6b349c2b-0b94-42d2-9650-2b56f744b5a6.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/1089/2102/files/6044PIROUETTESKORTBLACK_00107-Edit.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/W-1734-LIWT_Side-Crop.png?width=384",
        ),
    ),
    shop(
        id = "m-dr-squatch",
        name = "Dr. Squatch",
        ratingLabel = "۴.۸",
        logoUrl = "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
            "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
            "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
            "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=384",
        ),
    ),
    shop(
        id = "m-dollar",
        name = "Dollar Shave Club",
        ratingLabel = "۴.۳",
        logoUrl = "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384",
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
            "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384",
            "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
        ),
    ),
    shop(
        id = "m-kicks",
        name = "KICKS CREW",
        ratingLabel = "۴.۴",
        logoUrl = "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=384",
            "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
            "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384",
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
        ),
    ),
    shop(
        id = "m-vuori",
        name = "Vuori",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-028-BLAC-S_On-Model_Front-Crop_victoria_6b349c2b-0b94-42d2-9650-2b56f744b5a6.jpg?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-028-BLAC-S_On-Model_Front-Crop_victoria_6b349c2b-0b94-42d2-9650-2b56f744b5a6.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-019-BLAC-S_On-Model_Front-Crop_renee.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/1089/2102/files/6044PIROUETTESKORTBLACK_00107-Edit.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/W-1734-LIWT_Side-Crop.png?width=384",
        ),
    ),
)

private val mockBeautyMerchants = listOf(
    shop(
        id = "b-glow",
        name = "Glow Recipe",
        ratingLabel = "۴.۸",
        logoUrl = "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/bronze-rhode-logo.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/highlight-milk-3-main.png?width=384",
            "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/sunbed-main.png?width=384",
            "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/highlight-milk-2-main.png?width=384",
            "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/Lip_Liner_P_Inked.png?width=384",
        ),
    ),
    shop(
        id = "b-patrick",
        name = "Patrick Ta Beauty",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/patrick-ta.myshopify.com/1730929532/patricktalogo.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0099/0602/8608/files/Spring2025_BlushDuo_PDP_Out-Of-Office_Compact_Angled_83ca3444-9172-49bc-a654-78b74548f23c.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0099/0602/8608/files/PTB_ECOMM_Blush-Mini_She_sBlushing.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0099/0602/8608/files/PDP_-_Strawberry_Lip_Balm.png?width=384",
            "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/SwatchwProduct-LiquidGlowSTAY-N_AuraGlow.png?width=384",
        ),
    ),
    shop(
        id = "b-rhode",
        name = "rhode",
        ratingLabel = "۴.۹",
        logoUrl = "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/bronze-rhode-logo.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/sunbed-main.png?width=384",
            "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/highlight-milk-3-main.png?width=384",
            "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/highlight-milk-2-main.png?width=384",
            "https://cdn.shopify.com/s/files/1/0255/0189/2660/files/creamskinmask_1x1_f5eea8af-e201-458e-929e-cdb9db2e98c5.jpg?v=1784562645&width=384",
        ),
    ),
    shop(
        id = "b-sacheu",
        name = "SACHEU Beauty",
        ratingLabel = "۴.۶",
        logoUrl = "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/SACHEU-website-logo.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/Lip_Liner_P_Inked.png?width=384",
            "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/SwatchwProduct-LiquidGlowSTAY-N_AuraGlow.png?width=384",
            "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/Swatch_w_Product_-_Lip_Glaze_Elixir_Bunny_Tongue_a890f1b5-4278-46c8-95c5-f762369e732a.png?width=384",
            "https://cdn.shopify.com/s/files/1/0581/3849/3094/files/01_blurbalm_hypernova.gif?v=1784306979&width=384",
        ),
    ),
    shop(
        id = "b-pattern",
        name = "PATTERN Beauty",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/patternbeauty.myshopify.com/1762203721/PATTERN_LOGO_COLORWAYS_500x500_BLACK.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0149/4794/2500/files/hydration_shampoo_2000x2000_72dpicopy_1.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0149/4794/2500/files/RT_LeaveInConditioner9.8oz_Cement_Yellow_copy.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0149/4794/2500/files/Pattern_ps_leavein_jumbo1.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/1043/7322/files/Updated_Ghost_Bundle_Thumbnail_PDP_Detox_Fine.jpg?v=1784913483&width=384",
        ),
    ),
    shop(
        id = "b-laneige",
        name = "LANEIGE",
        ratingLabel = "۴.۵",
        logoUrl = "https://cdn.shopify.com/s/files/1/0255/0189/2660/files/creamskinmask_1x1_f5eea8af-e201-458e-929e-cdb9db2e98c5.jpg?v=1784562645&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0255/0189/2660/files/creamskinmask_1x1_f5eea8af-e201-458e-929e-cdb9db2e98c5.jpg?v=1784562645&width=384",
            "https://cdn.shopify.com/s/files/1/0560/0673/8000/files/ILIA_2026_TLE_LLM_White.jpg?v=1784757635&width=384",
            "https://cdn.shopify.com/s/files/1/2117/1151/files/The-Protector_PDP_Treatment_200ml_Shadow-2_2000x2000_4d4bcc6b-2f69-4491-9d9f-17993be9fa82.png?v=1785503208&width=384",
            "https://cdn.shopify.com/s/files/1/0047/4067/7699/files/It-Factor-50ml_11ml_8d2a9afd-86c5-416a-9ab0-dac0f17b7800.jpg?v=1783503950&width=384",
        ),
    ),
    shop(
        id = "b-ilia",
        name = "ILIA Beauty",
        ratingLabel = "۴.۶",
        logoUrl = "https://cdn.shopify.com/s/files/1/0560/0673/8000/files/ILIA_2026_TLE_LLM_White.jpg?v=1784757635&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0560/0673/8000/files/ILIA_2026_TLE_LLM_White.jpg?v=1784757635&width=384",
            "https://cdn.shopify.com/s/files/1/0255/0189/2660/files/creamskinmask_1x1_f5eea8af-e201-458e-929e-cdb9db2e98c5.jpg?v=1784562645&width=384",
            "https://cdn.shopify.com/s/files/1/0581/3849/3094/files/01_blurbalm_hypernova.gif?v=1784306979&width=384",
            "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/highlight-milk-3-main.png?width=384",
        ),
    ),
    shop(
        id = "b-ouai",
        name = "OUAI",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/s/files/1/1043/7322/files/Updated_Ghost_Bundle_Thumbnail_PDP_Detox_Fine.jpg?v=1784913483&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/1043/7322/files/Updated_Ghost_Bundle_Thumbnail_PDP_Detox_Fine.jpg?v=1784913483&width=384",
            "https://cdn.shopify.com/s/files/1/2117/1151/files/The-Protector_PDP_Treatment_200ml_Shadow-2_2000x2000_4d4bcc6b-2f69-4491-9d9f-17993be9fa82.png?v=1785503208&width=384",
            "https://cdn.shopify.com/s/files/1/0149/4794/2500/files/hydration_shampoo_2000x2000_72dpicopy_1.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0149/4794/2500/files/RT_LeaveInConditioner9.8oz_Cement_Yellow_copy.jpg?width=384",
        ),
    ),
    shop(
        id = "b-amika",
        name = "amika",
        ratingLabel = "۴.۵",
        logoUrl = "https://cdn.shopify.com/s/files/1/2117/1151/files/The-Protector_PDP_Treatment_200ml_Shadow-2_2000x2000_4d4bcc6b-2f69-4491-9d9f-17993be9fa82.png?v=1785503208&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/2117/1151/files/The-Protector_PDP_Treatment_200ml_Shadow-2_2000x2000_4d4bcc6b-2f69-4491-9d9f-17993be9fa82.png?v=1785503208&width=384",
            "https://cdn.shopify.com/s/files/1/1043/7322/files/Updated_Ghost_Bundle_Thumbnail_PDP_Detox_Fine.jpg?v=1784913483&width=384",
            "https://cdn.shopify.com/s/files/1/0149/4794/2500/files/Pattern_ps_leavein_jumbo1.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0255/0189/2660/files/creamskinmask_1x1_f5eea8af-e201-458e-929e-cdb9db2e98c5.jpg?v=1784562645&width=384",
        ),
    ),
    shop(
        id = "b-dossier",
        name = "Dossier Perfumes",
        ratingLabel = "۴.۸",
        logoUrl = "https://cdn.shopify.com/s/files/1/0047/4067/7699/files/It-Factor-50ml_11ml_8d2a9afd-86c5-416a-9ab0-dac0f17b7800.jpg?v=1783503950&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0047/4067/7699/files/It-Factor-50ml_11ml_8d2a9afd-86c5-416a-9ab0-dac0f17b7800.jpg?v=1783503950&width=384",
            "https://cdn.shopify.com/s/files/1/0560/0673/8000/files/ILIA_2026_TLE_LLM_White.jpg?v=1784757635&width=384",
            "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/highlight-milk-2-main.png?width=384",
            "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/Lip_Liner_P_Inked.png?width=384",
        ),
    ),
    shop(
        id = "b-rem",
        name = "rem beauty",
        ratingLabel = "۴.۴",
        logoUrl = "https://cdn.shopify.com/s/files/1/0581/3849/3094/files/01_blurbalm_hypernova.gif?v=1784306979&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0581/3849/3094/files/01_blurbalm_hypernova.gif?v=1784306979&width=384",
            "https://cdn.shopify.com/s/files/1/0099/0602/8608/files/PDP_-_Strawberry_Lip_Balm.png?width=384",
            "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/SwatchwProduct-LiquidGlowSTAY-N_AuraGlow.png?width=384",
            "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/sunbed-main.png?width=384",
        ),
    ),
    shop(
        id = "b-topicals",
        name = "Topicals",
        ratingLabel = "۴.۶",
        logoUrl = "https://cdn.shopify.com/s/files/1/0255/0189/2660/files/creamskinmask_1x1_f5eea8af-e201-458e-929e-cdb9db2e98c5.jpg?v=1784562645&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0255/0189/2660/files/creamskinmask_1x1_f5eea8af-e201-458e-929e-cdb9db2e98c5.jpg?v=1784562645&width=384",
            "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/highlight-milk-3-main.png?width=384",
            "https://cdn.shopify.com/s/files/1/0149/4794/2500/files/hydration_shampoo_2000x2000_72dpicopy_1.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/2117/1151/files/The-Protector_PDP_Treatment_200ml_Shadow-2_2000x2000_4d4bcc6b-2f69-4491-9d9f-17993be9fa82.png?v=1785503208&width=384",
        ),
    ),
)

private val mockFoodMerchants = listOf(
    shop(
        id = "f-caraway",
        name = "Caraway",
        ratingLabel = "۴.۸",
        logoUrl = "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
            "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
            "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384",
            "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=384",
        ),
    ),
    shop(
        id = "f-our-place",
        name = "Our Place",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
            "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
            "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=384",
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
        ),
    ),
    shop(
        id = "f-magnolia",
        name = "Magnolia",
        ratingLabel = "۴.۶",
        logoUrl = "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=384",
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
            "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384",
            "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
        ),
    ),
    shop(
        id = "f-brumate",
        name = "BruMate",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
            "https://cdn.shopify.com/s/files/1/1114/2308/files/Era-Flip-40-MochaDot.png?v=1784148747&width=384",
            "https://cdn.shopify.com/s/files/1/1114/2308/files/Era-40-WildflowerWhisper.png?v=1782340164&width=384",
            "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
        ),
    ),
    shop(
        id = "f-greenpan",
        name = "GreenPan",
        ratingLabel = "۴.۵",
        logoUrl = "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384",
            "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
            "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
            "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=384",
        ),
    ),
    shop(
        id = "f-buoy",
        name = "Buoy",
        ratingLabel = "۴.۶",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/buoyshop.myshopify.com/1707858183/21BuoyBrandUpdateBuoyLogoCharcoalCMYK.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0267/3351/0727/files/PDP_Hydration_Hero_Media_1.webp?width=384",
            "https://cdn.shopify.com/s/files/1/0267/3351/0727/files/PDP_Digestion_Hero_Media_1.webp?width=384",
            "https://cdn.shopify.com/s/files/1/0267/3351/0727/files/PDP_Mist_HeroMedia_1.webp?width=384",
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
        ),
    ),
    shop(
        id = "f-bpn",
        name = "Bare Performance Nutrition",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/bare-performance-nutrition.myshopify.com/1712935264/BPNstandard_white-01.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/1103/4864/files/BPNCREA-5.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/1103/4864/files/WHEY_PROTEIN_Vanilla_Render_V01_BPNWPC-VN-9.png?width=384",
            "https://cdn.shopify.com/s/files/1/1103/4864/files/GEL-WMCAF-BOX-1_1.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0267/3351/0727/files/PDP_Hydration_Hero_Media_1.webp?width=384",
        ),
    ),
    shop(
        id = "f-mitolife",
        name = "Mitolife",
        ratingLabel = "۴.۵",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/mitolife.myshopify.com/1754338600/Cym_LogoFiles_Wordmark_White_TM.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/1824/8017/files/FullCount_WebsitePDP_BothPackaging__Glut.png?width=384",
            "https://cdn.shopify.com/s/files/1/1824/8017/files/FullCount_WebsitePDP_BothPackaging__Mag_2.png?width=384",
            "https://cdn.shopify.com/s/files/1/1824/8017/files/FullCount_WebsitePDP_BothPackaging__D3.png?width=384",
            "https://cdn.shopify.com/s/files/1/1103/4864/files/BPNCREA-5.jpg?width=384",
        ),
    ),
    shop(
        id = "f-citizenry",
        name = "The Citizenry",
        ratingLabel = "۴.۶",
        logoUrl = "https://cdn.shopify.com/s/files/1/0438/1069/files/CZ_Linen_Bedding_French_Blue_01_Sheet_Set_e872ba82-953b-4ecd-a0e7-93e2dfee5509.jpg?v=1776470016&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0438/1069/files/CZ_Linen_Bedding_French_Blue_01_Sheet_Set_e872ba82-953b-4ecd-a0e7-93e2dfee5509.jpg?v=1776470016&width=384",
            "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384",
            "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=384",
            "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
        ),
    ),
    shop(
        id = "f-brooklinen",
        name = "Brooklinen",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384",
            "https://cdn.shopify.com/s/files/1/0438/1069/files/CZ_Linen_Bedding_French_Blue_01_Sheet_Set_e872ba82-953b-4ecd-a0e7-93e2dfee5509.jpg?v=1776470016&width=384",
            "https://cdn.shopify.com/s/files/1/2462/9621/files/Screenshot_2026-02-03_at_8.52.17_AM.png?v=1770130590&width=384",
            "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=384",
        ),
    ),
    shop(
        id = "f-buffy",
        name = "Buffy.co",
        ratingLabel = "۴.۵",
        logoUrl = "https://cdn.shopify.com/s/files/1/2462/9621/files/Screenshot_2026-02-03_at_8.52.17_AM.png?v=1770130590&width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/2462/9621/files/Screenshot_2026-02-03_at_8.52.17_AM.png?v=1770130590&width=384",
            "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384",
            "https://cdn.shopify.com/s/files/1/0438/1069/files/CZ_Linen_Bedding_French_Blue_01_Sheet_Set_e872ba82-953b-4ecd-a0e7-93e2dfee5509.jpg?v=1776470016&width=384",
            "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
        ),
    ),
)

private val mockBabyMerchants = listOf(
    shop(
        id = "baby-wildbird",
        name = "WildBird",
        ratingLabel = "۴.۸",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/mywildbird.myshopify.com/1761246168/WildBird_Sparrow.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/3040/7690/files/1.acadian-wrap-1.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/3040/7690/files/1.desert-lark-wrap-1.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/3040/7690/files/01.FlutterWrap.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Monster_Truck_LS_Toddler_PJs_01.jpg?width=384",
        ),
    ),
    shop(
        id = "baby-kyte",
        name = "Kyte Baby",
        ratingLabel = "۴.۹",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/kyte-baby-co.myshopify.com/1725505717/KB_logo_horizontal_white.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Monster_Truck_LS_Toddler_PJs_01.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/1701MVSM_01.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/SakuraIceCream_SSToddlerPJ_01.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/3040/7690/files/1.acadian-wrap-1.jpg?width=384",
        ),
    ),
    shop(
        id = "baby-tushbaby",
        name = "Tushbaby",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/tushbaby.myshopify.com/1784658067/ChatGPTImageJun302026at01_17_16PM2",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0060/9982/8802/files/GreyCarrier.webp?width=384",
            "https://cdn.shopify.com/s/files/1/0060/9982/8802/files/Snug-Black1.webp?width=384",
            "https://cdn.shopify.com/s/files/1/0060/9982/8802/files/Untitled_design.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/1701MVSM_01.jpg?width=384",
        ),
    ),
    shop(
        id = "baby-gathre",
        name = "Gathre",
        ratingLabel = "۴.۶",
        logoUrl = "https://cdn.shopify.com/s/files/1/0692/5295/files/logo.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0692/5295/files/GathreBBall_017.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0692/5295/files/gathre_holiday_209.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0692/5295/files/BriarSheetSet_Full_GSHSFULBRI_02.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0262/5915/files/POSHPEANUT2026-PP-PJ002-FZS-LIFESTYLE-2.jpg?width=384",
        ),
    ),
    shop(
        id = "baby-posh",
        name = "Posh Peanut",
        ratingLabel = "۴.۸",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/poshpeanuts-com.myshopify.com/1698097102/PPPrimaryWHT.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0262/5915/files/POSHPEANUT2026-PP-PJ002-FZS-LIFESTYLE-2.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0262/5915/files/POSHPEANUT2026-PP-PJ002-DBP-LIFESTYLE-2.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0262/5915/files/POSHPEANUT2026-PP-PJ002-FZL-LIFESTYLE-1.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0635/3423/5886/files/NTE1MzA0MzUzMTI3.jpg?width=384",
        ),
    ),
    shop(
        id = "baby-kickee",
        name = "Kickee Pants",
        ratingLabel = "۴.۷",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/kickeepants.myshopify.com/1687824898/KICKEEWt.png?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0635/3423/5886/files/NTE1MzA0MzUzMTI3.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0635/3423/5886/files/NTcxMDAyNDY4NTM.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0635/3423/5886/files/NDM1NTU1Mzg1MzM2.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/SakuraIceCream_SSToddlerPJ_01.jpg?width=384",
        ),
    ),
    shop(
        id = "baby-extra-1",
        name = "Little Sleepies",
        ratingLabel = "۴.۶",
        logoUrl = "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/1701MVSM_01.jpg?width=64",
        imageUrls = listOf(
            "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/1701MVSM_01.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/SakuraIceCream_SSToddlerPJ_01.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Monster_Truck_LS_Toddler_PJs_01.jpg?width=384",
            "https://cdn.shopify.com/s/files/1/0262/5915/files/POSHPEANUT2026-PP-PJ002-FZS-LIFESTYLE-2.jpg?width=384",
        ),
    ),
)
