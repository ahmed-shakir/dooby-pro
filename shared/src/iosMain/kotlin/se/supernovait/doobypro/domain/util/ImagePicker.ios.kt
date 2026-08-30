package se.supernovait.doobypro.domain.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.darwin.NSObject
import platform.posix.memcpy

/**
 * iOS implementation of the image picker.
 */
@Composable
actual fun rememberImagePickerLauncher(onResult: (ByteArray?) -> Unit): ImagePickerLauncher {
    // Delegate for Photos (PHPicker)
    val photoDelegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, null)
                val result = didFinishPicking.firstOrNull() as? PHPickerResult
                if (result == null) {
                    onResult(null)
                    return
                }

                result.itemProvider.loadDataRepresentationForTypeIdentifier("public.image") { data, _ ->
                    if (data != null) {
                        val bytes = ByteArray(data.length.toInt())
                        bytes.usePinned { pinned ->
                            memcpy(pinned.addressOf(0), data.bytes, data.length)
                        }
                        onResult(bytes)
                    } else {
                        onResult(null)
                    }
                }
            }
        }
    }

    // Delegate for Files (UIDocumentPicker)
    val fileDelegate = remember {
        object : NSObject(), UIDocumentPickerDelegateProtocol {
            @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
            override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
                val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
                if (url == null) {
                    onResult(null)
                    return
                }

                val data = NSData.create(contentsOfURL = url)
                if (data != null) {
                    val bytes = ByteArray(data.length.toInt())
                    bytes.usePinned { pinned ->
                        memcpy(pinned.addressOf(0), data.bytes, data.length)
                    }
                    onResult(bytes)
                } else {
                    onResult(null)
                }
            }

            override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                onResult(null)
            }
        }
    }

    return remember {
        object : ImagePickerLauncher {
            override fun launchPhotos() {
                val configuration = PHPickerConfiguration()
                configuration.filter = PHPickerFilter.imagesFilter
                configuration.selectionLimit = 1
                val picker = PHPickerViewController(configuration)
                picker.delegate = photoDelegate
                
                val controller = UIApplication.sharedApplication.keyWindow?.rootViewController
                controller?.presentViewController(picker, true, null)
            }

            override fun launchFiles() {
                val picker = UIDocumentPickerViewController(
                    forOpeningContentTypes = listOf(UTTypeImage),
                    asCopy = true
                )
                picker.delegate = fileDelegate
                
                val controller = UIApplication.sharedApplication.keyWindow?.rootViewController
                controller?.presentViewController(picker, true, null)
            }
        }
    }
}
