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

internal fun formatIsoDate(iso: String): String =
    com.vitran.shop.ui.util.formatIsoDateAsJalali(iso)
