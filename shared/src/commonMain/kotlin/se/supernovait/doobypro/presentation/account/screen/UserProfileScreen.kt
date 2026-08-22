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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Account_cancel_action
import doobypro.shared.generated.resources.screen_Account_delete_action
import doobypro.shared.generated.resources.screen_Account_delete_confirm_message
import doobypro.shared.generated.resources.screen_Account_delete_confirm_title
import doobypro.shared.generated.resources.screen_UserProfile_action_delete_account
import doobypro.shared.generated.resources.screen_UserProfile_action_save
import doobypro.shared.generated.resources.screen_UserProfile_action_saving
import doobypro.shared.generated.resources.screen_UserProfile_field_dob
import doobypro.shared.generated.resources.screen_UserProfile_field_dob_placeholder
import doobypro.shared.generated.resources.screen_UserProfile_field_email
import doobypro.shared.generated.resources.screen_UserProfile_field_first_name
import doobypro.shared.generated.resources.screen_UserProfile_field_last_name
import doobypro.shared.generated.resources.screen_UserProfile_field_phone
import doobypro.shared.generated.resources.screen_UserProfile_field_user_id
import doobypro.shared.generated.resources.screen_UserProfile_section_account
import doobypro.shared.generated.resources.screen_UserProfile_section_contact
import doobypro.shared.generated.resources.screen_UserProfile_section_personal
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.modal.dialog.LocalDialogState
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.account.AccountState
import se.supernovait.doobypro.presentation.account.event.AccountEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    state: AccountState,
    onEvent: (AccountEvent) -> Unit
) {
    val dialogState = LocalDialogState.current
    val deleteTitle = stringResource(Res.string.screen_Account_delete_confirm_title)
    val deleteMessage = stringResource(Res.string.screen_Account_delete_confirm_message)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(Res.string.screen_UserProfile_section_account),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        // Read-only User ID
        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_UserProfile_field_user_id)) },
            value = state.account?.user?.id ?: "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))
        Text(
            text = stringResource(Res.string.screen_UserProfile_section_personal),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_UserProfile_field_first_name)) },
            value = state.editFirstName,
            onValueChange = { onEvent(AccountEvent.UpdateFirstName(it)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_UserProfile_field_last_name)) },
            value = state.editLastName,
            onValueChange = { onEvent(AccountEvent.UpdateLastName(it)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_UserProfile_field_dob)) },
            value = state.editBirthDate,
            onValueChange = { onEvent(AccountEvent.UpdateBirthDate(it)) },
            placeholder = { Text(stringResource(Res.string.screen_UserProfile_field_dob_placeholder)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))
        Text(
            text = stringResource(Res.string.screen_UserProfile_section_contact),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_UserProfile_field_email)) },
            value = state.editEmail,
            onValueChange = { onEvent(AccountEvent.UpdateEmail(it)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        OutlinedTextField(
            label = { Text(stringResource(Res.string.screen_UserProfile_field_phone)) },
            value = state.editPhone,
            onValueChange = { onEvent(AccountEvent.UpdatePhoneNumber(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.large))

        Button(
            onClick = { onEvent(AccountEvent.SaveUserProfile) },
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (state.isSaving) stringResource(Res.string.screen_UserProfile_action_saving)
                else stringResource(Res.string.screen_UserProfile_action_save)
            )
        }

        Spacer(Modifier.height(MaterialTheme.spacing.extraLarge))

        Button(
            onClick = {
                dialogState.showConfirmation(
                    title = deleteTitle,
                    message = deleteMessage,
                    confirmLabel = Res.string.screen_Account_delete_action,
                    dismissLabel = Res.string.screen_Account_cancel_action,
                    onConfirm = { onEvent(AccountEvent.DeactivateAccount) },
                    onDismiss = { dialogState.hide() }
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.screen_UserProfile_action_delete_account))
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))
    }
}
