package se.supernovait.doobypro.presentation.account.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import doobypro.shared.generated.resources.Account_CompanyProfileTab_action_change_logo
import doobypro.shared.generated.resources.Account_CompanyProfileTab_field_company_id
import doobypro.shared.generated.resources.Account_CompanyProfileTab_field_display_name
import doobypro.shared.generated.resources.Account_CompanyProfileTab_field_legal_name
import doobypro.shared.generated.resources.Account_CompanyProfileTab_field_license_number
import doobypro.shared.generated.resources.Account_CompanyProfileTab_field_logo
import doobypro.shared.generated.resources.Account_CompanyProfileTab_label_company_details
import doobypro.shared.generated.resources.Account_CompanyProfileTab_label_registered_since
import doobypro.shared.generated.resources.Account_CompanyProfileTab_section_address
import doobypro.shared.generated.resources.Account_CompanyProfileTab_section_branding
import doobypro.shared.generated.resources.Account_CompanyProfileTab_section_contact
import doobypro.shared.generated.resources.Account_CompanyProfileTab_section_info
import doobypro.shared.generated.resources.Address_field_city
import doobypro.shared.generated.resources.Address_field_country
import doobypro.shared.generated.resources.Address_field_emirate
import doobypro.shared.generated.resources.Address_field_location_notes
import doobypro.shared.generated.resources.Address_field_postal_code
import doobypro.shared.generated.resources.Address_field_street
import doobypro.shared.generated.resources.Contact_details_field_email
import doobypro.shared.generated.resources.Contact_details_field_phone
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_info
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.SupernovaIcon
import se.supernovait.app.core.ui.component.action.SupernovaTextAction
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.selection.SupernovaSelectField
import se.supernovait.app.core.ui.theme.sizing
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.Emirate
import se.supernovait.doobypro.presentation.account.AccountEvent
import se.supernovait.doobypro.presentation.account.AccountState
import se.supernovait.doobypro.presentation.account.component.AccountCard
import se.supernovait.doobypro.presentation.account.component.AccountField

/**
 * Tab displaying the company profile information, including branding, basic info, and contact details.
 *
 * @param state The current account state.
 * @param onEvent Callback to handle UI events.
 * @param modifier The modifier to be applied to the tab content.
 */
@Composable
fun CompanyProfileTab(
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val fieldModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = MaterialTheme.spacing.extraSmall)

    val plainFieldModifier = Modifier.fillMaxWidth()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.medium)
    ) {
        // Branding Card
        AccountCard(
            title = stringResource(Res.string.Account_CompanyProfileTab_section_branding),
            isSaving = state.isSaving,
            isEditing = state.editingCardId == "company-branding",
            onEditClick = { onEvent(AccountEvent.EnterEditMode("company-branding")) },
            onCancelClick = { onEvent(AccountEvent.ExitEditMode) },
            onSaveClick = { onEvent(AccountEvent.SaveCompanyProfile) }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val logoUrl = if (state.editingCardId == "company-branding") state.editCompanyLogoUrl else state.account?.company?.logoUrl
                
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (logoUrl != null) {
                        // TODO: use an image loader (like Coil) to display the actual logo image
                        Text("Logo", style = MaterialTheme.typography.labelSmall)
                    } else {
                        SupernovaIcon(
                            icon = Res.drawable.ic_info,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(MaterialTheme.sizing.icon.extraLarge)
                        )
                    }
                }
                
                if (state.editingCardId == "company-branding") {
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    SupernovaTextField(
                        label = stringResource(Res.string.Account_CompanyProfileTab_field_logo),
                        value = state.editCompanyLogoUrl ?: "",
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyLogo(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    SupernovaTextAction(
                        label = stringResource(Res.string.Account_CompanyProfileTab_action_change_logo),
                        onClick = { /* TODO: trigger platform-specific image picker */ }
                    )
                }
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // Company Information Card
        AccountCard(
            title = stringResource(Res.string.Account_CompanyProfileTab_section_info),
            isSaving = state.isSaving,
            isEditing = state.editingCardId == "company-info",
            onEditClick = { onEvent(AccountEvent.EnterEditMode("company-info")) },
            onCancelClick = { onEvent(AccountEvent.ExitEditMode) },
            onSaveClick = { onEvent(AccountEvent.SaveCompanyProfile) }
        ) {
            if (state.editingCardId == "company-info") {
                // Edit Mode
                Column {
                    SupernovaTextField(
                        label = stringResource(Res.string.Account_CompanyProfileTab_field_legal_name),
                        value = state.editCompanyLegalName,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyLegalName(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    SupernovaTextField(
                        label = stringResource(Res.string.Account_CompanyProfileTab_field_display_name),
                        value = state.editCompanyDisplayName,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyDisplayName(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    SupernovaTextField(
                        label = stringResource(Res.string.Account_CompanyProfileTab_field_license_number),
                        value = state.editCompanyLicenseNumber,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyLicenseNumber(value)) },
                        modifier = fieldModifier
                    )
                }
            } else {
                // Display Mode
                AccountField(
                    label = stringResource(Res.string.Account_CompanyProfileTab_field_legal_name),
                    value = state.account?.company?.legalName ?: ""
                )
                Spacer(Modifier.height(MaterialTheme.spacing.medium))
                AccountField(
                    label = stringResource(Res.string.Account_CompanyProfileTab_field_display_name),
                    value = state.account?.company?.displayName ?: ""
                )
                Spacer(Modifier.height(MaterialTheme.spacing.medium))
                AccountField(
                    label = stringResource(Res.string.Account_CompanyProfileTab_field_license_number),
                    value = state.account?.company?.licenseNumber ?: ""
                )
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // Contact Information Card
        AccountCard(
            title = stringResource(Res.string.Account_CompanyProfileTab_section_contact),
            isSaving = state.isSaving,
            isEditing = state.editingCardId == "company-contact",
            onEditClick = { onEvent(AccountEvent.EnterEditMode("company-contact")) },
            onCancelClick = { onEvent(AccountEvent.ExitEditMode) },
            onSaveClick = { onEvent(AccountEvent.SaveCompanyProfile) }
        ) {
            if (state.editingCardId == "company-contact") {
                // Edit Mode
                Column {
                    SupernovaTextField(
                        label = stringResource(Res.string.Contact_details_field_email),
                        value = state.editCompanyEmail,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyEmail(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    SupernovaTextField(
                        label = stringResource(Res.string.Contact_details_field_phone),
                        value = state.editCompanyPhone,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyPhone(value)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = fieldModifier
                    )
                }
            } else {
                // Display Mode
                AccountField(
                    label = stringResource(Res.string.Contact_details_field_email),
                    value = state.account?.company?.email ?: ""
                )
                Spacer(Modifier.height(MaterialTheme.spacing.medium))
                AccountField(
                    label = stringResource(Res.string.Contact_details_field_phone),
                    value = state.account?.company?.phoneNumber ?: ""
                )
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // Address Card
        AccountCard(
            title = stringResource(Res.string.Account_CompanyProfileTab_section_address),
            isSaving = state.isSaving,
            isEditing = state.editingCardId == "company-address",
            onEditClick = { onEvent(AccountEvent.EnterEditMode("company-address")) },
            onCancelClick = { onEvent(AccountEvent.ExitEditMode) },
            onSaveClick = { onEvent(AccountEvent.SaveCompanyProfile) }
        ) {
            if (state.editingCardId == "company-address") {
                // Edit Mode
                Column {
                    SupernovaTextField(
                        label = stringResource(Res.string.Address_field_street),
                        value = state.editCompanyAddressStreet,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyAddressStreet(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    SupernovaTextField(
                        label = stringResource(Res.string.Address_field_city),
                        value = state.editCompanyAddressCity,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyAddressCity(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    
                    val emirateLabels = Emirate.entries.associateWith { stringResource(it.label) }
                    val selectedEmirate = Emirate.fromValue(state.editCompanyAddressSubdivision)
                    
                    SupernovaSelectField(
                        options = Emirate.entries,
                        selectedOption = selectedEmirate,
                        optionLabel = { emirateLabels[it] ?: it.value },
                        onOptionSelected = { onEvent(AccountEvent.UpdateCompanyAddressSubdivision(it.value)) },
                        label = stringResource(Res.string.Address_field_emirate),
                        modifier = plainFieldModifier
                    )
                    
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    SupernovaTextField(
                        label = stringResource(Res.string.Address_field_postal_code),
                        value = state.editCompanyAddressPostalCode,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyAddressPostalCode(value)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    SupernovaTextField(
                        label = stringResource(Res.string.Address_field_country),
                        value = state.editCompanyAddressCountry,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyAddressCountry(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    SupernovaTextField(
                        label = stringResource(Res.string.Address_field_location_notes),
                        value = state.editCompanyNotes,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyNotes(value)) },
                        modifier = fieldModifier,
                        isMultiline = true
                    )
                }
            } else {
                // Display Mode
                AccountField(
                    label = stringResource(Res.string.Address_field_street),
                    value = state.account?.company?.address?.street ?: ""
                )
                Spacer(Modifier.height(MaterialTheme.spacing.small))
                AccountField(
                    label = stringResource(Res.string.Address_field_city),
                    value = state.account?.company?.address?.city ?: ""
                )
                Spacer(Modifier.height(MaterialTheme.spacing.small))
                AccountField(
                    label = stringResource(Res.string.Address_field_emirate),
                    value = state.account?.company?.address?.subdivision ?: ""
                )
                state.account?.company?.address?.postalCode?.takeIf { it.isNotBlank() }?.let { postalCode ->
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    AccountField(
                        label = stringResource(Res.string.Address_field_postal_code),
                        value = postalCode
                    )
                }
                Spacer(Modifier.height(MaterialTheme.spacing.small))
                AccountField(
                    label = stringResource(Res.string.Address_field_country),
                    value = state.account?.company?.address?.country ?: ""
                )
                state.account?.company?.address?.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    AccountField(
                        label = stringResource(Res.string.Address_field_location_notes),
                        value = notes
                    )
                }
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // Company Details Card (Read-only)
        AccountCard(
            title = stringResource(Res.string.Account_CompanyProfileTab_label_company_details),
            isSaving = false,
            isEditing = false,
            onEditClick = null
        ) {
            AccountField(
                label = stringResource(Res.string.Account_CompanyProfileTab_field_company_id),
                value = state.account?.company?.id ?: "",
                isLocked = true
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            AccountField(
                label = stringResource(Res.string.Account_CompanyProfileTab_label_registered_since),
                value = state.registeredSince
            )
        }

        Spacer(Modifier.height(MaterialTheme.spacing.large))
    }
}
