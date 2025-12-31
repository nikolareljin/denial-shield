package com.denialshield.utils

import android.content.Context
import com.denialshield.data.model.DenialClaim
import com.denialshield.data.model.UserInfo
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AiRebuttalGenerator(private val context: Context) {

    private var llmInference: LlmInference? = null

    // Path to the local LLM model (e.g., Gemma 2B)
    // Note: User must provide this file in the assets or a specific path
    private val modelPath = "${context.filesDir.absolutePath}/model.bin"

    private fun setupLlm() {
        if (llmInference == null && File(modelPath).exists()) {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(1024)
                .setTopK(40)
                .setTemperature(0.7f)
                .setRandomSeed(101)
                .build()
            llmInference = LlmInference.createFromOptions(context, options)
        }
    }

    suspend fun generateRebuttal(
        userInfo: UserInfo,
        claim: DenialClaim
    ): String = withContext(Dispatchers.IO) {
        setupLlm()
        
        val prompt = """
            You are a medical billing expert helping a patient appeal a claim denial.
            Patient: ${userInfo.firstName} ${userInfo.lastName}
            Insurance: ${userInfo.insuranceName}
            Claim ID: ${claim.claimId}
            Provider: ${claim.providerName}
            Denial Reason: ${claim.denialReasonDescription}
            Policy Language: ${claim.policyLanguageCited}
            
            Write a professional, firm, and legally-sound rebuttal email to the insurance company 
            appealing this denial based on the provided policy language and medical necessity.
        """.trimIndent()

        return@withContext if (llmInference != null) {
            try {
                llmInference?.generateResponse(prompt) ?: fallbackGenerator(userInfo, claim)
            } catch (e: Exception) {
                "AI Generation Error: ${e.message}\n\nFallback:\n${fallbackGenerator(userInfo, claim)}"
            }
        } else {
            // If model is not found, use a sophisticated template (Fallback AI)
            "Note: Local AI model not found at $modelPath. using sophisticated template engine.\n\n" +
            fallbackGenerator(userInfo, claim)
        }
    }

    private fun fallbackGenerator(userInfo: UserInfo, claim: DenialClaim): String {
        return RebuttalGenerator.generateEmail(userInfo, claim)
    }
}
