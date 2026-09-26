package se.supernovait.doobypro.data.local.dao

import se.supernovait.doobypro.data.local.entity.BusinessHoursDayEntity

class FakeBusinessHoursDao : BusinessHoursDao {
    private val store = mutableMapOf<String, BusinessHoursDayEntity>()

    override suspend fun getBusinessHours(companyId: String): List<BusinessHoursDayEntity> {
        return store.values.filter { it.companyId == companyId }
    }

    override suspend fun getDayHours(companyId: String, dayOfWeek: Int): BusinessHoursDayEntity? {
        return store.values.firstOrNull { it.companyId == companyId && it.dayOfWeek == dayOfWeek }
    }

    override suspend fun upsert(entity: BusinessHoursDayEntity) {
        store[entity.id] = entity
    }

    override suspend fun upsertAll(entities: List<BusinessHoursDayEntity>) {
        entities.forEach { store[it.id] = it }
    }

    override suspend fun delete(entity: BusinessHoursDayEntity) {
        store.remove(entity.id)
    }

    override suspend fun deleteAllForCompany(companyId: String) {
        val keysToRemove = store.filter { it.value.companyId == companyId }.keys
        keysToRemove.forEach { store.remove(it) }
    }
}
