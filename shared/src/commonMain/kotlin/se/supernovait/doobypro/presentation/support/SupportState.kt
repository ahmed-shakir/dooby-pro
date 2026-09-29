package se.supernovait.doobypro.presentation.support

import se.supernovait.doobypro.domain.model.support.SupportRequestType

/**
 * UI state for the Support Center screen.
 */
data class SupportState(
    val selectedTab: Int = 0, // 0: Contact Support, 1: FAQs
    val searchQuery: String = "",
    val requestType: SupportRequestType = SupportRequestType.QUESTION,
    val message: String = "",
    val wantsCallback: Boolean = false,
    val isSubmitting: Boolean = false
)
