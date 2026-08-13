package se.supernovait.doobypro.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatformTools

fun initKoin(config: KoinAppDeclaration? = null) {
    // Check if Koin is already started
    if (KoinPlatformTools.defaultContext().getOrNull() != null) return

    startKoin {
        config?.invoke(this)
        modules(sharedModule, platformModule)
    }
}
