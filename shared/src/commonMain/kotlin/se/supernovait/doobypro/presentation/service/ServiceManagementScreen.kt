package se.supernovait.doobypro.presentation.service

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_add
import doobypro.shared.generated.resources.label_cancel
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.screen_Service_action_add_service
import doobypro.shared.generated.resources.screen_Service_dialog_delete_message
import doobypro.shared.generated.resources.screen_Service_dialog_delete_title
import doobypro.shared.generated.resources.screen_Service_empty_state
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.SupernovaEmptyState
import se.supernovait.app.core.ui.component.fab.LocalFabState
import se.supernovait.app.core.ui.component.modal.LocalBottomSheetState
import se.supernovait.app.core.ui.component.modal.dialog.LocalDialogState
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.presentation.service.component.ServiceFormSheet
import se.supernovait.doobypro.presentation.service.component.ServiceItem

@Composable
fun ServiceManagementScreen(
    uiState: ServiceState,
    onEvent: (ServiceEvent) -> Unit
) {
    val bottomSheetState = LocalBottomSheetState.current
    val fabState = LocalFabState.current
    val dialogState = LocalDialogState.current

    val deleteColor = MaterialTheme.colorScheme.error
    val deleteTitle = stringResource(Res.string.screen_Service_dialog_delete_title)
    val deleteMessage = stringResource(Res.string.screen_Service_dialog_delete_message)

    DisposableEffect(Unit) {
        fabState.set(
            icon = Res.drawable.ic_add,
            contentDescription = Res.string.screen_Service_action_add_service,
            onClick = {
                val newServiceTemplate = Service()
                onEvent(ServiceEvent.EditService(newServiceTemplate))
                bottomSheetState.show {
                    ServiceFormSheet(
                        service = newServiceTemplate,
                        currency = uiState.currency,
                        onSave = { title, description, price ->
                            onEvent(ServiceEvent.SaveService(title, description, price))
                            bottomSheetState.hide()
                        },
                        onDelete = null
                    )
                }
            }
        )
        onDispose {}
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.services.isEmpty() && !uiState.isLoading) {
            SupernovaEmptyState(
                titleRes = Res.string.screen_Service_empty_state
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = MaterialTheme.spacing.medium)
            ) {
                items(uiState.services) { service ->
                    ServiceItem(
                        service = service,
                        onEdit = {
                            onEvent(ServiceEvent.EditService(service))
                            bottomSheetState.show {
                                ServiceFormSheet(
                                    service = service,
                                    currency = uiState.currency,
                                    onSave = { title, description, price ->
                                        onEvent(ServiceEvent.SaveService(title, description, price))
                                        bottomSheetState.hide()
                                    },
                                    onDelete = {
                                        dialogState.showConfirmation(
                                            title = deleteTitle,
                                            message = deleteMessage,
                                            confirmLabel = Res.string.label_delete,
                                            dismissLabel = Res.string.label_cancel,
                                            primaryActionColor = deleteColor,
                                            onConfirm = {
                                                onEvent(ServiceEvent.DeleteService(service))
                                                bottomSheetState.hide()
                                                dialogState.hide()
                                            },
                                            onDismiss = { dialogState.hide() }
                                        )
                                    }
                                )
                            }
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                item { Spacer(Modifier.height(MaterialTheme.spacing.x5Large)) }
            }
        }
    }
}
