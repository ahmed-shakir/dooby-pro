package se.supernovait.doobypro.presentation.order

import org.jetbrains.compose.resources.StringResource
import se.supernovait.app.core.domain.auth.User
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.storage.StorageLocation

data class OrderState(
    val orders: List<Order> = emptyList(),
    val customers: List<User> = emptyList(),
    val services: List<Service> = emptyList(),
    val storageLocations: List<StorageLocation> = emptyList(),
    val editingOrder: Order? = null,
    val error: StringResource? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false
)
