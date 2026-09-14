package com.vitran.shop.ui.util

import kotlin.test.Test
import kotlin.test.assertEquals

class JalaliDateTest {

    @Test
    fun gregorianNowruz_roundTripsToJalaliFarvardin1() {
        val jalali = gregorianToJalali(2024, 3, 20)
        assertEquals(1403, jalali.year)
        assertEquals(1, jalali.month)
        assertEquals(1, jalali.day)

        val (gy, gm, gd) = jalaliToGregorian(jalali.year, jalali.month, jalali.day)
        assertEquals(2024, gy)
        assertEquals(3, gm)
        assertEquals(20, gd)
    }

    @Test
    fun isoParseAndFormat_roundTrip() {
        val iso = "1990-03-21"
        val jalali = parseIsoDateToJalali(iso)!!
        assertEquals(iso, jalaliToIsoDate(jalali))
    }

    @Test
    fun formatDisplay_usesPersianDigitsAndMonthName() {
        val display = formatJalaliDisplay(JalaliDate(1403, 1, 15))
        assertEquals("۱۵ فروردین ۱۴۰۳", display)
    }
}
