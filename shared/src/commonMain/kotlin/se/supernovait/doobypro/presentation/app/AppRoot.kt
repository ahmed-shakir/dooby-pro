package se.supernovait.doobypro.presentation.app

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.app_name
import doobypro.shared.generated.resources.ic_app_icon
import doobypro.shared.generated.resources.ic_dashboard
import doobypro.shared.generated.resources.ic_dashboard_selected
import doobypro.shared.generated.resources.ic_logout
import doobypro.shared.generated.resources.ic_menu
import doobypro.shared.generated.resources.ic_menu_selected
import doobypro.shared.generated.resources.ic_order
import doobypro.shared.generated.resources.ic_order_selected
import doobypro.shared.generated.resources.ic_service
import doobypro.shared.generated.resources.ic_service_selected
import doobypro.shared.generated.resources.ic_settings
import doobypro.shared.generated.resources.ic_settings_selected
import doobypro.shared.generated.resources.ic_storage
import doobypro.shared.generated.resources.ic_storage_selected
import doobypro.shared.generated.resources.ic_user_account
import doobypro.shared.generated.resources.ic_user_account_selected
import doobypro.shared.generated.resources.label_sign_out
import doobypro.shared.generated.resources.navigation_item_menu_label
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import se.supernovait.app.core.domain.auth.AuthenticationManager
import se.supernovait.app.core.domain.auth.AuthenticationState
import se.supernovait.app.core.domain.connectivity.ConnectivityManager
import se.supernovait.app.core.ui.component.drawer.LocalNavigationDrawerState
import se.supernovait.app.core.ui.component.drawer.NavigationDrawerSection
import se.supernovait.app.core.ui.component.navigation.LocalNavigationBarState
import se.supernovait.app.core.ui.component.navigation.NavigationItem
import se.supernovait.app.core.ui.component.scaffold.SupernovaScaffold
import se.supernovait.app.core.ui.component.topbar.LocalTopBarState
import se.supernovait.doobypro.AppConfig
import se.supernovait.doobypro.presentation.common.preview.ScreenPreviewContainer
import se.supernovait.doobypro.presentation.navigation.Route
import se.supernovait.doobypro.presentation.navigation.accountGraph
import se.supernovait.doobypro.presentation.navigation.introGraph
import se.supernovait.doobypro.presentation.navigation.mainGraph
import se.supernovait.doobypro.presentation.navigation.navigateWithRules
import se.supernovait.doobypro.presentation.navigation.settingsGraph

@Composable
fun AppRoot() {
    SupernovaScaffold(
        showDrawer = true,
        connectivityManager = koinInject<ConnectivityManager>()
    ) { innerPadding ->
        val coroutineScope = rememberCoroutineScope()
        val authManager = koinInject<AuthenticationManager>()
        val authState by authManager.authState.collectAsStateWithLifecycle()
        val isAuthenticated = authManager.isAuthenticated()

        val topBarState = LocalTopBarState.current
        val navigationBarState = LocalNavigationBarState.current
        val navigationDrawerState = LocalNavigationDrawerState.current

        val navController: NavHostController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val startScreen = Route.startScreen(isAuthenticated)
        val currentScreen = Route.parse(backStackEntry?.destination?.route, startScreen)

        LaunchedEffect(authState) {
            if (authState is AuthenticationState.NotAuthenticated && currentScreen != Route.Welcome) {
                navController.navigate(Route.Welcome) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        LaunchedEffect(topBarState, currentScreen) {
            if (currentScreen.showTopBar) {
                val title = if (currentScreen == Route.Dashboard) Res.string.app_name else currentScreen.label ?: Res.string.app_name
                val icon = if (currentScreen == Route.Dashboard) Res.drawable.ic_app_icon else null
                topBarState.title(title)
                topBarState.icon(icon)
                topBarState.actions(canNavigateBack = currentScreen != Route.Dashboard)
                topBarState.onNavigateUp({ navController.navigateUp() })
                topBarState.show()
            } else {
                topBarState.hide()
            }
        }

        LaunchedEffect(navigationBarState, currentScreen) {
            if (currentScreen.showBottomBar) {
                navigationBarState.items(listOf(
                    NavigationItem(
                        id = Route.Dashboard.name,
                        label = Route.Dashboard.label,
                        icon = Res.drawable.ic_dashboard,
                        selectedIcon = Res.drawable.ic_dashboard_selected,
                        onClick = { navController.navigateWithRules(Route.Dashboard) }
                    ),
                    NavigationItem(
                        id = Route.Orders.name,
                        label = Route.Orders.label,
                        icon = Res.drawable.ic_order,
                        selectedIcon = Res.drawable.ic_order_selected,
                        onClick = { navController.navigateWithRules(Route.Orders) }
                    ),
                    NavigationItem(
                        id = "menu",
                        label = Res.string.navigation_item_menu_label,
                        icon = Res.drawable.ic_menu,
                        selectedIcon = Res.drawable.ic_menu_selected,
                        onClick = {
                            coroutineScope.launch {
                                navigationDrawerState.open()
                            }
                        }
                    )
                ))

                navigationBarState.select(currentScreen.name)
                navigationBarState.show()
            } else {
                navigationBarState.hide()
            }
        }

        LaunchedEffect(navigationDrawerState, currentScreen) {
            navigationDrawerState.header(title = Res.string.app_name, icon = Res.drawable.ic_app_icon)
            navigationDrawerState.appVersion(AppConfig.VERSION_NAME)

            navigationDrawerState.sections(sections = listOf(
                NavigationDrawerSection(items = listOf(
                    NavigationItem(
                        id = Route.Dashboard.name,
                        label = Route.Dashboard.label,
                        icon = Res.drawable.ic_dashboard,
                        selectedIcon = Res.drawable.ic_dashboard_selected,
                        onClick = { navController.navigateWithRules(Route.Dashboard) }
                    ),
                    NavigationItem(
                        id = Route.Orders.name,
                        label = Route.Orders.label,
                        icon = Res.drawable.ic_order,
                        selectedIcon = Res.drawable.ic_order_selected,
                        onClick = { navController.navigateWithRules(Route.Orders) }
                    ),
                    NavigationItem(
                        id = Route.Services.name,
                        label = Route.Services.label,
                        icon = Res.drawable.ic_service,
                        selectedIcon = Res.drawable.ic_service_selected,
                        onClick = { navController.navigateWithRules(Route.Services) }
                    ),
                    NavigationItem(
                        id = Route.StorageManagement.name,
                        label = Route.StorageManagement.label,
                        icon = Res.drawable.ic_storage,
                        selectedIcon = Res.drawable.ic_storage_selected,
                        onClick = { navController.navigateWithRules(Route.StorageManagement) }
                    )
                )),
                NavigationDrawerSection(items = listOf(
                    NavigationItem(
                        id = Route.Account.name,
                        label = Route.Account.label,
                        icon = Res.drawable.ic_user_account,
                        selectedIcon = Res.drawable.ic_user_account_selected,
                        onClick = { navController.navigateWithRules(Route.Account) }
                    ),
                    NavigationItem(
                        id = Route.Settings.name,
                        label = Route.Settings.label,
                        icon = Res.drawable.ic_settings,
                        selectedIcon = Res.drawable.ic_settings_selected,
                        onClick = { navController.navigateWithRules(Route.Settings) }
                    )
                )),
                NavigationDrawerSection(items = listOf(
                    NavigationItem(
                        id = "sign_out",
                        label = Res.string.label_sign_out,
                        icon = Res.drawable.ic_logout,
                        onClick = { authManager.signOut() }
                    )
                ))
            ))

            navigationDrawerState.select(currentScreen.name)
        }

        NavHost(
            navController = navController,
            startDestination = startScreen,
            modifier = Modifier.padding(innerPadding)
        ) {
            introGraph(navController)
            accountGraph(navController)
            settingsGraph(navController)
            mainGraph(navController)
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    ScreenPreviewContainer {
        AppRoot()
    }
}
