package se.supernovait.doobypro.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import se.supernovait.app.core.domain.initialization.AppInitializer
import se.supernovait.app.core.domain.sharing.DeepLinkHandler
import se.supernovait.doobypro.domain.manager.OrderManager

class KoinHelper : KoinComponent {
    private val appInitializer: AppInitializer by inject()
    private val deepLinkHandler: DeepLinkHandler by inject()
    private val orderManager: OrderManager by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun getAppInitializer() = appInitializer

    fun handleDeepLink(url: String) = deepLinkHandler.handleDeepLink(url)

    fun checkAndNotifyOrderAlerts() {
        scope.launch {
            try {
                orderManager.checkAndNotifyOrderAlerts()
            } catch (_: Exception) {
                // Ignore or log error
            }
        }
    }
}
