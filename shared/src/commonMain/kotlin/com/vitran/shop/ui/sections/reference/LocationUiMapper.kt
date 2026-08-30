package com.vitran.shop.ui.sections.reference

import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.ui.components.admin.AdminSelectOption

fun City.toAdminSelectOption(): AdminSelectOption =
    AdminSelectOption(
        id = id.value.toString(),
        label = name,
    )

fun List<City>.toAdminSelectOptions(): List<AdminSelectOption> =
    map { it.toAdminSelectOption() }
