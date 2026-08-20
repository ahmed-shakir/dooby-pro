package se.supernovait.doobypro.domain.model.settings.printer

import kotlinx.serialization.Serializable

@Serializable
data class PrinterSettings(
    val connectionMethod: ConnectionMethod = ConnectionMethod.NETWORK,
    val printerIp: String? = null,
    val printerName: String? = null
)
