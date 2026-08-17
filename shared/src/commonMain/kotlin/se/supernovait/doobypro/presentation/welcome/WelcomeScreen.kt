package se.supernovait.doobypro.presentation.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.app_logo_content_description
import doobypro.shared.generated.resources.ic_app_icon
import doobypro.shared.generated.resources.screen_Welcome_action_app_info_label
import doobypro.shared.generated.resources.screen_Welcome_action_sign_in_label
import doobypro.shared.generated.resources.screen_Welcome_action_sign_up_label
import doobypro.shared.generated.resources.screen_Welcome_subtitle
import doobypro.shared.generated.resources.screen_Welcome_title
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.SupernovaIcon
import se.supernovait.app.core.ui.component.action.SupernovaButton
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.action.SupernovaTextButton
import se.supernovait.app.core.ui.component.text.SupernovaSubtitle
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.sizing
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.common.preview.ScreenPreviewContainer

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.large)
                .then(modifier)
        ) {
            BrandingSection()
            Spacer(modifier = Modifier.weight(1f))
            ActionButtons()
            InfoFooter()
        }
    }
}

@Composable
private fun BrandingSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SupernovaIcon(
            icon = Res.drawable.ic_app_icon,
            contentDescription = Res.string.app_logo_content_description,
            size = MaterialTheme.sizing.icon.x3Large,
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.extraLarge)
        )
        SupernovaTitle(text = Res.string.screen_Welcome_title)
        SupernovaSubtitle(
            text = Res.string.screen_Welcome_subtitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
        )
    }
}

@Composable
private fun ActionButtons() {
    SupernovaButton(
        label = stringResource(Res.string.screen_Welcome_action_sign_in_label),
        textStyle = MaterialTheme.typography.titleLarge,
        shape = MaterialTheme.shapes.small,
        onClick = { /* TODO: add action */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small)
    )

    SupernovaOutlinedButton(
        label = stringResource(Res.string.screen_Welcome_action_sign_up_label),
        textStyle = MaterialTheme.typography.titleLarge,
        shape = MaterialTheme.shapes.extraSmall,
        onClick = { /* TODO: add action */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small)
    )
}

@Composable
private fun InfoFooter() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = MaterialTheme.spacing.extraLarge)
    ) {
        HorizontalDivider()

        SupernovaTextButton(
            label = stringResource(Res.string.screen_Welcome_action_app_info_label),
            onClick = { /* TODO: add action */ }
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    ScreenPreviewContainer {
        WelcomeScreen()
    }
}
