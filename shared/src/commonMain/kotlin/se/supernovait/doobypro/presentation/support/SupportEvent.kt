package se.supernovait.doobypro.presentation.support

import se.supernovait.doobypro.domain.model.support.SupportRequestType

/**
 * Events for the Support Center screen.
 */
sealed interface SupportEvent {
    data class SelectTab(val index: Int) : SupportEvent
    data class UpdateSearchQuery(val query: String) : SupportEvent
    data class UpdateRequestType(val type: SupportRequestType) : SupportEvent
    data class UpdateMessage(val message: String) : SupportEvent
    data class UpdateWantsCallback(val wantsCallback: Boolean) : SupportEvent
    data class SubmitRequest(val onOpenEmail: (String) -> Unit) : SupportEvent
}
