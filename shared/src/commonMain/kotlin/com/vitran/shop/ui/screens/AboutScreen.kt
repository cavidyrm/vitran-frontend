package com.vitran.shop.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.components.SiteFooter
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.about.AboutCtaSection
import com.vitran.shop.ui.sections.about.AboutFeaturesSection
import com.vitran.shop.ui.sections.about.AboutHeroSection
import com.vitran.shop.ui.sections.about.AboutStatsSection
import com.vitran.shop.ui.sections.about.AboutStorySection
import com.vitran.shop.ui.sections.about.AboutTokens
import com.vitran.shop.ui.sections.about.rememberAboutFeatures
import com.vitran.shop.ui.sections.about.rememberAboutStats
import com.vitran.shop.ui.sections.about.rememberAboutStoryChecks
import com.vitran.shop.ui.shell.LocalDesktopLayout
import kotlinx.coroutines.launch

/**
 * Public About Us marketing page — route `/about`.
 * Shopper chrome stays; mock copy/images only.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AboutScreen(
    onHomeClick: () -> Unit,
    onCreateStore: () -> Unit,
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val storyBringIntoView = remember { BringIntoViewRequester() }
    val features = rememberAboutFeatures()
    val checks = rememberAboutStoryChecks()
    val stats = rememberAboutStats()
    val pagePad = if (isDesktop) AboutTokens.PagePadDesktop else AboutTokens.PagePadCompact

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = AboutTokens.ContentMaxWidth)
                .padding(horizontal = pagePad)
                .padding(top = pagePad),
        ) {
            AboutHeroSection(
                onStoryClick = {
                    scope.launch { storyBringIntoView.bringIntoView() }
                },
                onHomeClick = onHomeClick,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(AboutTokens.SectionGap))
            AboutFeaturesSection(
                features = features,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(AboutTokens.SectionGap))
            AboutStorySection(
                checks = checks,
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(storyBringIntoView),
            )
            Spacer(modifier = Modifier.height(AboutTokens.SectionGap))
            AboutStatsSection(
                stats = stats,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(AboutTokens.SectionGap))
            AboutCtaSection(
                onStartClick = onCreateStore,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        SiteFooter(
            onLinkClick = onFooterLinkClick,
            onLanguageClick = { /* mock — language settings not wired yet */ },
            onDownloadClick = { /* mock — store deep link not wired yet */ },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
