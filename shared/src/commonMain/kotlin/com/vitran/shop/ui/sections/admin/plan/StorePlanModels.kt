package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.DrawableResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_chart
import vitranshop.shared.generated.resources.ic_diamond
import vitranshop.shared.generated.resources.ic_package
import vitranshop.shared.generated.resources.ic_people
import vitranshop.shared.generated.resources.ic_send
import vitranshop.shared.generated.resources.ic_star_outline

/** Local tokens for store-plan merchant screens. */
object StorePlanTokens {
    val PageBackground = AdminTokens.PageBackground
    val CardBorder = AdminTokens.CardBorder
    val SoftBorder = AdminTokens.Brand.copy(alpha = 0.28f)
    val RecommendedBadgeBg = AdminTokens.Brand.copy(alpha = 0.12f)
    val DiscountPillBg = androidx.compose.ui.graphics.Color(0xFFDCFCE7)
    val DiscountPillText = androidx.compose.ui.graphics.Color(0xFF15803D)
    val PaidBadgeBg = androidx.compose.ui.graphics.Color(0xFFDCFCE7)
    val PaidBadgeText = androidx.compose.ui.graphics.Color(0xFF15803D)
    val FailedBadgeBg = androidx.compose.ui.graphics.Color(0xFFFEE2E2)
    val FailedBadgeText = androidx.compose.ui.graphics.Color(0xFFB91C1C)
    val ActiveBadgeBg = androidx.compose.ui.graphics.Color(0xFFDCFCE7)
    val ActiveBadgeText = androidx.compose.ui.graphics.Color(0xFF15803D)
    val CtaBannerBg = AdminTokens.Brand.copy(alpha = 0.08f)
    val ProgressTrack = androidx.compose.ui.graphics.Color(0xFFEDE9FE)
    val SectionGap = VitranSpacing.xxxl
    val CardRadius = VitranRadius.large
    val PageHorizontal = VitranSpacing.xl
    val PageMaxWidth = AdminTokens.PageMaxWidth
    val TopBarHeight = 64.dp
    val UsageCardMinHeight = 132.dp
}

enum class StorePlanBillingCycle {
    Monthly,
    Yearly,
}

enum class StorePlanTierId {
    Start,
    Growth,
    Professional,
}

@Immutable
data class StorePlanFeatureBullet(
    val text: String,
)

@Immutable
data class StorePlanTier(
    val id: StorePlanTierId,
    val title: String,
    val tagline: String,
    val monthlyPriceToman: Int,
    val yearlyPriceToman: Int,
    val icon: DrawableResource,
    val recommended: Boolean,
    val bullets: List<StorePlanFeatureBullet>,
)

@Immutable
data class StorePlanComparisonCell(
    val text: String? = null,
    val checked: Boolean = false,
    val empty: Boolean = false,
)

@Immutable
data class StorePlanComparisonRow(
    val featureLabel: String,
    val start: StorePlanComparisonCell,
    val growth: StorePlanComparisonCell,
    val professional: StorePlanComparisonCell,
)

@Immutable
data class StorePlanCatalog(
    val tiers: List<StorePlanTier>,
    val comparisonRows: List<StorePlanComparisonRow>,
    val yearlyDiscountPercent: Int,
)

enum class StorePlanUsageKind {
    Meter,
    Status,
}

@Immutable
data class StorePlanUsageItem(
    val id: String,
    val title: String,
    val valueLabel: String,
    val icon: DrawableResource,
    val kind: StorePlanUsageKind,
    val progress: Float? = null,
    val statusLabel: String? = null,
)

enum class StorePlanPaymentStatus {
    Paid,
    Failed,
}

@Immutable
data class StorePlanPaymentEntry(
    val id: String,
    val dateLabel: String,
    val amountLabel: String,
    val status: StorePlanPaymentStatus,
)

@Immutable
data class StorePlanSubscription(
    val tierId: StorePlanTierId,
    val tierTitle: String,
    val priceLabel: String,
    val renewalLabel: String,
    val benefits: List<String>,
    val usage: List<StorePlanUsageItem>,
    val payments: List<StorePlanPaymentEntry>,
    val storeName: String,
)

fun formatTomanAmount(amount: Int): String {
    val grouped = amount.toString()
        .reversed()
        .chunked(3)
        .joinToString("٬")
        .reversed()
    return toPersianDigits(grouped)
}

fun StorePlanTier.priceFor(cycle: StorePlanBillingCycle): Int =
    when (cycle) {
        StorePlanBillingCycle.Monthly -> monthlyPriceToman
        StorePlanBillingCycle.Yearly -> yearlyPriceToman
    }

@Composable
fun rememberMockStorePlanCatalog(): StorePlanCatalog = remember { mockStorePlanCatalog() }

@Composable
fun rememberMockStorePlanSubscription(): StorePlanSubscription =
    remember { mockStorePlanSubscription() }

fun mockStorePlanCatalog(): StorePlanCatalog =
    StorePlanCatalog(
        yearlyDiscountPercent = 0,
        tiers = listOf(
            StorePlanTier(
                id = StorePlanTierId.Start,
                title = "شروع",
                tagline = "برای شروع یک فروشگاه حرفه‌ای",
                monthlyPriceToman = 390_000,
                yearlyPriceToman = 390_000 * 12,
                icon = Res.drawable.ic_send,
                recommended = false,
                bullets = listOf(
                    StorePlanFeatureBullet("۵۰ محصول"),
                    StorePlanFeatureBullet("جایگاه ویژه ماهانه: ندارد"),
                    StorePlanFeatureBullet("تحلیل پایه"),
                    StorePlanFeatureBullet("اعضای تیم: ندارد"),
                ),
            ),
            StorePlanTier(
                id = StorePlanTierId.Growth,
                title = "رشد",
                tagline = "برای رشد و توسعه کسب‌وکار",
                monthlyPriceToman = 990_000,
                yearlyPriceToman = 990_000 * 12,
                icon = Res.drawable.ic_star_outline,
                recommended = true,
                bullets = listOf(
                    StorePlanFeatureBullet("۵۰۰ محصول"),
                    StorePlanFeatureBullet("۲۰ جایگاه ویژه ماهانه"),
                    StorePlanFeatureBullet("تحلیل پیشرفته"),
                    StorePlanFeatureBullet("۵ عضو تیم"),
                ),
            ),
            StorePlanTier(
                id = StorePlanTierId.Professional,
                title = "حرفه‌ای",
                tagline = "برای کسب‌وکارهای بزرگ",
                monthlyPriceToman = 2_990_000,
                yearlyPriceToman = 2_990_000 * 12,
                icon = Res.drawable.ic_diamond,
                recommended = false,
                bullets = listOf(
                    StorePlanFeatureBullet("۵۰۰ محصول"),
                    StorePlanFeatureBullet("۱۰۰ جایگاه ویژه ماهانه"),
                    StorePlanFeatureBullet("تحلیل حرفه‌ای"),
                    StorePlanFeatureBullet("اعضای نامحدود"),
                    StorePlanFeatureBullet("گزارش‌های سفارشی"),
                ),
            ),
        ),
        comparisonRows = listOf(
            StorePlanComparisonRow(
                featureLabel = "تعداد محصولات",
                start = StorePlanComparisonCell(text = "۵۰"),
                growth = StorePlanComparisonCell(text = "۵۰۰"),
                professional = StorePlanComparisonCell(text = "۵۰۰"),
            ),
            StorePlanComparisonRow(
                featureLabel = "جایگاه ویژه ماهانه",
                start = StorePlanComparisonCell(empty = true),
                growth = StorePlanComparisonCell(text = "۲۰"),
                professional = StorePlanComparisonCell(text = "۱۰۰"),
            ),
            StorePlanComparisonRow(
                featureLabel = "تحلیل‌ها",
                start = StorePlanComparisonCell(text = "پایه"),
                growth = StorePlanComparisonCell(text = "پیشرفته"),
                professional = StorePlanComparisonCell(text = "حرفه‌ای"),
            ),
            StorePlanComparisonRow(
                featureLabel = "اعضای تیم",
                start = StorePlanComparisonCell(empty = true),
                growth = StorePlanComparisonCell(text = "۵ عضو"),
                professional = StorePlanComparisonCell(text = "نامحدود"),
            ),
            StorePlanComparisonRow(
                featureLabel = "گزارش‌های سفارشی",
                start = StorePlanComparisonCell(empty = true),
                growth = StorePlanComparisonCell(empty = true),
                professional = StorePlanComparisonCell(checked = true),
            ),
        ),
    )

fun mockStorePlanSubscription(): StorePlanSubscription =
    StorePlanSubscription(
        tierId = StorePlanTierId.Growth,
        tierTitle = "پلن رشد",
        priceLabel = "${formatTomanAmount(990_000)} / ماه",
        renewalLabel = "تاریخ تمدید بعدی: ۲۰ خرداد ۱۴۰۳",
        storeName = "گالری نور",
        benefits = listOf(
            "نمایش بهتر محصولات در لیست‌ها",
            "گزارش‌های دقیق‌تر فروش و بازدید",
            "پشتیبانی اولویت‌دار",
        ),
        usage = listOf(
            StorePlanUsageItem(
                id = "products",
                title = "محصولات",
                valueLabel = "۳۲۰ از ۵۰۰",
                icon = Res.drawable.ic_package,
                kind = StorePlanUsageKind.Meter,
                progress = 0.64f,
            ),
            StorePlanUsageItem(
                id = "slots",
                title = "جایگاه ویژه",
                valueLabel = "۸ از ۲۰",
                icon = Res.drawable.ic_star_outline,
                kind = StorePlanUsageKind.Meter,
                progress = 0.4f,
            ),
            StorePlanUsageItem(
                id = "team",
                title = "اعضای تیم",
                valueLabel = "۳ از ۵",
                icon = Res.drawable.ic_people,
                kind = StorePlanUsageKind.Meter,
                progress = 0.6f,
            ),
            StorePlanUsageItem(
                id = "analytics",
                title = "تحلیل‌ها",
                valueLabel = "پیشرفته",
                icon = Res.drawable.ic_chart,
                kind = StorePlanUsageKind.Status,
                statusLabel = "فعال",
            ),
        ),
        payments = listOf(
            StorePlanPaymentEntry(
                id = "p1",
                dateLabel = "۲۰ خرداد ۱۴۰۳",
                amountLabel = "${formatTomanAmount(990_000)} تومان",
                status = StorePlanPaymentStatus.Paid,
            ),
            StorePlanPaymentEntry(
                id = "p2",
                dateLabel = "۲۰ اردیبهشت ۱۴۰۳",
                amountLabel = "${formatTomanAmount(990_000)} تومان",
                status = StorePlanPaymentStatus.Paid,
            ),
            StorePlanPaymentEntry(
                id = "p3",
                dateLabel = "۲۰ فروردین ۱۴۰۳",
                amountLabel = "${formatTomanAmount(990_000)} تومان",
                status = StorePlanPaymentStatus.Failed,
            ),
        ),
    )
