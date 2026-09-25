package se.supernovait.doobypro.domain.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.dataWithContentsOfURL
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual suspend fun loadLogoBytes(urlOrPath: String): ByteArray? = withContext(Dispatchers.Default) {
    try {
        val data = if (urlOrPath.startsWith("http://") || urlOrPath.startsWith("https://")) {
            NSData.dataWithContentsOfURL(NSURL(string = urlOrPath))
        } else {
            NSData.dataWithContentsOfFile(urlOrPath)
        }
        data?.let {
            val bytes = ByteArray(it.length.toInt())
            if (bytes.isNotEmpty()) {
                bytes.usePinned { pinned ->
                    memcpy(pinned.addressOf(0), it.bytes, it.length)
                }
                bytes
            } else null
        }
    } catch (_: Exception) {
        null
    }
}
