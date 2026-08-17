package se.supernovait.doobypro.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import se.supernovait.app.core.ui.component.text.SupernovaBoldLabel

fun NavGraphBuilder.welcomeGraph() {
    composable<Route.Welcome> {
        // TODO: implement WelcomeScreen
        SupernovaBoldLabel("WelcomeScreen - First-time setup screen")
    }
}
