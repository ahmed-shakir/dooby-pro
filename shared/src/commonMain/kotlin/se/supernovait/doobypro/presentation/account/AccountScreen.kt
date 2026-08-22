package se.supernovait.doobypro.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Account_agreements_label
import doobypro.shared.generated.resources.screen_Account_company_profile_label
import doobypro.shared.generated.resources.screen_Account_license_label
import doobypro.shared.generated.resources.screen_Account_user_profile_label
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.doobypro.presentation.account.component.ProfileHeroSection
import se.supernovait.doobypro.presentation.account.tab.AgreementTab
import se.supernovait.doobypro.presentation.account.tab.CompanyProfileTab
import se.supernovait.doobypro.presentation.account.tab.LicenseTab
import se.supernovait.doobypro.presentation.account.tab.UserProfileTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    state: AccountState,
    onEvent: (AccountEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = state.currentTab.ordinal,
        pageCount = { AccountTab.entries.size }
    )
    val coroutineScope = rememberCoroutineScope()

    // Sync pager when state changes (e.g. from external sources)
    LaunchedEffect(state.currentTab) {
        if (pagerState.currentPage != state.currentTab.ordinal) {
            pagerState.animateScrollToPage(state.currentTab.ordinal)
        }
    }

    // Sync state when pager settles on a new page
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                if (state.currentTab.ordinal != page) {
                    onEvent(AccountEvent.SwitchTab(AccountTab.entries[page]))
                }
            }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Profile Hero Section
        ProfileHeroSection(state = state)

        // Tab Navigation
        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(pagerState.currentPage),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            AccountTab.entries.forEach { tab ->
                Tab(
                    selected = pagerState.currentPage == tab.ordinal,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(tab.ordinal)
                        }
                    },
                    text = {
                        val labelRes = when (tab) {
                            AccountTab.USER_PROFILE -> Res.string.screen_Account_user_profile_label
                            AccountTab.COMPANY_PROFILE -> Res.string.screen_Account_company_profile_label
                            AccountTab.LICENSE -> Res.string.screen_Account_license_label
                            AccountTab.AGREEMENTS -> Res.string.screen_Account_agreements_label
                        }
                        SupernovaLabel(
                            text = labelRes,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) { page ->
            when (page) {
                0 -> UserProfileTab(state = state, onEvent = onEvent)
                1 -> CompanyProfileTab(state = state, onEvent = onEvent)
                2 -> LicenseTab(state = state)
                3 -> AgreementTab(state = state, onEvent = onEvent)
            }
        }
    }
}
