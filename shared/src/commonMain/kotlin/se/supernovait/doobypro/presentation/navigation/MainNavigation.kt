package se.supernovait.doobypro.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.spacing

fun NavGraphBuilder.mainGraph(
    navController: NavHostController
) {
    composable<Route.Dashboard> {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.mediumLarge)
        ) {
            SupernovaTitle(text = "My Dashboard")
        }
        // TODO: add navigation bar
        // TODO: add navigation drawer
        // TODO: add settings screen
    }
}
