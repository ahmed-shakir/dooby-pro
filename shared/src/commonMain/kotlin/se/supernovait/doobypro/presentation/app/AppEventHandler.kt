package se.supernovait.doobypro.presentation.app

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import se.supernovait.app.core.domain.event.AppEvent
import se.supernovait.app.core.ui.util.HandleAppEvents

@Composable
fun handleAppEvents(
    events: Flow<AppEvent>
) {
    HandleAppEvents(
        events = events
    )
}
