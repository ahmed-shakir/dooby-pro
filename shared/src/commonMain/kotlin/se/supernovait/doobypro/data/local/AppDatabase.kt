package se.supernovait.doobypro.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.execSQL
import androidx.room.useWriterConnection
import se.supernovait.app.core.data.persistence.RoomConverters
import se.supernovait.app.core.data.persistence.dao.LicenseDao
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.data.persistence.entity.LicenseEntity
import se.supernovait.app.core.data.persistence.entity.UserEntity
import se.supernovait.app.core.domain.initialization.InitializableDatabase
import se.supernovait.doobypro.data.local.dao.AccountDao
import se.supernovait.doobypro.data.local.dao.AgreementDao
import se.supernovait.doobypro.data.local.dao.CompanyDao
import se.supernovait.doobypro.data.local.dao.OrderDao
import se.supernovait.doobypro.data.local.dao.ServiceDao
import se.supernovait.doobypro.data.local.dao.StorageLocationDao
import se.supernovait.doobypro.data.local.entity.AccountEntity
import se.supernovait.doobypro.data.local.entity.AgreementEntity
import se.supernovait.doobypro.data.local.entity.CompanyEntity
import se.supernovait.doobypro.data.local.entity.OrderEntity
import se.supernovait.doobypro.data.local.entity.ServiceEntity
import se.supernovait.doobypro.data.local.entity.StorageLocationEntity
import se.supernovait.doobypro.domain.model.storage.StorageType

@Database(
    entities = [
        UserEntity::class,
        CompanyEntity::class,
        LicenseEntity::class,
        AgreementEntity::class,
        AccountEntity::class,
        OrderEntity::class,
        ServiceEntity::class,
        StorageLocationEntity::class
    ], version = 1
)
@TypeConverters(RoomConverters::class, DbConverters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase(), InitializableDatabase {
    abstract fun userDao(): UserDao
    abstract fun companyDao(): CompanyDao
    abstract fun licenseDao(): LicenseDao
    abstract fun agreementDao(): AgreementDao
    abstract fun accountDao(): AccountDao
    abstract fun orderDao(): OrderDao
    abstract fun serviceDao(): ServiceDao
    abstract fun storageLocationDao(): StorageLocationDao

    override suspend fun verify() {
        userDao().getCount()
        ensureDefaultStorageLocation()
    }

    private suspend fun ensureDefaultStorageLocation() {
        val dao = storageLocationDao()
        if (dao.getDefault() == null) {
            dao.upsert(
                StorageLocationEntity(
                    id = "default",
                    label = "Uncategorized",
                    type = StorageType.OTHER,
                    capacity = 0, // Unlimited
                    isDefault = true,
                    isActive = true
                )
            )
        }
    }

    override suspend fun clearTables() {
        clearAllTablesKmp()
    }

    companion object {
        const val DATABASE_FILENAME = "app.db"
    }
}

/**
 * Suspending version of clearAllTables for KMP.
 */
suspend fun AppDatabase.clearAllTablesKmp() {
    useWriterConnection { connection ->
        connection.execSQL("DELETE FROM users")
        connection.execSQL("DELETE FROM companies")
        connection.execSQL("DELETE FROM licenses")
        connection.execSQL("DELETE FROM agreements")
        connection.execSQL("DELETE FROM accounts")
        connection.execSQL("DELETE FROM orders")
        connection.execSQL("DELETE FROM services")
        connection.execSQL("DELETE FROM storage_locations")
    }
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
