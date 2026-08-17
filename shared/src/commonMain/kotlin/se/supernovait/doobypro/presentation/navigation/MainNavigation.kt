package se.supernovait.doobypro.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import se.supernovait.doobypro.presentation.welcome.WelcomeScreen

fun NavGraphBuilder.welcomeGraph() {
    composable<Route.Welcome> {
        // TODO: implement WelcomeScreen
        WelcomeScreen()
    }
}
