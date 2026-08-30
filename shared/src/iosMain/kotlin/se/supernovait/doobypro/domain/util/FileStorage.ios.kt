package se.supernovait.doobypro.domain.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToURL

class IosFileStorage : FileStorage {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun saveFile(fileName: String, bytes: ByteArray): String {
        val fileManager = NSFileManager.defaultManager
        val documentDirectory = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask).first() as NSURL
        val fileURL = documentDirectory.URLByAppendingPathComponent(fileName)!!
        
        val data = bytes.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
        }
        
        data.writeToURL(fileURL, true)
        return fileURL.path!!
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun deleteFile(path: String) {
        val fileManager = NSFileManager.defaultManager
        if (fileManager.fileExistsAtPath(path)) {
            fileManager.removeItemAtPath(path, null)
        }
    }
}

actual fun FileStorage(): FileStorage = IosFileStorage()
