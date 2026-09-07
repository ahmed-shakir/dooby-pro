package se.supernovait.doobypro.domain.model

import se.supernovait.app.core.domain.model.billing.Amount

/**
 * Domain model representing a customer service for laundry or dry cleaning.
 *
 * @property id The unique identifier for the service. Null for unsaved services.
 * @property title The human-readable name of the service (e.g., "Wash & Fold").
 * @property description A detailed description of what the service includes.
 * @property price The monetary cost of the service.
 */
data class Service(
    val id: String? = null,
    val title: String = "",
    val description: String = "",
    val price: Amount = Amount(0, "AED")
)
