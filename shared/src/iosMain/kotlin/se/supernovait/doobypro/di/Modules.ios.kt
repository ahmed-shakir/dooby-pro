package se.supernovait.doobypro.di

import androidx.room.RoomDatabase
import org.koin.dsl.module
import se.supernovait.app.core.data.persistence.IosDatabaseManager
import se.supernovait.doobypro.data.local.AppDatabase

actual val platformModule = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        val builder: RoomDatabase.Builder<AppDatabase> = IosDatabaseManager.createDatabaseBuilder(
            databaseName = AppDatabase.DATABASE_FILENAME
        )
        builder.fallbackToDestructiveMigration(true)
        builder
    }
}
