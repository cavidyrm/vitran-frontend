package com.vitran.shop.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranSize
import org.jetbrains.compose.resources.painterResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_nav_profile

/**
 * Renders the signed-in profile avatar. Does not load remote images itself.
 * Plug in Coil (or similar) later by providing a custom implementation.
 * Size is controlled by the caller via [modifier].
 */
interface AvatarRenderer {
    @Composable
    fun Avatar(avatarUrl: String?, modifier: Modifier)
}

object DefaultAvatarRenderer : AvatarRenderer {
    @Composable
    override fun Avatar(
        avatarUrl: String?,
        modifier: Modifier,
    ) {
        // avatarUrl reserved for a future image loader; placeholder only for now.
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_nav_profile),
                contentDescription = null,
                size = VitranSize.iconSmall,
            )
        }
    }
}
