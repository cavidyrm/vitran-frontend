package com.vitran.shop.ui.sections.account.cities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.vitran.shop.feature.location.domain.model.City

@Immutable
data class AccountCity(
    val id: Int,
    val name: String,
    val slug: String,
    val isActive: Boolean = true,
)

fun City.toAccountCity(): AccountCity =
    AccountCity(
        id = id.value.toInt(),
        name = name,
        slug = slug.value,
    )

internal object MockAccountCities {
    val items: SnapshotStateList<AccountCity> = mutableStateListOf<AccountCity>().also { list ->
        list.addAll(seedCities())
    }

    fun find(id: Int): AccountCity? = items.firstOrNull { it.id == id }

    fun update(city: AccountCity) {
        val index = items.indexOfFirst { it.id == city.id }
        if (index >= 0) items[index] = city
    }

    fun add(name: String, slug: String, isActive: Boolean = true): AccountCity {
        val nextId = (items.maxOfOrNull { it.id } ?: 0) + 1
        val city = AccountCity(
            id = nextId,
            name = name.trim(),
            slug = slug.trim(),
            isActive = isActive,
        )
        items.add(0, city)
        return city
    }

    fun delete(id: Int) {
        items.removeAll { it.id == id }
    }
}

@Composable
internal fun rememberMockAccountCities(): SnapshotStateList<AccountCity> = MockAccountCities.items

internal fun findMockAccountCity(cityId: String): AccountCity? {
    val id = cityId.toIntOrNull() ?: return null
    return MockAccountCities.find(id)
}

internal fun filterAccountCities(
    cities: List<AccountCity>,
    search: String,
): List<AccountCity> {
    val query = search.trim()
    if (query.isEmpty()) return cities
    return cities.filter { city ->
        city.name.contains(query, ignoreCase = true) ||
            city.slug.contains(query, ignoreCase = true)
    }
}

private fun seedCities(): List<AccountCity> = listOf(
    AccountCity(1, "تهران", "tehran"),
    AccountCity(2, "مشهد", "mashhad"),
    AccountCity(3, "اصفهان", "isfahan"),
    AccountCity(4, "شیراز", "shiraz", isActive = false),
    AccountCity(5, "تبریز", "tabriz"),
    AccountCity(6, "کرج", "karaj"),
    AccountCity(7, "قم", "qom"),
    AccountCity(8, "اهواز", "ahvaz"),
    AccountCity(9, "کرمانشاه", "kermanshah"),
    AccountCity(10, "ارومیه", "urmia"),
    AccountCity(11, "رشت", "rasht"),
    AccountCity(12, "زاهدان", "zahedan", isActive = false),
    AccountCity(13, "همدان", "hamadan"),
    AccountCity(14, "کرمان", "kerman"),
    AccountCity(15, "یزد", "yazd"),
    AccountCity(16, "اردبیل", "ardabil"),
    AccountCity(17, "بندرعباس", "bandar-abbas"),
    AccountCity(18, "اراک", "arak"),
    AccountCity(19, "اسلامشهر", "eslamshahr"),
    AccountCity(20, "زنجان", "zanjan", isActive = false),
    AccountCity(21, "سنندج", "sanandaj"),
    AccountCity(22, "قزوین", "qazvin"),
    AccountCity(23, "خرم‌آباد", "khorramabad"),
    AccountCity(24, "گرگان", "gorgan"),
    AccountCity(25, "ساری", "sari"),
    AccountCity(26, "کاشان", "kashan"),
    AccountCity(27, "نیشابور", "nishapur"),
    AccountCity(28, "سبزوار", "sabzevar"),
    AccountCity(29, "آمل", "amol"),
    AccountCity(30, "بابل", "babol"),
    AccountCity(31, "نجف‌آباد", "najafabad"),
    AccountCity(32, "قدس", "qods"),
    AccountCity(33, "ملارد", "malard", isActive = false),
    AccountCity(34, "خمینی‌شهر", "khomeyni-shahr"),
    AccountCity(35, "شهریار", "shahriar"),
    AccountCity(36, "پاکدشت", "pakdasht"),
    AccountCity(37, "بجنورد", "bojnurd"),
    AccountCity(38, "سیرجان", "sirjan"),
    AccountCity(39, "بوشهر", "bushehr"),
    AccountCity(40, "بیرجند", "birjand"),
    AccountCity(41, "ایلام", "ilam"),
    AccountCity(42, "مراغه", "maragheh"),
    AccountCity(43, "ماهشهر", "mahshahr"),
    AccountCity(44, "دزفول", "dezful"),
    AccountCity(45, "آبادان", "abadan"),
    AccountCity(46, "ورامین", "varamin"),
    AccountCity(47, "شهرکرد", "shahr-e-kord", isActive = false),
    AccountCity(48, "سمنان", "semnan"),
    AccountCity(49, "یاسوج", "yasuj"),
    AccountCity(50, "قوچان", "quchan"),
    AccountCity(51, "مرودشت", "marvdasht"),
    AccountCity(52, "زابل", "zabol"),
    AccountCity(53, "ساوه", "saveh"),
    AccountCity(54, "بروجرد", "borujerd"),
    AccountCity(55, "گنبد کاووس", "gonbad-e-kavus"),
)
