package se.supernovait.doobypro.presentation.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val default: Dp = SpacingDefaults.DEFAULT.dp,
    val divider: Dp = SpacingDefaults.DIVIDER.dp,
    val tiny: Dp = SpacingDefaults.TINY.dp,
    val extraSmall: Dp = SpacingDefaults.EXTRA_SMALL.dp,
    val small: Dp = SpacingDefaults.SMALL.dp,
    val medium: Dp = SpacingDefaults.MEDIUM.dp,
    val large: Dp = SpacingDefaults.LARGE.dp,
    val extraLarge: Dp = SpacingDefaults.EXTRA_LARGE.dp,
    val x2Large: Dp = SpacingDefaults.X2_LARGE.dp,
    val x3Large: Dp = SpacingDefaults.X3_LARGE.dp,
    val x4Large: Dp = SpacingDefaults.X4_LARGE.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
