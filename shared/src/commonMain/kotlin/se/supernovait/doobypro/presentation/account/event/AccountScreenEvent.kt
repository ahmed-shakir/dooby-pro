package se.supernovait.doobypro.presentation.account.event

sealed interface AccountScreenEvent {
    data object NavigateToUserProfile : AccountScreenEvent
    data object NavigateToCompanyProfile : AccountScreenEvent
    data object NavigateToLicense : AccountScreenEvent
    data object NavigateToAgreements : AccountScreenEvent
}
