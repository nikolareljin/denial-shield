package com.denialshield.utils

import android.content.Context
import com.denialshield.data.model.DenialClaim
import com.denialshield.data.model.UserInfo
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AiRebuttalGenerator(private val context: Context) {

    private var llmInference: LlmInference? = null

    companion object {
        const val MODEL_VERSION = 1
        const val MODEL_NAME = "model.bin"
        const val PREFS_NAME = "ai_prefs"
        const val KEY_MODEL_VERSION = "model_version"
    }

    private val modelPath = File(context.filesDir, MODEL_NAME).absolutePath

    private suspend fun syncModelIfNeeded() = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentVersion = prefs.getInt(KEY_MODEL_VERSION, 0)

        if (currentVersion < MODEL_VERSION || !File(modelPath).exists()) {
            try {
                context.assets.open(MODEL_NAME).use { input ->
                    FileOutputStream(modelPath).use { output ->
                        input.copyTo(output)
                    }
                }
                prefs.edit().putInt(KEY_MODEL_VERSION, MODEL_VERSION).apply()
            } catch (e: Exception) {
                // Asset might not exist yet if dev hasn't added it
            }
        }
    }

    private suspend fun setupLlm() {
        syncModelIfNeeded()
        if (llmInference == null && File(modelPath).exists()) {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(1024)
                .setTopK(40)
                .setTemperature(0.7f)
                .setRandomSeed(101)
                .build()
            llmInference = withContext(Dispatchers.IO) {
                LlmInference.createFromOptions(context, options)
            }
        }
    }

    suspend fun generateRebuttal(
        userInfo: UserInfo,
        claim: DenialClaim
    ): String = withContext(Dispatchers.IO) {
        setupLlm()
        
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
                llmInference?.generateResponse(prompt) ?: fallbackGenerator(userInfo, claim)
            } catch (e: Exception) {
                "AI Generation Error: ${e.message}\n\nFallback:\n${fallbackGenerator(userInfo, claim)}"
            }
        } else {
            // If model is not found, use a sophisticated template (Fallback AI)
            val modelStatus = if (!File(modelPath).exists()) "Model file not found." else "Model initialization failed."
            "Note: On-device AI ($modelStatus) is unavailable. Using template engine.\n\n" +
            fallbackGenerator(userInfo, claim)
        }
    }

    private fun fallbackGenerator(userInfo: UserInfo, claim: DenialClaim): String {
        return RebuttalGenerator.generateEmail(userInfo, claim)
    }
}
