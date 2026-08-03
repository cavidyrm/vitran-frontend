package com.vitran.shop.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.hero_wordmark
import vitranshop.shared.generated.resources.hero_wordmark_a11y

/**
 * Large brand wordmark for the Home hero (shop.app heroContainer logo).
 * Text stand-in until a dedicated wordmark asset exists.
 */
@Composable
fun HeroWordmark(
    modifier: Modifier = Modifier,
) {
    val label = stringResource(Res.string.hero_wordmark)
    val a11y = stringResource(Res.string.hero_wordmark_a11y)

    Text(
        text = label,
        modifier = modifier.semantics { contentDescription = a11y },
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.displaySmall.copy(
            fontWeight = FontWeight.Bold,
            // Matches shop.app wordmark visual weight (~72dp tall on desktop).
            fontSize = 56.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.5).sp,
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB)
@Composable
private fun HeroWordmarkPreview() {
    VitranTheme {
        Box(modifier = Modifier.padding(VitranSpacing.xl)) {
            HeroWordmark()
        }
    }
}
