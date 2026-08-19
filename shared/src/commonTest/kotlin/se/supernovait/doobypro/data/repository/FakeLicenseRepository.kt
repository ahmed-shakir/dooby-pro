package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.model.license.License
import se.supernovait.doobypro.domain.repository.LicenseRepository

class FakeLicenseRepository : LicenseRepository {
    private val licenses = MutableStateFlow<Map<String, License>>(emptyMap())

    override suspend fun getLicenses(accountId: String): List<License> {
        return licenses.value.values.filter { it.accountId == accountId }
    }

    override suspend fun getLicenseById(id: String): Result<License, DataError> {
        return licenses.value[id]?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
    }

    override suspend fun saveLicense(license: License): Result<String, DataError> {
        val id = license.id ?: "lic-${licenses.value.size}"
        val licenseWithId = license.copy(id = id)
        licenses.value += (id to licenseWithId)
        return Result.Success(id)
    }

    override suspend fun deleteLicense(license: License): Result<Unit, DataError> {
        licenses.value -= (license.id ?: "")
        return Result.Success(Unit)
    }
}
