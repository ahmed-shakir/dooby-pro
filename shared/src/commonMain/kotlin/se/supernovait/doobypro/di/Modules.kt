package se.supernovait.doobypro.di

import org.koin.core.module.Module
import org.koin.dsl.module
import se.supernovait.app.core.data.persistence.DatabaseFactory
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.doobypro.data.local.DoobyDatabase

expect val platformModule: Module

val sharedModule = module {
    single<DoobyDatabase> {
        DatabaseFactory.create(get())
    }

    single<UserDao> {
        get<DoobyDatabase>().userDao()
    }
}
