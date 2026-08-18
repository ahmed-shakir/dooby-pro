package se.supernovait.doobypro.presentation.common.preview

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import se.supernovait.app.core.domain.auth.AuthError
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.auth.AuthenticationManager
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.DataError
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.connectivity.ConnectivityManager
import se.supernovait.app.core.domain.connectivity.ConnectivityPolicy
import se.supernovait.app.core.domain.connectivity.ConnectivityStatus
import se.supernovait.app.core.domain.connectivity.ConnectivityStatusType
import se.supernovait.app.core.domain.connectivity.NetworkType
import se.supernovait.app.core.domain.initialization.AppInitializer
import se.supernovait.doobypro.presentation.welcome.account_setup.AccountSetupWizardViewModel
import kotlin.time.Duration.Companion.milliseconds

object PreviewKoinConfig {

    val previewModule = module {
        single<ConnectivityManager> { connectivityManager }
        single<AuthRepository> { authRepository }
        single<DataStore<Preferences>> { fakeDataStore }
        single { AuthenticationManager(get()) }
        single { AppInitializer(get(), get(), get(), minSplashDuration = 0.milliseconds) }

        viewModelOf(::AccountSetupWizardViewModel)
    }

    /* *** DEPENDENCIES *** */

    val connectivityManager = object : ConnectivityManager {
        override val connectivity: Flow<ConnectivityStatus> = flowOf(
            ConnectivityStatus(
                type = ConnectivityStatusType.ONLINE,
                networkType = NetworkType.WIFI,
                isConnected = true,
                isAllowed = true,
                isReachable = true
            )
        )
        override suspend fun status(): ConnectivityStatus = ConnectivityStatus(
            type = ConnectivityStatusType.ONLINE,
            networkType = NetworkType.WIFI,
            isConnected = true,
            isAllowed = true,
            isReachable = true
        )
        override suspend fun isConnected(): Boolean = true
        override suspend fun isReachable(): Boolean = true
        override suspend fun isAllowed(): Boolean = true
        override suspend fun isMetered(): Boolean = false
        override suspend fun isVpn(): Boolean = false
        override suspend fun isProxy(): Boolean = false
        override fun policy(): ConnectivityPolicy = ConnectivityPolicy.DEFAULT
        override fun setPolicy(policy: ConnectivityPolicy) {}
        override fun getLocalIpAddress(): String = "127.0.0.1"
        override fun getGateway(): String = "127.0.0.1"
        override fun getSubnetMask(): String = "255.255.255.0"
        override fun getDns(): String = "8.8.8.8"
        override fun getAddressType(): String = "DHCP"
        override fun getDiagnosticsSnapshot(): String = "Preview Diagnostics"
        override fun onAppResumed() {}
        override fun onAppPaused() {}
        override fun cleanup() {}
    }

    val authRepository = object : AuthRepository {
        override fun observeCurrentUserId(): Flow<String?> = flowOf(null)
        override fun observeUserById(id: String): Flow<User?> = flowOf(null)
        override suspend fun getCurrentUserId(): Result<String, AuthError> = Result.Failure(AuthError.USER_NOT_FOUND)
        override suspend fun getUserById(id: String): Result<User, DataError> = Result.Failure(DataError.NOT_FOUND)
        override suspend fun signIn(username: String): Result<User, AuthError> = Result.Failure(AuthError.USER_NOT_FOUND)
        override suspend fun signUp(user: User): Result<User, AuthError> = Result.Failure(AuthError.USER_NOT_FOUND)
        override suspend fun signOut() {}
    }

    val fakeDataStore = object : DataStore<Preferences> {
        private val state = MutableStateFlow(emptyPreferences())
        override val data: Flow<Preferences> = state
        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val next = transform(state.value)
            state.value = next
            return next
        }
    }
}
