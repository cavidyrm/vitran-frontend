package com.vitran.shop.ui.sections.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.about_feature_growth_body
import vitranshop.shared.generated.resources.about_feature_growth_title
import vitranshop.shared.generated.resources.about_feature_secure_body
import vitranshop.shared.generated.resources.about_feature_secure_title
import vitranshop.shared.generated.resources.about_feature_simple_body
import vitranshop.shared.generated.resources.about_feature_simple_title
import vitranshop.shared.generated.resources.about_feature_support_body
import vitranshop.shared.generated.resources.about_feature_support_title
import vitranshop.shared.generated.resources.about_stat_orders_label
import vitranshop.shared.generated.resources.about_stat_orders_value
import vitranshop.shared.generated.resources.about_stat_products_label
import vitranshop.shared.generated.resources.about_stat_products_value
import vitranshop.shared.generated.resources.about_stat_satisfaction_label
import vitranshop.shared.generated.resources.about_stat_satisfaction_value
import vitranshop.shared.generated.resources.about_stat_stores_label
import vitranshop.shared.generated.resources.about_stat_stores_value
import vitranshop.shared.generated.resources.about_story_check_improve
import vitranshop.shared.generated.resources.about_story_check_needs
import vitranshop.shared.generated.resources.about_story_check_satisfaction
import vitranshop.shared.generated.resources.ic_chart
import vitranshop.shared.generated.resources.ic_face
import vitranshop.shared.generated.resources.ic_headset
import vitranshop.shared.generated.resources.ic_package
import vitranshop.shared.generated.resources.ic_people
import vitranshop.shared.generated.resources.ic_policy_order
import vitranshop.shared.generated.resources.ic_shield
import vitranshop.shared.generated.resources.ic_sparkles

/** Mock hero / story images (office + workspace). */
internal object AboutMockImages {
    const val Hero =
        "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1200&q=80"
    const val Story =
        "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=1200&q=80"
}

@Immutable
data class AboutFeatureItem(
    val icon: DrawableResource,
    val title: String,
    val body: String,
)

@Immutable
data class AboutStatItem(
    val icon: DrawableResource,
    val value: String,
    val label: String,
)

@Composable
internal fun rememberAboutFeatures(): List<AboutFeatureItem> {
    val simpleTitle = stringResource(Res.string.about_feature_simple_title)
    val simpleBody = stringResource(Res.string.about_feature_simple_body)
    val secureTitle = stringResource(Res.string.about_feature_secure_title)
    val secureBody = stringResource(Res.string.about_feature_secure_body)
    val supportTitle = stringResource(Res.string.about_feature_support_title)
    val supportBody = stringResource(Res.string.about_feature_support_body)
    val growthTitle = stringResource(Res.string.about_feature_growth_title)
    val growthBody = stringResource(Res.string.about_feature_growth_body)
    return remember(
        simpleTitle, simpleBody, secureTitle, secureBody,
        supportTitle, supportBody, growthTitle, growthBody,
    ) {
        listOf(
            AboutFeatureItem(Res.drawable.ic_sparkles, simpleTitle, simpleBody),
            AboutFeatureItem(Res.drawable.ic_shield, secureTitle, secureBody),
            AboutFeatureItem(Res.drawable.ic_headset, supportTitle, supportBody),
            AboutFeatureItem(Res.drawable.ic_chart, growthTitle, growthBody),
        )
    }
}

@Composable
internal fun rememberAboutStoryChecks(): List<String> {
    val a = stringResource(Res.string.about_story_check_needs)
    val b = stringResource(Res.string.about_story_check_improve)
    val c = stringResource(Res.string.about_story_check_satisfaction)
    return remember(a, b, c) { listOf(a, b, c) }
}

@Composable
internal fun rememberAboutStats(): List<AboutStatItem> {
    val storesValue = stringResource(Res.string.about_stat_stores_value)
    val storesLabel = stringResource(Res.string.about_stat_stores_label)
    val productsValue = stringResource(Res.string.about_stat_products_value)
    val productsLabel = stringResource(Res.string.about_stat_products_label)
    val ordersValue = stringResource(Res.string.about_stat_orders_value)
    val ordersLabel = stringResource(Res.string.about_stat_orders_label)
    val satValue = stringResource(Res.string.about_stat_satisfaction_value)
    val satLabel = stringResource(Res.string.about_stat_satisfaction_label)
    return remember(
        storesValue, storesLabel, productsValue, productsLabel,
        ordersValue, ordersLabel, satValue, satLabel,
    ) {
        listOf(
            AboutStatItem(Res.drawable.ic_people, storesValue, storesLabel),
            AboutStatItem(Res.drawable.ic_package, productsValue, productsLabel),
            AboutStatItem(Res.drawable.ic_policy_order, ordersValue, ordersLabel),
            AboutStatItem(Res.drawable.ic_face, satValue, satLabel),
        )
    }
}
