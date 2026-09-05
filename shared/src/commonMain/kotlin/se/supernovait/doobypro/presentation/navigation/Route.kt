package se.supernovait.doobypro.presentation.navigation

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.navigation_item_account_label
import doobypro.shared.generated.resources.navigation_item_dashboard_label
import doobypro.shared.generated.resources.navigation_item_orders_label
import doobypro.shared.generated.resources.navigation_item_services_label
import doobypro.shared.generated.resources.navigation_item_settings_label
import doobypro.shared.generated.resources.navigation_item_storage_label
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
    data object Account : Route {
        override val label = Res.string.navigation_item_account_label
    }

    @Serializable
    data object Settings : Route {
        override val label = Res.string.navigation_item_settings_label
        override val showBottomBar = false
    }

    @Serializable
    data object SettingsCommon : Route {
        override val isTopLevel = false
    }

    @Serializable
    data object SettingsOrder : Route {
        override val isTopLevel = false
    }

    @Serializable
    data object SettingsReceipt : Route {
        override val isTopLevel = false
    }

    @Serializable
    data object SettingsPrinter : Route {
        override val isTopLevel = false
    }

    @Serializable
    data object SettingsNotifications : Route {
        override val isTopLevel = false
    }

    @Serializable
    data object Dashboard : Route {
        override val label = Res.string.navigation_item_dashboard_label
    }

    @Serializable
    data object Orders : Route {
        override val label = Res.string.navigation_item_orders_label
    }

    @Serializable
    data class OrderDetails(val id: String) : Route {
        override val isTopLevel = false
    }

    @Serializable
    data object Services : Route {
        override val label = Res.string.navigation_item_services_label
    }

    @Serializable
    data class ServiceDetails(val id: String) : Route {
        override val isTopLevel = false
    }

    @Serializable
    data object StorageManagement : Route {
        override val label = Res.string.navigation_item_storage_label
    }

    companion object {
        private val routes = listOf(
            Welcome, AccountSetup, AppInfo, Account, Support, Settings,
            SettingsCommon, SettingsOrder, SettingsReceipt, SettingsPrinter, SettingsNotifications,
            Dashboard, Orders, OrderDetails(""), Services, ServiceDetails(""), StorageManagement
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
