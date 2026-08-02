package com.vitran.shop.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

object VitranShapes {
    val pill = RoundedCornerShape(percent = 50)

    val material = Shapes(
        extraSmall = RoundedCornerShape(VitranRadius.small),
        small = RoundedCornerShape(VitranRadius.medium),
        medium = RoundedCornerShape(VitranRadius.large),
        large = RoundedCornerShape(VitranRadius.xl),
        extraLarge = RoundedCornerShape(VitranRadius.extraLarge),
    )
}
