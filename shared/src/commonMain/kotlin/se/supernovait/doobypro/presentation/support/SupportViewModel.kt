package se.supernovait.doobypro.presentation.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.encodeURLParameter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.app.core.domain.observability.analytics.AnalyticsTracker
import se.supernovait.app.core.domain.observability.crash.CrashReporter
import se.supernovait.app.core.domain.observability.logging.LogExporter
import se.supernovait.app.core.domain.observability.logging.LogLevel
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.doobypro.AppConfig
import se.supernovait.doobypro.domain.model.support.SupportRequestType
import se.supernovait.doobypro.domain.repository.AccountRepository
import se.supernovait.doobypro.domain.util.LogTags

/**
 * ViewModel for the Support Center screen, handling support requests and FAQ search state.
 */
class SupportViewModel(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val analyticsTracker: AnalyticsTracker,
    private val logExporter: LogExporter,
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportState())
    val uiState: StateFlow<SupportState> = _uiState.asStateFlow()

    fun onEvent(event: SupportEvent) {
        when (event) {
            is SupportEvent.SelectTab -> {
                logger.debug("Selecting support tab index: ${event.index}", tag = LogTags.SUPPORT_VM)
                _uiState.update { it.copy(selectedTab = event.index) }
            }
            is SupportEvent.UpdateSearchQuery -> _uiState.update { it.copy(searchQuery = event.query) }
            is SupportEvent.UpdateRequestType -> {
                logger.debug("Updating support request type: ${event.type}", tag = LogTags.SUPPORT_VM)
                _uiState.update { it.copy(requestType = event.type) }
            }
            is SupportEvent.UpdateMessage -> _uiState.update { it.copy(message = event.message) }
            is SupportEvent.UpdateWantsCallback -> _uiState.update { it.copy(wantsCallback = event.wantsCallback) }
            is SupportEvent.SubmitRequest -> {
                logger.info("Submitting support request of type: ${_uiState.value.requestType}", tag = LogTags.SUPPORT_VM)
                crashReporter.log("Submitting support request of type: ${_uiState.value.requestType}")
                analyticsTracker.trackEvent("support_request_submitted", mapOf("request_type" to _uiState.value.requestType.name))
                viewModelScope.launch {
                    val state = _uiState.value
                    var accountId = ""
                    var userId = ""
                    var userName = ""
                    var userEmail = ""
                    var userPhone = ""

                    val userIdResult = authRepository.getCurrentUserId()
                    if (userIdResult is Result.Success) {
                        val accountResult = accountRepository.getAccountByUserId(userIdResult.data)
                        accountId = accountResult.getOrNull()?.id.orEmpty()

                        val userResult = authRepository.getUserById(userIdResult.data)
                        if (userResult is Result.Success) {
                            val user = userResult.data

                            userId = user.id.orEmpty()
                            userName = "${user.firstname} ${user.lastname}".trim()
                            userEmail = user.email
                            userPhone = user.phoneNumber.orEmpty()
                        }
                    }

                    val logsAttachment = if (state.requestType == SupportRequestType.BUG_REPORT) {
                        val errorLogs = logExporter.getLogs(LogLevel.ERROR)
                            .ifEmpty { logExporter.getLogs(LogLevel.WARN) }
                            .ifEmpty { logExporter.getLogs() }
                            .takeLast(30)

                        buildString {
                            appendLine("--- ATTACHED ERROR LOGS ---")
                            if (errorLogs.isEmpty()) {
                                appendLine("No recorded error logs found.")
                            } else {
                                errorLogs.forEach { entry ->
                                    appendLine("[${entry.level.name}] [${entry.tag}] ${entry.message}")
                                    entry.throwable?.let { throwable ->
                                        appendLine("   Exception: ${throwable.message ?: throwable::class.simpleName}")
                                    }
                                }
                            }
                        }
                    } else ""

                    val subject = "[Support Request - ${state.requestType.name}] Dooby Pro"
                    val body = buildString {
                        appendLine("Request Type: ${state.requestType.name}")
                        appendLine("Account ID: $accountId")
                        appendLine("User ID: $userId")
                        appendLine("User Name: $userName")
                        appendLine("User Email: $userEmail")
                        appendLine("User Phone: $userPhone")
                        appendLine("Wants Callback: ${if (state.wantsCallback) "Yes" else "No"}")
                        appendLine("App Version: ${AppConfig.VERSION_NAME} (${AppConfig.VERSION_CODE})")
                        appendLine()
                        appendLine("Message:")
                        appendLine(state.message)
                        if (logsAttachment.isNotBlank()) {
                            appendLine()
                            append(logsAttachment)
                        }
                    }

                    val mailtoUrl = "mailto:${AppConfig.SUPPORT_EMAIL}?subject=${uriEncode(subject)}&body=${uriEncode(body)}"
                    logger.info("Support request prepared successfully, launching email client", tag = LogTags.SUPPORT_VM)
                    event.onOpenEmail(mailtoUrl)
                }
            }
        }
    }

    private fun uriEncode(value: String) = value.encodeURLParameter()
}
