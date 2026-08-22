package se.supernovait.doobypro.presentation.account.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Account_AgreementTab_label_download_all
import doobypro.shared.generated.resources.Account_AgreementTab_label_no_agreements
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_download
import doobypro.shared.generated.resources.label_actions
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaButton
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.account.AccountEvent
import se.supernovait.doobypro.presentation.account.AccountState
import se.supernovait.doobypro.presentation.account.component.AccountCard
import se.supernovait.doobypro.presentation.account.component.AgreementAccordionItem

@Composable
fun AgreementTab(
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.medium)
    ) {
        val agreements = state.account?.agreements ?: emptyList()

        if (agreements.isEmpty()) {
            SupernovaLabel(
                text = stringResource(Res.string.Account_AgreementTab_label_no_agreements),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(MaterialTheme.spacing.medium)
            )
        } else {
            agreements.forEachIndexed { index, agreement ->
                val id = agreement.id ?: return@forEachIndexed
                AgreementAccordionItem(
                    agreement = agreement,
                    isExpanded = state.expandedAgreementIds.contains(id),
                    onToggle = { onEvent(AccountEvent.ToggleAgreementExpansion(id)) }
                )
                if (index < agreements.size - 1) {
                    Spacer(Modifier.height(MaterialTheme.spacing.small))
                }
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // Actions Card
        AccountCard(
            title = stringResource(Res.string.label_actions),
            isSaving = false,
            isEditing = false,
            onEditClick = null
        ) {
            SupernovaButton(
                icon = Res.drawable.ic_download,
                label = Res.string.Account_AgreementTab_label_download_all,
                onClick = { /* TODO: add PDF download action */ },
                shape = MaterialTheme.shapes.extraSmall,
                enabled = agreements.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(MaterialTheme.spacing.large))
    }
}
