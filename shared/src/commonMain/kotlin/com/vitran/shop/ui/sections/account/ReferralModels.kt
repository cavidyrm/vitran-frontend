package com.vitran.shop.ui.sections.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember

enum class ReferralStatus {
    Successful,
    Pending,
}

enum class CreditStatus {
    Available,
    Used,
    Expired,
}

@Immutable
data class ReferralStats(
    val total: Int,
    val rewarded: Int,
    val pending: Int,
    val remainingSellerDays: Int,
    /** Available gift credit shown on the account hub banner (toman). */
    val availableCreditToman: Int = 0,
)

@Immutable
data class ReferralEntry(
    val id: String,
    val referredUserId: String,
    val phoneMasked: String,
    val status: ReferralStatus,
    val signedUpAt: String,
    val rewardedAt: String? = null,
)

@Immutable
data class ReferralCredit(
    val id: String,
    val planId: String,
    val planTitle: String,
    /** Days of seller credit still remaining. */
    val durationDays: Int,
    /** Original grant length — drives the credit progress bar. */
    val totalDays: Int = durationDays,
    val source: String,
    val status: CreditStatus,
    val createdAt: String,
    val isActive: Boolean = status == CreditStatus.Available,
)

@Immutable
data class ReferralProfile(
    val code: String,
    val inviteUrl: String,
    val stats: ReferralStats,
    val successful: List<ReferralEntry>,
    val pending: List<ReferralEntry>,
    val credits: List<ReferralCredit>,
)

@Composable
fun rememberMockReferralProfile(): ReferralProfile = remember {
    ReferralProfile(
        code = "V2",
        inviteUrl = "https://vitran.ir/signup?ref=V2",
        stats = ReferralStats(
            total = 3,
            rewarded = 2,
            pending = 1,
            remainingSellerDays = 30,
            availableCreditToman = 200_000,
        ),
        successful = listOf(
            ReferralEntry(
                id = "1",
                referredUserId = "5",
                phoneMasked = "0912***6789",
                status = ReferralStatus.Successful,
                signedUpAt = "2026-06-01T10:00:00Z",
                rewardedAt = "2026-06-05T14:00:00Z",
            ),
            ReferralEntry(
                id = "3",
                referredUserId = "7",
                phoneMasked = "0910***1122",
                status = ReferralStatus.Successful,
                signedUpAt = "2026-05-20T10:00:00Z",
                rewardedAt = "2026-05-25T14:00:00Z",
            ),
        ),
        pending = listOf(
            ReferralEntry(
                id = "2",
                referredUserId = "6",
                phoneMasked = "0913***4321",
                status = ReferralStatus.Pending,
                signedUpAt = "2026-06-08T09:00:00Z",
            ),
        ),
        credits = listOf(
            ReferralCredit(
                id = "1",
                planId = "2",
                planTitle = "Starter",
                durationDays = 30,
                totalDays = 40,
                source = "referral_referrer",
                status = CreditStatus.Available,
                createdAt = "2026-06-09T14:00:00Z",
                isActive = true,
            ),
        ),
    )
}

internal fun formatIsoDate(iso: String): String {
    val date = iso.substringBefore('T')
    val parts = date.split('-')
    if (parts.size != 3) return toPersianDigits(date.replace('-', '/'))
    val gy = parts[0].toIntOrNull() ?: return toPersianDigits(date.replace('-', '/'))
    val gm = parts[1].toIntOrNull() ?: return toPersianDigits(date.replace('-', '/'))
    val gd = parts[2].toIntOrNull() ?: return toPersianDigits(date.replace('-', '/'))
    val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
    val month = JalaliMonthNames.getOrElse(jm - 1) { return toPersianDigits(date.replace('-', '/')) }
    return toPersianDigits("$jd $month $jy")
}

private val JalaliMonthNames = listOf(
    "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
    "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند",
)

/** Converts a Gregorian date to Jalali (year, month, day). */
private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
    val gDm = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
    val gy2 = if (gm > 2) gy + 1 else gy
    var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) +
        ((gy2 + 399) / 400) + gd + gDm[gm - 1]
    var jy = -1595 + (33 * (days / 12053))
    days %= 12053
    jy += 4 * (days / 1461)
    days %= 1461
    if (days > 365) {
        jy += (days - 1) / 365
        days = (days - 1) % 365
    }
    val jm: Int
    val jd: Int
    if (days < 186) {
        jm = 1 + days / 31
        jd = 1 + days % 31
    } else {
        jm = 7 + (days - 186) / 30
        jd = 1 + (days - 186) % 30
    }
    return Triple(jy, jm, jd)
}
