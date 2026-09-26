package se.supernovait.doobypro.presentation.account.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
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
import doobypro.shared.generated.resources.Account_CompanyProfileTab_label_company_details
import doobypro.shared.generated.resources.Account_CompanyProfileTab_label_registered_since
import doobypro.shared.generated.resources.Account_CompanyProfileTab_picker_option_files
import doobypro.shared.generated.resources.Account_CompanyProfileTab_picker_option_photos
import doobypro.shared.generated.resources.Account_CompanyProfileTab_picker_title
import doobypro.shared.generated.resources.Account_CompanyProfileTab_section_address
import doobypro.shared.generated.resources.Account_CompanyProfileTab_section_branding
import doobypro.shared.generated.resources.Account_CompanyProfileTab_section_business_hours
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
import doobypro.shared.generated.resources.business_hours_subtitle
import doobypro.shared.generated.resources.ic_files
import doobypro.shared.generated.resources.ic_info
import doobypro.shared.generated.resources.ic_photo_library
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.SupernovaIcon
import se.supernovait.app.core.ui.component.action.SupernovaTextAction
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.modal.LocalBottomSheetState
import se.supernovait.app.core.ui.component.selection.SupernovaSelectField
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.sizing
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.Emirate
import se.supernovait.doobypro.domain.util.rememberImagePickerLauncher
import se.supernovait.doobypro.presentation.account.AccountEvent
import se.supernovait.doobypro.presentation.account.AccountState
import se.supernovait.doobypro.presentation.account.component.AccountCard
import se.supernovait.doobypro.presentation.account.component.AccountField
import se.supernovait.doobypro.presentation.account.component.LogoImage
import se.supernovait.doobypro.presentation.businesshours.component.BusinessHoursEditor

/**
 * Tab displaying the company profile information, including branding, basic info, and contact details.
 *
 * @param uiState The current account state.
 * @param onEvent Callback to handle UI events.
 * @param modifier The modifier to be applied to the tab content.
 */
@Composable
fun CompanyProfileTab(
    uiState: AccountState,
    onEvent: (AccountEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val plainFieldModifier = Modifier.fillMaxWidth()
    val fieldModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = MaterialTheme.spacing.extraSmall)

    val bottomSheetState = LocalBottomSheetState.current

    val imagePicker = rememberImagePickerLauncher { bytes ->
        bytes?.let { onEvent(AccountEvent.UpdateCompanyLogo(it)) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.medium)
    ) {
        // Branding Card
        AccountCard(
            title = stringResource(Res.string.Account_CompanyProfileTab_section_branding),
            isSaving = uiState.isSaving,
            isEditing = uiState.editingCardId == "company-branding",
            onEditClick = { onEvent(AccountEvent.EnterEditMode("company-branding")) },
            onCancelClick = { onEvent(AccountEvent.ExitEditMode) },
            onSaveClick = { onEvent(AccountEvent.SaveCompanyProfile) }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val logoUrl = if (uiState.editingCardId == "company-branding") uiState.editCompanyLogoUrl else uiState.account?.company?.logoUrl
                
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (logoUrl != null) {
                        LogoImage(
                            logoUrl = logoUrl,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        SupernovaIcon(
                            icon = Res.drawable.ic_info,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(MaterialTheme.sizing.icon.extraLarge)
                        )
                    }
                }
                
                if (uiState.editingCardId == "company-branding") {
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    SupernovaTextAction(
                        label = stringResource(Res.string.Account_CompanyProfileTab_action_change_logo),
                        onClick = {
                            bottomSheetState.show {
                                LogoSourcePickerSheet(
                                    onPhotosClick = {
                                        bottomSheetState.hide()
                                        imagePicker.launchPhotos()
                                    },
                                    onFilesClick = {
                                        bottomSheetState.hide()
                                        imagePicker.launchFiles()
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // Company Information Card
        AccountCard(
            title = stringResource(Res.string.Account_CompanyProfileTab_section_info),
            isSaving = uiState.isSaving,
            isEditing = uiState.editingCardId == "company-info",
            onEditClick = { onEvent(AccountEvent.EnterEditMode("company-info")) },
            onCancelClick = { onEvent(AccountEvent.ExitEditMode) },
            onSaveClick = { onEvent(AccountEvent.SaveCompanyProfile) }
        ) {
            if (uiState.editingCardId == "company-info") {
                // Edit Mode
                Column {
                    SupernovaTextField(
                        label = stringResource(Res.string.Account_CompanyProfileTab_field_legal_name),
                        value = uiState.editCompanyLegalName,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyLegalName(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    SupernovaTextField(
                        label = stringResource(Res.string.Account_CompanyProfileTab_field_display_name),
                        value = uiState.editCompanyDisplayName,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyDisplayName(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    SupernovaTextField(
                        label = stringResource(Res.string.Account_CompanyProfileTab_field_license_number),
                        value = uiState.editCompanyLicenseNumber,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyLicenseNumber(value)) },
                        modifier = fieldModifier
                    )
                }
            } else {
                // Display Mode
                AccountField(
                    label = stringResource(Res.string.Account_CompanyProfileTab_field_legal_name),
                    value = uiState.account?.company?.legalName ?: ""
                )
                Spacer(Modifier.height(MaterialTheme.spacing.medium))
                AccountField(
                    label = stringResource(Res.string.Account_CompanyProfileTab_field_display_name),
                    value = uiState.account?.company?.displayName ?: ""
                )
                Spacer(Modifier.height(MaterialTheme.spacing.medium))
                AccountField(
                    label = stringResource(Res.string.Account_CompanyProfileTab_field_license_number),
                    value = uiState.account?.company?.licenseNumber ?: ""
                )
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // Contact Information Card
        AccountCard(
            title = stringResource(Res.string.Account_CompanyProfileTab_section_contact),
            isSaving = uiState.isSaving,
            isEditing = uiState.editingCardId == "company-contact",
            onEditClick = { onEvent(AccountEvent.EnterEditMode("company-contact")) },
            onCancelClick = { onEvent(AccountEvent.ExitEditMode) },
            onSaveClick = { onEvent(AccountEvent.SaveCompanyProfile) }
        ) {
            if (uiState.editingCardId == "company-contact") {
                // Edit Mode
                Column {
                    SupernovaTextField(
                        label = stringResource(Res.string.Contact_details_field_email),
                        value = uiState.editCompanyEmail,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyEmail(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    SupernovaTextField(
                        label = stringResource(Res.string.Contact_details_field_phone),
                        value = uiState.editCompanyPhone,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyPhone(value)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = fieldModifier
                    )
                }
            } else {
                // Display Mode
                AccountField(
                    label = stringResource(Res.string.Contact_details_field_email),
                    value = uiState.account?.company?.email ?: ""
                )
                Spacer(Modifier.height(MaterialTheme.spacing.medium))
                AccountField(
                    label = stringResource(Res.string.Contact_details_field_phone),
                    value = uiState.account?.company?.phoneNumber ?: ""
                )
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // Address Card
        AccountCard(
            title = stringResource(Res.string.Account_CompanyProfileTab_section_address),
            isSaving = uiState.isSaving,
            isEditing = uiState.editingCardId == "company-address",
            onEditClick = { onEvent(AccountEvent.EnterEditMode("company-address")) },
            onCancelClick = { onEvent(AccountEvent.ExitEditMode) },
            onSaveClick = { onEvent(AccountEvent.SaveCompanyProfile) }
        ) {
            if (uiState.editingCardId == "company-address") {
                // Edit Mode
                Column {
                    SupernovaTextField(
                        label = stringResource(Res.string.Address_field_street),
                        value = uiState.editCompanyAddressStreet,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyAddressStreet(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    SupernovaTextField(
                        label = stringResource(Res.string.Address_field_city),
                        value = uiState.editCompanyAddressCity,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyAddressCity(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    
                    val emirateLabels = Emirate.entries.associateWith { stringResource(it.label) }
                    val selectedEmirate = Emirate.fromValue(uiState.editCompanyAddressSubdivision)
                    
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
                        value = uiState.editCompanyAddressPostalCode,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyAddressPostalCode(value)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    SupernovaTextField(
                        label = stringResource(Res.string.Address_field_country),
                        value = uiState.editCompanyAddressCountry,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyAddressCountry(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    SupernovaTextField(
                        label = stringResource(Res.string.Address_field_location_notes),
                        value = uiState.editCompanyNotes,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateCompanyNotes(value)) },
                        modifier = fieldModifier,
                        isMultiline = true
                    )
                }
            } else {
                // Display Mode
                AccountField(
                    label = stringResource(Res.string.Address_field_street),
                    value = uiState.account?.company?.address?.street ?: ""
                )
                Spacer(Modifier.height(MaterialTheme.spacing.small))
                AccountField(
                    label = stringResource(Res.string.Address_field_city),
                    value = uiState.account?.company?.address?.city ?: ""
                )
                Spacer(Modifier.height(MaterialTheme.spacing.small))
                AccountField(
                    label = stringResource(Res.string.Address_field_emirate),
                    value = uiState.account?.company?.address?.subdivision ?: ""
                )
                uiState.account?.company?.address?.postalCode?.takeIf { it.isNotBlank() }?.let { postalCode ->
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                    AccountField(
                        label = stringResource(Res.string.Address_field_postal_code),
                        value = postalCode
                    )
                }
                Spacer(Modifier.height(MaterialTheme.spacing.small))
                AccountField(
                    label = stringResource(Res.string.Address_field_country),
                    value = uiState.account?.company?.address?.country ?: ""
                )
                uiState.account?.company?.address?.notes?.takeIf { it.isNotBlank() }?.let { notes ->
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
                value = uiState.account?.company?.id ?: "",
                isLocked = true
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            AccountField(
                label = stringResource(Res.string.Account_CompanyProfileTab_label_registered_since),
                value = uiState.registeredSince
            )
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // Business Hours Card
        AccountCard(
            title = stringResource(Res.string.Account_CompanyProfileTab_section_business_hours),
            isSaving = uiState.businessHoursState.isSaving,
            isEditing = false,
            onEditClick = null
        ) {
            SupernovaLabel(
                text = stringResource(Res.string.business_hours_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.spacing.medium))
            BusinessHoursEditor(
                state = uiState.businessHoursState,
                onDayHoursUpdate = { day, hours -> onEvent(AccountEvent.UpdateDayHours(day, hours)) }
            )
        }

        Spacer(Modifier.height(MaterialTheme.spacing.large))
    }
}

/**
 * Bottom sheet content for selecting the image source (Photos or Files).
 */
@Composable
private fun LogoSourcePickerSheet(
    onPhotosClick: () -> Unit,
    onFilesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
            .padding(bottom = MaterialTheme.spacing.large)
    ) {
        SupernovaTitle(
            text = Res.string.Account_CompanyProfileTab_picker_title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
        )
        
        // Photos Option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPhotosClick() }
                .padding(vertical = MaterialTheme.spacing.medium)
        ) {
            SupernovaIcon(
                icon = Res.drawable.ic_photo_library,
                tint = contentColorFor(MaterialTheme.colorScheme.surfaceContainerLow),
                modifier = Modifier.size(MaterialTheme.sizing.icon.medium)
            )
            Spacer(Modifier.width(MaterialTheme.spacing.medium))
            SupernovaLabel(
                text = Res.string.Account_CompanyProfileTab_picker_option_photos,
                color = contentColorFor(MaterialTheme.colorScheme.surfaceContainerLow),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Files Option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onFilesClick() }
                .padding(vertical = MaterialTheme.spacing.medium)
        ) {
            SupernovaIcon(
                icon = Res.drawable.ic_files,
                tint = contentColorFor(MaterialTheme.colorScheme.surfaceContainerLow),
                modifier = Modifier.size(MaterialTheme.sizing.icon.medium)
            )
            Spacer(Modifier.width(MaterialTheme.spacing.medium))
            SupernovaLabel(
                text = Res.string.Account_CompanyProfileTab_picker_option_files,
                color = contentColorFor(MaterialTheme.colorScheme.surfaceContainerLow),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
