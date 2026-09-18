package se.supernovait.doobypro.domain.util

/**
 * Platform-specific function to load image bytes from a URL or absolute file path.
 */
expect suspend fun loadLogoBytes(urlOrPath: String): ByteArray?
