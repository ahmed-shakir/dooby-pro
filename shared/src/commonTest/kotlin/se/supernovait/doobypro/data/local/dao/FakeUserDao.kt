package se.supernovait.doobypro.data.local.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.data.persistence.entity.UserEntity

/**
 * A fake implementation of [UserDao] for unit testing.
 */
class FakeUserDao : UserDao {
    private val usersState = MutableStateFlow<Map<String, UserEntity>>(emptyMap())

    override suspend fun getCount(): Long {
        return usersState.value.size.toLong()
    }

    override fun observeUserById(id: String): Flow<UserEntity?> {
        return usersState.map { it[id] }
    }

    override fun getAll(): Flow<List<UserEntity>> {
        return usersState.map { it.values.toList() }
    }

    override suspend fun getAllByIds(ids: List<String>): List<UserEntity> {
        return ids.mapNotNull { usersState.value[it] }
    }

    override suspend fun getById(id: String): UserEntity? {
        return usersState.value[id]
    }

    override suspend fun getByUsername(username: String): UserEntity? {
        return usersState.value.values.find { it.username == username }
    }

    override suspend fun upsert(user: UserEntity) {
        usersState.value += (user.id to user)
    }

    override suspend fun delete(user: UserEntity) {
        usersState.value -= user.id
    }
}
