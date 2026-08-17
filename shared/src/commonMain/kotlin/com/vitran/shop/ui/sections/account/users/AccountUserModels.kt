package com.vitran.shop.ui.sections.account.users

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.vitran.shop.ui.sections.account.digitsOnly
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_users_role_customer
import vitranshop.shared.generated.resources.account_users_role_manager
import vitranshop.shared.generated.resources.account_users_role_seller
import vitranshop.shared.generated.resources.account_users_role_support
import vitranshop.shared.generated.resources.account_users_status_active
import vitranshop.shared.generated.resources.account_users_status_inactive

enum class AccountUserRole {
    Customer,
    Seller,
    Manager,
    Support,
}

enum class AccountUserStatus {
    Active,
    Inactive,
}

enum class AccountUsersSort {
    JoinedDesc,
    JoinedAsc,
}

@Immutable
data class AccountUser(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val roles: List<AccountUserRole>,
    val status: AccountUserStatus,
    val joinedJalali: String,
    val email: String = "",
    val phoneVerified: Boolean = true,
    val notificationsEnabled: Boolean = false,
    val lastLoginLabel: String = "",
    val orderCount: Int = 0,
    val inviteCount: Int = 0,
    val creditDays: Int = 0,
    val internalNote: String = "",
    val events: List<AccountUserEvent> = emptyList(),
) {
    val fullName: String get() = "$firstName $lastName"
    val initial: String get() = firstName.firstOrNull()?.toString().orEmpty()
}

enum class AccountUserEventKind {
    Verified,
    RoleUpdated,
    Created,
}

@Immutable
data class AccountUserEvent(
    val kind: AccountUserEventKind,
    val timestamp: String,
)

internal const val AccountUserNoteMaxLength = 500

@Immutable
data class AccountUsersFilterState(
    val search: String = "",
    val role: AccountUserRole? = null,
    val status: AccountUserStatus? = null,
    val phone: String = "",
) {
    val hasActiveFilters: Boolean
        get() = search.isNotBlank() || role != null || status != null || phone.isNotBlank()
}

internal val AccountUsersPageSizeOptions = listOf(10, 20, 50)

@Composable
internal fun AccountUserRole.label(): String = stringResource(
    when (this) {
        AccountUserRole.Customer -> Res.string.account_users_role_customer
        AccountUserRole.Seller -> Res.string.account_users_role_seller
        AccountUserRole.Manager -> Res.string.account_users_role_manager
        AccountUserRole.Support -> Res.string.account_users_role_support
    },
)

@Composable
internal fun AccountUserStatus.label(): String = stringResource(
    when (this) {
        AccountUserStatus.Active -> Res.string.account_users_status_active
        AccountUserStatus.Inactive -> Res.string.account_users_status_inactive
    },
)

internal fun filterAccountUsers(
    users: List<AccountUser>,
    filters: AccountUsersFilterState,
    sort: AccountUsersSort,
): List<AccountUser> {
    val searchDigits = digitsOnly(filters.search)
    val phoneDigits = digitsOnly(filters.phone)
    val filtered = users.filter { user ->
        val matchesSearch = searchDigits.isEmpty() || user.phone.contains(searchDigits)
        val matchesPhone = phoneDigits.isEmpty() || user.phone.contains(phoneDigits)
        val matchesRole = filters.role == null || user.roles.contains(filters.role)
        val matchesStatus = filters.status == null || user.status == filters.status
        matchesSearch && matchesPhone && matchesRole && matchesStatus
    }
    return when (sort) {
        AccountUsersSort.JoinedDesc -> filtered.sortedByDescending { it.joinedJalali }
        AccountUsersSort.JoinedAsc -> filtered.sortedBy { it.joinedJalali }
    }
}

@Composable
internal fun rememberMockAccountUsers(): List<AccountUser> = remember { mockAccountUsers() }

internal fun findMockAccountUser(userId: String): AccountUser? {
    val id = userId.toIntOrNull() ?: return null
    return mockAccountUsers().firstOrNull { it.id == id }
}

private fun mockAccountUsers(): List<AccountUser> {
    val customer = listOf(AccountUserRole.Customer)
    val seller = listOf(AccountUserRole.Customer, AccountUserRole.Seller)
    val manager = listOf(AccountUserRole.Manager)
    val support = listOf(AccountUserRole.Support)
    val sellerSupport = listOf(AccountUserRole.Seller, AccountUserRole.Support)
    val admin = listOf(AccountUserRole.Manager, AccountUserRole.Customer)
    val active = AccountUserStatus.Active
    val inactive = AccountUserStatus.Inactive
    return listOf(
        AccountUser(1, "سارا", "احمدی", "09121234501", customer, active, "1403/01/12"),
        AccountUser(2, "جاوید", "محمدی", "09123456789", seller, active, "1403/03/25"),
        AccountUser(3, "رضا", "کریمی", "09131234567", customer, active, "1403/02/08"),
        AccountUser(4, "نرگس", "رضایی", "09351234501", support, active, "1402/11/19"),
        AccountUser(5, "امیر", "حسینی", "09125551234", manager, active, "1402/08/03"),
        AccountUser(6, "فاطمه", "موسوی", "09011234567", customer, inactive, "1403/04/14"),
        AccountUser(7, "مهدی", "نوری", "09139876543", seller, active, "1403/05/02"),
        AccountUser(8, "زهرا", "کاظمی", "09901234501", customer, active, "1402/12/21"),
        AccountUser(9, "حسین", "اکبری", "09127654321", admin, active, "1401/09/17"),
        AccountUser(10, "مریم", "صادقی", "09359871234", customer, inactive, "1403/01/29"),
        AccountUser(11, "علی", "مرادی", "09124567890", seller, active, "1403/06/11"),
        AccountUser(12, "الناز", "جعفری", "09136789012", support, active, "1402/10/05"),
        AccountUser(13, "محمد", "رضوی", "09121230000", customer, active, "1403/03/01"),
        AccountUser(14, "شیدا", "محمودی", "09019876543", customer, active, "1403/07/18"),
        AccountUser(15, "پارسا", "عباسی", "09135671234", seller, inactive, "1402/07/22"),
        AccountUser(16, "یاسمن", "قاسمی", "09351239876", customer, active, "1403/02/27"),
        AccountUser(17, "کیان", "طاهری", "09127894561", manager, active, "1401/12/09"),
        AccountUser(18, "هانیه", "شریفی", "09907654321", support, active, "1403/04/03"),
        AccountUser(19, "آرمان", "نجفی", "09123450011", customer, active, "1403/08/07"),
        AccountUser(20, "نیلوفر", "حیدری", "09139870022", seller, active, "1402/06/15"),
        AccountUser(21, "بهرام", "یوسفی", "09121234567", customer, inactive, "1403/05/21"),
        AccountUser(22, "گلناز", "فرهادی", "09354561230", customer, active, "1403/01/05"),
        AccountUser(23, "سامان", "رستمی", "09126549870", sellerSupport, active, "1402/04/28"),
        AccountUser(24, "پریسا", "سلطانی", "09017654321", customer, active, "1403/06/30"),
        AccountUser(25, "نوید", "اسدی", "09131112233", manager, inactive, "1402/02/11"),
        AccountUser(26, "حدیث", "باقری", "09128765432", support, active, "1403/03/19"),
        AccountUser(27, "کامران", "زارعی", "09123450912", customer, active, "1403/07/02"),
        AccountUser(28, "آیدا", "نیکنام", "09901112233", seller, active, "1402/09/08"),
        AccountUser(29, "فرهاد", "کمالی", "09134567890", customer, active, "1403/04/25"),
        AccountUser(30, "سمیرا", "افشار", "09357654321", customer, inactive, "1403/02/14"),
        AccountUser(31, "احسان", "داودی", "09121239876", admin, active, "1401/05/20"),
        AccountUser(32, "مهسا", "جلالی", "09136784512", support, active, "1403/08/12"),
        AccountUser(33, "پویا", "رحیمی", "09125559876", seller, active, "1403/05/09"),
        AccountUser(34, "ترانه", "میرزایی", "09018889900", customer, active, "1402/11/01"),
        AccountUser(35, "شایان", "فتحی", "09139871212", customer, active, "1403/06/04"),
        AccountUser(36, "دنیا", "کرمی", "09351112233", seller, inactive, "1402/03/16"),
        AccountUser(37, "مسعود", "تقوی", "09127651234", manager, active, "1402/01/23"),
        AccountUser(38, "لیلا", "بهرامی", "09123457890", customer, active, "1403/07/27"),
        AccountUser(39, "رامین", "اکبری", "09131230099", support, active, "1403/01/18"),
        AccountUser(40, "نازنین", "فردوسی", "09904561230", customer, active, "1403/03/08"),
        AccountUser(41, "کیوان", "شفیعی", "09128760011", seller, active, "1402/08/29"),
        AccountUser(42, "آرزو", "نعمتی", "09121230912", customer, inactive, "1403/08/01"),
    ).map { it.withMockDetail() }
}

private fun AccountUser.withMockDetail(): AccountUser {
    val lastLogins = listOf("۲ ساعت پیش", "دیروز", "۳ روز پیش", "۱ هفته پیش")
    val email = if (id % 5 == 0) "" else "user$id@gmail.com"
    return copy(
        email = email,
        phoneVerified = status == AccountUserStatus.Active || id % 3 != 0,
        notificationsEnabled = id % 2 == 0,
        lastLoginLabel = lastLogins[(id - 1) % lastLogins.size],
        orderCount = (id * 3) % 24,
        inviteCount = id % 7,
        creditDays = if (id % 4 == 0) 0 else 30,
        internalNote = "",
        events = listOf(
            AccountUserEvent(AccountUserEventKind.Verified, "$joinedJalali — ۱۲:۳۰"),
            AccountUserEvent(AccountUserEventKind.RoleUpdated, "$joinedJalali — ۱۰:۱۵"),
            AccountUserEvent(AccountUserEventKind.Created, "$joinedJalali — ۰۹:۰۰"),
        ),
    )
}
