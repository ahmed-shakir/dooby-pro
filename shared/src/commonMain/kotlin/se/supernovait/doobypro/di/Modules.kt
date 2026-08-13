package se.supernovait.doobypro.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import se.supernovait.app.core.data.persistence.DatabaseFactory
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.di.coreModule
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.auth.AuthenticationManager
import se.supernovait.doobypro.data.local.DoobyDatabase
import se.supernovait.doobypro.data.repository.AuthRepositoryImpl

expect val platformModule: Module

val sharedModule = module {
    includes(coreModule)

    singleOf(::AuthenticationManager)
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()

    single<DoobyDatabase> {
        DatabaseFactory.create(get())
    }

    single<UserDao> {
        get<DoobyDatabase>().userDao()
    }
}
