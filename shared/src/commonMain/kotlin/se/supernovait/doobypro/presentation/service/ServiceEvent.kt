package se.supernovait.doobypro.presentation.service

import se.supernovait.doobypro.domain.model.Service

/**
 * Events for the service management domain.
 */
sealed interface ServiceEvent {
    /** Re-fetches the list of all services. */
    data object LoadServices : ServiceEvent

    /**
     * Sets the service that is currently being created or updated.
     * 
     * @param service The service instance to load into the form.
     */
    data class EditService(val service: Service) : ServiceEvent

    /**
     * Saves a service to the database.
     */
    data class SaveService(
        val title: String,
        val description: String,
        val priceValue: Long
    ) : ServiceEvent

    /**
     * Permanently deletes a service.
     */
    data class DeleteService(val service: Service) : ServiceEvent
}
