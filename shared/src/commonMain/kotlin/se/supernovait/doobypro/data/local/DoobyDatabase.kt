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
import se.supernovait.doobypro.data.local.dao.AgreementDao
import se.supernovait.doobypro.data.local.dao.CompanyDao
import se.supernovait.doobypro.data.local.dao.OrderDao
import se.supernovait.doobypro.data.local.dao.ServiceDao
import se.supernovait.doobypro.data.local.entity.AgreementEntity
import se.supernovait.doobypro.data.local.entity.CompanyEntity
import se.supernovait.doobypro.data.local.entity.OrderEntity
import se.supernovait.doobypro.data.local.entity.ServiceEntity

@Database(
    entities = [
        UserEntity::class,
        CompanyEntity::class,
        LicenseEntity::class,
        AgreementEntity::class,
        OrderEntity::class,
        ServiceEntity::class
    ], version = 1
)
@TypeConverters(RoomConverters::class, AppConverters::class)
@ConstructedBy(DoobyDatabaseConstructor::class)
abstract class DoobyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun companyDao(): CompanyDao
    abstract fun licenseDao(): LicenseDao
    abstract fun agreementDao(): AgreementDao
    abstract fun orderDao(): OrderDao
    abstract fun serviceDao(): ServiceDao

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
        connection.execSQL("DELETE FROM companies")
        connection.execSQL("DELETE FROM licenses")
        connection.execSQL("DELETE FROM agreements")
        connection.execSQL("DELETE FROM orders")
        connection.execSQL("DELETE FROM services")
    }
}

@Suppress("KotlinNoActualForExpect")
expect object DoobyDatabaseConstructor : RoomDatabaseConstructor<DoobyDatabase> {
    override fun initialize(): DoobyDatabase
}
