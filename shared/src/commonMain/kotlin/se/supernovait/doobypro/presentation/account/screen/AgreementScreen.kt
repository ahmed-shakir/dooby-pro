package se.supernovait.doobypro.presentation.account.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Agreement_error_not_found
import doobypro.shared.generated.resources.screen_Agreement_field_cancellation_date
import doobypro.shared.generated.resources.screen_Agreement_field_fee
import doobypro.shared.generated.resources.screen_Agreement_field_issue_date
import doobypro.shared.generated.resources.screen_Agreement_field_model
import doobypro.shared.generated.resources.screen_Agreement_field_serial
import doobypro.shared.generated.resources.screen_Agreement_field_status
import doobypro.shared.generated.resources.screen_Agreement_section_billing
import doobypro.shared.generated.resources.screen_Agreement_section_equipment
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.account.AccountState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgreementScreen(state: AccountState) {
    val agreement = state.account?.agreement

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium)
            .verticalScroll(rememberScrollState())
    ) {
        if (agreement == null) {
            Text(
                text = stringResource(Res.string.screen_Agreement_error_not_found),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = agreement.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))

            Text(
                text = agreement.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            Text(
                text = stringResource(Res.string.screen_Agreement_section_equipment),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))

            OutlinedTextField(
                label = { Text(stringResource(Res.string.screen_Agreement_field_model)) },
                value = agreement.equipmentModel,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))

            OutlinedTextField(
                label = { Text(stringResource(Res.string.screen_Agreement_field_serial)) },
                value = agreement.equipmentId,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            Text(
                text = stringResource(Res.string.screen_Agreement_section_billing),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))

            OutlinedTextField(
                label = { Text(stringResource(Res.string.screen_Agreement_field_status)) },
                value = agreement.status.name,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))

            OutlinedTextField(
                label = { Text(stringResource(Res.string.screen_Agreement_field_fee, agreement.billingFrequency.name)) },
                value = "${agreement.fee.value} ${agreement.fee.currency}",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))

            OutlinedTextField(
                label = { Text(stringResource(Res.string.screen_Agreement_field_issue_date)) },
                value = agreement.issueDate.toString(),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (agreement.cancellationDate != null) {
                Spacer(Modifier.height(MaterialTheme.spacing.small))
                OutlinedTextField(
                    label = { Text(stringResource(Res.string.screen_Agreement_field_cancellation_date)) },
                    value = agreement.cancellationDate.toString(),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
