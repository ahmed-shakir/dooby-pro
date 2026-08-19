package se.supernovait.doobypro.presentation.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.event.AppEvent

class WelcomeViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<AppEvent>()
    val events = _events.receiveAsFlow()

    fun onEvent(event: WelcomeScreenEvent) {
        when (event) {
            WelcomeScreenEvent.ShowSignInForm -> {
                _uiState.update { it.copy(showSignInForm = true, signInError = null, isUsernameEmpty = false) }
            }
            WelcomeScreenEvent.HideSignInForm -> {
                _uiState.update { it.copy(showSignInForm = false, signInError = null, isUsernameEmpty = false) }
            }
            is WelcomeScreenEvent.SignIn -> {
                signIn(event.username)
            }
            else -> { /* Navigation events are handled by the screen/nav graph */ }
        }
    }

    private fun signIn(username: String) {
        _uiState.update { it.copy(signInError = null, isUsernameEmpty = false) }

        if (username.isBlank()) {
            _uiState.update { it.copy(isUsernameEmpty = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSigningIn = true) }
            
            when (val result = authRepository.signIn(username)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSigningIn = false, showSignInForm = false) }
                    _events.send(AppEvent.SignIn)
                }
                is Result.Failure -> {
                    _uiState.update { it.copy(
                        isSigningIn = false,
                        signInError = result.error
                    ) }
                    _events.send(AppEvent.Failure(result.error))
                }
            }
        }
    }
}
