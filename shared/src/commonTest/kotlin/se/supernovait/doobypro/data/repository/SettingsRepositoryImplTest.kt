package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import se.supernovait.doobypro.data.local.preferences.FakeDataStore
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.model.settings.common.Currency
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsRepositoryImplTest {
    private lateinit var fakeDataStore: FakeDataStore
    private lateinit var repository: SettingsRepositoryImpl

    @BeforeTest
    fun setup() {
        fakeDataStore = FakeDataStore()
        repository = SettingsRepositoryImpl(fakeDataStore, Json { 
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }

    @Test
    fun `settings should return default values initially`() = runTest {
        val settings = repository.settings.first()
        assertEquals(Settings(), settings)
    }

    @Test
    fun `updateSettings should persist settings`() = runTest {
        val newSettings = Settings(
            common = Settings().common.copy(currency = Currency.AED)
        )
        repository.updateSettings(newSettings)
        
        val savedSettings = repository.settings.first()
        assertEquals(newSettings, savedSettings)
    }

    @Test
    fun `resetSettings should clear persisted settings`() = runTest {
        val newSettings = Settings(
            common = Settings().common.copy(currency = Currency.AED)
        )
        repository.updateSettings(newSettings)
        repository.resetSettings()
        
        val settings = repository.settings.first()
        assertEquals(Settings(), settings)
    }
}
