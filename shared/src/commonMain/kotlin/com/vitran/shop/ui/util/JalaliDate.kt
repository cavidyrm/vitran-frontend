package com.vitran.shop.ui.util

/** Jalali (Persian) calendar helpers shared by account/referral UI. */

val JalaliMonthNames: List<String> = listOf(
    "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
    "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند",
)

data class JalaliDate(val year: Int, val month: Int, val day: Int)

/** Converts a Gregorian date to Jalali (year, month, day). */
fun gregorianToJalali(gy: Int, gm: Int, gd: Int): JalaliDate {
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
    return JalaliDate(jy, jm, jd)
}

/** Converts a Jalali date to Gregorian (year, month, day). */
fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
    val jy2 = jy + 1595
    var days = -355668 + (365 * jy2) + ((jy2 / 33) * 8) + (((jy2 % 33) + 3) / 4) + jd
    days += if (jm < 7) (jm - 1) * 31 else ((jm - 7) * 30) + 186
    var gy = 400 * (days / 146097)
    days %= 146097
    if (days > 36524) {
        days--
        gy += 100 * (days / 36524)
        days %= 36524
        if (days >= 365) days++
    }
    gy += 4 * (days / 1461)
    days %= 1461
    if (days > 365) {
        gy += (days - 1) / 365
        days = (days - 1) % 365
    }
    var gd = days + 1
    val leap = (gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)
    val monthLengths = intArrayOf(0, 31, if (leap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    var gm = 1
    while (gm <= 12 && gd > monthLengths[gm]) {
        gd -= monthLengths[gm]
        gm++
    }
    return Triple(gy, gm, gd)
}

fun daysInJalaliMonth(year: Int, month: Int): Int = when {
    month <= 6 -> 31
    month <= 11 -> 30
    else -> if (isJalaliLeapYear(year)) 30 else 29
}

/**
 * 33-year Jalali leap cycle used for modern Iranian calendar years.
 * Leap when (year % 33) is in {1, 5, 9, 13, 17, 22, 26, 30}.
 */
fun isJalaliLeapYear(year: Int): Boolean {
    val rem = ((year % 33) + 33) % 33
    return rem == 1 || rem == 5 || rem == 9 || rem == 13 ||
        rem == 17 || rem == 22 || rem == 26 || rem == 30
}

/** Parses `yyyy-MM-dd` (optionally with time) to Jalali, or null if invalid. */
fun parseIsoDateToJalali(iso: String): JalaliDate? {
    val date = iso.substringBefore('T').trim()
    val parts = date.split('-')
    if (parts.size != 3) return null
    val gy = parts[0].toIntOrNull() ?: return null
    val gm = parts[1].toIntOrNull() ?: return null
    val gd = parts[2].toIntOrNull() ?: return null
    if (gm !in 1..12 || gd !in 1..31) return null
    return runCatching { gregorianToJalali(gy, gm, gd) }.getOrNull()
}

/** Formats Jalali as ISO Gregorian `yyyy-MM-dd`. */
fun jalaliToIsoDate(jalali: JalaliDate): String {
    val (gy, gm, gd) = jalaliToGregorian(jalali.year, jalali.month, jalali.day)
    return buildString {
        append(gy.toString().padStart(4, '0'))
        append('-')
        append(gm.toString().padStart(2, '0'))
        append('-')
        append(gd.toString().padStart(2, '0'))
    }
}

/** Human-readable Jalali with Persian digits, e.g. `۱۵ فروردین ۱۴۰۳`. */
fun formatJalaliDisplay(jalali: JalaliDate): String {
    val month = JalaliMonthNames.getOrElse(jalali.month - 1) { return "" }
    return toPersianDigits("${jalali.day} $month ${jalali.year}")
}

/** Formats an ISO date string for UI, or falls back to a slash-separated Persian digit string. */
fun formatIsoDateAsJalali(iso: String): String {
    val jalali = parseIsoDateToJalali(iso)
    return if (jalali != null) {
        formatJalaliDisplay(jalali)
    } else {
        toPersianDigits(iso.substringBefore('T').replace('-', '/'))
    }
}

fun toPersianDigits(value: String): String = buildString {
    value.forEach { ch ->
        append(
            when (ch) {
                '0' -> '۰'
                '1' -> '۱'
                '2' -> '۲'
                '3' -> '۳'
                '4' -> '۴'
                '5' -> '۵'
                '6' -> '۶'
                '7' -> '۷'
                '8' -> '۸'
                '9' -> '۹'
                else -> ch
            },
        )
    }
}
