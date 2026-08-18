package se.supernovait.doobypro.presentation.welcome

data class WelcomeState(
    val showSignInForm: Boolean = false,
    val isSigningIn: Boolean = false,
    val signInError: String? = null
)
