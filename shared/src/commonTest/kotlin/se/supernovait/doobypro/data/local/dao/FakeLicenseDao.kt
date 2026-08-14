package se.supernovait.doobypro.data.local.dao

import kotlinx.coroutines.flow.MutableStateFlow
import se.supernovait.app.core.data.persistence.dao.LicenseDao
import se.supernovait.app.core.data.persistence.entity.LicenseEntity

/**
 * A fake implementation of [LicenseDao] for unit testing.
 */
class FakeLicenseDao : LicenseDao {
    private val state = MutableStateFlow<Map<String, LicenseEntity>>(emptyMap())

    override suspend fun getCount(): Long {
        return state.value.size.toLong()
    }

    override suspend fun getByAccountId(accountId: String): List<LicenseEntity> {
        return state.value.values.filter { it.accountId == accountId }
    }

    override suspend fun getById(id: String): LicenseEntity? {
        return state.value[id]
    }

    override suspend fun upsert(license: LicenseEntity) {
        state.value += (license.id to license)
    }

    override suspend fun delete(license: LicenseEntity) {
        state.value -= license.id
    }
}
