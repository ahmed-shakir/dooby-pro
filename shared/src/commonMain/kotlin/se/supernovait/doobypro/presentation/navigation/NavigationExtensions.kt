package se.supernovait.doobypro.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import se.supernovait.app.core.domain.navigation.NavigationRoute
import se.supernovait.app.core.domain.navigation.computeOptions

/**
 * Custom navigation rules for Dooby Pro.
 *
 * 1. If navigating to the root (Dashboard or Welcome), clear the backstack.
 * 2. If navigating to a top-level route (e.g. Orders), maintain only [root, target] in backstack.
 * 3. If navigating to a detail route (e.g. OrderDetails), append to the current stack.
 *
 * @param route The destination route.
 */
fun NavController.navigateWithRules(route: NavigationRoute) {
    val options = route.computeOptions(currentRootRoute = graph.findStartDestination().route)

    if (options.isRoot) {
        navigate(options.routeName) {
            popUpTo(graph.findStartDestination().id) {
                inclusive = options.popUpInclusive
            }
        }
        return
    }

    navigate(options.routeName) {
        if (options.popUpToRoute != null) {
            popUpTo(options.popUpToRoute!!) {
                inclusive = options.popUpInclusive
                saveState = options.saveState
            }
            restoreState = options.restoreState
        }
        launchSingleTop = options.launchSingleTop
    }
}
