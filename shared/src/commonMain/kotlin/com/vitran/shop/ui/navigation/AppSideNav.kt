package com.vitran.shop.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.VitranOpacity
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_nav_categories
import vitranshop.shared.generated.resources.ic_nav_home
import vitranshop.shared.generated.resources.ic_nav_offers
import vitranshop.shared.generated.resources.ic_nav_profile
import vitranshop.shared.generated.resources.ic_nav_saved
import vitranshop.shared.generated.resources.ic_shop_logo
import vitranshop.shared.generated.resources.nav_account
import vitranshop.shared.generated.resources.nav_categories
import vitranshop.shared.generated.resources.nav_home
import vitranshop.shared.generated.resources.nav_offers
import vitranshop.shared.generated.resources.nav_saved
import vitranshop.shared.generated.resources.nav_saved_sign_in_a11y
import vitranshop.shared.generated.resources.nav_sign_in

@Composable
fun AppSideNav(
    currentRoute: Route,
    authState: NavAuthUiState,
    onNavigate: (Route) -> Unit,
    onLoginRequest: () -> Unit,
    avatarRenderer: AvatarRenderer,
    modifier: Modifier = Modifier,
) {
    val homeLabel = stringResource(Res.string.nav_home)
    val categoriesLabel = stringResource(Res.string.nav_categories)
    val offersLabel = stringResource(Res.string.nav_offers)
    val savedLabel = stringResource(Res.string.nav_saved)
    val signInLabel = stringResource(Res.string.nav_sign_in)
    val accountLabel = stringResource(Res.string.nav_account)
    val savedSignInA11y = stringResource(Res.string.nav_saved_sign_in_a11y)
    val isLoggedIn = authState is NavAuthUiState.SignedIn

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(VitranSize.sideRailWidth)
            .padding(vertical = VitranSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        LogoButton(onClick = { onNavigate(Route.Home) })

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            NavItemButton(
                painter = painterResource(Res.drawable.ic_nav_home),
                contentDescription = homeLabel,
                selected = currentRoute == Route.Home,
                onClick = { onNavigate(Route.Home) },
                showTooltip = true,
                tooltipText = homeLabel,
            )
            NavItemButton(
                painter = painterResource(Res.drawable.ic_nav_categories),
                contentDescription = categoriesLabel,
                selected = currentRoute == Route.Categories,
                onClick = { onNavigate(Route.Categories) },
                showTooltip = true,
                tooltipText = categoriesLabel,
            )
            NavItemButton(
                painter = painterResource(Res.drawable.ic_nav_offers),
                contentDescription = offersLabel,
                selected = currentRoute == Route.Offers,
                onClick = { onNavigate(Route.Offers) },
                showTooltip = true,
                tooltipText = offersLabel,
            )
            if (!isLoggedIn) {
                NavItemButton(
                    painter = painterResource(Res.drawable.ic_nav_saved),
                    contentDescription = savedSignInA11y,
                    selected = false,
                    enabled = true,
                    onClick = onLoginRequest,
                    showTooltip = true,
                    tooltipText = savedLabel,
                )
            } else {
                NavItemButton(
                    painter = painterResource(Res.drawable.ic_nav_saved),
                    contentDescription = savedLabel,
                    selected = currentRoute == Route.Saved,
                    onClick = { onNavigate(Route.Saved) },
                    showTooltip = true,
                    tooltipText = savedLabel,
                )
            }
        }

        if (!isLoggedIn) {
            SignInProfileButton(
                label = signInLabel,
                onClick = onLoginRequest,
            )
        } else {
            val interactionSource = remember { MutableInteractionSource() }
            val hovered by interactionSource.collectIsHoveredAsState()
            NavTooltipOverlay(
                tooltipText = accountLabel,
                anchorHovered = hovered,
                enabled = true,
            ) {
                Box(
                    modifier = Modifier
                        .hoverable(interactionSource)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onNavigate(Route.Account) },
                        )
                        .padding(VitranSpacing.sm),
                    contentAlignment = Alignment.Center,
                ) {
                    avatarRenderer.Avatar(
                        avatarUrl = authState.avatarUrl,
                        modifier = Modifier.size(VitranSize.avatarMedium),
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = when {
            pressed -> 0.96f
            hovered -> 1.05f
            else -> 1f
        },
        animationSpec = tween(300),
        label = "logoScale",
    )
    Icon(
        painter = painterResource(Res.drawable.ic_shop_logo),
        contentDescription = stringResource(Res.string.nav_home),
        tint = Color.Unspecified,
        modifier = Modifier
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(VitranSpacing.sm)
            .size(VitranSize.iconMedium)
            .scale(scale),
    )
}

@Composable
private fun SignInProfileButton(
    label: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    val opacity by animateFloatAsState(
        targetValue = if (hovered) 1f else VitranOpacity.INACTIVE,
        animationSpec = tween(150),
        label = "signInOpacity",
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(150),
        label = "signInScale",
    )

    Column(
        modifier = Modifier
            .height(VitranSize.profileItemHeight)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = VitranSpacing.md)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_nav_profile),
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = opacity),
        )
        Spacer(Modifier.height(2.dp))
        VitranText(
            text = label,
            style = VitranTextStyle.Label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = opacity),
        )
    }
}
