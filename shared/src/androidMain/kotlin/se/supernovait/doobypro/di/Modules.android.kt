package se.supernovait.doobypro.di

import androidx.room.RoomDatabase
import org.koin.dsl.module
import se.supernovait.app.core.data.persistence.AndroidDatabaseManager
import se.supernovait.doobypro.data.local.DoobyDatabase
import se.supernovait.doobypro.domain.util.applicationContext

actual val platformModule = module {
    single<RoomDatabase.Builder<DoobyDatabase>> {
        AndroidDatabaseManager.createDatabaseBuilder(
            context = applicationContext,
            databaseName = DoobyDatabase.DATABASE_FILENAME
        )
    }
}
