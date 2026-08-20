package se.supernovait.doobypro.presentation.settings.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Settings_include_on_receipt_label
import doobypro.shared.generated.resources.screen_Settings_paper_width_label
import doobypro.shared.generated.resources.screen_Settings_receipt_include_customer_name
import doobypro.shared.generated.resources.screen_Settings_receipt_include_order_time
import doobypro.shared.generated.resources.screen_Settings_receipt_include_payment_method
import doobypro.shared.generated.resources.screen_Settings_receipt_include_store_location
import se.supernovait.app.core.ui.component.container.SupernovaListGroup
import se.supernovait.app.core.ui.component.input.SupernovaSelectField
import se.supernovait.app.core.ui.component.selection.SupernovaToggle
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.settings.receipt.PaperWidth
import se.supernovait.doobypro.presentation.settings.SettingsState
import se.supernovait.doobypro.presentation.settings.event.SettingsScreenEvent

@Composable
fun ReceiptSettingsScreen(
    state: SettingsState,
    onEvent: (SettingsScreenEvent) -> Unit
) {
    SettingsScreen {
        SupernovaLabel(
            text = Res.string.screen_Settings_include_on_receipt_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small, bottom = MaterialTheme.spacing.small)
        )

        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_customer_name,
                    checked = state.settings.receipt.includeCustomerName,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeCustomerName(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_order_time,
                    checked = state.settings.receipt.includeOrderTime,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeOrderTime(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_payment_method,
                    checked = state.settings.receipt.includePaymentMethod,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludePaymentMethod(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_store_location,
                    checked = state.settings.receipt.includeStoreLocation,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeStoreLocation(it)) }
                )
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.large))

        SupernovaSelectField(
            label = Res.string.screen_Settings_paper_width_label,
            options = PaperWidth.entries,
            selectedOption = state.settings.receipt.paperWidth,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdatePaperWidth(it)) },
            optionLabel = { it.value }
        )
    }
}
