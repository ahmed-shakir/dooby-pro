package se.supernovait.doobypro.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.execSQL
import androidx.room.useWriterConnection
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.data.persistence.entity.UserEntity

@Database(
    entities = [
        UserEntity::class
    ], version = 1
)
@ConstructedBy(DoobyDatabaseConstructor::class)
abstract class DoobyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_FILENAME = "dooby_pro.db"
    }
}

/**
 * Suspending version of clearAllTables for KMP.
 */
suspend fun DoobyDatabase.clearAllTablesKmp() {
    useWriterConnection { connection ->
        connection.execSQL("DELETE FROM users")

        // Reset autoincrement sequences
        connection.execSQL("DELETE FROM sqlite_sequence WHERE name='users'")
    }
}

@Suppress("KotlinNoActualForExpect")
expect object DoobyDatabaseConstructor : RoomDatabaseConstructor<DoobyDatabase> {
    override fun initialize(): DoobyDatabase
}
