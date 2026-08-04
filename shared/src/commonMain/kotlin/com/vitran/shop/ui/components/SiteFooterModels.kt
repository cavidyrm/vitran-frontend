package com.vitran.shop.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.site_footer_col_information
import vitranshop.shared.generated.resources.site_footer_col_legal
import vitranshop.shared.generated.resources.site_footer_col_social
import vitranshop.shared.generated.resources.site_footer_col_start_selling
import vitranshop.shared.generated.resources.site_footer_link_build_store
import vitranshop.shared.generated.resources.site_footer_link_for_brands
import vitranshop.shared.generated.resources.site_footer_link_for_creators
import vitranshop.shared.generated.resources.site_footer_link_help_center
import vitranshop.shared.generated.resources.site_footer_link_instagram
import vitranshop.shared.generated.resources.site_footer_link_manage_cookies
import vitranshop.shared.generated.resources.site_footer_link_privacy
import vitranshop.shared.generated.resources.site_footer_link_privacy_choices
import vitranshop.shared.generated.resources.site_footer_link_terms
import vitranshop.shared.generated.resources.site_footer_link_vitran_pay
import vitranshop.shared.generated.resources.site_footer_link_x_twitter

/** Identifiers for mock footer link clicks (shop.app footer columns). */
enum class SiteFooterLinkId {
    ForBrands,
    ForCreators,
    BuildStore,
    VitranPay,
    HelpCenter,
    XTwitter,
    Instagram,
    Terms,
    Privacy,
    PrivacyChoices,
    ManageCookies,
    StartSellingFree,
}

@Immutable
data class SiteFooterLink(
    val id: SiteFooterLinkId,
    val label: String,
    /** shop.app “Your Privacy Choices” shows a small opt-out icon beside the label. */
    val showPrivacyChoicesIcon: Boolean = false,
)

@Immutable
data class SiteFooterColumn(
    val title: String,
    val links: List<SiteFooterLink>,
)

@Composable
fun rememberSiteFooterColumns(): List<SiteFooterColumn> {
    val startSelling = stringResource(Res.string.site_footer_col_start_selling)
    val forBrands = stringResource(Res.string.site_footer_link_for_brands)
    val forCreators = stringResource(Res.string.site_footer_link_for_creators)
    val buildStore = stringResource(Res.string.site_footer_link_build_store)
    val information = stringResource(Res.string.site_footer_col_information)
    val vitranPay = stringResource(Res.string.site_footer_link_vitran_pay)
    val helpCenter = stringResource(Res.string.site_footer_link_help_center)
    val social = stringResource(Res.string.site_footer_col_social)
    val xTwitter = stringResource(Res.string.site_footer_link_x_twitter)
    val instagram = stringResource(Res.string.site_footer_link_instagram)
    val legal = stringResource(Res.string.site_footer_col_legal)
    val terms = stringResource(Res.string.site_footer_link_terms)
    val privacy = stringResource(Res.string.site_footer_link_privacy)
    val privacyChoices = stringResource(Res.string.site_footer_link_privacy_choices)
    val manageCookies = stringResource(Res.string.site_footer_link_manage_cookies)

    return remember(
        startSelling, forBrands, forCreators, buildStore,
        information, vitranPay, helpCenter,
        social, xTwitter, instagram,
        legal, terms, privacy, privacyChoices, manageCookies,
    ) {
        listOf(
            SiteFooterColumn(
                title = startSelling,
                links = listOf(
                    SiteFooterLink(SiteFooterLinkId.ForBrands, forBrands),
                    SiteFooterLink(SiteFooterLinkId.ForCreators, forCreators),
                    SiteFooterLink(SiteFooterLinkId.BuildStore, buildStore),
                ),
            ),
            SiteFooterColumn(
                title = information,
                links = listOf(
                    SiteFooterLink(SiteFooterLinkId.VitranPay, vitranPay),
                    SiteFooterLink(SiteFooterLinkId.HelpCenter, helpCenter),
                ),
            ),
            SiteFooterColumn(
                title = social,
                links = listOf(
                    SiteFooterLink(SiteFooterLinkId.XTwitter, xTwitter),
                    SiteFooterLink(SiteFooterLinkId.Instagram, instagram),
                ),
            ),
            SiteFooterColumn(
                title = legal,
                links = listOf(
                    SiteFooterLink(SiteFooterLinkId.Terms, terms),
                    SiteFooterLink(SiteFooterLinkId.Privacy, privacy),
                    SiteFooterLink(
                        id = SiteFooterLinkId.PrivacyChoices,
                        label = privacyChoices,
                        showPrivacyChoicesIcon = true,
                    ),
                    SiteFooterLink(SiteFooterLinkId.ManageCookies, manageCookies),
                ),
            ),
        )
    }
}
