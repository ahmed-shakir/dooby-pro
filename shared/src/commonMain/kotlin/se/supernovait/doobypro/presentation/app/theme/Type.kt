package se.supernovait.doobypro.presentation.app.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.inter_italic_variable_font
import doobypro.shared.generated.resources.inter_variable_font
import doobypro.shared.generated.resources.manrope_variable_font
import org.jetbrains.compose.resources.Font

@Composable
fun Inter() = FontFamily(
    Font(
        resource = Res.font.inter_variable_font,
        weight = FontWeight.Normal
    ),
    Font(
        resource = Res.font.inter_italic_variable_font,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    ),
    Font(
        resource = Res.font.inter_variable_font,
        weight = FontWeight.Bold
    ),
    Font(
        resource = Res.font.inter_italic_variable_font,
        weight = FontWeight.Bold,
        style = FontStyle.Italic
    ),
)

@Composable
fun Manrope() = FontFamily(
    Font(
        resource = Res.font.manrope_variable_font,
        weight = FontWeight.Normal
    ),
    Font(
        resource = Res.font.manrope_variable_font,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    ),
    Font(
        resource = Res.font.manrope_variable_font,
        weight = FontWeight.Bold
    ),
    Font(
        resource = Res.font.manrope_variable_font,
        weight = FontWeight.Bold,
        style = FontStyle.Italic
    ),
)

@Composable
fun AppTypography() = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = Manrope()),
        displayMedium = displayMedium.copy(fontFamily = Manrope()),
        displaySmall = displaySmall.copy(fontFamily = Manrope()),
        headlineLarge = headlineLarge.copy(fontFamily = Manrope()),
        headlineMedium = headlineMedium.copy(fontFamily = Manrope()),
        headlineSmall = headlineSmall.copy(fontFamily = Manrope()),
        titleLarge = titleLarge.copy(fontFamily = Manrope()),
        titleMedium = titleMedium.copy(fontFamily = Manrope()),
        titleSmall = titleSmall.copy(fontFamily = Manrope()),
        bodyLarge = bodyLarge.copy(fontFamily = Inter()),
        bodyMedium = bodyMedium.copy(fontFamily = Inter()),
        bodySmall = bodySmall.copy(fontFamily = Inter()),
        labelLarge = labelLarge.copy(fontFamily = Inter()),
        labelMedium = labelMedium.copy(fontFamily = Inter()),
        labelSmall = labelSmall.copy(fontFamily = Inter())
    )
}
