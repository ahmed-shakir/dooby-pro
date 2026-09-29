package se.supernovait.doobypro.presentation.support

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_support_tab_contact
import doobypro.shared.generated.resources.screen_support_tab_faqs
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.support.tab.ContactSupportTab
import se.supernovait.doobypro.presentation.support.tab.FAQTab

/**
 * Support Center screen displaying a Contact Support request form and searchable FAQs separated by category.
 */
@Composable
fun SupportScreen(
    uiState: SupportState,
    onEvent: (SupportEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Column(modifier = modifier.fillMaxSize().padding(MaterialTheme.spacing.medium)) {
        PrimaryTabRow(selectedTabIndex = uiState.selectedTab) {
            Tab(
                selected = uiState.selectedTab == 0,
                onClick = { onEvent(SupportEvent.SelectTab(0)) },
                text = { Text(stringResource(Res.string.screen_support_tab_contact)) }
            )
            Tab(
                selected = uiState.selectedTab == 1,
                onClick = { onEvent(SupportEvent.SelectTab(1)) },
                text = { Text(stringResource(Res.string.screen_support_tab_faqs)) }
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        when (uiState.selectedTab) {
            0 -> ContactSupportTab(
                uiState = uiState,
                onEvent = onEvent,
                onOpenEmail = { mailtoUrl ->
                    runCatching { uriHandler.openUri(mailtoUrl) }
                }
            )
            1 -> FAQTab(uiState = uiState, onEvent = onEvent)
        }
    }
}
