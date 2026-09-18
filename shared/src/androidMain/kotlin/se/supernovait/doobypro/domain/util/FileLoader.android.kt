package se.supernovait.doobypro.domain.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

actual suspend fun loadLogoBytes(urlOrPath: String): ByteArray? = withContext(Dispatchers.IO) {
    try {
        if (urlOrPath.startsWith("http://") || urlOrPath.startsWith("https://")) {
            URL(urlOrPath).openStream().use { it.readBytes() }
        } else {
            val file = File(urlOrPath)
            if (file.exists()) file.readBytes() else null
        }
    } catch (e: Exception) {
        null
    }
}
