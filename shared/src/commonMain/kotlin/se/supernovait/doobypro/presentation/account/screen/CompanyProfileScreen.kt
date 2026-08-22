package se.supernovait.doobypro.presentation.account.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_CompanyProfile_action_save
import doobypro.shared.generated.resources.screen_CompanyProfile_action_saving
import doobypro.shared.generated.resources.screen_CompanyProfile_field_city
import doobypro.shared.generated.resources.screen_CompanyProfile_field_company_id
import doobypro.shared.generated.resources.screen_CompanyProfile_field_country
import doobypro.shared.generated.resources.screen_CompanyProfile_field_display_name
import doobypro.shared.generated.resources.screen_CompanyProfile_field_email
import doobypro.shared.generated.resources.screen_CompanyProfile_field_legal_name
import doobypro.shared.generated.resources.screen_CompanyProfile_field_license_number
import doobypro.shared.generated.resources.screen_CompanyProfile_field_notes
import doobypro.shared.generated.resources.screen_CompanyProfile_field_phone
import doobypro.shared.generated.resources.screen_CompanyProfile_field_postal_code
import doobypro.shared.generated.resources.screen_CompanyProfile_field_street
import doobypro.shared.generated.resources.screen_CompanyProfile_field_subdivision
import doobypro.shared.generated.resources.screen_CompanyProfile_section_address
import doobypro.shared.generated.resources.screen_CompanyProfile_section_contact
import doobypro.shared.generated.resources.screen_CompanyProfile_section_info
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.account.AccountState
import se.supernovait.doobypro.presentation.account.event.AccountEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyProfileScreen(
    state: AccountState,
    onEvent: (AccountEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(Res.string.screen_CompanyProfile_section_info),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        // Read-only Company ID
        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_CompanyProfile_field_company_id)) },
            value = state.account?.company?.id ?: "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_CompanyProfile_field_legal_name)) },
            value = state.editCompanyLegalName,
            onValueChange = { onEvent(AccountEvent.UpdateCompanyLegalName(it)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_CompanyProfile_field_display_name)) },
            value = state.editCompanyDisplayName,
            onValueChange = { onEvent(AccountEvent.UpdateCompanyDisplayName(it)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_CompanyProfile_field_license_number)) },
            value = state.editCompanyLicenseNumber,
            onValueChange = { onEvent(AccountEvent.UpdateCompanyLicenseNumber(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))
        Text(
            text = stringResource(Res.string.screen_CompanyProfile_section_contact),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_CompanyProfile_field_email)) },
            value = state.editCompanyEmail,
            onValueChange = { onEvent(AccountEvent.UpdateCompanyEmail(it)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_CompanyProfile_field_phone)) },
            value = state.editCompanyPhone,
            onValueChange = { onEvent(AccountEvent.UpdateCompanyPhone(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))
        Text(
            text = stringResource(Res.string.screen_CompanyProfile_section_address),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_CompanyProfile_field_street)) },
            value = state.editCompanyAddressStreet,
            onValueChange = { onEvent(AccountEvent.UpdateCompanyAddressStreet(it)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_CompanyProfile_field_city)) },
            value = state.editCompanyAddressCity,
            onValueChange = { onEvent(AccountEvent.UpdateCompanyAddressCity(it)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_CompanyProfile_field_subdivision)) },
            value = state.editCompanyAddressSubdivision,
            onValueChange = { onEvent(AccountEvent.UpdateCompanyAddressSubdivision(it)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_CompanyProfile_field_postal_code)) },
            value = state.editCompanyAddressPostalCode,
            onValueChange = { onEvent(AccountEvent.UpdateCompanyAddressPostalCode(it)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_CompanyProfile_field_country)) },
            value = state.editCompanyAddressCountry,
            onValueChange = { onEvent(AccountEvent.UpdateCompanyAddressCountry(it)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_CompanyProfile_field_notes)) },
            value = state.editCompanyNotes,
            onValueChange = { onEvent(AccountEvent.UpdateCompanyNotes(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.large))

        Button(
            onClick = { onEvent(AccountEvent.SaveCompanyProfile) },
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (state.isSaving) stringResource(Res.string.screen_CompanyProfile_action_saving)
                else stringResource(Res.string.screen_CompanyProfile_action_save)
            )
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))
    }
}
