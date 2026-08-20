package se.supernovait.doobypro.domain.repository

import kotlinx.coroutines.flow.Flow
import se.supernovait.doobypro.domain.model.settings.Settings

/**
 * Interface for managing application settings.
 */
interface SettingsRepository {
    /**
     * Observable stream of application settings.
     */
    val settings: Flow<Settings>

    /**
     * Updates the current settings.
     */
    suspend fun updateSettings(settings: Settings)

    /**
     * Resets settings to their default values.
     */
    suspend fun resetSettings()
}
