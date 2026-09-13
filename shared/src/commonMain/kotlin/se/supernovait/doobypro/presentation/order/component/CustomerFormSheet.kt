package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Contact_details_field_email
import doobypro.shared.generated.resources.Contact_details_field_phone
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.label_cancel
import doobypro.shared.generated.resources.label_save
import doobypro.shared.generated.resources.screen_AccountSetup_field_first_name
import doobypro.shared.generated.resources.screen_AccountSetup_field_last_name
import doobypro.shared.generated.resources.screen_Order_label_add_new_customer
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.spacing

@Composable
fun CustomerFormSheet(
    onSave: (User) -> Unit,
    onCancel: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.medium)
    ) {
        SupernovaTitle(
            text = stringResource(Res.string.screen_Order_label_add_new_customer),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaTextField(
            label = stringResource(Res.string.screen_AccountSetup_field_first_name),
            value = firstName,
            onValueChange = { v, _ -> firstName = v },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        SupernovaTextField(
            label = stringResource(Res.string.screen_AccountSetup_field_last_name),
            value = lastName,
            onValueChange = { v, _ -> lastName = v },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        SupernovaTextField(
            label = stringResource(Res.string.Contact_details_field_email),
            value = email,
            onValueChange = { v, _ -> email = v },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        SupernovaTextField(
            label = stringResource(Res.string.Contact_details_field_phone),
            value = phone,
            onValueChange = { v, _ -> phone = v },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.large))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            SupernovaOutlinedButton(
                label = Res.string.label_cancel,
                onClick = onCancel,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.weight(1f)
            )

            val isValid = firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank()

            SupernovaOutlinedButton(
                label = Res.string.label_save,
                onClick = {
                    onSave(
                        User(
                            username = "${firstName}_$lastName",
                            firstname = firstName,
                            lastname = lastName,
                            email = email,
                            phoneNumber = phone,
                            birthdate = null
                        )
                    )
                },
                enabled = isValid,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(Modifier.height(MaterialTheme.spacing.x5Large))
    }
}
