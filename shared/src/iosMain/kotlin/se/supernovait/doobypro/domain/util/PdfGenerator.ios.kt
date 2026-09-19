package se.supernovait.doobypro.domain.util

import doobypro.shared.generated.resources.Res
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import platform.UIKit.NSFontAttributeName
import platform.UIKit.NSStringDrawingUsesLineFragmentOrigin
import platform.UIKit.UIFont
import platform.UIKit.UIGraphicsBeginPDFContextToFile
import platform.UIKit.UIGraphicsBeginPDFPageWithInfo
import platform.UIKit.UIGraphicsEndPDFContext
import platform.UIKit.UIImage
import platform.UIKit.boundingRectWithSize
import platform.UIKit.drawAtPoint
import platform.UIKit.drawInRect
import platform.UIKit.sizeWithAttributes

class IosPdfGenerator : PdfGenerator {
    @OptIn(ExperimentalResourceApi::class, ExperimentalForeignApi::class)
    override suspend fun generatePdf(fileName: String, title: String, sections: List<PdfSection>): String? {
        return withContext(Dispatchers.Default) {
            val fileManager = NSFileManager.defaultManager
            val documentsDirectory = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask).first() as NSURL
            val fileUrl = documentsDirectory.URLByAppendingPathComponent(fileName) ?: return@withContext null
            val filePath = fileUrl.path ?: return@withContext null

            UIGraphicsBeginPDFContextToFile(filePath, CGRectMake(0.0, 0.0, 0.0, 0.0), null)
            UIGraphicsBeginPDFPageWithInfo(CGRectMake(0.0, 0.0, 595.0, 842.0), null)

            var y = 50.0
            val margin = 50.0

            // Draw Logos
            try {
                val appLogoBytes = Res.readBytes("drawable/ic_app_icon.png")
                val appLogoData = appLogoBytes.toNSData()
                val appLogo = UIImage.imageWithData(appLogoData)
                appLogo?.drawInRect(CGRectMake(margin, y, 48.0, 48.0))
            } catch (_: Exception) {
                // Fallback: draw app name
                val attributes = mapOf<Any?, Any?>(
                    NSFontAttributeName to UIFont.boldSystemFontOfSize(12.0)
                )
                @Suppress("CAST_NEVER_SUCCEEDS")
                (title as NSString).drawAtPoint(CGPointMake(margin, y), attributes)
            }

            y += 80.0

            // Draw title
            val titleAttributes = mapOf<Any?, Any?>(
                NSFontAttributeName to UIFont.boldSystemFontOfSize(24.0)
            )
            @Suppress("CAST_NEVER_SUCCEEDS")
            (title as NSString).drawAtPoint(CGPointMake(margin, y), titleAttributes)
            y += 40.0

            sections.forEach { section ->
                if (y > 800.0) return@forEach // Simple page limit

                when (section) {
                    is PdfSection.Header -> {
                        y += 10.0
                        val headerAttributes = mapOf<Any?, Any?>(
                            NSFontAttributeName to UIFont.boldSystemFontOfSize(16.0)
                        )
                        @Suppress("CAST_NEVER_SUCCEEDS")
                        (section.title as NSString).drawAtPoint(CGPointMake(margin, y), headerAttributes)
                        y += 24.0
                    }
                    is PdfSection.Text -> {
                        val textAttributes = mapOf<Any?, Any?>(
                            NSFontAttributeName to UIFont.systemFontOfSize(11.0)
                        )
                        @Suppress("CAST_NEVER_SUCCEEDS")
                        val textStr = section.content as NSString
                        val maxWidth = 595.0 - 2 * margin
                        
                        val boundingBox = textStr.boundingRectWithSize(
                            CGSizeMake(maxWidth, 1000.0),
                            NSStringDrawingUsesLineFragmentOrigin,
                            textAttributes,
                            null
                        )
                        
                        val height = boundingBox.useContents { size.height }
                        textStr.drawInRect(CGRectMake(margin, y, maxWidth, height), textAttributes)
                        y += height + 10.0
                    }
                    is PdfSection.KeyValue -> {
                        val boldAttributes = mapOf<Any?, Any?>(
                            NSFontAttributeName to UIFont.boldSystemFontOfSize(11.0)
                        )
                        val regularAttributes = mapOf<Any?, Any?>(
                            NSFontAttributeName to UIFont.systemFontOfSize(11.0)
                        )
                        
                        @Suppress("CAST_NEVER_SUCCEEDS")
                        val keyStr = "${section.key}: " as NSString
                        keyStr.drawAtPoint(CGPointMake(margin, y), boldAttributes)
                        
                        val keySize = keyStr.sizeWithAttributes(boldAttributes)
                        val keyWidth = keySize.useContents { width }
                        val valueX = margin + keyWidth
                        val valueWidth = 595.0 - margin - valueX
                        
                        @Suppress("CAST_NEVER_SUCCEEDS")
                        val valueStr = section.value as NSString
                        val valueBoundingBox = valueStr.boundingRectWithSize(
                            CGSizeMake(valueWidth, 1000.0),
                            NSStringDrawingUsesLineFragmentOrigin,
                            regularAttributes,
                            null
                        )
                        
                        val valueHeight = valueBoundingBox.useContents { size.height }
                        valueStr.drawInRect(CGRectMake(valueX, y, valueWidth, valueHeight), regularAttributes)
                        y += valueHeight + 5.0
                    }
                }
            }

            UIGraphicsEndPDFContext()
            filePath
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun ByteArray.toNSData(): NSData = usePinned {
        NSData.dataWithBytes(it.addressOf(0), size.toULong())
    }
}
