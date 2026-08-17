package se.supernovait.doobypro.presentation.common.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import se.supernovait.doobypro.presentation.app.theme.DoobyTheme

@Composable
fun ScreenPreviewContainer(content: @Composable () -> Unit) {
    KoinApplication(
        configuration = koinConfiguration {
            modules(PreviewKoinConfig.previewModule)
        }
    ) {
        DoobyTheme {
            Scaffold {
                Column(Modifier.padding(it).fillMaxWidth()) {
                    content()
                }
            }
        }
    }
}
