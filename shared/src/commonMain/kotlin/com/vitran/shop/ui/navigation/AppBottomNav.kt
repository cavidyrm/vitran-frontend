package com.vitran.shop.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranOpacity
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_nav_categories
import vitranshop.shared.generated.resources.ic_nav_home
import vitranshop.shared.generated.resources.ic_nav_offers
import vitranshop.shared.generated.resources.ic_nav_profile
import vitranshop.shared.generated.resources.ic_nav_saved
import vitranshop.shared.generated.resources.nav_categories
import vitranshop.shared.generated.resources.nav_home
import vitranshop.shared.generated.resources.nav_offers
import vitranshop.shared.generated.resources.nav_saved
import vitranshop.shared.generated.resources.nav_saved_sign_in_a11y
import vitranshop.shared.generated.resources.nav_sign_in

private val BottomShape = RoundedCornerShape(
    topStart = VitranRadius.extraLarge,
    topEnd = VitranRadius.extraLarge,
)

@Composable
fun AppBottomNav(
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
    val savedSignInA11y = stringResource(Res.string.nav_saved_sign_in_a11y)
    val isLoggedIn = authState is NavAuthUiState.SignedIn

    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .shadow(
                elevation = VitranElevation.medium,
                shape = BottomShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f),
            )
            .clip(BottomShape)
            .background(Color.White, BottomShape)
            .border(
                VitranSize.borderHairline,
                MaterialTheme.colorScheme.outline,
                BottomShape
            )
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xl, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavItemButton(
            painter = painterResource(Res.drawable.ic_nav_home),
            contentDescription = homeLabel,
            selected = currentRoute == Route.Home,
            onClick = { onNavigate(Route.Home) },
            showTooltip = false,
        )
        NavItemButton(
            painter = painterResource(Res.drawable.ic_nav_categories),
            contentDescription = categoriesLabel,
            selected = currentRoute == Route.Categories,
            onClick = { onNavigate(Route.Categories) },
            showTooltip = false,
        )
        NavItemButton(
            painter = painterResource(Res.drawable.ic_nav_offers),
            contentDescription = offersLabel,
            selected = currentRoute == Route.Offers,
            onClick = { onNavigate(Route.Offers) },
            showTooltip = false,
        )

        if (!isLoggedIn) {
            NavItemButton(
                painter = painterResource(Res.drawable.ic_nav_saved),
                contentDescription = savedSignInA11y,
                selected = false,
                onClick = onLoginRequest,
                showTooltip = false,
            )
        } else {
            NavItemButton(
                painter = painterResource(Res.drawable.ic_nav_saved),
                contentDescription = savedLabel,
                selected = currentRoute == Route.Saved,
                onClick = { onNavigate(Route.Saved) },
                showTooltip = false,
            )
        }

        if (!isLoggedIn) {
            Box(
                modifier = Modifier
                    .size(VitranSize.touchTarget)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onLoginRequest,
                    )
                    .padding(VitranSpacing.md),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(Res.drawable.ic_nav_profile),
                    contentDescription = signInLabel,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = VitranOpacity.INACTIVE),
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onNavigate(Route.Account) },
                    )
                    .padding(VitranSpacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                avatarRenderer.Avatar(
                    avatarUrl = authState.avatarUrl,
                    modifier = Modifier.size(VitranSize.avatarSmall),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AppBottomNavSignedOutPreview() {
    VitranTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Bottom,
        ) {
            AppBottomNav(
                currentRoute = Route.Home,
                authState = NavAuthUiState.SignedOut,
                onNavigate = {},
                onLoginRequest = {},
                avatarRenderer = DefaultAvatarRenderer,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun AppBottomNavSignedInPreview() {
    VitranTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Bottom,
        ) {
            AppBottomNav(
                currentRoute = Route.Home,
                authState = NavAuthUiState.SignedIn(avatarUrl = null),
                onNavigate = {},
                onLoginRequest = {},
                avatarRenderer = DefaultAvatarRenderer,
            )
        }
    }
}
