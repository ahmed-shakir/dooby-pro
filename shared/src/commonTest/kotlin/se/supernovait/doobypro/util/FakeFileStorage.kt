package se.supernovait.doobypro.util

import se.supernovait.doobypro.domain.util.FileStorage

class FakeFileStorage : FileStorage {
    private val files = mutableMapOf<String, ByteArray>()

    override suspend fun saveFile(fileName: String, bytes: ByteArray): String {
        files[fileName] = bytes
        return "/fake/path/$fileName"
    }

    override suspend fun deleteFile(path: String) {
        val fileName = path.substringAfterLast("/")
        files.remove(fileName)
    }

    fun getFile(fileName: String): ByteArray? = files[fileName]
}
