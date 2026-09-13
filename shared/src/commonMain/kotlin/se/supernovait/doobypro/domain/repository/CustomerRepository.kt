package se.supernovait.doobypro.domain.repository

import kotlinx.coroutines.flow.Flow
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError

/**
 * Repository for managing customers within the local store.
 * Unlike AuthRepository, this does not manage active sessions.
 */
interface CustomerRepository {
    /** Observes all customers. */
    fun getCustomers(): Flow<List<User>>

    /** Retrieves a customer by their unique ID. */
    suspend fun getCustomerById(id: String): Result<User, DataError>

    /** Saves or updates a customer record. */
    suspend fun saveCustomer(customer: User): Result<String, DataError>

    /** Deletes a customer record. */
    suspend fun deleteCustomer(customer: User): Result<Unit, DataError>
}
