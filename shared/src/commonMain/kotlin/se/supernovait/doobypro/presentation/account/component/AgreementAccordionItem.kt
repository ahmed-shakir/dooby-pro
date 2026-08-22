package se.supernovait.doobypro.presentation.account.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import doobypro.shared.generated.resources.Account_AgreementTab_billing_frequency_annual
import doobypro.shared.generated.resources.Account_AgreementTab_billing_frequency_monthly
import doobypro.shared.generated.resources.Account_AgreementTab_field_status
import doobypro.shared.generated.resources.Account_AgreementTab_label_cancellation_date
import doobypro.shared.generated.resources.Account_AgreementTab_label_equipment
import doobypro.shared.generated.resources.Account_AgreementTab_label_id
import doobypro.shared.generated.resources.Account_AgreementTab_label_issue_date
import doobypro.shared.generated.resources.Account_AgreementTab_label_lease_fee
import doobypro.shared.generated.resources.Account_AgreementTab_label_model
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_arrow_down
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.domain.model.billing.BillingFrequency
import se.supernovait.app.core.ui.component.SupernovaIcon
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTag
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.app.core.ui.theme.statusColor
import se.supernovait.doobypro.domain.model.agreement.Agreement
import se.supernovait.doobypro.domain.model.agreement.AgreementStatus

@Composable
fun AgreementAccordionItem(
    agreement: Agreement,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val statusColor = when (agreement.status) {
        AgreementStatus.ACTIVE -> MaterialTheme.statusColor.success
        AgreementStatus.CANCELLED -> MaterialTheme.statusColor.error
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .border(
                width = MaterialTheme.spacing.divider,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.small
            )
            .animateContentSize()
    ) {
        // Header - clickable
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .background(MaterialTheme.colorScheme.surface)
                .padding(MaterialTheme.spacing.medium)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Title
                SupernovaLabel(
                    text = agreement.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SupernovaLabel(
                        text = agreement.equipmentModel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(MaterialTheme.spacing.small))
                    SupernovaTag(
                        text = agreement.status.name.uppercase(),
                        containerColor = statusColor.copy(alpha = 0.12f),
                        contentColor = statusColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            Spacer(Modifier.width(MaterialTheme.spacing.medium))
            SupernovaIcon(
                icon = Res.drawable.ic_arrow_down,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
            )
        }

        // Content - expandable
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(MaterialTheme.spacing.medium)
            ) {
                Column {
                    AgreementDetailRow(
                        label = stringResource(Res.string.Account_AgreementTab_label_id),
                        value = agreement.id ?: "—"
                    )
                    Spacer(Modifier.height(12.dp))
                    AgreementDetailRow(
                        label = stringResource(Res.string.Account_AgreementTab_field_status),
                        value = agreement.status.name,
                        valueColor = statusColor
                    )
                    Spacer(Modifier.height(12.dp))
                    AgreementDetailRow(
                        label = stringResource(Res.string.Account_AgreementTab_label_equipment),
                        value = agreement.equipmentId
                    )
                    Spacer(Modifier.height(12.dp))
                    AgreementDetailRow(
                        label = stringResource(Res.string.Account_AgreementTab_label_model),
                        value = agreement.equipmentModel
                    )
                    Spacer(Modifier.height(12.dp))
                    val frequency = when (agreement.billingFrequency) {
                        BillingFrequency.Annual -> stringResource(Res.string.Account_AgreementTab_billing_frequency_annual)
                        BillingFrequency.Monthly -> stringResource(Res.string.Account_AgreementTab_billing_frequency_monthly)
                    }
                    AgreementDetailRow(
                        label = stringResource(Res.string.Account_AgreementTab_label_lease_fee),
                        value = "${agreement.fee.formatted}/$frequency"
                    )
                    Spacer(Modifier.height(12.dp))
                    AgreementDetailRow(
                        label = stringResource(Res.string.Account_AgreementTab_label_issue_date),
                        value = agreement.issueDate.toString()
                    )
                    agreement.cancellationDate?.let { date ->
                        Spacer(Modifier.height(12.dp))
                        AgreementDetailRow(
                            label = stringResource(Res.string.Account_AgreementTab_label_cancellation_date),
                            value = date.toString()
                        )
                    }
                }
            }
        }
    }
}
