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
import se.supernovait.doobypro.data.local.dao.OrderDao
import se.supernovait.doobypro.data.local.dao.ServiceDao
import se.supernovait.doobypro.data.repository.AuthRepositoryImpl
import se.supernovait.doobypro.data.repository.DoobyRepositoryImpl
import se.supernovait.doobypro.data.repository.OrderRepositoryImpl
import se.supernovait.doobypro.domain.repository.DoobyRepository
import se.supernovait.doobypro.domain.repository.OrderRepository

expect val platformModule: Module

val sharedModule = module {
    includes(coreModule)

    singleOf(::AuthenticationManager)
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    singleOf(::DoobyRepositoryImpl).bind<DoobyRepository>()
    singleOf(::OrderRepositoryImpl).bind<OrderRepository>()

    single<DoobyDatabase> {
        DatabaseFactory.create(get())
    }

    single<UserDao> {
        get<DoobyDatabase>().userDao()
    }

    single<OrderDao> {
        get<DoobyDatabase>().orderDao()
    }

    single<ServiceDao> {
        get<DoobyDatabase>().serviceDao()
    }
}
