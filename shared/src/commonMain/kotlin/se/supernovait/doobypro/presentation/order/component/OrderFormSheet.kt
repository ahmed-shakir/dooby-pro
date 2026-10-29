package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.app_action_close_label
import doobypro.shared.generated.resources.ic_add
import doobypro.shared.generated.resources.ic_delete
import doobypro.shared.generated.resources.ic_edit_square
import doobypro.shared.generated.resources.label_customize
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.label_edit
import doobypro.shared.generated.resources.label_save
import doobypro.shared.generated.resources.screen_Order_action_add_order
import doobypro.shared.generated.resources.screen_Order_action_create_order
import doobypro.shared.generated.resources.screen_Order_empty_services
import doobypro.shared.generated.resources.screen_Order_field_collection_method
import doobypro.shared.generated.resources.screen_Order_field_customer
import doobypro.shared.generated.resources.screen_Order_field_delivery_date
import doobypro.shared.generated.resources.screen_Order_field_delivery_fee
import doobypro.shared.generated.resources.screen_Order_field_delivery_method
import doobypro.shared.generated.resources.screen_Order_field_delivery_option
import doobypro.shared.generated.resources.screen_Order_field_discount_flat_amount
import doobypro.shared.generated.resources.screen_Order_field_discount_percentage
import doobypro.shared.generated.resources.screen_Order_field_express_fee
import doobypro.shared.generated.resources.screen_Order_field_express_fee_percentage
import doobypro.shared.generated.resources.screen_Order_field_notes
import doobypro.shared.generated.resources.screen_Order_field_payment_status
import doobypro.shared.generated.resources.screen_Order_field_pickup_fee
import doobypro.shared.generated.resources.screen_Order_field_service
import doobypro.shared.generated.resources.screen_Order_field_storage
import doobypro.shared.generated.resources.screen_Order_header_item
import doobypro.shared.generated.resources.screen_Order_header_price
import doobypro.shared.generated.resources.screen_Order_header_subtotal
import doobypro.shared.generated.resources.screen_Order_warning_missing_customer_address
import doobypro.shared.generated.resources.screen_Service_action_add_service
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.decimal.Decimal
import se.supernovait.app.core.domain.model.decimal.Percentage
import se.supernovait.app.core.domain.model.validation.ValidationPattern
import se.supernovait.app.core.ui.component.action.QuantityControl
import se.supernovait.app.core.ui.component.action.SupernovaIconButton
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.action.SupernovaTextAction
import se.supernovait.app.core.ui.component.action.SupernovaTextButton
import se.supernovait.app.core.ui.component.input.SupernovaDatetimeField
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.message.NoticeVariant
import se.supernovait.app.core.ui.component.message.SupernovaNotice
import se.supernovait.app.core.ui.component.modal.LocalBottomSheetState
import se.supernovait.app.core.ui.component.modal.dialog.LocalDialogState
import se.supernovait.app.core.ui.component.selection.SupernovaSelectField
import se.supernovait.app.core.ui.component.selection.SupernovaToggle
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.sizing
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.delivery.CollectionMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.DiscountType
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.settings.order.ExpressFeeMode
import se.supernovait.doobypro.domain.model.settings.order.ExpressFeeType
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.presentation.app.theme.statusColor
import kotlin.math.roundToLong

/**
 * Bottom sheet form for creating or editing an order.
 *
 * @param order The [Order] being created or edited.
 * @param customers List of available [User] customers.
 * @param services List of available [Service] catalog items.
 * @param storageLocations List of available [StorageLocation] storage areas.
 * @param isManualStorageMode Whether manual storage location selection is enabled.
 * @param expressFeeMode The current express fee mode from settings.
 * @param expressFeeType The current express fee calculation type from settings.
 * @param globalExpressFee The global express fee value from settings.
 * @param globalDeliveryFee The global delivery fee value from settings.
 * @param currency The currency symbol.
 * @param onSave Callback invoked when the user saves the order.
 * @param onDelete Optional callback invoked when the user deletes the order.
 */
@Composable
fun OrderFormSheet(
    order: Order,
    customers: List<User>,
    services: List<Service>,
    storageLocations: List<StorageLocation>,
    isManualStorageMode: Boolean,
    expressFeeMode: ExpressFeeMode,
    expressFeeType: ExpressFeeType,
    globalExpressFee: Long,
    globalDeliveryFee: Long,
    globalPickupFee: Long,
    subunitRoundingEnabled: Boolean,
    subunitRoundingValue: Long,
    currency: String,
    onSave: (Order) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val dialogState = LocalDialogState.current
    val bottomSheetState = LocalBottomSheetState.current

    val isNew = order.id == null
    val showDelete = onDelete != null && !isNew

    var selectedCustomer by remember { mutableStateOf(order.customer) }
    var orderItems by remember { mutableStateOf(order.items) }
    var selectedLocation by remember { mutableStateOf(order.storageLocation) }
    var deliveryDatetime by remember { mutableStateOf(order.deliveryDatetime) }
    var deliveryOption by remember { mutableStateOf(order.deliveryOption) }
    var deliveryMethod by remember { mutableStateOf(order.deliveryMethod) }
    var collectionMethod by remember { mutableStateOf(order.collectionMethod) }
    var isPaymentDone by remember { mutableStateOf(order.isPaymentDone) }

    var discountType by remember { mutableStateOf(order.discountType) }
    var discountText by remember {
        val initialText = when (order.discountType) {
            DiscountType.PERCENTAGE -> order.discountPercentage?.percentString ?: order.discount?.let { discount ->
                val subtotal = order.items.fold(Decimal.ZERO) { acc, item -> acc + item.subtotal.decimal }
                if (subtotal > Decimal.ZERO) {
                    val calcPct = discount / subtotal
                    calcPct.decimal.toPercentage().percentString
                } else null
            } ?: ""
            DiscountType.FLAT_AMOUNT -> order.discount?.value ?: ""
        }
        mutableStateOf(initialText)
    }

    val activeExpressFeeType = if (!isNew) (order.expressFeeType ?: expressFeeType) else expressFeeType

    var isCustomized by remember {
        mutableStateOf(!isNew || (order.expressFee != null && order.expressFee.raw != globalExpressFee))
    }

    var expressFeeText by remember {
        val initialText = when {
            !isNew -> {
                if (activeExpressFeeType == ExpressFeeType.PERCENTAGE) {
                    order.expressFeePercentage?.percentString ?: order.expressFee?.let { fee ->
                        val subtotal = order.items.fold(Decimal.ZERO) { acc, item -> acc + item.subtotal.decimal }
                        if (subtotal > Decimal.ZERO) {
                            val calcPct = fee / subtotal
                            calcPct.decimal.toPercentage().percentString
                        } else null
                    } ?: ""
                } else {
                    order.expressFee?.value ?: ""
                }
            }
            order.expressFee != null && (expressFeeMode == ExpressFeeMode.INDIVIDUAL || isCustomized) -> {
                if (activeExpressFeeType == ExpressFeeType.PERCENTAGE) {
                    order.expressFeePercentage?.percentString ?: order.expressFee.value
                } else {
                    order.expressFee.value
                }
            }
            expressFeeMode == ExpressFeeMode.GLOBAL && activeExpressFeeType == ExpressFeeType.PERCENTAGE -> {
                Percentage(globalExpressFee.toInt()).percentString
            }
            expressFeeMode == ExpressFeeMode.GLOBAL -> Amount(globalExpressFee, currency).value
            else -> ""
        }
        mutableStateOf(initialText)
    }
    var notes by remember { mutableStateOf(order.notes ?: "") }

    val deliveryOptionLabels = DeliveryOption.entries.associateWith { stringResource(it.label) }
    val deliveryMethodLabels = DeliveryMethod.entries.associateWith { stringResource(it.label) }
    val collectionMethodLabels = CollectionMethod.entries.associateWith { stringResource(it.label) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(MaterialTheme.spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SupernovaTitle(
                text = if (isNew) stringResource(Res.string.screen_Order_action_add_order) else stringResource(Res.string.label_edit),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
            SupernovaTextAction(
                label = Res.string.app_action_close_label,
                textStyle = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                onClick = { bottomSheetState.hide() }
            )
        }
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // If customer has no registered location, show attention warning message if either home service is selected (sticky at top of everything)
        if ((collectionMethod == CollectionMethod.HOME_PICKUP || deliveryMethod == DeliveryMethod.HOME_DELIVERY) && !selectedCustomer.hasRegisteredAddress) {
            SupernovaNotice(
                textResource = Res.string.screen_Order_warning_missing_customer_address,
                variant = NoticeVariant.Error
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. customer
            SupernovaSelectField(
                label = Res.string.screen_Order_field_customer,
                options = customers,
                selectedOption = selectedCustomer,
                onOptionSelected = { selectedCustomer = it },
                optionLabel = { "${it.firstname} ${it.lastname}" }
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            // 2. Services section with "+ Add Service" action and added items list
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SupernovaLabel(
                    text = Res.string.screen_Order_field_service,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                SupernovaTextAction(
                    label = Res.string.screen_Service_action_add_service,
                    icon = Res.drawable.ic_add,
                    textStyle = MaterialTheme.typography.labelMedium,
                    onClick = {
                        dialogState.showCustom(
                            onDismiss = { dialogState.hide() },
                            content = {
                                AddServiceDialog(
                                    services = services,
                                    existingItems = orderItems,
                                    onDismiss = { dialogState.hide() },
                                    onAddServices = { newItems ->
                                        orderItems = orderItems + newItems
                                        dialogState.hide()
                                    }
                                )
                            }
                        )
                    }
                )
            }

            Spacer(Modifier.height(MaterialTheme.spacing.small))

            if (orderItems.isEmpty()) {
                SupernovaLabel(
                    text = stringResource(Res.string.screen_Order_empty_services),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.small)
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), MaterialTheme.shapes.small)
                        .padding(MaterialTheme.spacing.medium)
                ) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.spacing.extraSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SupernovaLabel(
                            text = stringResource(Res.string.screen_Order_header_item),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1.7f)
                        )
                        SupernovaLabel(
                            text = stringResource(Res.string.screen_Order_header_price),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1.1f)
                        )
                        SupernovaLabel(
                            text = stringResource(Res.string.screen_Order_header_subtotal),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1.2f)
                        )
                        Spacer(Modifier.width(32.dp)) // space for delete icon
                    }

                    orderItems.forEachIndexed { index, item ->
                        if (index > 0) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                thickness = MaterialTheme.spacing.divider,
                                modifier = Modifier.padding(bottom = MaterialTheme.spacing.extraSmall)
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            SupernovaLabel(
                                text = item.service.title,
                                style = MaterialTheme.typography.bodyMediumEmphasized,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(MaterialTheme.spacing.extraSmall))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Reusable Quantity Control component
                                QuantityControl(
                                    quantity = item.quantity,
                                    onQuantityChange = { newQty ->
                                        orderItems = orderItems.toMutableList().also {
                                            it[index] = item.copy(quantity = newQty)
                                        }
                                    },
                                    style = MaterialTheme.typography.labelSmallEmphasized,
                                    modifier = Modifier.weight(1.5f)
                                )

                                // Price
                                SupernovaLabel(
                                    text = item.service.price.formatted,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )

                                // Subtotal
                                SupernovaLabel(
                                    text = item.subtotal.formatted,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1.2f)
                                )

                                // Remove action
                                SupernovaIconButton(
                                    icon = Res.drawable.ic_delete,
                                    contentDescription = Res.string.label_delete,
                                    tint = MaterialTheme.statusColor.error,
                                    modifier = Modifier.size(MaterialTheme.sizing.icon.small),
                                    onClick = {
                                        orderItems = orderItems.filterIndexed { i, _ -> i != index }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            // 3. storageLocation
            SupernovaSelectField(
                label = Res.string.screen_Order_field_storage,
                options = storageLocations,
                selectedOption = selectedLocation,
                onOptionSelected = { selectedLocation = it },
                optionLabel = { it.label },
                enabled = isManualStorageMode || !isNew
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            // 4. deliveryDatetime
            SupernovaDatetimeField(
                label = stringResource(Res.string.screen_Order_field_delivery_date),
                value = deliveryDatetime,
                onValueChange = { datetime, isValid ->
                    if (isValid && datetime != null) {
                        deliveryDatetime = datetime
                    }
                }
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            // 5. deliveryOption
            SupernovaSelectField(
                label = Res.string.screen_Order_field_delivery_option,
                options = DeliveryOption.entries,
                selectedOption = deliveryOption,
                onOptionSelected = { deliveryOption = it },
                optionLabel = { deliveryOptionLabels[it] ?: it.name }
            )

            // Express Fee Type is fetched from settings for new orders, or preserved from existing orders.
            if (deliveryOption == DeliveryOption.EXPRESS) {
                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                val isGlobalMode = expressFeeMode == ExpressFeeMode.GLOBAL
                val isFieldEnabled = !isNew || !isGlobalMode || isCustomized
                val displayValue = if (isNew && isGlobalMode && !isCustomized) {
                    if (activeExpressFeeType == ExpressFeeType.PERCENTAGE) {
                        Percentage(globalExpressFee.toInt()).percentString
                    } else {
                        Amount(globalExpressFee, currency).value
                    }
                } else {
                    expressFeeText
                }

                val expressFeeLabel = if (activeExpressFeeType == ExpressFeeType.PERCENTAGE) {
                    stringResource(Res.string.screen_Order_field_express_fee_percentage)
                } else {
                    stringResource(Res.string.screen_Order_field_express_fee, currency)
                }

                if (isNew && isGlobalMode && !isCustomized) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = MaterialTheme.spacing.small),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        SupernovaTextAction(
                            label = Res.string.label_customize,
                            icon = Res.drawable.ic_edit_square,
                            textStyle = MaterialTheme.typography.labelMedium,
                            onClick = {
                                isCustomized = true
                                expressFeeText = if (activeExpressFeeType == ExpressFeeType.PERCENTAGE) {
                                    Percentage(globalExpressFee.toInt()).percentString
                                } else {
                                    Amount(globalExpressFee, currency).value
                                }
                            }
                        )
                    }
                }

                SupernovaTextField(
                    label = expressFeeLabel,
                    value = displayValue,
                    onValueChange = { v, _ ->
                        if (v.isEmpty() || v.matches(Regex(ValidationPattern.DECIMAL.pattern))) {
                            expressFeeText = v
                        }
                    },
                    enabled = isFieldEnabled,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            // 6. collectionMethod
            SupernovaSelectField(
                label = Res.string.screen_Order_field_collection_method,
                options = CollectionMethod.entries,
                selectedOption = collectionMethod,
                onOptionSelected = { method -> collectionMethod = method },
                optionLabel = { method -> collectionMethodLabels[method] ?: method.name }
            )

            val pickupFeeRaw = if (!isNew && order.pickupFee != null) order.pickupFee.raw else globalPickupFee
            if (collectionMethod == CollectionMethod.HOME_PICKUP && pickupFeeRaw > 0L) {
                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                SupernovaTextField(
                    label = stringResource(Res.string.screen_Order_field_pickup_fee, currency),
                    value = Amount(globalPickupFee, currency).value,
                    onValueChange = { _, _ -> },
                    enabled = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            // 7. deliveryMethod
            SupernovaSelectField(
                label = Res.string.screen_Order_field_delivery_method,
                options = DeliveryMethod.entries,
                selectedOption = deliveryMethod,
                onOptionSelected = { deliveryMethod = it },
                optionLabel = { deliveryMethodLabels[it] ?: it.name }
            )

            val deliveryFeeRaw = if (!isNew && order.deliveryFee != null) order.deliveryFee.raw else globalDeliveryFee
            if (deliveryMethod == DeliveryMethod.HOME_DELIVERY && deliveryFeeRaw > 0L) {
                Spacer(Modifier.height(MaterialTheme.spacing.medium))

                SupernovaTextField(
                    label = stringResource(Res.string.screen_Order_field_delivery_fee, currency),
                    value = Amount(globalDeliveryFee, currency).value,
                    onValueChange = { _, _ -> },
                    enabled = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            // Discount Section as one unified unit (OutlinedTextField with trailing toggle button)
            val discountLabel = if (discountType == DiscountType.PERCENTAGE) {
                stringResource(Res.string.screen_Order_field_discount_percentage)
            } else {
                stringResource(Res.string.screen_Order_field_discount_flat_amount, currency)
            }

            val toggleLabel = if (discountType == DiscountType.PERCENTAGE) currency else "%"

            SupernovaTextField(
                label = discountLabel,
                value = discountText,
                trailingIcon = {
                    SupernovaTextButton(
                        label = toggleLabel,
                        color = MaterialTheme.colorScheme.primary,
                        textStyle = MaterialTheme.typography.labelMediumEmphasized,
                        onClick = {
                            discountType = if (discountType == DiscountType.PERCENTAGE) {
                                DiscountType.FLAT_AMOUNT
                            } else {
                                DiscountType.PERCENTAGE
                            }
                        }
                    )
                },
                onValueChange = { v, _ ->
                    if (v.isEmpty() || v.matches(Regex(ValidationPattern.DECIMAL.pattern))) {
                        discountText = v
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            // 7. notes
            SupernovaTextField(
                label = stringResource(Res.string.screen_Order_field_notes),
                value = notes,
                onValueChange = { v, _ -> notes = v },
                isMultiline = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            // 8. isPaymentDone
            SupernovaToggle(
                label = Res.string.screen_Order_field_payment_status,
                checked = isPaymentDone,
                onCheckedChange = { isPaymentDone = it }
            )

            Spacer(Modifier.height(MaterialTheme.spacing.large))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                if (showDelete) {
                    SupernovaOutlinedButton(
                        label = Res.string.label_delete,
                        onClick = onDelete,
                        shape = MaterialTheme.shapes.extraSmall,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.statusColor.error),
                        modifier = Modifier.weight(1f)
                    )
                }

                val isExpressValid = deliveryOption != DeliveryOption.EXPRESS ||
                    (!isNew) ||
                    (expressFeeMode == ExpressFeeMode.GLOBAL && !isCustomized) ||
                    (expressFeeText.isNotBlank() && expressFeeText.toDoubleOrNull() != null)

                val isValid = selectedCustomer.id != null && orderItems.isNotEmpty() && isExpressValid

                SupernovaOutlinedButton(
                    label = if (isNew) Res.string.screen_Order_action_create_order else Res.string.label_save,
                    onClick = {
                        val itemsSubtotal = orderItems.fold(Decimal.ZERO) { acc, item -> acc + item.subtotal.decimal }
                        val (calculatedExpressFee, savedExpressFeeType, savedExpressFeePercentage) = if (deliveryOption == DeliveryOption.EXPRESS) {
                            if (!isNew) {
                                // Existing order: Express fee type is never changeable and settings are not reflected.
                                val expressFeeInput = expressFeeText.toDoubleOrNull()
                                if (activeExpressFeeType == ExpressFeeType.PERCENTAGE) {
                                    if (expressFeeInput != null) {
                                        val percentage = Percentage.fromPercent(expressFeeInput)
                                        val feeAmount = Amount(itemsSubtotal, currency) * percentage
                                        Triple(feeAmount, ExpressFeeType.PERCENTAGE, percentage)
                                    } else if (order.expressFeePercentage != null) {
                                        val feeAmountDecimal = itemsSubtotal * order.expressFeePercentage
                                        val feeAmount = Amount(feeAmountDecimal, currency)
                                        Triple(feeAmount, ExpressFeeType.PERCENTAGE, order.expressFeePercentage)
                                    } else {
                                        Triple(order.expressFee, ExpressFeeType.PERCENTAGE, null)
                                    }
                                } else {
                                    val feeAmountRaw = if (expressFeeInput != null) {
                                        (expressFeeInput * 100.0).roundToLong()
                                    } else {
                                        order.expressFee?.raw ?: 0L
                                    }
                                    val feeAmount = Amount(feeAmountRaw, currency)
                                    Triple(feeAmount, ExpressFeeType.FLAT_FEE, null)
                                }
                            } else {
                                // New order: Express fee type is fetched from settings. Settings defaults apply if global mode and not customized.
                                if (expressFeeMode == ExpressFeeMode.GLOBAL && !isCustomized) {
                                    if (expressFeeType == ExpressFeeType.PERCENTAGE) {
                                        val percentage = Percentage.fromBasisPoints(globalExpressFee.toInt())
                                        val feeAmount = Amount(itemsSubtotal, currency) * percentage
                                        Triple(feeAmount, ExpressFeeType.PERCENTAGE, percentage)
                                    } else {
                                        val feeAmount = Amount(globalExpressFee, currency)
                                        Triple(feeAmount, ExpressFeeType.FLAT_FEE, null)
                                    }
                                } else {
                                    val expressFeeInput = expressFeeText.toDoubleOrNull()
                                    if (expressFeeType == ExpressFeeType.PERCENTAGE) {
                                        val pctDouble = expressFeeInput ?: (globalExpressFee / 100.0)
                                        val percentage = Percentage.fromPercent(pctDouble)
                                        val feeAmount = Amount(itemsSubtotal, currency) * percentage
                                        Triple(feeAmount, ExpressFeeType.PERCENTAGE, percentage)
                                    } else {
                                        val feeAmountRaw = if (expressFeeInput != null) {
                                            (expressFeeInput * 100.0).roundToLong()
                                        } else {
                                            globalExpressFee
                                        }
                                        val feeAmount = Amount(feeAmountRaw, currency)
                                        Triple(feeAmount, ExpressFeeType.FLAT_FEE, null)
                                    }
                                }
                            }
                        } else {
                            Triple<Amount?, ExpressFeeType?, Percentage?>(null, null, null)
                        }

                        val calculatedPickupFee = if (collectionMethod == CollectionMethod.HOME_PICKUP) {
                            if (!isNew && order.pickupFee != null) {
                                order.pickupFee
                            } else {
                                Amount(globalPickupFee, currency)
                            }
                        } else {
                            null
                        }

                        val calculatedDeliveryFee = if (deliveryMethod == DeliveryMethod.HOME_DELIVERY) {
                            if (!isNew && order.deliveryFee != null) {
                                order.deliveryFee
                            } else {
                                Amount(globalDeliveryFee, currency)
                            }
                        } else {
                            null
                        }

                        val discountInput = discountText.toDoubleOrNull()
                        val (calculatedDiscount, savedDiscountType, savedDiscountPercentage) = if (discountInput != null && discountInput > 0.0) {
                            if (discountType == DiscountType.PERCENTAGE) {
                                val percentage = Percentage.fromPercent(discountInput)
                                val discountAmount = Amount(itemsSubtotal, currency) * percentage
                                Triple(discountAmount, DiscountType.PERCENTAGE, percentage)
                            } else {
                                val discountRaw = (discountInput * 100.0).roundToLong()
                                val discountAmount = Amount(discountRaw, currency)
                                Triple(discountAmount, DiscountType.FLAT_AMOUNT, null)
                            }
                        } else {
                            Triple<Amount?, DiscountType, Percentage?>(null, DiscountType.FLAT_AMOUNT, null)
                        }

                        val discount = calculatedDiscount?.decimal ?: Decimal.ZERO
                        val baseTotal = itemsSubtotal + (calculatedExpressFee?.decimal ?: Decimal.ZERO) + (calculatedDeliveryFee?.decimal ?: Decimal.ZERO) + (calculatedPickupFee?.decimal ?: Decimal.ZERO) - discount
                        val calculatedSubunitRounding = if (subunitRoundingEnabled && subunitRoundingValue > 0L) {
                            val subunitRounding = Decimal(subunitRoundingValue)
                            val remainder = baseTotal % subunitRounding
                            // TODO: update when Supernova Core v1.14.8 is used
                            val rounded = when {
                                remainder == Decimal.ZERO -> baseTotal
                                remainder.unscaled <= subunitRoundingValue / 2.0 -> baseTotal - remainder
                                else -> baseTotal + (subunitRounding - remainder)
                            }
                            val roundingDiff = rounded - baseTotal
                            if (roundingDiff != Decimal.ZERO) Amount(roundingDiff, currency) else null
                        } else {
                            if (!isNew) order.subunitRounding else null
                        }

                        onSave(order.copy(
                            customer = selectedCustomer,
                            items = orderItems,
                            isPaymentDone = isPaymentDone,
                            storageLocation = selectedLocation,
                            deliveryDatetime = deliveryDatetime,
                            deliveryOption = deliveryOption,
                            deliveryMethod = deliveryMethod,
                            collectionMethod = collectionMethod,
                            expressFeeType = savedExpressFeeType,
                            expressFeePercentage = savedExpressFeePercentage,
                            expressFee = calculatedExpressFee,
                            deliveryFee = calculatedDeliveryFee,
                            pickupFee = calculatedPickupFee,
                            subunitRounding = calculatedSubunitRounding,
                            discountType = savedDiscountType,
                            discountPercentage = savedDiscountPercentage,
                            discount = calculatedDiscount,
                            notes = notes.ifBlank { null }
                        ))
                    },
                    enabled = isValid,
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = if (showDelete) Modifier.weight(1f) else Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(MaterialTheme.spacing.x5Large))
        }
    }
}
