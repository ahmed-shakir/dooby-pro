package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.data.persistence.mapper.toEntity
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.repository.CustomerRepository
import kotlin.coroutines.CoroutineContext

class CustomerRepositoryImpl(
    private val userDao: UserDao
) : CustomerRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun getCustomers(): Flow<List<User>> {
        return userDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCustomerById(id: String): Result<User, DataError> {
        return withContext(ioContext) {
            userDao.getById(id)?.toDomain()?.let {
                Result.Success(it)
            } ?: Result.Failure(DataError.NOT_FOUND)
        }
    }

    override suspend fun saveCustomer(customer: User): Result<String, DataError> {
        return withContext(ioContext) {
            try {
                val entity = customer.toEntity()
                userDao.upsert(entity)
                Result.Success(entity.id)
            } catch (_: Exception) {
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun deleteCustomer(customer: User): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                userDao.delete(customer.toEntity())
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }
}
