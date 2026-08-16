package se.supernovait.doobypro.presentation.common.preview

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import se.supernovait.doobypro.presentation.app.theme.DoobyTheme

@Composable
fun ComponentPreviewContainer(content: @Composable () -> Unit) {
    DoobyTheme {
        Surface {
            content()
        }
    }
}
