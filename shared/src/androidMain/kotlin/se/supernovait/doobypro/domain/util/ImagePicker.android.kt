package se.supernovait.doobypro.domain.util

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Android implementation of the image picker.
 */
@Composable
actual fun rememberImagePickerLauncher(onResult: (ByteArray?) -> Unit): ImagePickerLauncher {
    // Launcher for Photos (Modern Photo Picker)
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val bytes = applicationContext.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            onResult(bytes)
        } else {
            onResult(null)
        }
    }

    // Launcher for Files (Generic Content Picker)
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val bytes = applicationContext.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            onResult(bytes)
        } else {
            onResult(null)
        }
    }

    return remember {
        object : ImagePickerLauncher {
            override fun launchPhotos() {
                photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            override fun launchFiles() {
                fileLauncher.launch("image/*")
            }
        }
    }
}
