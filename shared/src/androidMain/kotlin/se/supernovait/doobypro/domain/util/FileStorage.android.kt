package se.supernovait.doobypro.domain.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AndroidFileStorage : FileStorage {
    override suspend fun saveFile(fileName: String, bytes: ByteArray): String = withContext(Dispatchers.IO) {
        val file = File(applicationContext.filesDir, fileName)
        file.writeBytes(bytes)
        file.absolutePath
    }

    override suspend fun deleteFile(path: String) {
        withContext(Dispatchers.IO) {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }
    }
}

actual fun FileStorage(): FileStorage = AndroidFileStorage()
