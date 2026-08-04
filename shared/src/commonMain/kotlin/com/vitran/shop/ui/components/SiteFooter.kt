package com.vitran.shop.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_badge_app_store
import vitranshop.shared.generated.resources.ic_badge_google_play
import vitranshop.shared.generated.resources.ic_footer_qr
import vitranshop.shared.generated.resources.ic_vitran_wordmark
import vitranshop.shared.generated.resources.site_footer_app_store_a11y
import vitranshop.shared.generated.resources.site_footer_copyright
import vitranshop.shared.generated.resources.site_footer_google_play_a11y
import vitranshop.shared.generated.resources.site_footer_language
import vitranshop.shared.generated.resources.site_footer_mission
import vitranshop.shared.generated.resources.site_footer_powered_brand
import vitranshop.shared.generated.resources.site_footer_powered_by
import vitranshop.shared.generated.resources.site_footer_qr_a11y
import vitranshop.shared.generated.resources.site_footer_start_selling_free
import vitranshop.shared.generated.resources.site_footer_wordmark_a11y

/** shop.app footer `text-text-tertiary` ≈ rgba(0,0,0,0.56). */
private val FooterTertiary = Color.Black.copy(alpha = 0.56f)

/** shop.app subfooter “Powered by” `#7B7B7B`. */
private val FooterCaptionMuted = Color(0xFF7B7B7B)

/** shop.app QR tile `bg-bg-fill-fixed-dark`. */
private val FooterQrBackground = Color(0xFF121212)

/** shop.app privacy-choices badge blue. */
private val PrivacyChoicesBlue = Color(0xFF0066FF)

/** shop.app footer wordmark `h-space-24`. */
private val FooterWordmarkHeight = 24.dp

/** Intrinsic aspect of [ic_vitran_wordmark] (viewport 114 × 42). */
private const val WordmarkAspect = 114f / 42f

/** shop.app store badge `h-space-40`, aspect 120×40. */
private val StoreBadgeHeight = 40.dp
private val StoreBadgeWidth = 120.dp

/** shop.app QR container 92×92, image 88×88. */
private val QrTileSize = 92.dp
private val QrImageSize = 88.dp

/** shop.app `pt-space-48` compact / `md:pt-[136px]` desktop. */
private val FooterTopPadCompact = 48.dp
private val FooterTopPadDesktop = 136.dp

/**
 * Site footer matching shop.app home footer layout.
 * Vitran-branded Persian copy; all actions are mock stubs.
 */
@Composable
fun SiteFooter(
    onLinkClick: (SiteFooterLinkId) -> Unit,
    onLanguageClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
    columns: List<SiteFooterColumn> = rememberSiteFooterColumns(),
) {
    val isDesktop = LocalDesktopLayout.current
    val horizontalPad = if (isDesktop) {
        VitranSpacing.xxxl + VitranSpacing.lg
    } else {
        VitranSpacing.lg
    }
    val topPad = if (isDesktop) FooterTopPadDesktop else FooterTopPadCompact

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = horizontalPad,
                end = horizontalPad,
                top = topPad,
                bottom = VitranSpacing.xxxl,
            ),
    ) {
        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xxxl),
            ) {
                BrandAndDownloads(
                    onDownloadClick = onDownloadClick,
                    modifier = Modifier.weight(5f),
                )
                LinkColumnsRow(
                    columns = columns,
                    onLinkClick = onLinkClick,
                    modifier = Modifier.weight(7f),
                )
            }
        } else {
            BrandAndDownloads(
                onDownloadClick = onDownloadClick,
                showDownloads = false,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(VitranSpacing.sm))
            LinkColumnsStack(
                columns = columns,
                onLinkClick = onLinkClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(if (isDesktop) VitranSpacing.sm else VitranSpacing.xxxl))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isDesktop) VitranSpacing.xxxl else VitranSpacing.xxl),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        )

        SubFooter(
            onStartSellingFreeClick = {
                onLinkClick(SiteFooterLinkId.StartSellingFree)
            },
            onLanguageClick = onLanguageClick,
            isDesktop = isDesktop,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun BrandAndDownloads(
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDownloads: Boolean = true,
) {
    val mission = stringResource(Res.string.site_footer_mission)
    val wordmarkA11y = stringResource(Res.string.site_footer_wordmark_a11y)
    val qrA11y = stringResource(Res.string.site_footer_qr_a11y)
    val appStoreA11y = stringResource(Res.string.site_footer_app_store_a11y)
    val playA11y = stringResource(Res.string.site_footer_google_play_a11y)

    Column(modifier = modifier) {
        Image(
            painter = painterResource(Res.drawable.ic_vitran_wordmark),
            contentDescription = wordmarkA11y,
            modifier = Modifier
                .padding(bottom = VitranSpacing.sm)
                .size(
                    width = FooterWordmarkHeight * WordmarkAspect,
                    height = FooterWordmarkHeight,
                ),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(FooterTertiary),
        )

        Text(
            text = mission,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(bottom = VitranSpacing.xxxl),
            color = FooterTertiary,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Normal,
            ),
        )

        if (showDownloads) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(QrTileSize)
                        .clip(RoundedCornerShape(VitranRadius.small))
                        .background(FooterQrBackground)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDownloadClick,
                        )
                        .semantics { contentDescription = qrA11y },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_footer_qr),
                        contentDescription = null,
                        modifier = Modifier.size(QrImageSize),
                        contentScale = ContentScale.Fit,
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_badge_app_store),
                        contentDescription = appStoreA11y,
                        modifier = Modifier
                            .size(width = StoreBadgeWidth, height = StoreBadgeHeight)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onDownloadClick,
                            ),
                        contentScale = ContentScale.Fit,
                    )
                    Image(
                        painter = painterResource(Res.drawable.ic_badge_google_play),
                        contentDescription = playA11y,
                        modifier = Modifier
                            .size(width = StoreBadgeWidth, height = StoreBadgeHeight)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onDownloadClick,
                            ),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
    }
}

@Composable
private fun LinkColumnsRow(
    columns: List<SiteFooterColumn>,
    onLinkClick: (SiteFooterLinkId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        columns.forEach { column ->
            FooterLinkColumn(
                column = column,
                onLinkClick = onLinkClick,
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 200.dp),
            )
        }
    }
}

@Composable
private fun LinkColumnsStack(
    columns: List<SiteFooterColumn>,
    onLinkClick: (SiteFooterLinkId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        columns.forEach { column ->
            FooterLinkColumn(
                column = column,
                onLinkClick = onLinkClick,
                modifier = Modifier
                    .widthIn(max = 200.dp)
                    .padding(bottom = VitranSpacing.xxxl),
            )
        }
    }
}

@Composable
private fun FooterLinkColumn(
    column: SiteFooterColumn,
    onLinkClick: (SiteFooterLinkId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = column.title,
            modifier = Modifier.padding(bottom = VitranSpacing.sm),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Normal,
            ),
        )
        column.links.forEach { link ->
            Row(
                modifier = Modifier
                    .padding(bottom = VitranSpacing.sm)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onLinkClick(link.id) },
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            ) {
                Text(
                    text = link.label,
                    color = FooterTertiary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                )
                if (link.showPrivacyChoicesIcon) {
                    PrivacyChoicesIcon(
                        modifier = Modifier.size(width = 30.dp, height = 14.dp),
                    )
                }
            }
        }
    }
}

/** Approximate shop.app “Your Privacy Choices” badge (blue pill + check / X). */
@Composable
private fun PrivacyChoicesIcon(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = PrivacyChoicesBlue,
            cornerRadius = CornerRadius(h / 2f, h / 2f),
            size = Size(w, h),
        )
        // Check
        val checkPath = Path().apply {
            moveTo(w * 0.18f, h * 0.52f)
            lineTo(w * 0.28f, h * 0.68f)
            lineTo(w * 0.42f, h * 0.32f)
        }
        drawPath(
            path = checkPath,
            color = Color.White,
            style = Stroke(width = h * 0.12f),
        )
        // X
        drawLine(
            color = Color.White,
            start = Offset(w * 0.62f, h * 0.32f),
            end = Offset(w * 0.82f, h * 0.68f),
            strokeWidth = h * 0.12f,
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.82f, h * 0.32f),
            end = Offset(w * 0.62f, h * 0.68f),
            strokeWidth = h * 0.12f,
        )
    }
}

@Composable
private fun SubFooter(
    onStartSellingFreeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    isDesktop: Boolean,
    modifier: Modifier = Modifier,
) {
    val poweredBy = stringResource(Res.string.site_footer_powered_by)
    val poweredBrand = stringResource(Res.string.site_footer_powered_brand)
    val startFree = stringResource(Res.string.site_footer_start_selling_free)
    val language = stringResource(Res.string.site_footer_language)
    val copyright = stringResource(Res.string.site_footer_copyright)

    val captionStyle = MaterialTheme.typography.labelSmall.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
    )
    val captionBold = captionStyle.copy(fontWeight = FontWeight.SemiBold)

    if (isDesktop) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                Text(text = poweredBy, color = FooterCaptionMuted, style = captionBold)
                Text(text = poweredBrand, color = FooterCaptionMuted, style = captionBold)
                Text(text = "|", color = FooterCaptionMuted, style = captionStyle)
                Text(
                    text = startFree,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onStartSellingFreeClick,
                    ),
                    color = FooterTertiary,
                    style = captionStyle,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            ) {
                Text(
                    text = language,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onLanguageClick,
                    ),
                    color = FooterTertiary,
                    style = captionStyle,
                )
                Text(text = copyright, color = FooterTertiary, style = captionStyle)
            }
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                Text(text = poweredBy, color = FooterCaptionMuted, style = captionBold)
                Text(text = poweredBrand, color = FooterCaptionMuted, style = captionBold)
                Text(text = "|", color = FooterCaptionMuted, style = captionStyle)
                Text(
                    text = startFree,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onStartSellingFreeClick,
                    ),
                    color = FooterTertiary,
                    style = captionStyle,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                Text(
                    text = language,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onLanguageClick,
                    ),
                    color = FooterTertiary,
                    style = captionStyle,
                )
                Text(text = copyright, color = FooterTertiary, style = captionStyle)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB, widthDp = 390)
@Composable
private fun SiteFooterCompactPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides false) {
            SiteFooter(
                onLinkClick = {},
                onLanguageClick = {},
                onDownloadClick = {},
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB, widthDp = 1100)
@Composable
private fun SiteFooterDesktopPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            SiteFooter(
                onLinkClick = {},
                onLanguageClick = {},
                onDownloadClick = {},
            )
        }
    }
}
