package se.supernovait.doobypro.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import se.supernovait.app.core.data.persistence.DatabaseFactory
import se.supernovait.app.core.data.persistence.dao.LicenseDao
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.di.coreModule
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.auth.AuthenticationManager
import se.supernovait.doobypro.data.local.AppDatabase
import se.supernovait.doobypro.data.local.dao.AccountDao
import se.supernovait.doobypro.data.local.dao.AgreementDao
import se.supernovait.doobypro.data.local.dao.CompanyDao
import se.supernovait.doobypro.data.local.dao.OrderDao
import se.supernovait.doobypro.data.local.dao.ServiceDao
import se.supernovait.doobypro.data.repository.AccountRepositoryImpl
import se.supernovait.doobypro.data.repository.AgreementRepositoryImpl
import se.supernovait.doobypro.data.repository.AuthRepositoryImpl
import se.supernovait.doobypro.data.repository.CompanyRepositoryImpl
import se.supernovait.doobypro.data.repository.LicenseRepositoryImpl
import se.supernovait.doobypro.data.repository.OrderRepositoryImpl
import se.supernovait.doobypro.data.repository.ServiceRepositoryImpl
import se.supernovait.doobypro.domain.repository.AccountRepository
import se.supernovait.doobypro.domain.repository.AgreementRepository
import se.supernovait.doobypro.domain.repository.CompanyRepository
import se.supernovait.doobypro.domain.repository.LicenseRepository
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.repository.ServiceRepository

expect val platformModule: Module

val sharedModule = module {
    includes(coreModule)

    singleOf(::AuthenticationManager)
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    singleOf(::AccountRepositoryImpl).bind<AccountRepository>()
    singleOf(::CompanyRepositoryImpl).bind<CompanyRepository>()
    singleOf(::LicenseRepositoryImpl).bind<LicenseRepository>()
    singleOf(::AgreementRepositoryImpl).bind<AgreementRepository>()
    singleOf(::ServiceRepositoryImpl).bind<ServiceRepository>()
    singleOf(::OrderRepositoryImpl).bind<OrderRepository>()

    single<AppDatabase> {
        DatabaseFactory.create(get())
    }

    single<AccountDao> {
        get<AppDatabase>().accountDao()
    }

    single<UserDao> {
        get<AppDatabase>().userDao()
    }

    single<CompanyDao> {
        get<AppDatabase>().companyDao()
    }

    single<LicenseDao> {
        get<AppDatabase>().licenseDao()
    }

    single<AgreementDao> {
        get<AppDatabase>().agreementDao()
    }

    single<OrderDao> {
        get<AppDatabase>().orderDao()
    }

    single<ServiceDao> {
        get<AppDatabase>().serviceDao()
    }
}
