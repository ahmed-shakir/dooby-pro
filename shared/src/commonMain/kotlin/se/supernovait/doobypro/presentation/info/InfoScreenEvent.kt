package se.supernovait.doobypro.presentation.info

/**
 * Events for the App Info screen.
 */
sealed interface InfoScreenEvent {
    data object NavigateBack : InfoScreenEvent
    data object NavigateToSupport : InfoScreenEvent
}
