package se.supernovait.doobypro.presentation.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Sizing(
    val icon: Icon = Icon()
)

data class Icon(
    val default: Dp = IconDefaults.DEFAULT.dp,
    val small: Dp = IconDefaults.SMALL.dp,
    val medium: Dp = IconDefaults.MEDIUM.dp,
    val large: Dp = IconDefaults.LARGE.dp,
    val extraLarge: Dp = IconDefaults.EXTRA_LARGE.dp
)

val LocalSizing = staticCompositionLocalOf { Sizing() }

val MaterialTheme.sizing: Sizing
    @Composable
    @ReadOnlyComposable
    get() = LocalSizing.current
