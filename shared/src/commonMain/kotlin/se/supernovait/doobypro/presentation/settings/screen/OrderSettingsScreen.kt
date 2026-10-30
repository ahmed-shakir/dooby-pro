package se.supernovait.doobypro.presentation.settings.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Settings_order_auto_print_order_tag_label
import doobypro.shared.generated.resources.screen_Settings_order_auto_print_receipts_label
import doobypro.shared.generated.resources.screen_Settings_order_behavior_label
import doobypro.shared.generated.resources.screen_Settings_order_collection_method_label
import doobypro.shared.generated.resources.screen_Settings_order_default_label
import doobypro.shared.generated.resources.screen_Settings_order_default_service_label
import doobypro.shared.generated.resources.screen_Settings_order_delivery_fee_value_label
import doobypro.shared.generated.resources.screen_Settings_order_delivery_method_label
import doobypro.shared.generated.resources.screen_Settings_order_delivery_option_label
import doobypro.shared.generated.resources.screen_Settings_order_express_fee_mode_label
import doobypro.shared.generated.resources.screen_Settings_order_express_fee_type_label
import doobypro.shared.generated.resources.screen_Settings_order_express_fee_value_label
import doobypro.shared.generated.resources.screen_Settings_order_fees_label
import doobypro.shared.generated.resources.screen_Settings_order_handling_time_label
import doobypro.shared.generated.resources.screen_Settings_order_pickup_fee_value_label
import doobypro.shared.generated.resources.screen_Settings_order_subunit_rounding_enabled_label
import doobypro.shared.generated.resources.screen_Settings_order_subunit_rounding_label
import doobypro.shared.generated.resources.screen_Settings_order_subunit_rounding_value_label
import doobypro.shared.generated.resources.screen_Settings_order_tag_print_mode_label
import doobypro.shared.generated.resources.screen_Settings_order_tax_enabled_label
import doobypro.shared.generated.resources.screen_Settings_order_tax_label
import doobypro.shared.generated.resources.screen_Settings_order_tax_percentage_label
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.decimal.Percentage
import se.supernovait.app.core.domain.model.validation.ValidationPattern
import se.supernovait.app.core.ui.component.container.SupernovaListGroup
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.selection.SupernovaSelectField
import se.supernovait.app.core.ui.component.selection.SupernovaSelectionGroup
import se.supernovait.app.core.ui.component.selection.SupernovaToggle
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.delivery.CollectionMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.settings.order.ExpressFeeMode
import se.supernovait.doobypro.domain.model.settings.order.ExpressFeeType
import se.supernovait.doobypro.domain.model.settings.order.OrderTagPrintMode
import se.supernovait.doobypro.presentation.settings.SettingsState
import se.supernovait.doobypro.presentation.settings.event.SettingsScreenEvent
import kotlin.math.roundToLong

@Composable
fun OrderSettingsScreen(
    uiState: SettingsState,
    onEvent: (SettingsScreenEvent) -> Unit
) {
    val selectedService = uiState.services.find { it.id == uiState.settings.order.defaultServiceId }
    val deliveryOptionLabels = DeliveryOption.entries.associateWith { stringResource(it.label) }
    val deliveryMethodLabels = DeliveryMethod.entries.associateWith { stringResource(it.label) }
    val collectionMethodLabels = CollectionMethod.entries.associateWith { stringResource(it.label) }
    val orderTagPrintModeLabels = OrderTagPrintMode.entries.associateWith { stringResource(it.label) }
    val expressFeeModeLabels = ExpressFeeMode.entries.associateWith { stringResource(it.label) }
    val currency = uiState.settings.common.currency.symbol
    val expressFeeTypeLabels = ExpressFeeType.entries.associateWith { feeType ->
        when (feeType) {
            ExpressFeeType.FLAT_FEE -> stringResource(feeType.label, currency)
            ExpressFeeType.PERCENTAGE -> stringResource(feeType.label)
        }
    }

    val orderSettings = uiState.settings.order

    var deliveryDaysText by remember(orderSettings.defaultDeliveryDaysOffset) {
        mutableStateOf(orderSettings.defaultDeliveryDaysOffset.toString())
    }

    val isExpressPercentage = orderSettings.expressFeeType == ExpressFeeType.PERCENTAGE

    var expressFeeValueText by remember(orderSettings.expressFeeValue, isExpressPercentage) {
        val text = if (isExpressPercentage) {
            Percentage(orderSettings.expressFeeValue.toInt()).percentString
        } else {
            Amount(orderSettings.expressFeeValue, currency).value
        }
        mutableStateOf(text)
    }

    var pickupFeeValueText by remember(orderSettings.pickupFeeValue) {
        mutableStateOf(Amount(orderSettings.pickupFeeValue, currency).value)
    }

    var deliveryFeeValueText by remember(orderSettings.deliveryFeeValue) {
        mutableStateOf(Amount(orderSettings.deliveryFeeValue, currency).value)
    }

    var taxPercentageText by remember(orderSettings.taxPercentage) {
        mutableStateOf(Percentage(orderSettings.taxPercentage.toInt()).percentString)
    }

    val roundingValueFloat = (orderSettings.subunitRoundingValue / 100.0).toFloat()

    SettingsScreen {
        SupernovaLabel(
            text = Res.string.screen_Settings_order_default_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small, bottom = MaterialTheme.spacing.small)
        )

        SupernovaSelectField(
            label = Res.string.screen_Settings_order_default_service_label,
            options = uiState.services,
            selectedOption = selectedService,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateDefaultServiceId(it.id)) },
            optionLabel = { it.title }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaSelectField(
            label = Res.string.screen_Settings_order_delivery_option_label,
            options = DeliveryOption.entries,
            selectedOption = orderSettings.defaultDeliveryOption,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateDefaultDeliveryOption(it)) },
            optionLabel = { deliveryOptionLabels[it] ?: "" }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaSelectField(
            label = Res.string.screen_Settings_order_delivery_method_label,
            options = DeliveryMethod.entries,
            selectedOption = orderSettings.defaultDeliveryMethod,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateDefaultDeliveryMethod(it)) },
            optionLabel = { deliveryMethodLabels[it] ?: "" }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaSelectField(
            label = Res.string.screen_Settings_order_collection_method_label,
            options = CollectionMethod.entries,
            selectedOption = orderSettings.defaultCollectionMethod,
            onOptionSelected = { method -> onEvent(SettingsScreenEvent.UpdateDefaultCollectionMethod(method)) },
            optionLabel = { method -> collectionMethodLabels[method] ?: "" }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaTextField(
            label = stringResource(Res.string.screen_Settings_order_handling_time_label),
            value = deliveryDaysText,
            onValueChange = { newValue, _ ->
                deliveryDaysText = newValue
                newValue.toIntOrNull()?.let {
                    onEvent(SettingsScreenEvent.UpdateDefaultDeliveryDaysOffset(it))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        SupernovaLabel(
            text = Res.string.screen_Settings_order_fees_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )

        SupernovaTextField(
            label = stringResource(Res.string.screen_Settings_order_delivery_fee_value_label, currency),
            value = deliveryFeeValueText,
            onValueChange = { newValue, _ ->
                if (newValue.isEmpty() || newValue.matches(Regex(ValidationPattern.DECIMAL.pattern))) {
                    deliveryFeeValueText = newValue
                    val rawValue = ((newValue.toDoubleOrNull() ?: 0.0) * 100).toLong()
                    onEvent(SettingsScreenEvent.UpdateDeliveryFeeValue(rawValue))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaTextField(
            label = stringResource(Res.string.screen_Settings_order_pickup_fee_value_label, currency),
            value = pickupFeeValueText,
            onValueChange = { newValue, _ ->
                if (newValue.isEmpty() || newValue.matches(Regex(ValidationPattern.DECIMAL.pattern))) {
                    pickupFeeValueText = newValue
                    val rawValue = ((newValue.toDoubleOrNull() ?: 0.0) * 100).toLong()
                    onEvent(SettingsScreenEvent.UpdatePickupFeeValue(rawValue))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaSelectField(
            label = Res.string.screen_Settings_order_express_fee_mode_label,
            options = ExpressFeeMode.entries,
            selectedOption = orderSettings.expressFeeMode,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateExpressFeeMode(it)) },
            optionLabel = { expressFeeModeLabels[it] ?: "" },
        )

        if (orderSettings.expressFeeMode == ExpressFeeMode.GLOBAL) {
            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            SupernovaSelectField(
                label = Res.string.screen_Settings_order_express_fee_type_label,
                options = ExpressFeeType.entries,
                selectedOption = orderSettings.expressFeeType,
                onOptionSelected = { onEvent(SettingsScreenEvent.UpdateExpressFeeType(it)) },
                optionLabel = { expressFeeTypeLabels[it] ?: "" },
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            SupernovaTextField(
                label = stringResource(
                    Res.string.screen_Settings_order_express_fee_value_label,
                    if (isExpressPercentage) "%" else currency,
                ),
                value = expressFeeValueText,
                onValueChange = { newValue, _ ->
                    if (newValue.isEmpty() || newValue.matches(Regex(ValidationPattern.DECIMAL.pattern))) {
                        expressFeeValueText = newValue
                        val rawValue = ((newValue.toDoubleOrNull() ?: 0.0) * 100).roundToLong()
                        onEvent(SettingsScreenEvent.UpdateExpressFeeValue(rawValue))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                ),
            )
        }

        SupernovaLabel(
            text = Res.string.screen_Settings_order_subunit_rounding_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )
        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_order_subunit_rounding_enabled_label,
                    checked = orderSettings.subunitRoundingEnabled,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateSubunitRoundingEnabled(it)) }
                )
            }
            item {
                if (orderSettings.subunitRoundingEnabled) {
                    Column(modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.extraSmall)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SupernovaLabel(
                                text = stringResource(Res.string.screen_Settings_order_subunit_rounding_value_label, currency),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            SupernovaLabel(
                                text = Amount(orderSettings.subunitRoundingValue, currency).formatted,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(MaterialTheme.spacing.extraSmall))
                        Slider(
                            value = roundingValueFloat.coerceIn(0.01f, 0.99f),
                            onValueChange = { newValue ->
                                val rawValue = ((newValue * 100.0).roundToLong()).coerceIn(1L, 99L)
                                onEvent(SettingsScreenEvent.UpdateSubunitRoundingValue(rawValue))
                            },
                            valueRange = 0.01f..0.99f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        SupernovaLabel(
            text = Res.string.screen_Settings_order_tax_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )
        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_order_tax_enabled_label,
                    checked = orderSettings.taxEnabled,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateTaxEnabled(it)) }
                )
            }
            item {
                if (orderSettings.taxEnabled) {
                    Column(modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.extraSmall)) {
                        SupernovaTextField(
                            label = stringResource(Res.string.screen_Settings_order_tax_percentage_label),
                            value = taxPercentageText,
                            onValueChange = { newValue, _ ->
                                if (newValue.isEmpty() || newValue.matches(Regex(ValidationPattern.DECIMAL.pattern))) {
                                    taxPercentageText = newValue
                                    val rawValue = ((newValue.toDoubleOrNull() ?: 0.0) * 100).roundToLong()
                                    onEvent(SettingsScreenEvent.UpdateTaxPercentage(rawValue))
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        SupernovaLabel(
            text = Res.string.screen_Settings_order_behavior_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )
        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_order_auto_print_receipts_label,
                    checked = orderSettings.autoPrintReceipts,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateAutoPrintReceipts(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_order_auto_print_order_tag_label,
                    checked = orderSettings.autoPrintOrderTag,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateAutoPrintOrderTag(it)) }
                )
            }
        }

        if (orderSettings.autoPrintOrderTag) {
            SupernovaLabel(
                text = Res.string.screen_Settings_order_tag_print_mode_label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
            )
            SupernovaSelectionGroup(
                options = OrderTagPrintMode.entries,
                selectedOption = orderSettings.orderTagPrintMode,
                onOptionSelected = { onEvent(SettingsScreenEvent.UpdateOrderTagPrintMode(it)) },
                optionLabel = { orderTagPrintModeLabels[it] ?: "" },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
