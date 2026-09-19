package se.supernovait.doobypro.domain.util

import android.content.ContentValues
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.graphics.scale
import doobypro.shared.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.File
import java.io.FileOutputStream

class AndroidPdfGenerator : PdfGenerator {
    @OptIn(ExperimentalResourceApi::class)
    override suspend fun generatePdf(fileName: String, title: String, sections: List<PdfSection>): String? {
        return withContext(Dispatchers.IO) {
            val pdfDocument = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()

            var y = 50f
            val margin = 50f

            // Draw Logos
            try {
                // App Logo (PNG)
                val appLogoBytes = Res.readBytes("drawable/ic_app_icon.png")
                val appLogo = BitmapFactory.decodeByteArray(appLogoBytes, 0, appLogoBytes.size)
                if (appLogo != null) {
                    val scaledAppLogo = appLogo.scale(48, 48, true)
                    canvas.drawBitmap(scaledAppLogo, margin, y, paint)
                }
            } catch (_: Exception) {
                // Fallback to text if logos fail
                paint.textSize = 10f
                canvas.drawText("Dooby Pro", margin, y + 20f, paint)
            }

            y += 80f

            // Draw title
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 24f
            canvas.drawText(title, margin, y, paint)
            y += 40f

            // Draw sections
            paint.typeface = Typeface.DEFAULT
            sections.forEach { section ->
                if (y > pageHeight - 50f) return@forEach // Simple limit for one page

                when (section) {
                    is PdfSection.Header -> {
                        y += 10f
                        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        paint.textSize = 16f
                        canvas.drawText(section.title, margin, y, paint)
                        y += 24f
                        paint.typeface = Typeface.DEFAULT
                    }
                    is PdfSection.Text -> {
                        paint.textSize = 11f
                        y = drawWrappedText(canvas, section.content, margin, y, pageWidth - 2 * margin, paint)
                    }
                    is PdfSection.KeyValue -> {
                        paint.textSize = 11f
                        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        canvas.drawText("${section.key}: ", margin, y, paint)
                        val keyWidth = paint.measureText("${section.key}: ")
                        paint.typeface = Typeface.DEFAULT
                        
                        val valueWidth = pageWidth - 2 * margin - keyWidth
                        y = drawWrappedText(canvas, section.value, margin + keyWidth, y, valueWidth, paint)
                    }
                }
            }

            pdfDocument.finishPage(page)

            // Save to public Downloads directory
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = applicationContext.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                    pdfDocument.close()
                    uri.toString()
                } else {
                    pdfDocument.close()
                    null
                }
            } else {
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadDir, fileName)
                try {
                    FileOutputStream(file).use {
                        pdfDocument.writeTo(it)
                    }
                    pdfDocument.close()
                    file.absolutePath
                } catch (_: Exception) {
                    pdfDocument.close()
                    null
                }
            }
        }
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Float,
        startY: Float,
        maxWidth: Float,
        paint: Paint
    ): Float {
        var currentY = startY
        val lines = text.split("\n")
        
        lines.forEach { line ->
            var remaining = line
            while (remaining.isNotEmpty()) {
                val count = paint.breakText(remaining, true, maxWidth, null)
                if (count <= 0) break
                
                var end = count
                if (count < remaining.length) {
                    // Try to break at space
                    val lastSpace = remaining.substring(0, count).lastIndexOf(' ')
                    if (lastSpace != -1) {
                        end = lastSpace + 1
                    }
                }
                
                canvas.drawText(remaining.substring(0, end).trim(), x, currentY, paint)
                remaining = remaining.substring(end)
                currentY += paint.textSize * 1.5f
            }
        }
        return currentY
    }
}
