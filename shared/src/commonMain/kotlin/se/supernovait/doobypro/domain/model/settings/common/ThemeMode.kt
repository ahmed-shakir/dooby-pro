package se.supernovait.doobypro.domain.model.settings.common

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.theme_mode_dark
import doobypro.shared.generated.resources.theme_mode_light
import doobypro.shared.generated.resources.theme_mode_system
import org.jetbrains.compose.resources.StringResource

enum class ThemeMode(val label: StringResource) {
    LIGHT(Res.string.theme_mode_light),
    DARK(Res.string.theme_mode_dark),
    SYSTEM(Res.string.theme_mode_system)
}
