package se.supernovait.doobypro.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route : NavigationRoute {

    /**
     * First-time setup screen
     */
    @Serializable
    data object Welcome : Route {
        override val showTopBar = false
        override val showBottomBar = false
    }

    @Serializable
    data object AccountSetup : Route {
        override val showTopBar = false
        override val showBottomBar = false
    }

    @Serializable
    data object AppInfo : Route {
        override val showTopBar = false
        override val showBottomBar = false
    }

    @Serializable
    data object Support : Route {
        override val showBottomBar = false
    }

    @Serializable
    data object Settings : Route {
        override val showBottomBar = false
    }

    @Serializable
    data object Dashboard : Route

    @Serializable
    data object Orders : Route

    @Serializable
    data class OrderDetails(val id: String) : Route

    @Serializable
    data object Services : Route

    @Serializable
    data class ServiceDetails(val id: String) : Route

    @Serializable
    data object Account : Route

    @Serializable
    data object UserProfile : Route

    @Serializable
    data object CompanyProfile : Route

    @Serializable
    data object License : Route

    @Serializable
    data object EquipmentLeaseAgreement : Route


    companion object {
        fun startScreen(isAuthenticated: Boolean): Route {
            println("Navigation route - isAuthenticated: $isAuthenticated")
            return if (isAuthenticated) Dashboard else Welcome
        }

        fun parse(route: String?, defaultRoute: Route = Welcome): Route {
            return when (route?.substringBefore("/")?.substringBefore("?")) {
                Welcome::class.qualifiedName -> Welcome
                AccountSetup::class.qualifiedName -> AccountSetup
                AppInfo::class.qualifiedName -> AppInfo
                Support::class.qualifiedName -> Support
                Settings::class.qualifiedName -> Settings
                Dashboard::class.qualifiedName -> Dashboard
                Orders::class.qualifiedName -> Orders
                OrderDetails::class.qualifiedName -> OrderDetails(id = "")
                Services::class.qualifiedName -> Services
                ServiceDetails::class.qualifiedName -> ServiceDetails(id = "")
                Account::class.qualifiedName -> Account
                UserProfile::class.qualifiedName -> UserProfile
                CompanyProfile::class.qualifiedName -> CompanyProfile
                License::class.qualifiedName -> License
                EquipmentLeaseAgreement::class.qualifiedName -> EquipmentLeaseAgreement
                else -> defaultRoute
            }
        }
    }
}
