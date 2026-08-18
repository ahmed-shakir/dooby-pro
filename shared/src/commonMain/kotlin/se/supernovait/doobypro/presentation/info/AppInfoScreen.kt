package se.supernovait.doobypro.presentation.info

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.app_action_back_label
import doobypro.shared.generated.resources.app_developer_name
import doobypro.shared.generated.resources.app_logo_content_description
import doobypro.shared.generated.resources.app_name
import doobypro.shared.generated.resources.app_version
import doobypro.shared.generated.resources.ic_app_icon
import doobypro.shared.generated.resources.ic_arrow_back
import doobypro.shared.generated.resources.screen_AppInfo_check_updates_label
import doobypro.shared.generated.resources.screen_AppInfo_copyright
import doobypro.shared.generated.resources.screen_AppInfo_description
import doobypro.shared.generated.resources.screen_AppInfo_developer_label
import doobypro.shared.generated.resources.screen_AppInfo_legal_section_title
import doobypro.shared.generated.resources.screen_AppInfo_licenses_label
import doobypro.shared.generated.resources.screen_AppInfo_support_action_label
import doobypro.shared.generated.resources.screen_AppInfo_support_note
import doobypro.shared.generated.resources.screen_AppInfo_support_section_title
import doobypro.shared.generated.resources.screen_AppInfo_website_label
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.SupernovaIcon
import se.supernovait.app.core.ui.component.action.SupernovaButton
import se.supernovait.app.core.ui.component.action.SupernovaIconButton
import se.supernovait.app.core.ui.component.action.SupernovaTextButton
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.sizing
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.AppConfig
import se.supernovait.doobypro.presentation.common.preview.ScreenPreviewContainer

@Composable
fun AppInfoScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Box(modifier = Modifier.fillMaxSize().then(modifier)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

            AppHeader()

            AppDescription()

            DeveloperInfo(
                onVisitWebsite = { uriHandler.openUri(AppConfig.WEBSITE_URL) }
            )

            LegalSection(
                onPrivacyPolicyClick = { /* TODO: link to Privacy Policy */ },
                onTermsOfServiceClick = { /* TODO: link to Terms Of Service */ },
                onLicensesClick = { uriHandler.openUri(AppConfig.LICENSE_URL) }
            )

            // TODO: add when link is available
            /*SupportNote(
                onFeedbackClick = { *//* TODO: link to support form *//* }
            )*/

            // TODO: add when link is available
            // UpdateSection()

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Copyright()
        }

        // Modern floating back button
        SupernovaIconButton(
            icon = Res.drawable.ic_arrow_back,
            contentDescription = stringResource(Res.string.app_action_back_label),
            onClick = onBack,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = MaterialTheme.spacing.small, start = MaterialTheme.spacing.small)
                .align(Alignment.TopStart)
        )
    }
}

@Composable
private fun AppHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        SupernovaIcon(
            icon = Res.drawable.ic_app_icon,
            contentDescription = Res.string.app_logo_content_description,
            size = MaterialTheme.sizing.icon.x4Large,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
        )
        SupernovaTitle(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.headlineMedium
        )
        SupernovaLabel(
            text = stringResource(Res.string.app_version, AppConfig.VERSION_NAME),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AppDescription() {
    SupernovaLabel(
        text = stringResource(Res.string.screen_AppInfo_description),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small)
    )
}

@Composable
private fun DeveloperInfo(onVisitWebsite: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            SupernovaLabel(
                text = stringResource(Res.string.screen_AppInfo_developer_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            SupernovaLabel(
                text = stringResource(Res.string.app_developer_name),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            SupernovaTextButton(
                label = stringResource(Res.string.screen_AppInfo_website_label),
                onClick = onVisitWebsite
            )
        }
    }
}

@Composable
private fun LegalSection(
    onPrivacyPolicyClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit,
    onLicensesClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SupernovaLabel(
            text = stringResource(Res.string.screen_AppInfo_legal_section_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
        )
        
        HorizontalDivider(thickness = 0.5.dp)

        // TODO: add when links is available
        /*LegalLinkRow(
            label = stringResource(Res.string.screen_AppInfo_privacy_policy_label),
            onClick = onPrivacyPolicyClick
        )
        HorizontalDivider(thickness = 0.5.dp)
        
        LegalLinkRow(
            label = stringResource(Res.string.screen_AppInfo_terms_of_service_label),
            onClick = onTermsOfServiceClick
        )
        HorizontalDivider(thickness = 0.5.dp)*/
        
        LegalLinkRow(
            label = stringResource(Res.string.screen_AppInfo_licenses_label),
            onClick = onLicensesClick
        )
        HorizontalDivider(thickness = 0.5.dp)
    }
}

@Composable
private fun LegalLinkRow(label: String, onClick: () -> Unit) {
    SupernovaTextButton(
        label = label,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp) // Minimum touch target
    )
}

@Composable
private fun SupportNote(onFeedbackClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            SupernovaLabel(
                text = stringResource(Res.string.screen_AppInfo_support_section_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            SupernovaLabel(
                text = stringResource(Res.string.screen_AppInfo_support_note),
                style = MaterialTheme.typography.bodyMedium
            )
            SupernovaButton(
                label = stringResource(Res.string.screen_AppInfo_support_action_label),
                shape = MaterialTheme.shapes.extraSmall,
                onClick = onFeedbackClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun UpdateSection() {
    SupernovaTextButton(
        label = stringResource(Res.string.screen_AppInfo_check_updates_label),
        onClick = { /* TODO: connect to app store */ }
    )
}

@Composable
private fun Copyright() {
    SupernovaLabel(
        text = stringResource(Res.string.screen_AppInfo_copyright),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    ScreenPreviewContainer {
        AppInfoScreen(onBack = {})
    }
}
