package se.supernovait.doobypro.domain.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import se.supernovait.app.core.domain.remoteconfig.RemoteConfigManager
import se.supernovait.doobypro.AppConfig

sealed interface UpdateStatus {
    data object UpToDate : UpdateStatus
    data class Required(val minVersion: Long, val updateUrl: String) : UpdateStatus
    data class Recommended(val recommendedVersion: Long, val updateUrl: String) : UpdateStatus
}

class AppUpdateManager(
    private val remoteConfigManager: RemoteConfigManager
) {
    private val _updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.UpToDate)
    val updateStatus: StateFlow<UpdateStatus> = _updateStatus.asStateFlow()

    suspend fun checkForUpdates() {
        remoteConfigManager.fetchAndActivate()
        val updateUrl = remoteConfigManager.getString("update_url")
        val minSupported = remoteConfigManager.getLong("min_supported_version_code")
        val recommended = remoteConfigManager.getLong("recommended_version_code")
        val currentVersion = AppConfig.VERSION_CODE.toLong()

        if (minSupported > 0 && currentVersion < minSupported) {
            _updateStatus.value = UpdateStatus.Required(minSupported, updateUrl)
        } else if (recommended > 0 && currentVersion < recommended) {
            _updateStatus.value = UpdateStatus.Recommended(recommended, updateUrl)
        } else {
            _updateStatus.value = UpdateStatus.UpToDate
        }
    }
}
