package se.supernovait.doobypro.domain.model.settings.printer

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.connection_method_bluetooth
import doobypro.shared.generated.resources.connection_method_network
import org.jetbrains.compose.resources.StringResource

enum class ConnectionMethod(val label: StringResource) {
    NETWORK(Res.string.connection_method_network),
    BLUETOOTH(Res.string.connection_method_bluetooth)
}
