package se.supernovait.doobypro.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import se.supernovait.app.core.data.persistence.AndroidDatabaseManager
import se.supernovait.app.core.data.persistence.DatabaseFactory
import se.supernovait.app.core.data.persistence.DatabaseMigrationCallback
import se.supernovait.app.core.data.persistence.backup.DatabaseBackupConfig
import se.supernovait.doobypro.data.local.AppDatabase
import se.supernovait.doobypro.domain.util.AndroidPdfGenerator
import se.supernovait.doobypro.domain.util.PdfGenerator
import se.supernovait.doobypro.domain.util.applicationContext

actual val platformModule = module {
    single<PdfGenerator> { AndroidPdfGenerator() }

    single<String>(named("databasePath")) {
        AndroidDatabaseManager.getDatabasePath(
            context = applicationContext,
            databaseName = AppDatabase.DATABASE_FILENAME
        )
    }

    single<AppDatabase> {
        val context = applicationContext
        val dbName = AppDatabase.DATABASE_FILENAME

        DatabaseFactory.create(
            builder = AndroidDatabaseManager.createDatabaseBuilder<AppDatabase>(
                context = context,
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
            useDestructiveMigration = false, // Keep false in production!
            backupConfig = DatabaseBackupConfig(
                enabled = true,
                maxBackupsToKeep = 3
            ),
            databasePath = AndroidDatabaseManager.getDatabasePath(context, dbName)
        )
    }
}
