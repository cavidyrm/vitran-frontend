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

/**
 * One product thumbnail peek at the bottom of a merchant spotlight card.
 */
@Immutable
data class HomeShopProductPeek(
    val id: String,
    val imageUrl: String,
)

/**
 * shop.app `large-product-focused-merchant-card` mock model.
 *
 * [useLightText] maps to shop.app `text-text-fixed-light` vs `text-text-fixed-dark`
 * for the name / rating overlay on the cover.
 * [logoUrl] may be blank when the merchant has no usable raster wordmark.
 */
@Immutable
data class HomeShopCard(
    val id: String,
    val name: String,
    val ratingLabel: String,
    val coverUrl: String,
    val logoUrl: String,
    val brandColor: Color,
    val useLightText: Boolean,
    val products: List<HomeShopProductPeek>,
) {
    init {
        require(products.size == 3) { "Merchant spotlight expects exactly 3 product peeks" }
    }
}

/**
 * One Home category section: header title + horizontal merchant carousel.
 */
@Immutable
data class HomeCategoryShopSection(
    val id: String,
    val title: String,
    val shops: List<HomeShopCard>,
)

/**
 * Mock category shop rows matching shop.app logged-out home order and CDN assets.
 */
@Composable
fun rememberMockHomeCategoryShopSections(): List<HomeCategoryShopSection> {
    val baby = stringResource(Res.string.home_category_baby)
    val beauty = stringResource(Res.string.home_category_beauty)
    val fitness = stringResource(Res.string.home_category_fitness)
    val food = stringResource(Res.string.home_category_food)
    return remember(baby, beauty, fitness, food) {
        listOf(
            HomeCategoryShopSection(
                id = "baby",
                title = baby,
                shops = mockBabyShops(),
            ),
            HomeCategoryShopSection(
                id = "beauty",
                title = beauty,
                shops = mockBeautyShops(),
            ),
            HomeCategoryShopSection(
                id = "fitness",
                title = fitness,
                shops = mockFitnessShops(),
            ),
            HomeCategoryShopSection(
                id = "food",
                title = food,
                shops = mockFoodShops(),
            ),
        )
    }
}

private fun rgb(r: Int, g: Int, b: Int): Color = Color(r, g, b)

private fun peeks(vararg pairs: Pair<String, String>): List<HomeShopProductPeek> =
    pairs.map { (id, url) -> HomeShopProductPeek(id = id, imageUrl = url) }

private fun mockBabyShops(): List<HomeShopCard> = listOf(
    HomeShopCard(
        id = "wildbird",
        name = "WildBird",
        ratingLabel = "۴٫۸ (۸٫۶ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/mywildbird.myshopify.com/1741733573/wildbird_carrier_-510.jpeg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/mywildbird.myshopify.com/1761246168/WildBird_Sparrow.png?width=640",
        brandColor = rgb(165, 137, 107),
        useLightText = true,
        products = peeks(
            "7352315183170" to "https://cdn.shopify.com/s/files/1/3040/7690/files/1.acadian-wrap-1.jpg?width=384",
            "7352315478082" to "https://cdn.shopify.com/s/files/1/3040/7690/files/1.desert-lark-wrap-1.jpg?width=384",
            "7563912511554" to "https://cdn.shopify.com/s/files/1/3040/7690/files/01.FlutterWrap.jpg?width=384",
        ),
    ),
    HomeShopCard(
        id = "kytebaby",
        name = "Kyte Baby",
        ratingLabel = "۴٫۹ (۵۳۷٫۴ هزار)",
        coverUrl = "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/preview_images/88d5ab1e44164cc3825026bcbd8c2109.thumbnail.0000000000.jpg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/kyte-baby-co.myshopify.com/1725505717/KB_logo_horizontal_white.png?width=640",
        brandColor = rgb(187, 175, 169),
        useLightText = false,
        products = peeks(
            "14982433407087" to "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/Monster_Truck_LS_Toddler_PJs_01.jpg?width=384",
            "15358858133615" to "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/1701MVSM_01.jpg?width=384",
            "14982216941679" to "https://cdn.shopify.com/s/files/1/0019/7106/0847/files/SakuraIceCream_SSToddlerPJ_01.jpg?width=384",
        ),
    ),
    HomeShopCard(
        id = "tushbaby",
        name = "Tushbaby",
        ratingLabel = "۴٫۵ (۱۲٫۷ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/tushbaby.myshopify.com/1763691496/shopappcover-6.png?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/tushbaby.myshopify.com/1784658067/ChatGPTImageJun302026at01_17_16PM2",
        brandColor = rgb(187, 175, 169),
        useLightText = false,
        products = peeks(
            "1815161176130" to "https://cdn.shopify.com/s/files/1/0060/9982/8802/files/GreyCarrier.webp?width=384",
            "6952237072450" to "https://cdn.shopify.com/s/files/1/0060/9982/8802/files/Snug-Black1.webp?width=384",
            "7554456617026" to "https://cdn.shopify.com/s/files/1/0060/9982/8802/files/Untitled_design.jpg?width=384",
        ),
    ),
    HomeShopCard(
        id = "gathre",
        name = "Gathre",
        ratingLabel = "۴٫۷ (۱٫۶ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/www-lets-playground-shopify-com.myshopify.com/1757699625/thumbnail.png?width=800",
        logoUrl = "https://cdn.shopify.com/s/files/1/0692/5295/files/logo.png?width=640",
        brandColor = rgb(164, 128, 93),
        useLightText = true,
        products = peeks(
            "7801343475781" to "https://cdn.shopify.com/s/files/1/0692/5295/files/GathreBBall_017.jpg?width=384",
            "6898217484357" to "https://cdn.shopify.com/s/files/1/0692/5295/files/gathre_holiday_209.jpg?width=384",
            "8046859944005" to "https://cdn.shopify.com/s/files/1/0692/5295/files/BriarSheetSet_Full_GSHSFULBRI_02.jpg?width=384",
        ),
    ),
    HomeShopCard(
        id = "poshpeanut",
        name = "Posh Peanut",
        ratingLabel = "۴٫۸ (۸۰٫۸ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/poshpeanuts-com.myshopify.com/1775173771/POSHPEANUT2026-MIX-VINTAGE-LIFESTYLE-8.jpeg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/poshpeanuts-com.myshopify.com/1698097102/PPPrimaryWHT.png?width=640",
        brandColor = rgb(202, 198, 204),
        useLightText = false,
        products = peeks(
            "8069244747952" to "https://cdn.shopify.com/s/files/1/0262/5915/files/POSHPEANUT2026-PP-PJ002-FZS-LIFESTYLE-2.jpg?width=384",
            "7829689434288" to "https://cdn.shopify.com/s/files/1/0262/5915/files/POSHPEANUT2026-PP-PJ002-DBP-LIFESTYLE-2.jpg?width=384",
            "8069245567152" to "https://cdn.shopify.com/s/files/1/0262/5915/files/POSHPEANUT2026-PP-PJ002-FZL-LIFESTYLE-1.jpg?width=384",
        ),
    ),
    HomeShopCard(
        id = "kickeepants",
        name = "KicKee Pants",
        ratingLabel = "۴٫۹ (۷٫۵ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/kickeepants.myshopify.com/1736549278/KickeePantsSpring-216.jpeg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/kickeepants.myshopify.com/1687824898/KICKEEWt.png?width=640",
        brandColor = rgb(191, 179, 173),
        useLightText = false,
        products = peeks(
            "8941756285166" to "https://cdn.shopify.com/s/files/1/0635/3423/5886/files/NTE1MzA0MzUzMTI3.jpg?width=384",
            "8941824278766" to "https://cdn.shopify.com/s/files/1/0635/3423/5886/files/NTcxMDAyNDY4NTM.jpg?width=384",
            "9252233249006" to "https://cdn.shopify.com/s/files/1/0635/3423/5886/files/NDM1NTU1Mzg1MzM2.jpg?width=384",
        ),
    ),
)

private fun mockBeautyShops(): List<HomeShopCard> = listOf(
    HomeShopCard(
        id = "rhodeskin",
        name = "rhode",
        ratingLabel = "۴٫۸ (۳۲۰٫۳ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/rhodeskin.myshopify.com/1780941184/BL_Rhode_0426-4_SummerCampaign_BL_Shot_02_0082_DK_F055.png.png?width=800",
        logoUrl = "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/bronze-rhode-logo.png?width=640",
        brandColor = rgb(136, 108, 93),
        useLightText = true,
        products = peeks(
            "9274169753838" to "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/highlight-milk-3-main.png?width=384",
            "9267050348782" to "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/sunbed-main.png?width=384",
            "9274169458926" to "https://cdn.shopify.com/s/files/1/0606/5451/8510/files/highlight-milk-2-main.png?width=384",
        ),
    ),
    HomeShopCard(
        id = "sacheu",
        name = "SACHEU",
        ratingLabel = "۴٫۵ (۵٫۶ هزار)",
        coverUrl = "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/SA_Brow_and_Freckle_DTC_Banner_Desktop_1.jpg?width=800",
        logoUrl = "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/SACHEU-website-logo.png?width=640",
        brandColor = rgb(208, 181, 174),
        useLightText = false,
        products = peeks(
            "6768045326385" to "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/Lip_Liner_P_Inked.png?width=384",
            "7032018665521" to "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/SwatchwProduct-LiquidGlowSTAY-N_AuraGlow.png?width=384",
            "6928256106545" to "https://cdn.shopify.com/s/files/1/0257/7583/3137/files/Swatch_w_Product_-_Lip_Glaze_Elixir_Bunny_Tongue_a890f1b5-4278-46c8-95c5-f762369e732a.png?width=384",
        ),
    ),
    HomeShopCard(
        id = "patrickta",
        name = "Patrick Ta Beauty",
        ratingLabel = "۴٫۶ (۲۲٫۵ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/patrick-ta.myshopify.com/1741213000/GATES_PTB_BRAND_SHANNON_DUO_001_P_PQ-10155C_2000px.jpeg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/patrick-ta.myshopify.com/1730929532/patricktalogo.png?width=640",
        brandColor = rgb(101, 81, 71),
        useLightText = true,
        products = peeks(
            "6782927142993" to "https://cdn.shopify.com/s/files/1/0099/0602/8608/files/Spring2025_BlushDuo_PDP_Out-Of-Office_Compact_Angled_83ca3444-9172-49bc-a654-78b74548f23c.jpg?width=384",
            "15251865960817" to "https://cdn.shopify.com/s/files/1/0099/0602/8608/files/PTB_ECOMM_Blush-Mini_She_sBlushing.jpg?width=384",
            "15251859931505" to "https://cdn.shopify.com/s/files/1/0099/0602/8608/files/PDP_-_Strawberry_Lip_Balm.png?width=384",
        ),
    ),
    HomeShopCard(
        id = "patternbeauty",
        name = "Pattern Beauty",
        ratingLabel = "۴٫۷ (۵۱٫۷ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/patternbeauty.myshopify.com/1762293821/10-31-PATTERN_ECOMM_B2G1_CONTENTBLOCKRESIZE1.jpeg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/patternbeauty.myshopify.com/1762203721/PATTERN_LOGO_COLORWAYS_500x500_BLACK.png?width=640",
        brandColor = rgb(208, 148, 8),
        useLightText = true,
        products = peeks(
            "3655232258148" to "https://cdn.shopify.com/s/files/1/0149/4794/2500/files/hydration_shampoo_2000x2000_72dpicopy_1.jpg?width=384",
            "3661723762788" to "https://cdn.shopify.com/s/files/1/0149/4794/2500/files/RT_LeaveInConditioner9.8oz_Cement_Yellow_copy.jpg?width=384",
            "7187360284772" to "https://cdn.shopify.com/s/files/1/0149/4794/2500/files/Pattern_ps_leavein_jumbo1.jpg?width=384",
        ),
    ),
)

private fun mockFitnessShops(): List<HomeShopCard> = listOf(
    HomeShopCard(
        id = "nuuds",
        name = "nuuds",
        ratingLabel = "۴٫۸ (۶۳٫۱ هزار)",
        coverUrl = "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/preview_images/ea965da7a38b49a18f26a5923e1a482f.thumbnail.0000000000.jpg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/nuuds-com.myshopify.com/1763055255/KP_NUUDS_WORDMARK_RGB_WHITE.png?width=640",
        brandColor = rgb(97, 85, 95),
        useLightText = true,
        products = peeks(
            "8028606791931" to "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-019-BLAC-S_On-Model_Front-Crop_renee.jpg?width=384",
            "8298698998011" to "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/Gen1_W-028-BLAC-S_On-Model_Front-Crop_victoria_6b349c2b-0b94-42d2-9650-2b56f744b5a6.jpg?width=384",
            "9305620316411" to "https://cdn.shopify.com/s/files/1/0654/5565/3115/files/W-1734-LIWT_Side-Crop.png?width=384",
        ),
    ),
    HomeShopCard(
        id = "justaddbuoy",
        name = "Buoy",
        ratingLabel = "۴٫۷ (۱۲٫۳ هزار)",
        coverUrl = "https://cdn.shopify.com/s/files/1/0267/3351/0727/files/preview_images/dab8aba9380243cebb2e07421ec50838.thumbnail.0000000000.jpg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/buoyshop.myshopify.com/1707858183/21BuoyBrandUpdateBuoyLogoCharcoalCMYK.png?width=640",
        brandColor = rgb(211, 207, 213),
        useLightText = false,
        products = peeks(
            "6587209515079" to "https://cdn.shopify.com/s/files/1/0267/3351/0727/files/PDP_Hydration_Hero_Media_1.webp?width=384",
            "8939258151212" to "https://cdn.shopify.com/s/files/1/0267/3351/0727/files/PDP_Digestion_Hero_Media_1.webp?width=384",
            "9810625364268" to "https://cdn.shopify.com/s/files/1/0267/3351/0727/files/PDP_Mist_HeroMedia_1.webp?width=384",
        ),
    ),
    HomeShopCard(
        id = "popflexactive",
        name = "POPFLEX®",
        ratingLabel = "۴٫۷ (۱۲۴٫۹ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/popflex.myshopify.com/1717427316/62.jpeg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/popflex.myshopify.com/1717427546/popflex-circle-logo---white-large2.png?width=640",
        brandColor = rgb(189, 176, 178),
        useLightText = false,
        products = peeks(
            "6825478422611" to "https://cdn.shopify.com/s/files/1/1089/2102/files/6044PIROUETTESKORTBLACK_00107-Edit.jpg?width=384",
            "7064461017171" to "https://cdn.shopify.com/s/files/1/1089/2102/files/PirouetteSkortwithPockets-DigitalLavender_2844-Edited.jpg?width=384",
            "7457295138899" to "https://cdn.shopify.com/s/files/1/1089/2102/files/F1054LONGERPIROUETTESKORTBLACK_00228-Edit.jpg?width=384",
        ),
    ),
    HomeShopCard(
        id = "bareperformancenutrition",
        name = "Bare Performance Nutrition",
        ratingLabel = "۴٫۹ (۳۲٫۳ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/bare-performance-nutrition.myshopify.com/1696529077/hybridsupplements.jpeg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/bare-performance-nutrition.myshopify.com/1712935264/BPNstandard_white-01.png?width=640",
        brandColor = rgb(109, 89, 79),
        useLightText = true,
        products = peeks(
            "4146710662" to "https://cdn.shopify.com/s/files/1/1103/4864/files/BPNCREA-5.jpg?width=384",
            "10273783430" to "https://cdn.shopify.com/s/files/1/1103/4864/files/WHEY_PROTEIN_Vanilla_Render_V01_BPNWPC-VN-9.png?width=384",
            "7695188721836" to "https://cdn.shopify.com/s/files/1/1103/4864/files/GEL-WMCAF-BOX-1_1.jpg?width=384",
        ),
    ),
    HomeShopCard(
        id = "cymbiotika",
        name = "CYMBIOTIKA",
        ratingLabel = "۴٫۷ (۴۴٫۸ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/mitolife.myshopify.com/1779403106/unnamed.jpg.jpeg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/mitolife.myshopify.com/1754338600/Cym_LogoFiles_Wordmark_White_TM.png?width=640",
        brandColor = rgb(209, 198, 184),
        useLightText = false,
        products = peeks(
            "4696220368943" to "https://cdn.shopify.com/s/files/1/1824/8017/files/FullCount_WebsitePDP_BothPackaging__Glut.png?width=384",
            "4798467014703" to "https://cdn.shopify.com/s/files/1/1824/8017/files/FullCount_WebsitePDP_BothPackaging__Mag_2.png?width=384",
            "4459880382511" to "https://cdn.shopify.com/s/files/1/1824/8017/files/FullCount_WebsitePDP_BothPackaging__D3.png?width=384",
        ),
    ),
    HomeShopCard(
        id = "rhoback",
        name = "RHOBACK",
        ratingLabel = "۴٫۸ (۶۱٫۳ هزار)",
        coverUrl = "https://cdn.shopify.com/s/files/1/1366/9275/files/preview_images/570cc7bcc64a4b8cbe5cb527f602e3aa.thumbnail.0000000000.jpg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/rhoback.myshopify.com/1768337492/Logo_Text_Stacked_White.png?width=640",
        brandColor = rgb(87, 67, 73),
        useLightText = true,
        products = peeks(
            "7233496678483" to "https://cdn.shopify.com/s/files/1/1366/9275/files/1776_USA_SS_POLO_1.jpg?width=384",
            "7316718583891" to "https://cdn.shopify.com/s/files/1/1366/9275/files/RED_WHITE___HIBISCUS_ADMIRAL_NAVY_SS_POLO_1.jpg?width=384",
            "6957514621011" to "https://cdn.shopify.com/s/files/1/1366/9275/files/MPOL0007-0367-D0012_MULLIGAN_FAIRWAY_GREEN_WHITE_MPOL_1.jpg?width=384",
        ),
    ),
)

private fun mockFoodShops(): List<HomeShopCard> = listOf(
    HomeShopCard(
        id = "javvycoffee",
        name = "Javvy Coffee",
        ratingLabel = "۴٫۴ (۱۰۲٫۸ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/drink-javy.myshopify.com/1732745503/javycoffee.jpeg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/drink-javy.myshopify.com/1732745595/javvylogo.png?width=640",
        brandColor = rgb(169, 141, 111),
        useLightText = true,
        products = peeks(
            "8199293042849" to "https://cdn.shopify.com/s/files/1/0435/8216/1057/files/javvy-original.png?width=384",
            "8199294976161" to "https://cdn.shopify.com/s/files/1/0435/8216/1057/files/javvy-saltedcaramel.png?width=384",
            "8199293534369" to "https://cdn.shopify.com/s/files/1/0435/8216/1057/files/javvy-frenchvanilla.png?width=384",
        ),
    ),
    HomeShopCard(
        id = "rockysmatcha",
        name = "rocky's matcha",
        ratingLabel = "۴٫۸ (۶٫۸ هزار)",
        coverUrl = "https://cdn.shopify.com/s/files/1/0689/8037/5866/files/preview_images/39d08fc2ddbb4f0ebeeefedc758d58ea.thumbnail.0000000000.jpg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/rockys-matcha.myshopify.com/1715737157/rm-logo-shop-01.png?width=640",
        brandColor = rgb(141, 145, 147),
        useLightText = true,
        products = peeks(
            "8633348555066" to "https://cdn.shopify.com/s/files/1/0689/8037/5866/files/rocky_s_matcha_Ceremonial_Blend_Matcha_20g_1.jpg?width=384",
            "9715651445050" to "https://cdn.shopify.com/s/files/1/0689/8037/5866/files/rocky_s_matcha_Osada_Ceremonial_Blend_Matcha_20g_1.png?width=384",
            "10059555176762" to "https://cdn.shopify.com/s/files/1/0689/8037/5866/files/rockys-matcha-horii-shichimeien-ceremonial-blend-20g-1.jpg?width=384",
        ),
    ),
    HomeShopCard(
        id = "bonescoffeecompany",
        name = "Bones Coffee Company",
        ratingLabel = "۴٫۸ (۶۸٫۵ هزار)",
        coverUrl = "https://cdn.shopify.com/s/files/1/1475/5488/files/preview_images/15fd19cf8d174cdb8a118ae4e435712a.thumbnail.0000000000.jpg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/bones-coffee-company.myshopify.com/1730940233/bonescoffeelogo.png?width=640",
        brandColor = rgb(206, 73, 46),
        useLightText = true,
        products = peeks(
            "7229323378740" to "https://cdn.shopify.com/s/files/1/1475/5488/files/BBCBagFront.jpg?width=384",
            "442784383013" to "https://cdn.shopify.com/s/files/1/1475/5488/files/HIGBagFront.jpg?width=384",
            "1687514153012" to "https://cdn.shopify.com/s/files/1/1475/5488/files/cinnamon_roll_12oz_front_-_GR.jpg?width=384",
        ),
    ),
    HomeShopCard(
        id = "harney",
        name = "Harney & Sons Fine Teas",
        ratingLabel = "۴٫۸ (۸۰٫۹ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/harney.myshopify.com/1767902673/E70A0215.jpeg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/harney.myshopify.com/1732636020/HT_Logo_Horizontal-WhiteGold.png?width=640",
        brandColor = rgb(120, 108, 85),
        useLightText = true,
        products = peeks(
            "224796180486" to "https://cdn.shopify.com/s/files/1/1234/1342/products/file_tmp_WYGyMVZEVaNBS2Ig.jpg?width=384",
            "224840646662" to "https://cdn.shopify.com/s/files/1/1234/1342/products/Cup_Shots_Paris.jpg?width=384",
            "224803946502" to "https://cdn.shopify.com/s/files/1/1234/1342/files/2023_Cup_Shots_Earl_Grey_Supreme.jpg?width=384",
        ),
    ),
    HomeShopCard(
        id = "ballerinafarm",
        name = "Ballerina Farm",
        ratingLabel = "۴٫۹ (۱۳٫۶ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/ballerina-farm.myshopify.com/1747146265/bf.jpeg?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/ballerina-farm.myshopify.com/1747146262/Frame1.png?width=640",
        brandColor = rgb(144, 132, 110),
        useLightText = true,
        products = peeks(
            "7665362796625" to "https://cdn.shopify.com/s/files/1/0121/0721/9025/files/Copy-of-071326_PBFarmerProtein_PDP_Digital_AK-1-2.jpg?width=384",
            "14804272775538" to "https://cdn.shopify.com/s/files/1/0121/0721/9025/files/1.-Raspberry-Lemon-1.jpg?width=384",
            "15048871215474" to "https://cdn.shopify.com/s/files/1/0121/0721/9025/files/052026_FarmerHydrate_Campaign_PDP_Digital_Square_AK-5.jpg?width=384",
        ),
    ),
    HomeShopCard(
        id = "chamberlaincoffeeus",
        name = "Chamberlain Coffee",
        ratingLabel = "۴٫۷ (۲۲٫۹ هزار)",
        coverUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/chamberlaincoffee.myshopify.com/1730992001/chamberlaincoffeebg.png?width=800",
        logoUrl = "https://cdn.shopify.com/shop-assets/shopify_brokers/chamberlaincoffee.myshopify.com/1701130775/CamberlainCoffeeLogoWHITERGB.png?width=640",
        brandColor = rgb(47, 59, 73),
        useLightText = true,
        products = peeks(
            "6945969045685" to "https://cdn.shopify.com/s/files/1/0424/8862/7355/files/OriginalMatcha_PDP_2048px_05_b48b9b3d-4315-4f50-878c-aebd96bd57d7.png?width=384",
            "6318157299893" to "https://cdn.shopify.com/s/files/1/0424/8862/7355/files/Ground_Espresso_US_PDP_2048px_01_74a0df19-f029-4f59-b37c-2cb4d52d1f62.png?width=384",
            "5409247920283" to "https://cdn.shopify.com/s/files/1/0424/8862/7355/files/GroundMediumUS_PDP_2048px_01_19b14e9f-d449-40fa-ad2a-5c821930ddef.png?width=384",
        ),
    ),
)

/** Flat list of all Home merchant product peeks — used by product-detail catalog. */
fun allMockHomeProductPeeks(): List<HomeShopProductPeek> =
    (mockBabyShops() + mockBeautyShops() + mockFitnessShops() + mockFoodShops())
        .flatMap { it.products }
