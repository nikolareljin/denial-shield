package com.denialshield.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DocumentProcessor(private val context: Context) {

    companion object {
        private const val TAG = "DocumentProcessor"
    }

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    init {
        PDFBoxResourceLoader.init(context)
    }

    suspend fun processImage(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val image = try {
                InputImage.fromFilePath(context, uri)
            } catch (e: Exception) {
                Log.w(TAG, "InputImage.fromFilePath failed for $uri; falling back to bitmap decode.", e)
                val bitmap = decodeBitmapFromUri(uri)
                    ?: return@withContext ""
                InputImage.fromBitmap(bitmap, 0)
            }
            val result = recognizer.process(image).await()
            if (result.text.isBlank()) {
                Log.w(TAG, "OCR returned empty text for $uri")
            }
            result.text.trim()
        } catch (e: Exception) {
            Log.e(TAG, "Error processing image: $uri", e)
            ""
        }
    }

    suspend fun processPdf(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                PDDocument.load(inputStream).use { document ->
                    val stripper = PDFTextStripper()
                    stripper.getText(document)
                }?.trim().orEmpty()
            } ?: run {
                Log.e(TAG, "Error processing PDF: unable to open file for $uri")
                ""
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing PDF: $uri", e)
            ""
        }
    }

    fun extractPolicyLanguage(rawText: String): String {
        val cleanedText = rawText.trim()
        if (cleanedText.isEmpty()) {
            return ""
        }

        // Simple heuristic: look for sentences containing specific keywords
        val keywords = listOf(
            "policy", "coverage", "exclusion", "section", "benefit", 
            "medical necessity", "not covered", "experimental", "investigational",
            "clinical criteria", "prior authorization", "appeal"
        )
        
        val sentences = cleanedText.split(Regex("(?<=[.!?])\\s+"))
        val relevantSentences = sentences.filter { sentence ->
            keywords.any { keyword -> sentence.contains(keyword, ignoreCase = true) }
        }
        
        return if (relevantSentences.isEmpty()) {
            cleanedText
        } else {
            relevantSentences.distinct().joinToString("\n\n")
        }
    }

    private fun decodeBitmapFromUri(uri: Uri, maxDimension: Int = 4096): Bitmap? {
        val resolver = context.contentResolver
        val boundsOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        resolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, boundsOptions)
        } ?: return null

        val maxSide = maxOf(boundsOptions.outWidth, boundsOptions.outHeight)
        val sampleSize = if (maxSide > maxDimension) {
            var size = 1
            while (maxSide / size > maxDimension) {
                size *= 2
            }
            size
        } else {
            1
        }

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }

        return resolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, decodeOptions)
        }
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
        addOnCanceledListener { cont.cancel() }
    }
}
