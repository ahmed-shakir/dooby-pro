package se.supernovait.doobypro.presentation.common.preview

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import se.supernovait.doobypro.presentation.app.theme.DoobyTheme

@Composable
fun ComponentPreviewContainer(content: @Composable () -> Unit) {
    KoinApplication(
        configuration = koinConfiguration {
            modules(PreviewKoinConfig.previewModule)
        }
    ) {
        DoobyTheme {
            Scaffold {
                content()
            }
        }
    }
}
