package se.supernovait.doobypro.presentation.account.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.company_logo_content_description
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.stringResource
import se.supernovait.doobypro.domain.util.loadLogoBytes

/**
 * A reusable image component that loads a logo image asynchronously from either a network URL
 * or a local file path and displays it.
 *
 * @param logoUrl The URL or file path of the logo.
 * @param modifier The modifier to apply to the image.
 */
@Composable
fun LogoImage(
    logoUrl: String,
    contentDescription: StringResource = Res.string.company_logo_content_description,
    modifier: Modifier = Modifier
) {
    val imageBitmap by produceState<ImageBitmap?>(initialValue = null, key1 = logoUrl) {
        value = try {
            val bytes = loadLogoBytes(logoUrl)
            bytes?.decodeToImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    imageBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = stringResource(contentDescription),
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}
