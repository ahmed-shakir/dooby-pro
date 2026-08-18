package se.supernovait.doobypro.presentation.welcome

import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.location.Address
import se.supernovait.doobypro.domain.model.Company

sealed interface WelcomeScreenEvent {
    data object NavigateToAppInfo: WelcomeScreenEvent
    data object NavigateToAccountSetupWizard: WelcomeScreenEvent
    data class SignIn(val username: String): WelcomeScreenEvent
    data class SignUp(val user: User, val company: Company, val address: Address): WelcomeScreenEvent
    data object ShowSignInForm: WelcomeScreenEvent
    data object HideSignInForm: WelcomeScreenEvent
}
