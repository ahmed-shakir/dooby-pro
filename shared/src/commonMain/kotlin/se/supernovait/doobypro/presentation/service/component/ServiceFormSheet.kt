package se.supernovait.doobypro.presentation.service.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.label_edit
import doobypro.shared.generated.resources.label_save
import doobypro.shared.generated.resources.screen_Service_action_add_service
import doobypro.shared.generated.resources.screen_Service_field_description
import doobypro.shared.generated.resources.screen_Service_field_price
import doobypro.shared.generated.resources.screen_Service_field_title
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.Service

@Composable
fun ServiceFormSheet(
    service: Service,
    currency: String,
    onSave: (String, String, Long) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by remember { mutableStateOf(service.title) }
    var description by remember { mutableStateOf(service.description) }
    var priceValue by remember { mutableStateOf(service.price.value) }

    val isNew = service.id == null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
    ) {
        SupernovaTitle(
            text = if (isNew) stringResource(Res.string.screen_Service_action_add_service) else stringResource(Res.string.label_edit),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaTextField(
            label = stringResource(Res.string.screen_Service_field_title),
            value = title,
            onValueChange = { v, _ -> title = v },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaTextField(
            label = stringResource(Res.string.screen_Service_field_description),
            value = description,
            onValueChange = { v, _ -> description = v },
            isMultiline = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaTextField(
            label = stringResource(Res.string.screen_Service_field_price, currency),
            value = priceValue,
            onValueChange = { v, _ -> 
                // Basic validation for decimal input
                if (v.isEmpty() || v.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                    priceValue = v
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.large))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            if (onDelete != null && !isNew) {
                SupernovaOutlinedButton(
                    label = Res.string.label_delete,
                    onClick = onDelete,
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(Modifier.weight(1f))
            }

            SupernovaOutlinedButton(
                label = Res.string.label_save,
                onClick = { 
                    val rawPrice = (priceValue.toDoubleOrNull() ?: 0.0) * 100
                    onSave(title, description, rawPrice.toLong()) 
                },
                enabled = title.isNotBlank() && description.isNotBlank() && priceValue.isNotBlank(),
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
