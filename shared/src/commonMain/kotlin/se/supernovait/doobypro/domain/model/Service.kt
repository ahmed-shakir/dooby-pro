package se.supernovait.doobypro.domain.model

import se.supernovait.app.core.domain.model.billing.Amount

/**
 * Domain model representing a service offered by the platform.
 *
 * @property id The unique identifier for the service. Null for unsaved services.
 * @property title The title of the service.
 * @property description A detailed description of what the service provides.
 * @property price The cost associated with the service.
 */
data class Service(
    val id: String? = null,
    val title: String,
    val description: String,
    val price: Amount
)
