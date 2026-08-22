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
import doobypro.shared.generated.resources.screen_License_error_not_found
import doobypro.shared.generated.resources.screen_License_field_expiry_date
import doobypro.shared.generated.resources.screen_License_field_id
import doobypro.shared.generated.resources.screen_License_field_issue_date
import doobypro.shared.generated.resources.screen_License_field_status
import doobypro.shared.generated.resources.screen_License_field_tier
import doobypro.shared.generated.resources.screen_License_section_description
import doobypro.shared.generated.resources.screen_License_section_details
import doobypro.shared.generated.resources.screen_License_section_validity
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.account.AccountState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseScreen(state: AccountState) {
    val license = state.account?.license

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium)
            .verticalScroll(rememberScrollState())
    ) {
        if (license == null) {
            Text(
                text = stringResource(Res.string.screen_License_error_not_found),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Text(
                text = stringResource(Res.string.screen_License_section_details),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))

            OutlinedTextField(
                label = { Text(stringResource(Res.string.screen_License_field_id)) },
                value = license.id,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))

            OutlinedTextField(
                label = { Text(stringResource(Res.string.screen_License_field_tier)) },
                value = license.tier.name,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))

            OutlinedTextField(
                label = { Text(stringResource(Res.string.screen_License_field_status)) },
                value = license.licenseStatus.name,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            Text(
                text = stringResource(Res.string.screen_License_section_validity),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))

            OutlinedTextField(
                label = { Text(stringResource(Res.string.screen_License_field_issue_date)) },
                value = license.issueDate.toString(),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))

            OutlinedTextField(
                label = { Text(stringResource(Res.string.screen_License_field_expiry_date)) },
                value = license.expiryDate.toString(),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            Text(
                text = stringResource(Res.string.screen_License_section_description),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))

            Text(
                text = license.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
