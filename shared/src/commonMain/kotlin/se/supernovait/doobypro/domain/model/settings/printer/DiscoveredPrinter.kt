package se.supernovait.doobypro.domain.model.settings.printer

import kotlinx.serialization.Serializable

@Serializable
data class DiscoveredPrinter(
    val name: String,
    val address: String
)