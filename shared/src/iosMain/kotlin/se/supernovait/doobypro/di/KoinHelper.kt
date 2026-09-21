package se.supernovait.doobypro.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import se.supernovait.app.core.domain.initialization.AppInitializer
import se.supernovait.app.core.domain.sharing.DeepLinkHandler

class KoinHelper : KoinComponent {
    private val appInitializer: AppInitializer by inject()
    private val deepLinkHandler: DeepLinkHandler by inject()

    fun getAppInitializer(): AppInitializer = appInitializer

    fun handleDeepLink(url: String) {
        deepLinkHandler.handleDeepLink(url)
    }
}
