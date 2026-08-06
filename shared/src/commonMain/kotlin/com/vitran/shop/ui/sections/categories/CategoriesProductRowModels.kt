package com.vitran.shop.ui.sections.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.categories_product_row_new_in_beauty
import vitranshop.shared.generated.resources.categories_product_row_top_rated_fitness
import vitranshop.shared.generated.resources.categories_product_row_top_rated_home
import vitranshop.shared.generated.resources.categories_product_row_top_rated_menswear
import vitranshop.shared.generated.resources.categories_product_row_top_rated_womenswear

/**
 * One product tile in a Categories “Top rated / New in” carousel
 * (shop.app `/categories` product rows).
 */
@Immutable
data class CategoriesProduct(
    val id: String,
    val storeName: String,
    val title: String,
    val imageUrl: String,
    /** When null, stars and review count are hidden (some “New in” cards). */
    val rating: Float? = null,
    val reviewCountLabel: String? = null,
    val priceLabel: String,
    val compareAtPriceLabel: String? = null,
    val discountLabel: String? = null,
)

/**
 * One header + horizontal product carousel on Categories.
 */
@Immutable
data class CategoriesProductRowSection(
    val id: String,
    val title: String,
    val products: List<CategoriesProduct>,
)

/**
 * Mock product rows matching shop.app Explore order and CDN imagery.
 */
@Composable
fun rememberMockCategoriesProductRows(): List<CategoriesProductRowSection> {
    val homeTitle = stringResource(Res.string.categories_product_row_top_rated_home)
    val menswearTitle = stringResource(Res.string.categories_product_row_top_rated_menswear)
    val beautyTitle = stringResource(Res.string.categories_product_row_new_in_beauty)
    val womenswearTitle = stringResource(Res.string.categories_product_row_top_rated_womenswear)
    val fitnessTitle = stringResource(Res.string.categories_product_row_top_rated_fitness)

    return remember(homeTitle, menswearTitle, beautyTitle, womenswearTitle, fitnessTitle) {
        listOf(
            CategoriesProductRowSection(
                id = "top-rated-home",
                title = homeTitle,
                products = mockTopRatedHome,
            ),
            CategoriesProductRowSection(
                id = "top-rated-menswear",
                title = menswearTitle,
                products = mockTopRatedMenswear,
            ),
            CategoriesProductRowSection(
                id = "new-in-beauty",
                title = beautyTitle,
                products = mockNewInBeauty,
            ),
            CategoriesProductRowSection(
                id = "top-rated-womenswear",
                title = womenswearTitle,
                products = mockTopRatedWomenswear,
            ),
            CategoriesProductRowSection(
                id = "top-rated-fitness",
                title = fitnessTitle,
                products = mockTopRatedFitness,
            ),
        )
    }
}

private fun product(
    id: String,
    storeName: String,
    title: String,
    imageUrl: String,
    priceLabel: String,
    reviewCountLabel: String? = null,
    compareAtPriceLabel: String? = null,
    discountLabel: String? = null,
): CategoriesProduct = CategoriesProduct(
    id = id,
    storeName = storeName,
    title = title,
    imageUrl = imageUrl,
    rating = if (reviewCountLabel != null) 5f else null,
    reviewCountLabel = reviewCountLabel,
    priceLabel = priceLabel,
    compareAtPriceLabel = compareAtPriceLabel,
    discountLabel = discountLabel,
)

private val mockTopRatedHome = listOf(
    product(
        id = "home-0",
        storeName = "Caraway",
        title = "Baking Sheet Duo",
        imageUrl = "https://cdn.shopify.com/s/files/1/0258/6273/3906/files/baking-sheet-duo_cream_hero.jpg?v=1785830420&width=384",
        priceLabel = "۴٬۴۳۰٬۰۰۰ تومان",
        reviewCountLabel = "(۲.۱K)",
    ),
    product(
        id = "home-1",
        storeName = "Our Place",
        title = "Ceramic Nonstick Perfect Pot 6.5 qt.",
        imageUrl = "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/black-perfect-pot.jpg?v=1741708962&width=384",
        priceLabel = "۶٬۲۶۰٬۰۰۰ تومان",
        reviewCountLabel = "(۸.۹K)",
    ),
    product(
        id = "home-2",
        storeName = "Magnolia",
        title = "Magnolia Gathered Candle",
        imageUrl = "https://cdn.shopify.com/s/files/1/0207/8508/files/GTHRDC9OZ2022_ba1e7a20-44e1-4f05-9167-30fe5ec0dda4.jpg?v=1784063282&width=384",
        priceLabel = "۱٬۲۶۰٬۰۰۰ تومان",
        reviewCountLabel = "(۶۱۹)",
    ),
    product(
        id = "home-3",
        storeName = "BruMate",
        title = "Era 40oz | Lilac Dusk",
        imageUrl = "https://cdn.shopify.com/s/files/1/1114/2308/files/era-40-lilac-dusk.png?v=1762278174&width=384",
        priceLabel = "۱٬۶۸۰٬۰۰۰ تومان",
        reviewCountLabel = "(۱.۳K)",
    ),
    product(
        id = "home-4",
        storeName = "GreenPan",
        title = "5-Piece Silicone Utensil Set | Black",
        imageUrl = "https://cdn.shopify.com/s/files/1/0531/1217/6808/products/CC005770-001-2.jpg?v=1766159889&width=384",
        priceLabel = "۱٬۶۸۰٬۰۰۰ تومان",
        reviewCountLabel = "(۳۴۴)",
    ),
    product(
        id = "home-5",
        storeName = "Brooklinen",
        title = "Mulberry Silk Pillowcase",
        imageUrl = "https://cdn.shopify.com/s/files/1/0951/7126/files/BKL_24-07_Silk_Driftwood_Pillowcase_2x_WOgrey.jpg?v=1762276136&width=384",
        priceLabel = "۲٬۹۰۰٬۰۰۰ تومان",
        reviewCountLabel = "(۱.۶K)",
    ),
    product(
        id = "home-6",
        storeName = "Buffy.co",
        title = "Wiggle Pillow",
        imageUrl = "https://cdn.shopify.com/s/files/1/2462/9621/files/Screenshot_2026-02-03_at_8.52.17_AM.png?v=1770130590&width=384",
        priceLabel = "۴٬۵۸۰٬۰۰۰ تومان",
        reviewCountLabel = "(۶۹۶)",
    ),
    product(
        id = "home-7",
        storeName = "The Citizenry",
        title = "Stonewashed Linen Sheet Set",
        imageUrl = "https://cdn.shopify.com/s/files/1/0438/1069/files/CZ_Linen_Bedding_French_Blue_01_Sheet_Set_e872ba82-953b-4ecd-a0e7-93e2dfee5509.jpg?v=1776470016&width=384",
        priceLabel = "۱۴٬۶۶۰٬۰۰۰ تومان",
        reviewCountLabel = "(۸۶۸)",
    ),
    product(
        id = "home-8",
        storeName = "Branch",
        title = "Ergonomic Chair",
        imageUrl = "https://cdn.shopify.com/s/files/1/0124/5662/4187/files/bb1.webp?v=1762198436&width=384",
        priceLabel = "۱۵٬۰۸۰٬۰۰۰ تومان",
        reviewCountLabel = "(۶.۵K)",
    ),
    product(
        id = "home-9",
        storeName = "Thuma",
        title = "Classic Headboard",
        imageUrl = "https://cdn.shopify.com/s/files/1/2448/0687/products/220919_The-Headboard_Walnut_2_PDP.jpg?v=1664384103&width=384",
        priceLabel = "۲۷٬۱۰۰٬۰۰۰ تومان",
        reviewCountLabel = "(۲۲۵)",
    ),
    product(
        id = "home-10",
        storeName = "Our Place",
        title = "Spruce Steamer",
        imageUrl = "https://cdn.shopify.com/s/files/1/0024/4137/9915/files/sprucesteamer.jpg?v=1704912440&width=384",
        priceLabel = "۱٬۴۷۰٬۰۰۰ تومان",
        reviewCountLabel = "(۲.۵K)",
    ),
    product(
        id = "home-11",
        storeName = "Ruggable",
        title = "Poppy Fields Doormat",
        imageUrl = "https://cdn.shopify.com/s/files/1/1033/0751/products/poppy-fields-A-RC-DR006-DM23.jpg?v=1634325899&width=384",
        priceLabel = "۴٬۱۶۰٬۰۰۰ تومان",
        reviewCountLabel = "(۴۷۴)",
    ),
)

private val mockTopRatedMenswear = listOf(
    product(
        id = "men-0",
        storeName = "KICKS CREW",
        title = "Air Jordan 11 'Legend Blue' 2024 CT8012-104",
        imageUrl = "https://cdn.shopify.com/s/files/1/0603/3031/1875/files/main-square_aff3da54-bdf6-4c96-ba6a-3ff5d6a073db.jpg?v=1772238984&width=384",
        priceLabel = "۱۳٬۹۸۰٬۰۰۰ تومان",
        reviewCountLabel = "(۱۶۶)",
    ),
    product(
        id = "men-1",
        storeName = "BYLT Basics",
        title = "Hooded Drop-Cut Long Sleeve",
        imageUrl = "https://cdn.shopify.com/s/files/1/1464/5034/files/250717_Hooded_Dc_LS_White37180_2HiRes.jpg?v=1775583436&width=384",
        priceLabel = "۲٬۷۳۰٬۰۰۰ تومان",
        reviewCountLabel = "(۹۴۷)",
    ),
    product(
        id = "men-2",
        storeName = "True Classic",
        title = "Black Active Crew Neck Tee",
        imageUrl = "https://cdn.shopify.com/s/files/1/0220/4008/4552/files/cloudinary__trueclassictees__image__upload__TCT_4003_Short-Sleeve-Active-Crew_Black_Medium_Lifestyle_2025_NOV_2_isribf_imcj9r__cld.jpg?v=1767817645&width=384",
        priceLabel = "۱٬۸۹۰٬۰۰۰ تومان",
        reviewCountLabel = "(۹۸۸)",
    ),
    product(
        id = "men-3",
        storeName = "Taylor Stitch",
        title = "The Apres Pant in Charcoal Sashiko",
        imageUrl = "https://cdn.shopify.com/s/files/1/0070/1922/products/instock_m_q126_The_Apres_Pant-CharcoalSashiko_portrait_001.jpg?v=1770069330&width=384",
        priceLabel = "۵٬۳۸۰٬۰۰۰ تومان",
        reviewCountLabel = "(۶۷۷)",
    ),
    product(
        id = "men-4",
        storeName = "Dixxon Flannel Co.",
        title = "Elastic Stretch Belt",
        imageUrl = "https://cdn.shopify.com/s/files/1/1008/2786/products/elastic-stretch-belt-336307.png?v=1642469482&width=384",
        priceLabel = "۱٬۲۶۰٬۰۰۰ تومان",
        reviewCountLabel = "(۳۰۶)",
    ),
    product(
        id = "men-5",
        storeName = "Jack Archer",
        title = "Jetsetter Tech Pant Slim Fit -- Ice Gray",
        imageUrl = "https://cdn.shopify.com/s/files/1/0531/1506/0388/files/JA_JetsetterPant_IceGray_SSC_1.jpg?v=1770154955&width=384",
        priceLabel = "۳٬۷۴۰٬۰۰۰ تومان",
        reviewCountLabel = "(۴۳۶)",
    ),
    product(
        id = "men-6",
        storeName = "Dr. Squatch",
        title = "Bar Soap 6-Pack",
        imageUrl = "https://cdn.shopify.com/s/files/1/0275/7784/3817/files/6pk-Bundle-XrDf.png?v=1776989445&width=384",
        priceLabel = "۱٬۷۶۰٬۰۰۰ تومان",
        reviewCountLabel = "(۲.۱K)",
    ),
    product(
        id = "men-7",
        storeName = "KUIU",
        title = "Attack Pant | Verde",
        imageUrl = "https://cdn.shopify.com/s/files/1/0558/1914/1278/products/40001-V2_FrontTQ_AttackPant_2021.png?v=1729293672&width=384",
        priceLabel = "۶٬۲۶۰٬۰۰۰ تومان",
        reviewCountLabel = "(۱.۳K)",
    ),
    product(
        id = "men-8",
        storeName = "Dollar Shave Club",
        title = "Men's Shave Butter",
        imageUrl = "https://cdn.shopify.com/s/files/1/0568/3943/8384/files/sbs.png?v=1782164294&width=384",
        priceLabel = "۳۴۰٬۰۰۰ تومان",
        reviewCountLabel = "(۲۴۹)",
    ),
    product(
        id = "men-9",
        storeName = "BRUNT Workwear",
        title = "The Shevlin Full-Zip Hoodie",
        imageUrl = "https://cdn.shopify.com/s/files/1/0332/2911/1429/files/ShevlinFullZipHtrGreenFront-3000x3000-2567b33_17915abc-853f-4eca-8bc3-3e5892c314d5.jpg?v=1762199285&width=384",
        priceLabel = "۲٬۹۴۰٬۰۰۰ تومان",
        reviewCountLabel = "(۳۱۷)",
    ),
    product(
        id = "men-10",
        storeName = "Gymshark US",
        title = "Gymshark Geo Seamless T-Shirt - Black/Charcoal Grey",
        imageUrl = "https://cdn.shopify.com/s/files/1/0156/6146/files/GeoSeamlessT-ShirtBlackCharcoalGreyA5A2D-BBF9-1838_A-Edit_42ac3599-544f-4ffe-ba22-06a37987225b.jpg?v=1754992733&width=384",
        priceLabel = "۱٬۵۱۰٬۰۰۰ تومان",
        reviewCountLabel = "(۵۸۴)",
    ),
    product(
        id = "men-11",
        storeName = "Dixxon Flannel Co.",
        title = "Elm St 2.0 Flannel - 13 Years of Flannels",
        imageUrl = "https://cdn.shopify.com/s/files/1/1008/2786/files/elm-st-20-flannel-13-years-of-flannels-9311085.jpg?v=1771114393&width=384",
        priceLabel = "۲٬۵۲۰٬۰۰۰ تومان",
        reviewCountLabel = "(۶۰۵)",
    ),
)

private val mockNewInBeauty = listOf(
    product(
        id = "beauty-0",
        storeName = "Glow Recipe",
        title = "Pomegranate Peptide Firming Serum",
        imageUrl = "https://cdn.shopify.com/s/files/1/0543/8301/files/NEW5_14_24_PDP_CLAIM_REFRESH_POMEGRANATE_SERUM-01.jpg?v=1762198204&width=384",
        priceLabel = "۱٬۶۸۰٬۰۰۰ تومان",
        reviewCountLabel = "(۲۸۵)",
    ),
    product(
        id = "beauty-1",
        storeName = "Patrick Ta Beauty",
        title = "She Left Me On Red Strawberry Blush Duo",
        imageUrl = "https://cdn.shopify.com/s/files/1/0099/0602/8608/files/PDP_-_Strawberry_Blush.png?v=1784225187&width=384",
        priceLabel = "۱٬۹۷۰٬۰۰۰ تومان",
        reviewCountLabel = "(۲۹)",
    ),
    product(
        id = "beauty-2",
        storeName = "goop",
        title = "Wave Texturizing Mist",
        imageUrl = "https://cdn.shopify.com/s/files/1/0890/8305/2400/files/roz_texturizing-spray_9_85b6b389-2b34-41cb-a41d-13e43d1b2cfb.jpg?v=1785255597&width=384",
        priceLabel = "۱٬۴۷۰٬۰۰۰ تومان",
    ),
    product(
        id = "beauty-3",
        storeName = "Topicals",
        title = "Faded Dark Spot Peel Pads",
        imageUrl = "https://cdn.shopify.com/s/files/1/0503/2932/1627/files/260709_TOPICALS_Web_FadedPeelPads_Ppage_1.jpg?v=1784304496&width=384",
        priceLabel = "۸۴۰٬۰۰۰ تومان",
    ),
    product(
        id = "beauty-4",
        storeName = "Athena Club",
        title = "Hair & Body Mist Trio",
        imageUrl = "https://cdn.shopify.com/s/files/1/0763/3774/2108/files/BS_GV_SS_FullSizeMistTrio_Center_aligned.webp?v=1784322400&width=384",
        priceLabel = "۱٬۶۸۰٬۰۰۰ تومان",
    ),
    product(
        id = "beauty-5",
        storeName = "Pleasing",
        title = "Big Lip HA Moisture Balm SPF 30+",
        imageUrl = "https://cdn.shopify.com/s/files/1/0577/7149/1504/files/240523_PLEASING_S1_BIG_LIP_SPF_98570_FF.png?v=1721200981&width=384",
        priceLabel = "۱٬۲۲۰٬۰۰۰ تومان",
    ),
    product(
        id = "beauty-6",
        storeName = "rem beauty",
        title = "blur butter + hypernova satin matte blush set",
        imageUrl = "https://cdn.shopify.com/s/files/1/0581/3849/3094/files/01_blurbalm_hypernova.gif?v=1784306979&width=384",
        priceLabel = "۱٬۶۳۰٬۰۰۰ تومان",
        compareAtPriceLabel = "۱٬۸۱۰٬۰۰۰ تومان",
        discountLabel = "۱۰٪ تخفیف",
    ),
    product(
        id = "beauty-7",
        storeName = "amika",
        title = "the protector | color protect + strengthen treatment",
        imageUrl = "https://cdn.shopify.com/s/files/1/2117/1151/files/The-Protector_PDP_Treatment_200ml_Shadow-2_2000x2000_4d4bcc6b-2f69-4491-9d9f-17993be9fa82.png?v=1785503208&width=384",
        priceLabel = "۱٬۴۳۰٬۰۰۰ تومان",
    ),
    product(
        id = "beauty-8",
        storeName = "LANEIGE",
        title = "Cream Skin Milky Hydration Sheet Mask",
        imageUrl = "https://cdn.shopify.com/s/files/1/0255/0189/2660/files/creamskinmask_1x1_f5eea8af-e201-458e-929e-cdb9db2e98c5.jpg?v=1784562645&width=384",
        priceLabel = "۸۰۰٬۰۰۰ تومان",
    ),
    product(
        id = "beauty-9",
        storeName = "ILIA Beauty",
        title = "The Thru Line + Limitless Eye Duo",
        imageUrl = "https://cdn.shopify.com/s/files/1/0560/0673/8000/files/ILIA_2026_TLE_LLM_White.jpg?v=1784757635&width=384",
        priceLabel = "۲٬۳۱۰٬۰۰۰ تومان",
    ),
    product(
        id = "beauty-10",
        storeName = "OUAI",
        title = "Detox Duo - Fine Hair",
        imageUrl = "https://cdn.shopify.com/s/files/1/1043/7322/files/Updated_Ghost_Bundle_Thumbnail_PDP_Detox_Fine.jpg?v=1784913483&width=384",
        priceLabel = "۲٬۴۸۰٬۰۰۰ تومان",
    ),
    product(
        id = "beauty-11",
        storeName = "Dossier Perfumes",
        title = "IT Factor 50ml + 11ml",
        imageUrl = "https://cdn.shopify.com/s/files/1/0047/4067/7699/files/It-Factor-50ml_11ml_8d2a9afd-86c5-416a-9ab0-dac0f17b7800.jpg?v=1783503950&width=384",
        priceLabel = "۲٬۴۸۰٬۰۰۰ تومان",
    ),
)

private val mockTopRatedWomenswear = listOf(
    product(
        id = "women-0",
        storeName = "Tuckernuck",
        title = "Heritage Madras Jagger Dress",
        imageUrl = "https://cdn.shopify.com/s/files/1/0630/4999/0366/files/1700125_01.jpg?v=1779804348&width=384",
        priceLabel = "۹٬۵۸۰٬۰۰۰ تومان",
        reviewCountLabel = "(۷۰)",
    ),
    product(
        id = "women-1",
        storeName = "Dolce Vita",
        title = "JULIO SANDALS TORTOISE VINYL",
        imageUrl = "https://cdn.shopify.com/s/files/1/0037/3807/5202/files/DOLCEVITA-SANDAL_JULIO_TORTOISE_VINYL-02.jpg?v=1758816127&width=384",
        priceLabel = "۳٬۱۵۰٬۰۰۰ تومان",
        reviewCountLabel = "(۳۰۸)",
    ),
    product(
        id = "women-2",
        storeName = "AG Jeans",
        title = "Ex-Boyfriend Short",
        imageUrl = "https://cdn.shopify.com/s/files/1/0664/9036/8232/files/SGM1E38TDSMDW_9.jpg?v=1784754651&width=384",
        priceLabel = "۸٬۱۹۰٬۰۰۰ تومان",
        reviewCountLabel = "(۵۹)",
    ),
    product(
        id = "women-3",
        storeName = "Kotn",
        title = "Women's Off Shoulder Tee in White",
        imageUrl = "https://cdn.shopify.com/s/files/1/0932/1356/files/20260128_ECOMM_SS26_20_WOMENSOFFSHOULDERTEE_WHITE_13216.jpg?v=1771343002&width=384",
        priceLabel = "۱٬۵۱۰٬۰۰۰ تومان",
        reviewCountLabel = "(۸۲)",
    ),
    product(
        id = "women-4",
        storeName = "Negative",
        title = "Whipped Track Pant in Black",
        imageUrl = "https://cdn.shopify.com/s/files/1/0221/4866/products/Lounge_WhippedPant_Black_Ksenia_01.jpg?v=1703230203&width=384",
        priceLabel = "۹٬۰۳۰٬۰۰۰ تومان",
        reviewCountLabel = "(۴۵۵)",
    ),
    product(
        id = "women-5",
        storeName = "Reebok",
        title = "Club C 85 Vintage Shoes",
        imageUrl = "https://cdn.shopify.com/s/files/1/0862/7834/0912/files/100000317_SLC_eCom_s.jpg?v=1781189947&width=384",
        priceLabel = "۳٬۵۷۰٬۰۰۰ تومان",
        reviewCountLabel = "(۱۵۶)",
    ),
    product(
        id = "women-6",
        storeName = "EVEREVE",
        title = "Billie Bomber",
        imageUrl = "https://cdn.shopify.com/s/files/1/0638/1561/4629/files/042326_JuneEcomm_EVSU26WJ52-BN_0881_1.jpg?v=1779818001&width=384",
        priceLabel = "۵٬۴۶۰٬۰۰۰ تومان",
        reviewCountLabel = "(۲)",
    ),
    product(
        id = "women-7",
        storeName = "AYR",
        title = "The High Hopes",
        imageUrl = "https://cdn.shopify.com/s/files/1/1212/1112/files/highhopes-black_CAT_f339ba48-b27d-4f0b-8b41-bae0e8a5b6ec.jpg?v=1774455080&width=384",
        priceLabel = "۶٬۹۳۰٬۰۰۰ تومان",
        reviewCountLabel = "(۷۹۳)",
    ),
    product(
        id = "women-8",
        storeName = "FARM Rio",
        title = "Off-White Stitched Garden Midi Dress",
        imageUrl = "https://cdn.shopify.com/s/files/1/0077/6673/6963/files/358185_01.jpg?v=1771609837&width=384",
        priceLabel = "۸٬۷۴۰٬۰۰۰ تومان",
        reviewCountLabel = "(۱۴)",
    ),
    product(
        id = "women-9",
        storeName = "Steve Madden",
        title = "BIGMONA NATURAL RAFFIA",
        imageUrl = "https://cdn.shopify.com/s/files/1/2170/8465/files/STEVEMADDEN_SHOES_BIGMONA_NATURAL-RAFFIA_01_UPDATED.jpg?v=1747749805&width=384",
        priceLabel = "۳٬۳۶۰٬۰۰۰ تومان",
        reviewCountLabel = "(۷۹۱)",
    ),
    product(
        id = "women-10",
        storeName = "Veronica Beard",
        title = "Lois Linen-Blend Vest",
        imageUrl = "https://cdn.shopify.com/s/files/1/0089/4432/0612/files/2602PL8211166_BLACK_MULTI_01.jpg?v=1766763352&width=384",
        priceLabel = "۷٬۸۵۰٬۰۰۰ تومان",
        compareAtPriceLabel = "۱۹٬۶۶۰٬۰۰۰ تومان",
        discountLabel = "۶۰٪ تخفیف",
        reviewCountLabel = "(۱۶)",
    ),
    product(
        id = "women-11",
        storeName = "gorjana",
        title = "Wilder Mini Alphabet Bracelet",
        imageUrl = "https://cdn.shopify.com/s/files/1/0015/3849/0427/files/APR24_PRO_244-202A-G_01_2602b01e-fe97-4ae8-b531-bb5f58093702.jpg?v=1712183794&width=384",
        priceLabel = "۲٬۴۴۰٬۰۰۰ تومان",
        reviewCountLabel = "(۳۸۴)",
    ),
)

private val mockTopRatedFitness = listOf(
    product(
        id = "fitness-0",
        storeName = "BUILT",
        title = "Strawberries 'N Cream Puff",
        imageUrl = "https://cdn.shopify.com/s/files/1/0119/4829/4202/files/StrawberriesNCream_Puff_DTC_LP_02_hover_250203.jpg?v=1738603845&width=384",
        priceLabel = "۱٬۲۶۰٬۰۰۰ تومان",
        reviewCountLabel = "(۴.۴K)",
    ),
    product(
        id = "fitness-1",
        storeName = "Gymshark US",
        title = "Gymshark Crest 7\" Shorts - Light Grey Marl",
        imageUrl = "https://cdn.shopify.com/s/files/1/0156/6146/files/Crest7ShortsLightGreyMarlA2A1S-GBFG-1832_A-Edit_3c4fa77c-ad61-4efb-b677-d48f72ba8675.jpg?v=1744725856&width=384",
        priceLabel = "۳۳۰٬۰۰۰ تومان",
        reviewCountLabel = "(۴۴۴)",
    ),
    product(
        id = "fitness-2",
        storeName = "MoonBrew",
        title = "The Magnesium Sleep Aid",
        imageUrl = "https://cdn.shopify.com/s/files/1/0572/3311/3246/files/OTP_Hot_Cocoa.png?v=1753288496&width=384",
        priceLabel = "۲٬۰۲۰٬۰۰۰ تومان",
        reviewCountLabel = "(۵۰۸)",
    ),
    product(
        id = "fitness-3",
        storeName = "The Feed",
        title = "LMNT Samples",
        imageUrl = "https://cdn.shopify.com/s/files/1/1515/2714/files/lmnt_samples_3pk.png?v=1771262036&width=384",
        priceLabel = "۲۲۰٬۰۰۰ تومان",
        reviewCountLabel = "(۱.۱K)",
    ),
    product(
        id = "fitness-4",
        storeName = "POPFLEX®",
        title = "CloudCushion Vegan Suede Yoga Mat - Cool Cosmos 0.5\" Thick",
        imageUrl = "https://cdn.shopify.com/s/files/1/1089/2102/files/BF-Popflex-Galaxy-Mat-Extra-Thick-StackedView-Edit_2999c758-a9e6-40a0-b4e7-9f76c865cf8a.jpg?v=1777450540&width=384",
        priceLabel = "۲٬۹۴۰٬۰۰۰ تومان",
        reviewCountLabel = "(۳۶۵)",
    ),
    product(
        id = "fitness-5",
        storeName = "LSKD",
        title = "Daily 7\" Short - Black-White",
        imageUrl = "https://cdn.shopify.com/s/files/1/0993/2004/files/L-Model-Daily-7-Short-Black-White-3.jpg?v=1759820802&width=384",
        priceLabel = "۱٬۳۵۰٬۰۰۰ تومان",
        reviewCountLabel = "(۱K)",
    ),
    product(
        id = "fitness-6",
        storeName = "Adanola",
        title = "3 Pack Socks - White, Black, Grey",
        imageUrl = "https://cdn.shopify.com/s/files/1/2156/4663/files/Socks_Mix-BGW.png?v=1752583922&width=384",
        priceLabel = "۱٬۹۳۰٬۰۰۰ تومان",
        reviewCountLabel = "(۴۲۲)",
    ),
    product(
        id = "fitness-7",
        storeName = "AG1 (US)",
        title = "AG1: Flavor Sampler (3ct)",
        imageUrl = "https://cdn.shopify.com/s/files/1/1523/4600/files/image_35_2.png?v=1759767917&width=384",
        priceLabel = "۶۳۰٬۰۰۰ تومان",
        reviewCountLabel = "(۵۳۶)",
    ),
    product(
        id = "fitness-8",
        storeName = "Vitality",
        title = "Cloud II™ Pant - Midnight",
        imageUrl = "https://cdn.shopify.com/s/files/1/0005/7750/3289/files/DSC01244.jpg?v=1761153659&width=384",
        priceLabel = "۴٬۶۲۰٬۰۰۰ تومان",
        reviewCountLabel = "(۱.۶K)",
    ),
    product(
        id = "fitness-9",
        storeName = "O POSITIV",
        title = "Sex Collection",
        imageUrl = "https://cdn.shopify.com/s/files/1/0588/9340/2261/files/Sex-Collection-Bundle.png?v=1762197984&width=384",
        priceLabel = "۳٬۲۸۰٬۰۰۰ تومان",
        reviewCountLabel = "(۳۱۲)",
    ),
    product(
        id = "fitness-10",
        storeName = "Beyond Yoga",
        title = "Spacedye™ Caught In The Midi High Waisted Legging",
        imageUrl = "https://cdn.shopify.com/s/files/1/0265/6141/3219/files/SD3243_darkest-night_13629.jpg?v=1752018034&width=384",
        priceLabel = "۴٬۱۶۰٬۰۰۰ تومان",
        reviewCountLabel = "(۹۸۵)",
    ),
    product(
        id = "fitness-11",
        storeName = "Gymshark US",
        title = "Gymshark Crew Socks 5pk - White",
        imageUrl = "https://cdn.shopify.com/s/files/1/0156/6146/products/CREWSOCKSWHITE5PKI3A1Y-WBBM.A.jpg?v=1678803010&width=384",
        priceLabel = "۳۷۰٬۰۰۰ تومان",
        reviewCountLabel = "(۶۵۷)",
    ),
)

/** Flat list of all Categories product-row mocks — used by product-detail catalog. */
fun allMockCategoriesProducts(): List<CategoriesProduct> =
    mockTopRatedHome +
        mockTopRatedMenswear +
        mockNewInBeauty +
        mockTopRatedWomenswear +
        mockTopRatedFitness
