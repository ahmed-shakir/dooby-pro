package se.supernovait.doobypro.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import se.supernovait.app.core.domain.logging.Logger
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.util.LogTags
import kotlin.coroutines.CoroutineContext

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
    private val logger: Logger
) : SettingsRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO
    private val settingsKey = stringPreferencesKey("app_settings")

    override val settings: Flow<Settings> = dataStore.data.map { preferences ->
        val jsonString = preferences[settingsKey]
        if (jsonString != null) {
            try {
                json.decodeFromString<Settings>(jsonString)
            } catch (e: Exception) {
                logger.warn("Failed to decode settings, reverting to default", e, tag = LogTags.SETTINGS_REPO)
                Settings()
            }
        } else {
            Settings()
        }
    }

    override suspend fun updateSettings(settings: Settings) {
        withContext(ioContext) {
            logger.info("Updating app settings", tag = LogTags.SETTINGS_REPO)
            dataStore.edit { preferences ->
                preferences[settingsKey] = json.encodeToString(settings)
            }
        }
    }

    override suspend fun resetSettings() {
        withContext(ioContext) {
            logger.info("Resetting app settings to default", tag = LogTags.SETTINGS_REPO)
            dataStore.edit { preferences ->
                preferences.remove(settingsKey)
            }
        }
    }
}
