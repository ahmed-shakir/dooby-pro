package se.supernovait.doobypro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import se.supernovait.doobypro.data.local.entity.CompanyEntity

/**
 * Data Access Object for the "companies" table.
 */
@Dao
interface CompanyDao {

    /**
     * Observes the list of all companies.
     */
    @Query("SELECT * FROM companies")
    fun getAll(): Flow<List<CompanyEntity>>

    /**
     * Retrieves a company by its ID.
     */
    @Query("SELECT * FROM companies WHERE id = :id")
    suspend fun getById(id: String): CompanyEntity?

    /**
     * Inserts or updates a company profile.
     */
    @Upsert
    suspend fun upsert(company: CompanyEntity)

    /**
     * Deletes a company profile.
     */
    @Delete
    suspend fun delete(company: CompanyEntity)
}
