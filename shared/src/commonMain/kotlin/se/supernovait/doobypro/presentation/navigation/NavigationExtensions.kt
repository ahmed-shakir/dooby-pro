package se.supernovait.doobypro.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

/**
 * Custom navigation rules for Dooby Pro.
 *
 * 1. If navigating to the root (Dashboard or Welcome), clear the backstack.
 * 2. If navigating to a top-level route (e.g. Orders), maintain only [root, target] in backstack.
 * 3. If navigating to a detail route (e.g. OrderDetails), append to the current stack.
 *
 * @param route The destination route.
 */
fun NavController.navigateWithRules(route: Route) {
    // Derive the current root from the graph's start destination
    val root = Route.parse(graph.findStartDestination().route)

    if (route == root) {
        navigate(route) {
            // Pop everything including the current root to ensure a fresh start
            popUpTo(graph.id) { inclusive = true }
        }
        return
    }

    navigate(route) {
        if (route.isTopLevel) {
            // Maintain only [Root, Target] in the stack
            popUpTo(root) {
                inclusive = false
                saveState = true
            }
            restoreState = true
        }
        // Prevent multiple instances of the same destination on top
        launchSingleTop = true
    }
}
