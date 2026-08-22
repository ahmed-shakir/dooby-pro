package se.supernovait.doobypro.presentation.account.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import doobypro.shared.generated.resources.Account_UserProfileTab_field_address
import doobypro.shared.generated.resources.Account_UserProfileTab_field_dob
import doobypro.shared.generated.resources.Account_UserProfileTab_field_first_name
import doobypro.shared.generated.resources.Account_UserProfileTab_field_last_name
import doobypro.shared.generated.resources.Account_UserProfileTab_field_role
import doobypro.shared.generated.resources.Account_UserProfileTab_field_status
import doobypro.shared.generated.resources.Account_UserProfileTab_field_user_id
import doobypro.shared.generated.resources.Account_UserProfileTab_field_username
import doobypro.shared.generated.resources.Account_UserProfileTab_label_add_home_address
import doobypro.shared.generated.resources.Account_UserProfileTab_label_member_since
import doobypro.shared.generated.resources.Account_UserProfileTab_section_account
import doobypro.shared.generated.resources.Account_UserProfileTab_section_contact
import doobypro.shared.generated.resources.Account_UserProfileTab_section_personal
import doobypro.shared.generated.resources.Address_field_city
import doobypro.shared.generated.resources.Address_field_emirate
import doobypro.shared.generated.resources.Address_field_street
import doobypro.shared.generated.resources.Contact_details_field_email
import doobypro.shared.generated.resources.Contact_details_field_phone
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.label_sign_out
import doobypro.shared.generated.resources.screen_Account_delete_action
import doobypro.shared.generated.resources.screen_Account_dialog_cancel_action
import doobypro.shared.generated.resources.screen_Account_dialog_delete_action
import doobypro.shared.generated.resources.screen_Account_dialog_delete_confirm_message
import doobypro.shared.generated.resources.screen_Account_dialog_delete_confirm_title
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.domain.auth.UserStatus
import se.supernovait.app.core.ui.component.action.SupernovaTextAction
import se.supernovait.app.core.ui.component.input.SupernovaDateField
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.modal.dialog.LocalDialogState
import se.supernovait.app.core.ui.component.selection.SupernovaSelectField
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.Emirate
import se.supernovait.doobypro.presentation.account.AccountEvent
import se.supernovait.doobypro.presentation.account.AccountState
import se.supernovait.doobypro.presentation.account.component.AccountCard
import se.supernovait.doobypro.presentation.account.component.AccountField
import se.supernovait.doobypro.presentation.app.theme.statusColor

/**
 * Tab displaying the user profile information.
 *
 * @param state The current account state.
 * @param onEvent Callback to handle UI events.
 * @param modifier The modifier to be applied to the tab content.
 */
@Composable
fun UserProfileTab(
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val dialogState = LocalDialogState.current
    val deleteTitle = stringResource(Res.string.screen_Account_dialog_delete_confirm_title)
    val deleteMessage = stringResource(Res.string.screen_Account_dialog_delete_confirm_message)

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
        // Personal Information Card
        AccountCard(
            title = stringResource(Res.string.Account_UserProfileTab_section_personal),
            isSaving = state.isSaving,
            isEditing = state.editingCardId == "personal-info",
            onEditClick = { onEvent(AccountEvent.EnterEditMode("personal-info")) },
            onCancelClick = { onEvent(AccountEvent.ExitEditMode) },
            onSaveClick = { onEvent(AccountEvent.SaveUserProfile) }
        ) {
            if (state.editingCardId == "personal-info") {
                // Edit Mode
                Column {
                    SupernovaTextField(
                        label = stringResource(Res.string.Account_UserProfileTab_field_first_name),
                        value = state.editUserFirstName,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateFirstName(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    SupernovaTextField(
                        label = stringResource(Res.string.Account_UserProfileTab_field_last_name),
                        value = state.editUserLastName,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateLastName(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    SupernovaDateField(
                        label = stringResource(Res.string.Account_UserProfileTab_field_dob),
                        value = state.editUserBirthDate,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateBirthDate(value)) },
                        modifier = plainFieldModifier
                    )
                }
            } else {
                // Display Mode
                AccountField(
                    label = stringResource(Res.string.Account_UserProfileTab_field_first_name),
                    value = state.account?.user?.firstname ?: ""
                )
                Spacer(Modifier.height(MaterialTheme.spacing.medium))
                AccountField(
                    label = stringResource(Res.string.Account_UserProfileTab_field_last_name),
                    value = state.account?.user?.lastname ?: ""
                )
                state.account?.user?.birthdate?.toString()?.takeIf { it.isNotBlank() }?.let { dob ->
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    AccountField(
                        label = stringResource(Res.string.Account_UserProfileTab_field_dob),
                        value = dob
                    )
                }
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // Contact Information Card
        AccountCard(
            title = stringResource(Res.string.Account_UserProfileTab_section_contact),
            isSaving = state.isSaving,
            isEditing = state.editingCardId == "contact-info",
            onEditClick = { onEvent(AccountEvent.EnterEditMode("contact-info")) },
            onCancelClick = { onEvent(AccountEvent.ExitEditMode) },
            onSaveClick = { onEvent(AccountEvent.SaveUserProfile) }
        ) {
            if (state.editingCardId == "contact-info") {
                // Edit Mode
                Column {
                    SupernovaTextField(
                        label = stringResource(Res.string.Contact_details_field_email),
                        value = state.editUserEmail,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdateEmail(value)) },
                        modifier = fieldModifier
                    )
                    Spacer(Modifier.height(MaterialTheme.spacing.medium))
                    SupernovaTextField(
                        label = stringResource(Res.string.Contact_details_field_phone),
                        value = state.editUserPhone,
                        onValueChange = { value, _ -> onEvent(AccountEvent.UpdatePhoneNumber(value)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = fieldModifier
                    )
                }
            } else {
                // Display Mode
                AccountField(
                    label = stringResource(Res.string.Contact_details_field_email),
                    value = state.account?.user?.email ?: ""
                )
                Spacer(Modifier.height(MaterialTheme.spacing.medium))
                AccountField(
                    label = stringResource(Res.string.Contact_details_field_phone),
                    value = state.account?.user?.phoneNumber ?: ""
                )
            }
        }

        val userAddress = state.account?.user?.address
        val isEditingAddress = state.editingCardId == "user-address"

        if (userAddress != null || isEditingAddress) {
            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            AccountCard(
                title = stringResource(Res.string.Account_UserProfileTab_field_address),
                isSaving = state.isSaving,
                isEditing = isEditingAddress,
                onEditClick = { onEvent(AccountEvent.EnterEditMode("user-address")) },
                onCancelClick = { onEvent(AccountEvent.ExitEditMode) },
                onSaveClick = { onEvent(AccountEvent.SaveUserProfile) }
            ) {
                if (isEditingAddress) {
                    Column {
                        SupernovaTextField(
                            label = stringResource(Res.string.Address_field_street),
                            value = state.editUserAddressStreet,
                            onValueChange = { value, _ -> onEvent(AccountEvent.UpdateUserAddressStreet(value)) },
                            modifier = fieldModifier
                        )
                        Spacer(Modifier.height(MaterialTheme.spacing.medium))
                        SupernovaTextField(
                            label = stringResource(Res.string.Address_field_city),
                            value = state.editUserAddressCity,
                            onValueChange = { value, _ -> onEvent(AccountEvent.UpdateUserAddressCity(value)) },
                            modifier = fieldModifier
                        )
                        Spacer(Modifier.height(MaterialTheme.spacing.medium))
                        
                        val emirateLabels = Emirate.entries.associateWith { stringResource(it.label) }
                        val selectedEmirate = Emirate.fromValue(state.editUserAddressSubdivision)
                        
                        SupernovaSelectField(
                            label = stringResource(Res.string.Address_field_emirate),
                            options = Emirate.entries,
                            selectedOption = selectedEmirate,
                            optionLabel = { emirate -> emirateLabels[emirate] ?: "" },
                            onOptionSelected = { onEvent(AccountEvent.UpdateUserAddressSubdivision(it.value)) },
                            modifier = plainFieldModifier
                        )
                    }
                } else {
                    userAddress?.let { address ->
                        AccountField(
                            label = stringResource(Res.string.Address_field_street),
                            value = address.street
                        )
                        Spacer(Modifier.height(MaterialTheme.spacing.medium))
                        AccountField(
                            label = stringResource(Res.string.Address_field_city),
                            value = address.city
                        )
                        address.subdivision?.let { subdivision ->
                            Spacer(Modifier.height(MaterialTheme.spacing.medium))
                            AccountField(
                                label = stringResource(Res.string.Address_field_emirate),
                                value = subdivision
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // Account Information Card (Read-only)
        AccountCard(
            title = stringResource(Res.string.Account_UserProfileTab_section_account),
            isSaving = false,
            isEditing = false,
            onEditClick = null
        ) {
            AccountField(
                label = stringResource(Res.string.Account_UserProfileTab_field_user_id),
                value = state.account?.user?.id ?: "",
                isLocked = true
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            AccountField(
                label = stringResource(Res.string.Account_UserProfileTab_field_username),
                value = state.account?.user?.username ?: ""
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            AccountField(
                label = stringResource(Res.string.Account_UserProfileTab_field_role),
                value = state.account?.user?.role?.toString() ?: ""
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            AccountField(
                label = stringResource(Res.string.Account_UserProfileTab_field_status),
                value = state.account?.user?.status?.asString() ?: "",
                isStatus = true,
                valueColor = when (state.account?.user?.status) {
                    is UserStatus.Active -> MaterialTheme.statusColor.success
                    is UserStatus.Deactivated -> MaterialTheme.statusColor.warning
                    is UserStatus.SoftDeleted -> MaterialTheme.statusColor.error
                    null -> MaterialTheme.colorScheme.onSurface
                }
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            AccountField(
                label = stringResource(Res.string.Account_UserProfileTab_label_member_since),
                value = state.memberSince
            )
        }

        if (state.account?.user?.address == null && !isEditingAddress) {
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            SupernovaTextAction(
                label = stringResource(Res.string.Account_UserProfileTab_label_add_home_address),
                onClick = { onEvent(AccountEvent.EnterEditMode("user-address")) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(MaterialTheme.spacing.large))

        SupernovaTextAction(
            label = stringResource(Res.string.label_sign_out),
            onClick = { onEvent(AccountEvent.SignOut) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.mediumLarge))

        val redColor = MaterialTheme.statusColor.error
        SupernovaTextAction(
            label = stringResource(Res.string.screen_Account_delete_action),
            color = redColor,
            onClick = {
                dialogState.showConfirmation(
                    title = deleteTitle,
                    message = deleteMessage,
                    primaryActionColor = redColor,
                    confirmLabel = Res.string.screen_Account_dialog_delete_action,
                    dismissLabel = Res.string.screen_Account_dialog_cancel_action,
                    onConfirm = { onEvent(AccountEvent.DeactivateAccount) },
                    onDismiss = { dialogState.hide() }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.large))
    }
}
