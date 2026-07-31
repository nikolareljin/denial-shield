package com.denialshield.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.denialshield.data.model.DenialClaim
import com.denialshield.data.model.UserInfo
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

object PdfExporter {

    suspend fun exportToPdf(context: Context, userInfo: UserInfo, claim: DenialClaim): Uri? = withContext(Dispatchers.IO) {
        try {
            val fileName = "Rebuttal_${claim.claimId}.pdf"
            val file = File(context.cacheDir, fileName)
            
            val document = PDDocument()
            val page = PDPage()
            document.addPage(page)
            
            PDPageContentStream(document, page).use { contentStream: PDPageContentStream ->
                contentStream.beginText()
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16f)
                contentStream.setLeading(20f)
                contentStream.newLineAtOffset(50f, 750f)
                contentStream.showText("Medical Appeal Rebuttal")
                contentStream.newLine()
                
                contentStream.setFont(PDType1Font.HELVETICA, 12f)
                contentStream.newLine()
                contentStream.showText("Date: ${Date()}")
                contentStream.newLine()
                contentStream.showText("Patient: ${userInfo.firstName} ${userInfo.lastName}")
                contentStream.newLine()
                contentStream.showText("Insurance: ${userInfo.insuranceName} (${userInfo.insuranceMemberId})")
                contentStream.newLine()
                contentStream.showText("Provider: ${claim.providerName}")
                contentStream.newLine()
                contentStream.showText("Claim ID: ${claim.claimId}")
                contentStream.newLine()
                contentStream.newLine()
                
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12f)
                contentStream.showText("Rebuttal Text:")
                contentStream.newLine()
                contentStream.setFont(PDType1Font.HELVETICA, 10f)
                
                // Simple text wrapping (could be better but works for basic needs)
                val lines = claim.generatedRebuttal.split("\n")
                for (line in lines) {
                    val sanitizedLine = line.replace("\r", "").replace("\t", "    ")
                    if (sanitizedLine.isEmpty()) {
                        contentStream.newLine()
                        continue
                    }
                    
                    // Brute force wrapping for long lines
                    var currentLine = sanitizedLine
                    while (currentLine.length > 80) {
                        contentStream.showText(currentLine.substring(0, 80))
                        contentStream.newLine()
                        currentLine = currentLine.substring(80)
                    }
                    contentStream.showText(currentLine)
                    contentStream.newLine()
                }
                
                contentStream.endText()
            }
            
            document.save(file)
            document.close()
            
            return@withContext FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
