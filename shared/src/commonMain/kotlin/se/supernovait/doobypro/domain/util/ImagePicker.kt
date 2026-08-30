package se.supernovait.doobypro.domain.util

import androidx.compose.runtime.Composable

/**
 * Interface for launching the platform-specific image picker.
 */
interface ImagePickerLauncher {
    /**
     * Launches the system photo library picker.
     */
    fun launchPhotos()

    /**
     * Launches the system file browser to pick an image.
     */
    fun launchFiles()
}

/**
 * Creates and remembers an [ImagePickerLauncher].
 * @param onResult Callback with the picked image bytes or null if cancelled.
 */
@Composable
expect fun rememberImagePickerLauncher(onResult: (ByteArray?) -> Unit): ImagePickerLauncher
