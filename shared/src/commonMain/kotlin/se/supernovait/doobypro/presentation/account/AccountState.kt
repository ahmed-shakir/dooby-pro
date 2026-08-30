package se.supernovait.doobypro.presentation.account

import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import se.supernovait.doobypro.domain.model.Account

/**
 * UI state for the Account screens.
 *
 * @property account The unified account model containing user and company data.
 * @property isLoading Whether the account data is being fetched.
 * @property isSaving Whether a profile update is in progress.
 * @property error An optional error message to display as a [StringResource].
 * @property editFirstName Current value of the first name in edit mode.
 * @property editLastName Current value of the last name in edit mode.
 * @property editBirthDate Current value of the birth date (ISO string) in edit mode.
 * @property editEmail Current value of the user email in edit mode.
 * @property editPhone Current value of the user phone in edit mode.
 * @property editProfileImageUrl Current value of the profile image URL in edit mode.
 * @property editCompanyLegalName Current value of the company legal name in edit mode.
 * @property editCompanyDisplayName Current value of the company display name in edit mode.
 * @property editCompanyLicenseNumber Current value of the company license number in edit mode.
 * @property editCompanyEmail Current value of the company email in edit mode.
 * @property editCompanyPhone Current value of the company phone in edit mode.
 * @property editCompanyAddressStreet Current value of the street address in edit mode.
 * @property editCompanyAddressCity Current value of the city in edit mode.
 * @property editCompanyAddressSubdivision Current value of the subdivision in edit mode.
 * @property editCompanyAddressPostalCode Current value of the postal code in edit mode.
 * @property editCompanyAddressCountry Current value of the country in edit mode.
 * @property editCompanyNotes Current value of the location notes in edit mode.
 * @property editCompanyLogoUrl Current value of the company logo URL in edit mode.
 * @property currentTab The currently selected tab in the account view.
 * @property editingCardId The ID of the card currently in edit mode, or null if none.
 * @property expandedAgreementIds Set of agreement IDs that are currently expanded in the UI.
 * @property memberSince Formatted string representing when the user joined.
 * @property registeredSince Formatted string representing when the company was registered.
 */
data class AccountState(
    val account: Account? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: StringResource? = null,
    
    // Form states (editable copies)
    val editUserFirstName: String = "",
    val editUserLastName: String = "",
    val editUserBirthDate: LocalDate? = null,
    val editUserEmail: String = "",
    val editUserPhone: String = "",
    val editUserAddressStreet: String = "",
    val editUserAddressCity: String = "",
    val editUserAddressSubdivision: String = "",
    
    val editCompanyLegalName: String = "",
    val editCompanyDisplayName: String = "",
    val editCompanyLicenseNumber: String = "",
    val editCompanyEmail: String = "",
    val editCompanyPhone: String = "",
    val editCompanyAddressStreet: String = "",
    val editCompanyAddressCity: String = "",
    val editCompanyAddressSubdivision: String = "",
    val editCompanyAddressPostalCode: String = "",
    val editCompanyAddressCountry: String = "",
    val editCompanyNotes: String = "",
    val editCompanyLogoUrl: String? = null,

    // UI state
    val currentTab: AccountTab = AccountTab.USER_PROFILE,
    val editingCardId: String? = null,
    val expandedAgreementIds: Set<String> = emptySet(),
    
    val memberSince: String = "",
    val registeredSince: String = ""
)

enum class AccountTab {
    USER_PROFILE,
    COMPANY_PROFILE,
    LICENSE,
    AGREEMENTS
}
