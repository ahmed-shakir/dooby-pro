package se.supernovait.doobypro.presentation.support.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_support_form_message
import doobypro.shared.generated.resources.screen_support_form_request_type
import doobypro.shared.generated.resources.screen_support_form_submit
import doobypro.shared.generated.resources.screen_support_form_wants_callback
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaButton
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.selection.SupernovaSelectField
import se.supernovait.app.core.ui.component.selection.SupernovaToggle
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.support.SupportRequestType
import se.supernovait.doobypro.presentation.support.SupportEvent
import se.supernovait.doobypro.presentation.support.SupportState

@Composable
fun ContactSupportTab(
    uiState: SupportState,
    onEvent: (SupportEvent) -> Unit,
    onOpenEmail: (String) -> Unit
) {
    val requestTypeLabels = SupportRequestType.entries.associateWith { stringResource(it.label) }

    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        SupernovaSelectField(
            options = SupportRequestType.entries,
            selectedOption = uiState.requestType,
            optionLabel = { requestTypeLabels[it] ?: "" },
            onOptionSelected = { onEvent(SupportEvent.UpdateRequestType(it)) },
            label = stringResource(Res.string.screen_support_form_request_type)
        )

        SupernovaTextField(
            value = uiState.message,
            onValueChange = { value, _ -> onEvent(SupportEvent.UpdateMessage(value)) },
            label = stringResource(Res.string.screen_support_form_message),
            isMultiline = true,
            modifier = Modifier.height(150.dp)
        )

        SupernovaToggle(
            label = Res.string.screen_support_form_wants_callback,
            checked = uiState.wantsCallback,
            onCheckedChange = { onEvent(SupportEvent.UpdateWantsCallback(it)) }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        SupernovaButton(
            label = stringResource(Res.string.screen_support_form_submit),
            onClick = { onEvent(SupportEvent.SubmitRequest(onOpenEmail)) },
            shape = MaterialTheme.shapes.extraSmall,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
