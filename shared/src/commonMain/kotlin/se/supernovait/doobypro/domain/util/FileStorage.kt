package se.supernovait.doobypro.domain.util

/**
 * Interface for saving and retrieving files from the app's internal storage.
 */
interface FileStorage {
    /**
     * Saves the given bytes as a file with the specified name.
     * @param fileName Name of the file including extension.
     * @param bytes Data to be saved.
     * @return The absolute path to the saved file.
     */
    suspend fun saveFile(fileName: String, bytes: ByteArray): String

    /**
     * Deletes the file at the specified path.
     * @param path Absolute path to the file.
     */
    suspend fun deleteFile(path: String)
}

/**
 * Factory function to create a platform-specific [FileStorage] instance.
 */
expect fun FileStorage(): FileStorage
