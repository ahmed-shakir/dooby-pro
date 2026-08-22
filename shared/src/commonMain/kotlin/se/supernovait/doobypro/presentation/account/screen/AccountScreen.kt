package se.supernovait.doobypro.presentation.account.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Account_agreements_desc
import doobypro.shared.generated.resources.screen_Account_agreements_label
import doobypro.shared.generated.resources.screen_Account_company_profile_desc
import doobypro.shared.generated.resources.screen_Account_company_profile_label
import doobypro.shared.generated.resources.screen_Account_license_desc
import doobypro.shared.generated.resources.screen_Account_license_label
import doobypro.shared.generated.resources.screen_Account_user_profile_desc
import doobypro.shared.generated.resources.screen_Account_user_profile_label
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.list.SupernovaListItem
import se.supernovait.doobypro.presentation.account.event.AccountScreenEvent

@Composable
fun AccountScreen(
    onEvent: (AccountScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxSize().then(modifier)) {
        SupernovaListItem(
            title = stringResource(Res.string.screen_Account_user_profile_label),
            description = stringResource(Res.string.screen_Account_user_profile_desc),
            onClick = { onEvent(AccountScreenEvent.NavigateToUserProfile) }
        )
        SupernovaListItem(
            title = stringResource(Res.string.screen_Account_company_profile_label),
            description = stringResource(Res.string.screen_Account_company_profile_desc),
            onClick = { onEvent(AccountScreenEvent.NavigateToCompanyProfile) }
        )
        SupernovaListItem(
            title = stringResource(Res.string.screen_Account_license_label),
            description = stringResource(Res.string.screen_Account_license_desc),
            onClick = { onEvent(AccountScreenEvent.NavigateToLicense) }
        )
        SupernovaListItem(
            title = stringResource(Res.string.screen_Account_agreements_label),
            description = stringResource(Res.string.screen_Account_agreements_desc),
            onClick = { onEvent(AccountScreenEvent.NavigateToAgreements) }
        )
    }
}
