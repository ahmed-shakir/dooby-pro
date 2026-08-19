package se.supernovait.doobypro.presentation.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.Flow
import org.koin.compose.koinInject
import se.supernovait.app.core.domain.auth.AuthenticationManager
import se.supernovait.app.core.domain.event.AppEvent
import se.supernovait.app.core.ui.util.HandleAppEvents
import se.supernovait.doobypro.presentation.navigation.Route

@Composable
fun AppEventHandler(
    events: Flow<AppEvent>,
    navController: NavHostController? = null
) {
    val authManager = koinInject<AuthenticationManager>()

    HandleAppEvents(
        events = events,
        onEvent = { event ->
            when (event) {
                AppEvent.NavigateBack -> {
                    navController?.popBackStack()
                    true
                }
                AppEvent.SignIn -> {
                    navController?.let { navigator ->
                        navigator.navigate(Route.Dashboard) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    true
                }
                AppEvent.SignOut -> {
                    authManager.signOut()
                    true
                }
                else -> false // Let default handler handle events
            }
        }
    )
}
