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
import doobypro.shared.generated.resources.Account_LicenseTab_error_not_found
import doobypro.shared.generated.resources.Account_LicenseTab_field_expiry_date
import doobypro.shared.generated.resources.Account_LicenseTab_field_id
import doobypro.shared.generated.resources.Account_LicenseTab_field_issue_date
import doobypro.shared.generated.resources.Account_LicenseTab_field_status
import doobypro.shared.generated.resources.Account_LicenseTab_field_tier
import doobypro.shared.generated.resources.Account_LicenseTab_label_download_license
import doobypro.shared.generated.resources.Account_LicenseTab_section_description
import doobypro.shared.generated.resources.Account_LicenseTab_section_details
import doobypro.shared.generated.resources.Account_LicenseTab_section_validity
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_download
import doobypro.shared.generated.resources.label_actions
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.domain.model.license.LicenseStatus
import se.supernovait.app.core.ui.component.action.SupernovaButton
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.app.core.ui.theme.statusColor
import se.supernovait.doobypro.presentation.account.AccountState
import se.supernovait.doobypro.presentation.account.component.AccountCard
import se.supernovait.doobypro.presentation.account.component.AccountField

@Composable
fun LicenseTab(
    state: AccountState,
    modifier: Modifier = Modifier
) {
    val license = state.account?.license

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.medium)
    ) {
        if (license == null) {
            SupernovaLabel(
                text = stringResource(Res.string.Account_LicenseTab_error_not_found),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(MaterialTheme.spacing.medium)
            )
        } else {
            // License Details Card
            AccountCard(
                title = stringResource(Res.string.Account_LicenseTab_section_details),
                isSaving = false,
                isEditing = false,
                onEditClick = null
            ) {
                AccountField(
                    label = stringResource(Res.string.Account_LicenseTab_field_id),
                    value = license.id,
                    isLocked = true
                )
                Spacer(Modifier.height(MaterialTheme.spacing.medium))
                AccountField(
                    label = stringResource(Res.string.Account_LicenseTab_field_tier),
                    value = license.tier.toString()
                )
                Spacer(Modifier.height(MaterialTheme.spacing.medium))
                AccountField(
                    label = stringResource(Res.string.Account_LicenseTab_field_status),
                    value = license.licenseStatus.toString(),
                    isStatus = true,
                    valueColor = when (license.licenseStatus) {
                        LicenseStatus.Active -> MaterialTheme.statusColor.success
                        LicenseStatus.Expired -> MaterialTheme.statusColor.error
                    }
                )
            }

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            // Validity Card
            AccountCard(
                title = stringResource(Res.string.Account_LicenseTab_section_validity),
                isSaving = false,
                isEditing = false,
                onEditClick = null
            ) {
                AccountField(
                    label = stringResource(Res.string.Account_LicenseTab_field_issue_date),
                    value = license.issueDate.toString()
                )
                Spacer(Modifier.height(MaterialTheme.spacing.medium))
                AccountField(
                    label = stringResource(Res.string.Account_LicenseTab_field_expiry_date),
                    value = license.expiryDate.toString()
                )
            }

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            // Description Card
            AccountCard(
                title = stringResource(Res.string.Account_LicenseTab_section_description),
                isSaving = false,
                isEditing = false,
                onEditClick = null
            ) {
                SupernovaLabel(
                    text = license.description,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
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
                    label = Res.string.Account_LicenseTab_label_download_license,
                    onClick = { /* TODO: add PDF download action */ },
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(MaterialTheme.spacing.large))
        }
    }
}
