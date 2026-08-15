package se.supernovait.doobypro.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import se.supernovait.app.core.domain.initialization.AppInitializer

class KoinHelper : KoinComponent {
    private val appInitializer: AppInitializer by inject()

    fun getAppInitializer(): AppInitializer = appInitializer
}
