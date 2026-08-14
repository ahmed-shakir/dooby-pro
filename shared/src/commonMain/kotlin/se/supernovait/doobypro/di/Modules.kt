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
import se.supernovait.doobypro.data.local.DoobyDatabase
import se.supernovait.doobypro.data.local.dao.AgreementDao
import se.supernovait.doobypro.data.local.dao.CompanyDao
import se.supernovait.doobypro.data.local.dao.OrderDao
import se.supernovait.doobypro.data.local.dao.ServiceDao
import se.supernovait.doobypro.data.repository.AgreementRepositoryImpl
import se.supernovait.doobypro.data.repository.AuthRepositoryImpl
import se.supernovait.doobypro.data.repository.CompanyRepositoryImpl
import se.supernovait.doobypro.data.repository.LicenseRepositoryImpl
import se.supernovait.doobypro.data.repository.OrderRepositoryImpl
import se.supernovait.doobypro.data.repository.ServiceRepositoryImpl
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
    singleOf(::CompanyRepositoryImpl).bind<CompanyRepository>()
    singleOf(::LicenseRepositoryImpl).bind<LicenseRepository>()
    singleOf(::AgreementRepositoryImpl).bind<AgreementRepository>()
    singleOf(::ServiceRepositoryImpl).bind<ServiceRepository>()
    singleOf(::OrderRepositoryImpl).bind<OrderRepository>()

    single<DoobyDatabase> {
        DatabaseFactory.create(get())
    }

    single<UserDao> {
        get<DoobyDatabase>().userDao()
    }

    single<CompanyDao> {
        get<DoobyDatabase>().companyDao()
    }

    single<LicenseDao> {
        get<DoobyDatabase>().licenseDao()
    }

    single<AgreementDao> {
        get<DoobyDatabase>().agreementDao()
    }

    single<OrderDao> {
        get<DoobyDatabase>().orderDao()
    }

    single<ServiceDao> {
        get<DoobyDatabase>().serviceDao()
    }
}
