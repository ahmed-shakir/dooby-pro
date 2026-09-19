package se.supernovait.doobypro.domain.util

interface PdfGenerator {
    suspend fun generatePdf(
        fileName: String,
        title: String,
        sections: List<PdfSection>
    ): String?
}

sealed class PdfSection {
    data class Header(val title: String) : PdfSection()
    data class Text(val content: String) : PdfSection()
    data class KeyValue(val key: String, val value: String) : PdfSection()
}
