package se.supernovait.doobypro.presentation.settings.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Settings_currency_label
import doobypro.shared.generated.resources.screen_Settings_date_format_label
import doobypro.shared.generated.resources.screen_Settings_language_label
import doobypro.shared.generated.resources.screen_Settings_theme_label
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.input.SupernovaSelectField
import se.supernovait.app.core.ui.component.selection.SupernovaSelectionGroup
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.settings.common.Currency
import se.supernovait.doobypro.domain.model.settings.common.DateFormat
import se.supernovait.doobypro.domain.model.settings.common.Language
import se.supernovait.doobypro.domain.model.settings.common.ThemeMode
import se.supernovait.doobypro.presentation.settings.SettingsState
import se.supernovait.doobypro.presentation.settings.event.SettingsScreenEvent

@Composable
fun CommonSettingsScreen(
    uiState: SettingsState,
    onEvent: (SettingsScreenEvent) -> Unit
) {
    val currencyLabels = Currency.entries.associateWith { stringResource(it.label) }
    val themeLabels = ThemeMode.entries.associateWith { stringResource(it.label) }

    SettingsScreen {
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        SupernovaSelectField(
            label = Res.string.screen_Settings_currency_label,
            options = Currency.entries,
            selectedOption = uiState.settings.common.currency,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateCurrency(it)) },
            optionLabel = { currencyLabels[it] ?: "" }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaSelectField(
            label = Res.string.screen_Settings_date_format_label,
            options = DateFormat.entries,
            selectedOption = uiState.settings.common.dateFormat,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateDateFormat(it)) },
            optionLabel = { it.pattern }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaSelectField(
            label = Res.string.screen_Settings_language_label,
            options = Language.entries,
            selectedOption = uiState.settings.common.language,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateLanguage(it)) },
            optionLabel = { it.label },
            enabled = false
        )

        SupernovaLabel(
            text = Res.string.screen_Settings_theme_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )

        SupernovaSelectionGroup(
            options = ThemeMode.entries,
            selectedOption = uiState.settings.common.themeMode,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateThemeMode(it)) },
            optionLabel = { themeLabels[it] ?: "" }
        )
    }
}
