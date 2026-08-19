package se.supernovait.doobypro.presentation.welcome

import se.supernovait.app.core.domain.error.AuthError

data class WelcomeState(
    val showSignInForm: Boolean = false,
    val isSigningIn: Boolean = false,
    val signInError: AuthError? = null,
    val isUsernameEmpty: Boolean = false
)
