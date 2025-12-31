package com.denialshield.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tomroush.pdfbox.android.PDFBoxResourceLoader
import com.tomroush.pdfbox.pdmodel.PDDocument
import com.tomroush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream

class DocumentProcessor(private val context: Context) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    init {
        PDFBoxResourceLoader.init(context)
    }

    suspend fun processImage(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image).await()
            result.text
        } catch (e: Exception) {
            "Error processing image: ${e.message}"
        }
    }

    suspend fun processPdf(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()
            val text = stripper.getText(document)
            document.close()
            text
        } catch (e: Exception) {
            "Error processing PDF: ${e.message}"
        }
    }

    fun extractPolicyLanguage(rawText: String): String {
        // Simple heuristic: look for sentences containing specific keywords
        val keywords = listOf(
            "policy", "coverage", "exclusion", "section", "benefit", 
            "medical necessity", "not covered", "experimental", "investigational",
            "clinical criteria", "prior authorization", "appeal"
        )
        
        val sentences = rawText.split(Regex("(?<=[.!?])\\s+"))
        val relevantSentences = sentences.filter { sentence ->
            keywords.any { keyword -> sentence.contains(keyword, ignoreCase = true) }
        }
        
        return relevantSentences.distinct().joinToString("\n\n")
    }
}
