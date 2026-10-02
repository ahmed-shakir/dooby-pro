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
import se.supernovait.app.core.domain.logging.Logger
import se.supernovait.doobypro.domain.repository.CustomerRepository
import se.supernovait.doobypro.domain.util.LogTags
import kotlin.coroutines.CoroutineContext

class CustomerRepositoryImpl(
    private val userDao: UserDao,
    private val logger: Logger
) : CustomerRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun getCustomers(): Flow<List<User>> {
        logger.debug("Observing customers list", tag = LogTags.CUSTOMER_REPO)
        return userDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCustomerById(id: String): Result<User, DataError> {
        return withContext(ioContext) {
            logger.debug("Fetching customer profile with ID: $id", tag = LogTags.CUSTOMER_REPO)
            userDao.getById(id)?.toDomain()?.let {
                Result.Success(it)
            } ?: run {
                logger.warn("Customer profile not found with ID: $id", tag = LogTags.CUSTOMER_REPO)
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun saveCustomer(customer: User): Result<String, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Saving customer profile for customer: ${customer.username}", tag = LogTags.CUSTOMER_REPO)
                val entity = customer.toEntity()
                userDao.upsert(entity)
                Result.Success(entity.id)
            } catch (e: Exception) {
                logger.error("Error saving customer profile for customer: ${customer.username}", e, tag = LogTags.CUSTOMER_REPO)
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun deleteCustomer(customer: User): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Deleting customer profile for customer with ID: ${customer.id}", tag = LogTags.CUSTOMER_REPO)
                userDao.delete(customer.toEntity())
                Result.Success(Unit)
            } catch (e: Exception) {
                logger.error("Error deleting customer profile for customer with ID: ${customer.id}", e, tag = LogTags.CUSTOMER_REPO)
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }
}
