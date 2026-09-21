package se.supernovait.doobypro.di

import androidx.room.RoomDatabase
import org.koin.dsl.module
import se.supernovait.app.core.data.persistence.AndroidDatabaseManager
import se.supernovait.app.core.domain.sharing.ShareConfiguration
import se.supernovait.doobypro.data.local.AppDatabase
import se.supernovait.doobypro.domain.util.AndroidPdfGenerator
import se.supernovait.doobypro.domain.util.PdfGenerator
import se.supernovait.doobypro.domain.util.applicationContext

actual val platformModule = module {
    single<ShareConfiguration> { ShareConfiguration.https(host = "doobypro.supernovait.se") }
    single<PdfGenerator> { AndroidPdfGenerator() }
    single<RoomDatabase.Builder<AppDatabase>> {
        val builder: RoomDatabase.Builder<AppDatabase> = AndroidDatabaseManager.createDatabaseBuilder(
            context = applicationContext,
            databaseName = AppDatabase.DATABASE_FILENAME
        )
        builder.fallbackToDestructiveMigration(true)
        builder
    }
}
