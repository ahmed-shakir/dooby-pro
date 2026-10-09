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
import se.supernovait.app.core.domain.observability.analytics.AnalyticsTracker
import se.supernovait.app.core.domain.observability.crash.CrashReporter
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.app.core.domain.observability.performance.PerformanceMonitor
import se.supernovait.app.core.domain.observability.performance.traceAsync
import se.supernovait.doobypro.domain.util.LogTags

class WelcomeViewModel(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val analyticsTracker: AnalyticsTracker,
    private val performanceMonitor: PerformanceMonitor,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<AppEvent>()
    val events = _events.receiveAsFlow()

    init {
        logger.info("WelcomeViewModel initialized", tag = LogTags.WELCOME_VM)
    }

    fun onEvent(event: WelcomeScreenEvent) {
        logger.debug("Handling event: $event", tag = LogTags.WELCOME_VM)
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
            logger.warn("Sign in attempted with empty username", tag = LogTags.WELCOME_VM)
            _uiState.update { it.copy(isUsernameEmpty = true) }
            return
        }

        viewModelScope.launch {
            performanceMonitor.traceAsync("WelcomeViewModel.signIn") {
                logger.info("Initiating sign in for username: $username", tag = LogTags.WELCOME_VM)
                _uiState.update { it.copy(isSigningIn = true) }
                
                when (val result = authRepository.signIn(username)) {
                    is Result.Success -> {
                        logger.info("Sign in succeeded for username: $username", tag = LogTags.WELCOME_VM)
                        crashReporter.log("Sign in succeeded for username $username in WelcomeViewModel")
                        analyticsTracker.setUserId(result.data.id ?: username)
                        analyticsTracker.trackEvent("sign_in_success", mapOf("username" to username))
                        _uiState.update { it.copy(isSigningIn = false, showSignInForm = false) }
                        _events.send(AppEvent.SignIn)
                    }
                    is Result.Failure -> {
                        logger.warn("Sign in failed for username: $username with error: ${result.error}", tag = LogTags.WELCOME_VM)
                        crashReporter.log("Sign in failed for username $username in WelcomeViewModel")
                        analyticsTracker.trackEvent("sign_in_failed", mapOf("username" to username, "error" to result.error.toString()))
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
}
