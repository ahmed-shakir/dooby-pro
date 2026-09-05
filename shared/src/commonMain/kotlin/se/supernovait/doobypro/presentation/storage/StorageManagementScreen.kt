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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_add
import doobypro.shared.generated.resources.screen_Storage_action_add_location
import doobypro.shared.generated.resources.screen_Storage_empty_state
import se.supernovait.app.core.ui.component.fab.LocalFabState
import se.supernovait.app.core.ui.component.modal.LocalBottomSheetState
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.presentation.storage.component.StorageLocationFormSheet
import se.supernovait.doobypro.presentation.storage.component.StorageLocationItem

@Composable
fun StorageManagementScreen(
    state: StorageState,
    onEvent: (StorageEvent) -> Unit
) {
    val bottomSheetState = LocalBottomSheetState.current
    val fabState = LocalFabState.current

    DisposableEffect(Unit) {
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
        onDispose {
            fabState.clear()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.locations.isEmpty() && !state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.large), contentAlignment = Alignment.Center) {
                SupernovaLabel(
                    text = Res.string.screen_Storage_empty_state,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = MaterialTheme.spacing.medium)
            ) {
                items(state.locations) { location ->
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
                                        onEvent(StorageEvent.DeleteLocation(location))
                                        bottomSheetState.hide()
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
