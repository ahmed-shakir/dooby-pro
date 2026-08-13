package se.supernovait.doobypro.presentation.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.app_icon
import org.jetbrains.compose.resources.painterResource
import se.supernovait.app.core.ui.component.preview.ScreenPreviewContainer
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.app.theme.DoobyTheme

@Composable
fun App() {
    DoobyTheme {
        Scaffold { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text("Welcome to Dooby Pro")
                Image(
                    painter = painterResource(Res.drawable.app_icon),
                    contentDescription = null,
                    modifier = Modifier.padding(MaterialTheme.spacing.large)
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview() {
    ScreenPreviewContainer {
        App()
    }
}
