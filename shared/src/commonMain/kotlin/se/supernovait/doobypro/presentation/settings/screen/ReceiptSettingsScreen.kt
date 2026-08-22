package se.supernovait.doobypro.presentation.settings.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Settings_receipt_company_details_label
import doobypro.shared.generated.resources.screen_Settings_receipt_customer_details_label
import doobypro.shared.generated.resources.screen_Settings_receipt_delivery_details_label
import doobypro.shared.generated.resources.screen_Settings_receipt_footer_details_label
import doobypro.shared.generated.resources.screen_Settings_receipt_include_company_address
import doobypro.shared.generated.resources.screen_Settings_receipt_include_company_email
import doobypro.shared.generated.resources.screen_Settings_receipt_include_company_logo
import doobypro.shared.generated.resources.screen_Settings_receipt_include_company_name
import doobypro.shared.generated.resources.screen_Settings_receipt_include_company_phone
import doobypro.shared.generated.resources.screen_Settings_receipt_include_customer_name
import doobypro.shared.generated.resources.screen_Settings_receipt_include_delivery_date
import doobypro.shared.generated.resources.screen_Settings_receipt_include_delivery_method
import doobypro.shared.generated.resources.screen_Settings_receipt_include_delivery_option
import doobypro.shared.generated.resources.screen_Settings_receipt_include_order_items
import doobypro.shared.generated.resources.screen_Settings_receipt_include_order_notes
import doobypro.shared.generated.resources.screen_Settings_receipt_include_order_number
import doobypro.shared.generated.resources.screen_Settings_receipt_include_order_time
import doobypro.shared.generated.resources.screen_Settings_receipt_include_order_total
import doobypro.shared.generated.resources.screen_Settings_receipt_include_terms_and_conditions
import doobypro.shared.generated.resources.screen_Settings_receipt_order_details_label
import doobypro.shared.generated.resources.screen_Settings_receipt_paper_width_label
import se.supernovait.app.core.ui.component.container.SupernovaListGroup
import se.supernovait.app.core.ui.component.selection.SupernovaSelectField
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
        // Company Details
        SupernovaLabel(
            text = Res.string.screen_Settings_receipt_company_details_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small, bottom = MaterialTheme.spacing.small)
        )
        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_company_logo,
                    checked = state.settings.receipt.includeCompanyLogo,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeCompanyLogo(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_company_name,
                    checked = state.settings.receipt.includeCompanyName,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeCompanyName(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_company_address,
                    checked = state.settings.receipt.includeCompanyAddress,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeCompanyAddress(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_company_phone,
                    checked = state.settings.receipt.includeCompanyPhone,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeCompanyPhone(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_company_email,
                    checked = state.settings.receipt.includeCompanyEmail,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeCompanyEmail(it)) }
                )
            }
        }

        // Customer Details
        SupernovaLabel(
            text = Res.string.screen_Settings_receipt_customer_details_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )
        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_customer_name,
                    checked = state.settings.receipt.includeCustomerName,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeCustomerName(it)) }
                )
            }
        }

        // Order Details
        SupernovaLabel(
            text = Res.string.screen_Settings_receipt_order_details_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )
        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_order_number,
                    checked = state.settings.receipt.includeOrderNumber,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeOrderNumber(it)) }
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
                    label = Res.string.screen_Settings_receipt_include_order_items,
                    checked = state.settings.receipt.includeOrderItems,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeOrderItems(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_order_total,
                    checked = state.settings.receipt.includeOrderTotal,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeOrderTotal(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_order_notes,
                    checked = state.settings.receipt.includeOrderNotes,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeOrderNotes(it)) }
                )
            }
        }

        // Delivery Details
        SupernovaLabel(
            text = Res.string.screen_Settings_receipt_delivery_details_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )
        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_delivery_date,
                    checked = state.settings.receipt.includeDeliveryDate,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeDeliveryDate(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_delivery_option,
                    checked = state.settings.receipt.includeDeliveryOption,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeDeliveryOption(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_delivery_method,
                    checked = state.settings.receipt.includeDeliveryMethod,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeDeliveryMethod(it)) }
                )
            }
        }

        // Footer Details
        SupernovaLabel(
            text = Res.string.screen_Settings_receipt_footer_details_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )
        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_receipt_include_terms_and_conditions,
                    checked = state.settings.receipt.includeTermsAndConditions,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateIncludeTermsAndConditions(it)) }
                )
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.large))

        SupernovaSelectField(
            label = Res.string.screen_Settings_receipt_paper_width_label,
            options = PaperWidth.entries,
            selectedOption = state.settings.receipt.paperWidth,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdatePaperWidth(it)) },
            optionLabel = { it.value }
        )
        
        Spacer(Modifier.height(MaterialTheme.spacing.medium))
    }
}
