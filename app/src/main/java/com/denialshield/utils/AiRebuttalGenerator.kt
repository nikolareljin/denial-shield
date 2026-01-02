package com.denialshield.utils

import android.content.Context
import android.os.Build
import android.util.Log
import com.denialshield.BuildConfig
import com.denialshield.data.model.DenialClaim
import com.denialshield.data.model.UserInfo
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class AiRebuttalGenerator(private val context: Context) {

    private var llmInference: LlmInference? = null
    private var llmUnavailable = false
    private var llmNativeLibraryMissing = false

    companion object {
        private const val TAG = "AiRebuttalGenerator"
        const val MODEL_VERSION = 2
        const val MODEL_NAME = "model.bin"
        const val MODEL_DOWNLOAD_URL =
            "https://huggingface.co/litert-community/SmolLM-135M-Instruct/resolve/main/" +
                "SmolLM-135M-Instruct_multi-prefill-seq_q8_ekv1280.task?download=true"
        const val PREFS_NAME = "ai_prefs"
        const val KEY_MODEL_VERSION = "model_version"
    }

    private val modelDir = context.getExternalFilesDir("models") ?: context.filesDir
    private val modelFile = File(modelDir, MODEL_NAME)
    private val modelPath = modelFile.absolutePath

    private suspend fun syncModelIfNeeded(onStatus: (String) -> Unit) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentVersion = prefs.getInt(KEY_MODEL_VERSION, 0)

        if (!modelDir.exists() && !modelDir.mkdirs()) {
            Log.w(TAG, "Failed to create model directory at ${modelDir.absolutePath}")
        }

        if (currentVersion < MODEL_VERSION || !modelFile.exists() || modelFile.length() == 0L) {
            try {
                onStatus("Downloading AI model (first run)...")
                downloadModel()
                if (modelFile.length() == 0L) {
                    modelFile.delete()
                    Log.e(TAG, "Model download failed; file is empty.")
                } else {
                    prefs.edit().putInt(KEY_MODEL_VERSION, MODEL_VERSION).apply()
                    Log.i(TAG, "Model synced to ${modelFile.absolutePath} (${modelFile.length()} bytes).")
                }
            } catch (e: Exception) {
                onStatus("Model download failed. Please check network or HF token.")
                Log.e(TAG, "Failed to download model to ${modelFile.absolutePath}", e)
            }
        } else {
            Log.d(TAG, "Model already present at ${modelFile.absolutePath} (${modelFile.length()} bytes).")
        }
    }

    private fun isAbiSupported(): Boolean {
        val supported = setOf("arm64-v8a", "armeabi-v7a")
        return Build.SUPPORTED_ABIS.any { it in supported }
    }

    private fun hasNativeRuntime(): Boolean {
        val libDir = context.applicationInfo.nativeLibraryDir ?: return false
        val libFile = File(libDir, "libllm_inference_engine_jni.so")
        return libFile.exists()
    }

    private fun downloadModel() {
        if (!modelDir.exists() && !modelDir.mkdirs()) {
            throw IllegalStateException("Unable to create model directory at ${modelDir.absolutePath}")
        }

        val tmpFile = File(modelDir, "${MODEL_NAME}.download")
        if (tmpFile.exists()) {
            tmpFile.delete()
        }
        val connection = URL(MODEL_DOWNLOAD_URL).openConnection() as HttpsURLConnection
        connection.instanceFollowRedirects = true
        connection.connectTimeout = 30_000
        connection.readTimeout = 30_000
        connection.setRequestProperty("User-Agent", "DenialShield/1.0")
        connection.setRequestProperty("Accept", "*/*")
        if (BuildConfig.HF_TOKEN.isNotBlank()) {
            connection.setRequestProperty("Authorization", "Bearer ${BuildConfig.HF_TOKEN}")
        }

        connection.connect()
        val responseCode = connection.responseCode
        if (responseCode !in 200..299) {
            if (responseCode == 401) {
                throw IllegalStateException("Model download unauthorized (HTTP 401). Add HF_TOKEN.")
            }
            throw IllegalStateException("Model download failed with HTTP $responseCode")
        }

        connection.inputStream.use { stream ->
            BufferedInputStream(stream).use { input ->
                FileOutputStream(tmpFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        if (tmpFile.length() == 0L) {
            tmpFile.delete()
            throw IllegalStateException("Downloaded model is empty.")
        }

        if (!tmpFile.renameTo(modelFile)) {
            tmpFile.copyTo(modelFile, overwrite = true)
            tmpFile.delete()
            Log.w(TAG, "Atomic rename failed; copied model to ${modelFile.absolutePath}")
        } else {
            Log.i(TAG, "Model download complete at ${modelFile.absolutePath}")
        }
    }

    private suspend fun setupLlm(onStatus: (String) -> Unit) {
        if (!isAbiSupported()) {
            llmNativeLibraryMissing = true
            llmUnavailable = true
            Log.w(TAG, "Unsupported ABI for GenAI runtime: ${Build.SUPPORTED_ABIS.joinToString()}")
            return
        }
        if (!hasNativeRuntime()) {
            llmNativeLibraryMissing = true
            llmUnavailable = true
            onStatus("AI runtime not available on this device.")
            Log.w(TAG, "Native GenAI runtime library missing in ${context.applicationInfo.nativeLibraryDir}")
            return
        }
        onStatus("Loading AI model...")
        syncModelIfNeeded(onStatus)
        if (llmInference == null && modelFile.exists() && modelFile.length() > 0L) {
            try {
                val options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath(modelPath)
                    .setMaxTokens(1024)
                    .build()
                llmInference = withContext(Dispatchers.IO) {
                    LlmInference.createFromOptions(context, options)
                }
                llmUnavailable = false
                Log.i(TAG, "LlmInference initialized with model at $modelPath")
            } catch (e: UnsatisfiedLinkError) {
                llmNativeLibraryMissing = true
                llmUnavailable = true
                llmInference = null
                Log.e(TAG, "Native AI library missing. Unable to initialize LlmInference.", e)
            } catch (e: NoClassDefFoundError) {
                llmNativeLibraryMissing = true
                llmUnavailable = true
                llmInference = null
                Log.e(TAG, "LlmInference class failed to load. Native AI library likely missing.", e)
            } catch (e: Throwable) {
                // If model loading fails, llmInference will remain null, and the fallback will be used.
                llmUnavailable = true
                llmInference = null
                Log.e(TAG, "Failed to initialize LlmInference with model at $modelPath", e)
            }
        } else if (!modelFile.exists() || modelFile.length() == 0L) {
            llmUnavailable = true
            Log.w(TAG, "Model file missing or empty at $modelPath")
        }
    }

    suspend fun generateRebuttal(
        userInfo: UserInfo,
        claim: DenialClaim,
        onStatus: (String) -> Unit = {}
    ): String = withContext(Dispatchers.IO) {
        onStatus("Preparing AI rebuttal...")
        setupLlm(onStatus)
        
        val prompt = """
            You are a medical billing expert helping a patient appeal a claim denial.
            
            Patient Information:
            - Name: ${userInfo.firstName} ${userInfo.lastName}
            - Insurance: ${userInfo.insuranceName}
            - Member ID: ${userInfo.insuranceMemberId}
            
            Claim Context:
            - Provider: ${claim.providerName}
            - Claim ID: ${claim.claimId}
            - Denial Reason: ${claim.denialReasonDescription}
            - Supporting Policy Language: ${claim.policyLanguageCited}
            
            Write a detailed, formal medical appeal letter. 
            The letter should follow this structure:
            
            1. [Date]
            2. [Insurance Company Name and Address]
            3. Subject: Formal Appeal for Claim ID: ${claim.claimId}
            4. Salutation (e.g., Dear Appeals Committee,)
            5. Introduction: State the purpose of the letter and the claim being appealed.
            6. Argument: Use the provided policy language to argue for medical necessity or coverage. Be specific.
            7. Conclusion: Demand a re-evaluation and state the desired outcome.
            8. Closing (e.g., Sincerely, [Patient Name])
            
            Generate only the text of the letter. Do not include any meta-commentary.
        """.trimIndent()

        return@withContext if (llmInference != null) {
            try {
                onStatus("Generating rebuttal...")
                val response = llmInference?.generateResponse(prompt)
                if (response.isNullOrBlank()) {
                    Log.w(TAG, "LLM returned empty response; using template fallback.")
                    fallbackGenerator(userInfo, claim)
                } else {
                    response
                }
            } catch (e: Exception) {
                Log.e(TAG, "AI generation failed; using template fallback.", e)
                "AI Generation Error: ${e.message}\n\nFallback:\n${fallbackGenerator(userInfo, claim)}"
            }
        } else {
            // If model is not found, use a sophisticated template (Fallback AI)
            val modelStatus = when {
                !modelFile.exists() -> "Model file not found."
                modelFile.length() == 0L -> "Model file is empty."
                llmNativeLibraryMissing -> "Native AI library unavailable."
                llmUnavailable -> "Model initialization failed."
                else -> "Model initialization failed."
            }
            "Note: On-device AI ($modelStatus) is unavailable. Using template engine.\n\n" +
                fallbackGenerator(userInfo, claim)
        }
    }

    private fun fallbackGenerator(userInfo: UserInfo, claim: DenialClaim): String {
        return RebuttalGenerator.generateEmail(userInfo, claim)
    }
}
