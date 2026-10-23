package se.supernovait.doobypro.di

import org.koin.dsl.module
import se.supernovait.app.core.data.persistence.DatabaseFactory
import se.supernovait.app.core.data.persistence.DatabaseMigrationCallback
import se.supernovait.app.core.data.persistence.IosDatabaseManager
import se.supernovait.app.core.data.persistence.backup.DatabaseBackupConfig
import se.supernovait.doobypro.data.local.AppDatabase
import se.supernovait.doobypro.domain.util.IosPdfGenerator
import se.supernovait.doobypro.domain.util.PdfGenerator

actual val platformModule = module {
    single<PdfGenerator> { IosPdfGenerator() }

    single<AppDatabase> {
        val dbName = AppDatabase.DATABASE_FILENAME

        DatabaseFactory.create(
            builder = IosDatabaseManager.createDatabaseBuilder<AppDatabase>(
                databaseName = dbName
            ),
            migrations = listOf(),
            callbacks = listOf(
                DatabaseMigrationCallback(
                    logger = get(),
                    crashReporter = get(),
                    analyticsTracker = get()
                )
            ),
            useDestructiveMigration = false,
            backupConfig = DatabaseBackupConfig(
                enabled = true,
                maxBackupsToKeep = 3
            ),
            databasePath = IosDatabaseManager.getDatabasePath(dbName)
        )
    }
}
