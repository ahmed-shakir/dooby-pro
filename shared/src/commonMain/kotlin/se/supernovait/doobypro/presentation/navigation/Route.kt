package se.supernovait.doobypro.presentation.navigation

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.navigation_item_account_label
import doobypro.shared.generated.resources.navigation_item_app_info_label
import doobypro.shared.generated.resources.navigation_item_cancelled_orders_label
import doobypro.shared.generated.resources.navigation_item_dashboard_label
import doobypro.shared.generated.resources.navigation_item_notifications_label
import doobypro.shared.generated.resources.navigation_item_orders_label
import doobypro.shared.generated.resources.navigation_item_services_label
import doobypro.shared.generated.resources.navigation_item_settings_common_label
import doobypro.shared.generated.resources.navigation_item_settings_label
import doobypro.shared.generated.resources.navigation_item_settings_notification_label
import doobypro.shared.generated.resources.navigation_item_settings_order_label
import doobypro.shared.generated.resources.navigation_item_settings_printer_label
import doobypro.shared.generated.resources.navigation_item_settings_receipt_label
import doobypro.shared.generated.resources.navigation_item_settings_storage_label
import doobypro.shared.generated.resources.navigation_item_storage_label
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import se.supernovait.app.core.domain.navigation.NavigationRoute
import se.supernovait.app.core.domain.navigation.ParameterizedRoute

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
        override val label = Res.string.navigation_item_app_info_label
        override val showTopBar = false
        override val showBottomBar = false
    }

    @Serializable
    data object Support : Route {
        override val showBottomBar = false
    }

    @Serializable
    data object Notifications : Route {
        override val label = Res.string.navigation_item_notifications_label
    }

    @Serializable
    data object Account : Route {
        override val label = Res.string.navigation_item_account_label
        override val showNotificationAction = true
    }

    @Serializable
    data object Settings : Route {
        override val label = Res.string.navigation_item_settings_label
        override val showBottomBar = false
        override val showNotificationAction = true
    }

    @Serializable
    data object SettingsCommon : Route {
        override val label = Res.string.navigation_item_settings_common_label
        override val isTopLevel = false
        override val showBottomBar = false
    }

    @Serializable
    data object SettingsOrder : Route {
        override val label = Res.string.navigation_item_settings_order_label
        override val isTopLevel = false
        override val showBottomBar = false
    }

    @Serializable
    data object SettingsStorage : Route {
        override val label = Res.string.navigation_item_settings_storage_label
        override val isTopLevel = false
        override val showBottomBar = false
    }

    @Serializable
    data object SettingsReceipt : Route {
        override val label = Res.string.navigation_item_settings_receipt_label
        override val isTopLevel = false
        override val showBottomBar = false
    }

    @Serializable
    data object SettingsPrinter : Route {
        override val label = Res.string.navigation_item_settings_printer_label
        override val isTopLevel = false
        override val showBottomBar = false
    }

    @Serializable
    data object SettingsNotification : Route {
        override val label = Res.string.navigation_item_settings_notification_label
        override val isTopLevel = false
        override val showBottomBar = false
    }

    @Serializable
    data object Dashboard : Route {
        override val label = Res.string.navigation_item_dashboard_label
        override val showNotificationAction = true
    }

    @Serializable
    data object Orders : Route {
        override val label = Res.string.navigation_item_orders_label
        override val showFab = true
    }

    @Serializable
    data object CancelledOrders : Route {
        override val label = Res.string.navigation_item_cancelled_orders_label
        override val isTopLevel = false
    }

    @Serializable
    data class OrderDetails(val id: String) : Route, ParameterizedRoute<Route> {
        @Transient
        override val label = Res.string.navigation_item_orders_label
        override val isTopLevel = false
        override val param = id
        override fun copyWithParam(param: String) = copy(id = param)
    }

    @Serializable
    data object Services : Route {
        override val label = Res.string.navigation_item_services_label
        override val showFab = true
    }

    @Serializable
    data class ServiceDetails(val id: String) : Route, ParameterizedRoute<Route> {
        override val isTopLevel = false
        override val param = id
        override fun copyWithParam(param: String) = copy(id = param)
    }

    @Serializable
    data object StorageManagement : Route {
        override val label = Res.string.navigation_item_storage_label
        override val showFab = true
    }

    companion object {
        private val routes get() = listOf(
            Welcome, AccountSetup, AppInfo, Support, Notifications, Account, Settings,
            SettingsCommon, SettingsOrder, SettingsStorage, SettingsReceipt, SettingsPrinter, SettingsNotification,
            Dashboard, Orders, CancelledOrders, OrderDetails(""), Services, ServiceDetails(""), StorageManagement
        )

        fun parse(route: String?, data: String? = null, defaultRoute: Route = Welcome): Route {
            if (route == null) return defaultRoute
            val routeName = route.substringBefore("/").substringBefore("?").substringAfterLast(".").substringAfterLast("$")
            val matched = routes.find { it.name == routeName } ?: defaultRoute

            return if (data != null && matched is ParameterizedRoute<*>) {
                @Suppress("UNCHECKED_CAST")
                (matched as ParameterizedRoute<Route>).copyWithParam(data)
            } else {
                matched
            }
        }

        fun startScreen(isAuthenticated: Boolean): Route {
            println("Navigation route - isAuthenticated: $isAuthenticated")
            return if (isAuthenticated) Dashboard else Welcome
        }
    }
}
