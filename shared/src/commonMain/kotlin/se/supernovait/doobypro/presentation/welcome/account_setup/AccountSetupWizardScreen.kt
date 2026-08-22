package se.supernovait.doobypro.presentation.welcome.account_setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import doobypro.shared.generated.resources.Address_field_city
import doobypro.shared.generated.resources.Address_field_country
import doobypro.shared.generated.resources.Address_field_emirate
import doobypro.shared.generated.resources.Address_field_location_notes
import doobypro.shared.generated.resources.Address_field_postal_code
import doobypro.shared.generated.resources.Address_field_street
import doobypro.shared.generated.resources.Contact_details_field_email
import doobypro.shared.generated.resources.Contact_details_field_phone
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_AccountSetup_action_back
import doobypro.shared.generated.resources.screen_AccountSetup_action_continue
import doobypro.shared.generated.resources.screen_AccountSetup_action_create_account
import doobypro.shared.generated.resources.screen_AccountSetup_confirmation_note
import doobypro.shared.generated.resources.screen_AccountSetup_field_company_display_name
import doobypro.shared.generated.resources.screen_AccountSetup_field_company_email
import doobypro.shared.generated.resources.screen_AccountSetup_field_company_legal_name
import doobypro.shared.generated.resources.screen_AccountSetup_field_company_phone
import doobypro.shared.generated.resources.screen_AccountSetup_field_dob
import doobypro.shared.generated.resources.screen_AccountSetup_field_first_name
import doobypro.shared.generated.resources.screen_AccountSetup_field_last_name
import doobypro.shared.generated.resources.screen_AccountSetup_field_license_number
import doobypro.shared.generated.resources.screen_AccountSetup_field_username
import doobypro.shared.generated.resources.screen_AccountSetup_step_indicator
import doobypro.shared.generated.resources.screen_AccountSetup_subtitle_step1
import doobypro.shared.generated.resources.screen_AccountSetup_subtitle_step2
import doobypro.shared.generated.resources.screen_AccountSetup_subtitle_step3
import doobypro.shared.generated.resources.screen_AccountSetup_subtitle_step4
import doobypro.shared.generated.resources.screen_AccountSetup_summary_company
import doobypro.shared.generated.resources.screen_AccountSetup_summary_location
import doobypro.shared.generated.resources.screen_AccountSetup_summary_user
import doobypro.shared.generated.resources.screen_AccountSetup_summary_username
import doobypro.shared.generated.resources.screen_AccountSetup_title_step1
import doobypro.shared.generated.resources.screen_AccountSetup_title_step2
import doobypro.shared.generated.resources.screen_AccountSetup_title_step3
import doobypro.shared.generated.resources.screen_AccountSetup_title_step4
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaButton
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.input.SupernovaDateField
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.selection.SupernovaSelectField
import se.supernovait.app.core.ui.component.text.SupernovaSubtitle
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.AppDefaults
import se.supernovait.doobypro.domain.model.Emirate
import se.supernovait.doobypro.presentation.common.preview.ScreenPreviewContainer

/**
 * Main Composable for the Account Setup Wizard.
 * Displays a multi-step form to collect user, company, and address information.
 *
 * @param uiState Current state of the wizard from [AccountSetupWizardViewModel].
 * @param onEvent Callback for user actions.
 */
@Composable
fun AccountSetupWizardScreen(
    uiState: AccountSetupWizardState,
    onEvent: (AccountSetupWizardEvent) -> Unit
) {
    Scaffold(
        bottomBar = {
            WizardBottomBar(
                currentStep = uiState.currentStep,
                isCreatingAccount = uiState.isCreatingAccount,
                onBack = { onEvent(AccountSetupWizardEvent.OnBackClick) },
                onNext = { onEvent(AccountSetupWizardEvent.OnNextClick) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(MaterialTheme.spacing.medium)
        ) {
            WizardProgress(currentStep = uiState.currentStep)

            AnimatedContent(targetState = uiState.currentStep) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    when (step) {
                        1 -> Step1(uiState, onEvent)
                        2 -> Step2(uiState, onEvent)
                        3 -> Step3(uiState, onEvent)
                        4 -> Step4(uiState)
                    }
                }
            }
        }
    }
}

@Composable
private fun WizardProgress(currentStep: Int) {
    val progress = currentStep / 4f
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.spacing.medium)) {
        Text(
            text = stringResource(Res.string.screen_AccountSetup_step_indicator, currentStep, 4),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.extraSmall)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.extraSmall),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun WizardBottomBar(
    currentStep: Int,
    isCreatingAccount: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
    ) {
        if (currentStep > 1) {
            SupernovaOutlinedButton(
                label = stringResource(Res.string.screen_AccountSetup_action_back),
                shape = MaterialTheme.shapes.extraSmall,
                onClick = onBack,
                modifier = Modifier.weight(1f)
            )
        }
        SupernovaButton(
            label = if (currentStep == 4) stringResource(Res.string.screen_AccountSetup_action_create_account)
            else stringResource(Res.string.screen_AccountSetup_action_continue),
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = MaterialTheme.shapes.extraSmall,
            onClick = onNext,
            enabled = !isCreatingAccount,
            loading = isCreatingAccount,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun Step1(state: AccountSetupWizardState, onEvent: (AccountSetupWizardEvent) -> Unit) {
    SupernovaTitle(text = stringResource(Res.string.screen_AccountSetup_title_step1))
    SupernovaSubtitle(text = stringResource(Res.string.screen_AccountSetup_subtitle_step1), fontWeight = FontWeight.Normal)
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

    SupernovaTextField(
        label = stringResource(Res.string.screen_AccountSetup_field_first_name),
        value = state.firstName,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateFirstName(value)) }
    )
    SupernovaTextField(
        label = stringResource(Res.string.screen_AccountSetup_field_last_name),
        value = state.lastName,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateLastName(value)) }
    )
    SupernovaTextField(
        label = stringResource(Res.string.screen_AccountSetup_field_username),
        value = state.username,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateUsername(value)) }
    )
    SupernovaDateField(
        label = stringResource(Res.string.screen_AccountSetup_field_dob),
        placeholder = "YYYY-MM-DD",
        value = state.birthDate,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateBirthDate(value)) }
    )
    SupernovaTextField(
        label = stringResource(Res.string.Contact_details_field_email),
        value = state.email,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateEmail(value)) }
    )
    SupernovaTextField(
        label = stringResource(Res.string.Contact_details_field_phone),
        value = state.phoneNumber,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdatePhoneNumber(value)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )
}

@Composable
private fun Step2(state: AccountSetupWizardState, onEvent: (AccountSetupWizardEvent) -> Unit) {
    SupernovaTitle(text = stringResource(Res.string.screen_AccountSetup_title_step2))
    SupernovaSubtitle(text = stringResource(Res.string.screen_AccountSetup_subtitle_step2), fontWeight = FontWeight.Normal)
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

    SupernovaTextField(
        label = stringResource(Res.string.screen_AccountSetup_field_company_legal_name),
        value = state.companyLegalName,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateCompanyLegalName(value)) }
    )
    SupernovaTextField(
        label = stringResource(Res.string.screen_AccountSetup_field_company_display_name),
        value = state.companyDisplayName,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateCompanyDisplayName(value)) }
    )
    SupernovaTextField(
        label = stringResource(Res.string.screen_AccountSetup_field_license_number),
        value = state.licenseNumber,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateLicenseNumber(value)) }
    )
    SupernovaTextField(
        label = stringResource(Res.string.screen_AccountSetup_field_company_email),
        value = state.companyEmail,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateCompanyEmail(value)) }
    )
    SupernovaTextField(
        label = stringResource(Res.string.screen_AccountSetup_field_company_phone),
        value = state.companyPhone,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateCompanyPhone(value)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )
}

@Composable
private fun Step3(state: AccountSetupWizardState, onEvent: (AccountSetupWizardEvent) -> Unit) {
    val emirateLabels = Emirate.entries.associateWith { stringResource(it.label) }

    SupernovaTitle(text = stringResource(Res.string.screen_AccountSetup_title_step3))
    SupernovaSubtitle(text = stringResource(Res.string.screen_AccountSetup_subtitle_step3), fontWeight = FontWeight.Normal)
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

    SupernovaTextField(
        label = stringResource(Res.string.Address_field_street),
        value = state.streetAddress,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateStreetAddress(value)) }
    )
    SupernovaTextField(
        label = stringResource(Res.string.Address_field_city),
        value = state.city,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateCity(value)) }
    )
    SupernovaSelectField(
        label = stringResource(Res.string.Address_field_emirate),
        options = Emirate.entries,
        selectedOption = Emirate.fromValue(state.subdivision),
        onOptionSelected = { onEvent(AccountSetupWizardEvent.UpdateSubdivision(it.value)) },
        optionLabel = { emirateLabels[it] ?: "" }
    )
    SupernovaTextField(
        label = stringResource(Res.string.Address_field_postal_code),
        value = state.postalCode,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdatePostalCode(value)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
    SupernovaTextField(
        label = stringResource(Res.string.Address_field_country),
        value = state.country,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateCountry(value)) }
    )
    SupernovaTextField(
        label = stringResource(Res.string.Address_field_location_notes),
        value = state.notes,
        isMultiline = true,
        onValueChange = { value, _ -> onEvent(AccountSetupWizardEvent.UpdateNotes(value)) }
    )
}

@Composable
private fun Step4(state: AccountSetupWizardState) {
    SupernovaTitle(text = stringResource(Res.string.screen_AccountSetup_title_step4))
    SupernovaSubtitle(text = stringResource(Res.string.screen_AccountSetup_subtitle_step4), fontWeight = FontWeight.Normal)
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryItem(stringResource(Res.string.screen_AccountSetup_summary_user), "${state.firstName} ${state.lastName}")
            SummaryItem(stringResource(Res.string.screen_AccountSetup_summary_username), state.username)
            SummaryItem(stringResource(Res.string.screen_AccountSetup_summary_company), state.companyDisplayName)
            SummaryItem(stringResource(Res.string.Contact_details_field_phone), state.phoneNumber)
            SummaryItem(stringResource(Res.string.Contact_details_field_email), state.email)
            
            val location = buildString {
                if (state.subdivision.isNotBlank()) {
                    append(state.subdivision)
                    append(", ")
                }
                append(AppDefaults.COUNTRY)
            }
            SummaryItem(stringResource(Res.string.screen_AccountSetup_summary_location), location)
        }
    }

    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

    Text(
        text = stringResource(Res.string.screen_AccountSetup_confirmation_note),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    ScreenPreviewContainer {
        AccountSetupWizardScreen(
            uiState = AccountSetupWizardState(currentStep = 4),
            onEvent = { }
        )
    }
}
