package se.supernovait.doobypro.util

import se.supernovait.doobypro.domain.util.PdfGenerator
import se.supernovait.doobypro.domain.util.PdfSection

class FakePdfGenerator : PdfGenerator {
    var lastGeneratedFileName: String? = null
    var lastGeneratedTitle: String? = null
    var lastGeneratedSections: List<PdfSection>? = null
    var generatePdfCalledCount = 0

    override suspend fun generatePdf(fileName: String, title: String, sections: List<PdfSection>): String? {
        lastGeneratedFileName = fileName
        lastGeneratedTitle = title
        lastGeneratedSections = sections
        generatePdfCalledCount++
        return "fake/path/$fileName"
    }
}
