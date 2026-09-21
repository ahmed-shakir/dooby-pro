package se.supernovait.doobypro.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import se.supernovait.app.core.data.persistence.DatabaseFactory
import se.supernovait.app.core.data.persistence.dao.LicenseDao
import se.supernovait.app.core.data.persistence.dao.NotificationDao
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.di.coreModule
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.initialization.InitializableDatabase
import se.supernovait.app.core.domain.sharing.ShareConfiguration
import se.supernovait.doobypro.data.local.AppDatabase
import se.supernovait.doobypro.data.local.dao.AccountDao
import se.supernovait.doobypro.data.local.dao.AgreementDao
import se.supernovait.doobypro.data.local.dao.CompanyDao
import se.supernovait.doobypro.data.local.dao.OrderDao
import se.supernovait.doobypro.data.local.dao.ServiceDao
import se.supernovait.doobypro.data.local.dao.StorageLocationDao
import se.supernovait.doobypro.data.repository.AccountRepositoryImpl
import se.supernovait.doobypro.data.repository.AgreementRepositoryImpl
import se.supernovait.doobypro.data.repository.AuthRepositoryImpl
import se.supernovait.doobypro.data.repository.CompanyRepositoryImpl
import se.supernovait.doobypro.data.repository.CustomerRepositoryImpl
import se.supernovait.doobypro.data.repository.LicenseRepositoryImpl
import se.supernovait.doobypro.data.repository.OrderRepositoryImpl
import se.supernovait.doobypro.data.repository.ServiceRepositoryImpl
import se.supernovait.doobypro.data.repository.SettingsRepositoryImpl
import se.supernovait.doobypro.data.repository.StorageLocationRepositoryImpl
import se.supernovait.doobypro.domain.manager.OrderManager
import se.supernovait.doobypro.domain.manager.OrderQueryManager
import se.supernovait.doobypro.domain.manager.StorageLocationManager
import se.supernovait.doobypro.domain.repository.AccountRepository
import se.supernovait.doobypro.domain.repository.AgreementRepository
import se.supernovait.doobypro.domain.repository.CompanyRepository
import se.supernovait.doobypro.domain.repository.CustomerRepository
import se.supernovait.doobypro.domain.repository.LicenseRepository
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import se.supernovait.doobypro.domain.util.FileStorage
import se.supernovait.doobypro.presentation.account.AccountViewModel
import se.supernovait.doobypro.presentation.notification.NotificationViewModel
import se.supernovait.doobypro.presentation.order.OrderViewModel
import se.supernovait.doobypro.presentation.order.details.OrderDetailsViewModel
import se.supernovait.doobypro.presentation.service.ServiceViewModel
import se.supernovait.doobypro.presentation.settings.SettingsViewModel
import se.supernovait.doobypro.presentation.storage.StorageViewModel
import se.supernovait.doobypro.presentation.welcome.WelcomeViewModel
import se.supernovait.doobypro.presentation.welcome.account_setup.AccountSetupWizardViewModel

expect val platformModule: Module

val sharedModule = module {
    includes(coreModule)

    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    singleOf(::AccountRepositoryImpl).bind<AccountRepository>()
    singleOf(::CompanyRepositoryImpl).bind<CompanyRepository>()
    singleOf(::LicenseRepositoryImpl).bind<LicenseRepository>()
    singleOf(::AgreementRepositoryImpl).bind<AgreementRepository>()
    singleOf(::CustomerRepositoryImpl).bind<CustomerRepository>()
    singleOf(::ServiceRepositoryImpl).bind<ServiceRepository>()
    singleOf(::OrderRepositoryImpl).bind<OrderRepository>()
    singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()
    singleOf(::StorageLocationRepositoryImpl).bind<StorageLocationRepository>()

    singleOf(::OrderManager)
    singleOf(::OrderQueryManager)
    singleOf(::StorageLocationManager)

    viewModelOf(::WelcomeViewModel)
    viewModelOf(::AccountSetupWizardViewModel)
    viewModelOf(::NotificationViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::OrderViewModel)
    viewModelOf(::OrderDetailsViewModel)
    viewModelOf(::ServiceViewModel)
    viewModelOf(::StorageViewModel)

    single<FileStorage> { FileStorage() }

    single<List<ShareConfiguration>> {
        listOf(
            ShareConfiguration.custom("doobypro"),
            ShareConfiguration.https(host = "doobypro.supernovait.se")
        )
    }

    single<AppDatabase> {
        DatabaseFactory.create(get())
    }

    single<InitializableDatabase> {
        get<AppDatabase>()
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

    single<NotificationDao> {
        get<AppDatabase>().notificationDao()
    }

    single<OrderDao> {
        get<AppDatabase>().orderDao()
    }

    single<ServiceDao> {
        get<AppDatabase>().serviceDao()
    }

    single<StorageLocationDao> {
        get<AppDatabase>().storageLocationDao()
    }
}
