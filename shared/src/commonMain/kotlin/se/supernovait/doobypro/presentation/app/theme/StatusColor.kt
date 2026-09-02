package se.supernovait.doobypro.presentation.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class StatusColor(
    val info: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val active: Color
)

val lightStatusColors = StatusColor(
    info = Color(0xFF0066FF),
    success = Color(0xFF2E7D32),
    warning = Color(0xFFF57C00),
    error = Color(0xFFB3261E),
    active = Color(0xFF4CAF50)
)

val darkStatusColors = StatusColor(
    info = Color(0xFF64B5F6),
    success = Color(0xFF81C784),
    warning = Color(0xFFFFB74D),
    error = Color(0xFFFFB4AA), // Matches errorDark in Color.kt
    active = Color(0xFF66BB6A)
)

val LocalStatus = staticCompositionLocalOf { lightStatusColors }

val MaterialTheme.statusColor: StatusColor
    @Composable
    @ReadOnlyComposable
    get() = LocalStatus.current
