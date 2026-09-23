package se.supernovait.doobypro.presentation.storage

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_add
import doobypro.shared.generated.resources.label_cancel
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.screen_Storage_action_add_location
import doobypro.shared.generated.resources.screen_Storage_dialog_delete_message
import doobypro.shared.generated.resources.screen_Storage_dialog_delete_title
import doobypro.shared.generated.resources.screen_Storage_empty_state
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.SupernovaEmptyState
import se.supernovait.app.core.ui.component.fab.LocalFabState
import se.supernovait.app.core.ui.component.modal.LocalBottomSheetState
import se.supernovait.app.core.ui.component.modal.dialog.LocalDialogState
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.presentation.storage.component.StorageLocationFormSheet
import se.supernovait.doobypro.presentation.storage.component.StorageLocationItem

@Composable
fun StorageManagementScreen(
    uiState: StorageState,
    onEvent: (StorageEvent) -> Unit
) {
    val bottomSheetState = LocalBottomSheetState.current
    val fabState = LocalFabState.current
    val dialogState = LocalDialogState.current

    val deleteColor = MaterialTheme.colorScheme.error
    val deleteTitle = stringResource(Res.string.screen_Storage_dialog_delete_title)
    val deleteMessage = stringResource(Res.string.screen_Storage_dialog_delete_message)

    LaunchedEffect(Unit) {
        fabState.set(
            icon = Res.drawable.ic_add,
            contentDescription = Res.string.screen_Storage_action_add_location,
            onClick = {
                val newLocation = StorageLocation()
                onEvent(StorageEvent.EditLocation(newLocation))
                bottomSheetState.show {
                    StorageLocationFormSheet(
                        location = newLocation,
                        onSave = { label, type, capacity ->
                            onEvent(StorageEvent.SaveLocation(label, type, capacity))
                            bottomSheetState.hide()
                        },
                        onDelete = null
                    )
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.locations.isEmpty() && !uiState.isLoading) {
            SupernovaEmptyState(
                titleRes = Res.string.screen_Storage_empty_state
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = MaterialTheme.spacing.medium)
            ) {
                items(uiState.locations) { location ->
                    StorageLocationItem(
                        location = location,
                        onEdit = {
                            onEvent(StorageEvent.EditLocation(location))
                            bottomSheetState.show {
                                StorageLocationFormSheet(
                                    location = location,
                                    onSave = { label, type, capacity ->
                                        onEvent(StorageEvent.SaveLocation(label, type, capacity))
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
                                                onEvent(StorageEvent.DeleteLocation(location))
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
