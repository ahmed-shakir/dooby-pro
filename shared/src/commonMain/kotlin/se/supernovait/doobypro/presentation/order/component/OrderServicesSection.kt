package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Order_empty_services
import doobypro.shared.generated.resources.screen_Order_field_service
import doobypro.shared.generated.resources.screen_Order_label_delivery_fee
import doobypro.shared.generated.resources.screen_Order_label_express_fee
import doobypro.shared.generated.resources.screen_Order_label_express_fee_with_percentage
import doobypro.shared.generated.resources.screen_Order_label_subtotal
import doobypro.shared.generated.resources.screen_Order_label_subunit_rounding
import doobypro.shared.generated.resources.screen_Order_label_total_price
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.decimal.Percentage
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.AppDefaults
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.settings.order.ExpressFeeType

/**
 * Renders the services section of an order in a clean, receipt/invoice layout.
 *
 * Each line item displays the service title and line subtotal on the main line, with
 * quantity and unit price on the secondary line to comfortably accommodate long service names
 * and large monetary values. Below the line items, fees (express or delivery) are shown
 * if applicable, followed by the grand total price.
 *
 * @param order The [Order] containing items, express fee, delivery fee, and total price.
 * @param expressFeeType The fallback [ExpressFeeType] used if the order has no stored express fee type.
 * @param expressFeeValue The fallback express fee percentage or value setting.
 * @param modifier Optional [Modifier] to be applied to the layout.
 */
@Composable
fun OrderServicesSection(
    order: Order,
    expressFeeType: ExpressFeeType = ExpressFeeType.PERCENTAGE,
    expressFeeValue: Long = 15,
    modifier: Modifier = Modifier,
) {
    val items = order.items
    val currency = items.firstOrNull()?.service?.price?.currency ?: AppDefaults.CURRENCY
    val itemsSubtotal = Amount(items.sumOf { it.subtotal.raw }, currency)
    val expressFeeAmount = order.expressFee?.takeIf { it.raw > 0 }
    val deliveryFeeAmount = order.deliveryFee?.takeIf { it.raw > 0 }
    val subunitRoundingAmount = order.subunitRounding?.takeIf { it.raw != 0L }
    val hasExtras = (expressFeeAmount != null) || (deliveryFeeAmount != null) || (subunitRoundingAmount != null)

    Column(modifier = modifier.fillMaxWidth()) {
        SupernovaLabel(
            text = stringResource(Res.string.screen_Order_field_service),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.small),
        )

        if (items.isEmpty()) {
            SupernovaLabel(
                text = stringResource(Res.string.screen_Order_empty_services),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.small,
                    )
                    .padding(MaterialTheme.spacing.medium),
            ) {
                // Item rows
                items.forEachIndexed { index, item ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            thickness = MaterialTheme.spacing.divider,
                            modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall, Alignment.CenterVertically),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = MaterialTheme.spacing.extraSmall),
                    ) {
                        SupernovaLabel(
                            text = item.service.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            SupernovaLabel(
                                text = "${item.quantity} × ${item.service.price.formatted}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            SupernovaLabel(
                                text = item.subtotal.formatted,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                // Breakdown & Totals Section
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = MaterialTheme.spacing.divider,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.small, bottom = MaterialTheme.spacing.small),
                )

                // Subtotal & Fee Rows (only if there are fees or rounding)
                if (hasExtras) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = MaterialTheme.spacing.extraSmall),
                    ) {
                        SupernovaLabel(
                            text = stringResource(Res.string.screen_Order_label_subtotal),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        SupernovaLabel(
                            text = itemsSubtotal.formatted,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    // Express Fee Row
                    if (expressFeeAmount != null) {
                        val activeExpressFeeType = order.expressFeeType ?: expressFeeType
                        val expressFeeLabel = if (activeExpressFeeType == ExpressFeeType.PERCENTAGE) {
                            val pctText = if (order.expressFeePercentage != null) {
                                order.expressFeePercentage.formatted
                            } else if (itemsSubtotal.raw > 0) {
                                val calcPct = (expressFeeAmount.raw.toDouble() * 100.0) / itemsSubtotal.raw.toDouble()
                                Percentage.fromPercent(calcPct).formatted
                            } else {
                                Percentage.fromPercent(expressFeeValue.toDouble()).formatted
                            }

                            stringResource(Res.string.screen_Order_label_express_fee_with_percentage, pctText)
                        } else {
                            stringResource(Res.string.screen_Order_label_express_fee)
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = MaterialTheme.spacing.extraSmall),
                        ) {
                            SupernovaLabel(
                                text = expressFeeLabel,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            SupernovaLabel(
                                text = expressFeeAmount.formatted,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }

                    // Delivery Fee Row
                    if (deliveryFeeAmount != null) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = MaterialTheme.spacing.extraSmall),
                        ) {
                            SupernovaLabel(
                                text = stringResource(Res.string.screen_Order_label_delivery_fee),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            SupernovaLabel(
                                text = deliveryFeeAmount.formatted,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }

                    // Subunit Rounding Row
                    if (subunitRoundingAmount != null) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = MaterialTheme.spacing.extraSmall),
                        ) {
                            SupernovaLabel(
                                text = stringResource(Res.string.screen_Order_label_subunit_rounding),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            SupernovaLabel(
                                text = subunitRoundingAmount.formatted,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = MaterialTheme.spacing.divider,
                        modifier = Modifier.padding(top = MaterialTheme.spacing.small, bottom = MaterialTheme.spacing.small),
                    )
                }

                // Grand Total Row
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = MaterialTheme.spacing.extraSmall),
                ) {
                    SupernovaLabel(
                        text = stringResource(Res.string.screen_Order_label_total_price),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    SupernovaLabel(
                        text = order.totalPrice.formatted,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
