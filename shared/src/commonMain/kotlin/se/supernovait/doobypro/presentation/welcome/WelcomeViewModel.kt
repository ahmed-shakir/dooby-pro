package se.supernovait.doobypro.presentation.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.sheet_SignIn_error_empty_username
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.event.AppEvent
import se.supernovait.app.core.domain.extension.error.asString

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
                _uiState.update { it.copy(showSignInForm = true, signInError = null) }
            }
            WelcomeScreenEvent.HideSignInForm -> {
                _uiState.update { it.copy(showSignInForm = false, signInError = null) }
            }
            is WelcomeScreenEvent.SignIn -> {
                signIn(event.username)
            }
            else -> { /* Navigation events are handled by the screen/nav graph */ }
        }
    }

    private fun signIn(username: String) {
        _uiState.update { it.copy(signInError = null) }

        if (username.isBlank()) {
            viewModelScope.launch {
                val error = getString(Res.string.sheet_SignIn_error_empty_username)
                _uiState.update { it.copy(signInError = error) }
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSigningIn = true, signInError = null) }
            
            when (val result = authRepository.signIn(username)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSigningIn = false, showSignInForm = false) }
                    _events.send(AppEvent.SignIn)
                }
                is Result.Failure -> {
                    _uiState.update { it.copy(
                        isSigningIn = false,
                        signInError = result.error.asString()
                    ) }
                    _events.send(AppEvent.Failure(result.error))
                }
            }
        }
    }
}
