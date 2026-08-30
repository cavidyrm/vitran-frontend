package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.content.presentation.PublicStaticPageViewModel
import com.vitran.shop.ui.components.SafeHtml
import com.vitran.shop.ui.components.SiteFooter
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.about.AboutTokens
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.di.vitranKoinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun StaticPageScreen(
    slug: String,
    onFooterLinkClick: (SiteFooterLinkId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PublicStaticPageViewModel = vitranKoinViewModel(
        parameters = { parametersOf(slug) },
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pagePad = if (LocalDesktopLayout.current) AboutTokens.PagePadDesktop else AboutTokens.PagePadCompact
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = AboutTokens.ContentMaxWidth)
                .padding(horizontal = pagePad, vertical = VitranSpacing.xxxl),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xl),
        ) {
            when (val current = state) {
                is PublicStaticPageViewModel.UiState.Content -> {
                    VitranText(current.page.title, VitranTextStyle.Headline)
                    SafeHtml(current.page.bodyHtml, Modifier.fillMaxWidth())
                }
                is PublicStaticPageViewModel.UiState.Error ->
                    VitranText(current.message ?: "دریافت صفحه انجام نشد", VitranTextStyle.Body)
                PublicStaticPageViewModel.UiState.Loading ->
                    VitranText("در حال دریافت صفحه…", VitranTextStyle.Body)
                PublicStaticPageViewModel.UiState.NotFound ->
                    VitranText("این صفحه یافت نشد", VitranTextStyle.Body)
            }
        }
        SiteFooter(
            onLinkClick = onFooterLinkClick,
            onLanguageClick = {},
            onDownloadClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
