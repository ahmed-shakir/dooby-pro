package se.supernovait.doobypro.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route : NavigationRoute {

    @Serializable
    data object Welcome : Route {
        override val showTopBar = false
        override val showBottomBar = false
    }

    /**
     * First-time setup screen
     */
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
    data class OrderDetails(val id: String) : Route {
        override val isTopLevel = false
    }

    @Serializable
    data object Services : Route

    @Serializable
    data class ServiceDetails(val id: String) : Route {
        override val isTopLevel = false
    }

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
        private val routes = listOf(
            Welcome, AccountSetup, AppInfo, Support, Settings,
            Dashboard, Orders, OrderDetails(""), Services, ServiceDetails(""),
            Account, UserProfile, CompanyProfile, License, EquipmentLeaseAgreement
        ).associateBy { it.name }

        fun startScreen(isAuthenticated: Boolean): Route {
            println("Navigation route - isAuthenticated: $isAuthenticated")
            return if (isAuthenticated) Dashboard else Welcome
        }

        fun parse(route: String?, defaultRoute: Route = Welcome): Route {
            val routeName = route?.substringBefore("/")?.substringBefore("?")
            return routes[routeName] ?: defaultRoute
        }
    }
}
