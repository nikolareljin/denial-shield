package com.denialshield.rebuttal

import com.denialshield.data.model.DenialClaim
import com.denialshield.data.model.UserInfo

/**
 * Runs the available engines in preference order and returns the first letter
 * one of them produces.
 *
 * The fallback is the point: an engine that cannot run on this device — no
 * model installed, no supported runtime, not configured — declines, and the
 * next one answers. There is no state in which the user is left without a
 * draft, and the letter always says which engine wrote it.
 */
class RebuttalGenerator(
    private val engines: List<RebuttalEngine> = listOf(
        OnDeviceLlmEngine(),
        TemplateRebuttalEngine()
    )
) {

    /**
     * @param onStatus receives a short progress line per attempt, for the UI.
     */
    suspend fun generate(
        userInfo: UserInfo,
        claim: DenialClaim,
        onStatus: (String) -> Unit = {}
    ): String {
        val declined = mutableListOf<String>()

        for (engine in engines) {
            onStatus("Trying ${engine.label}...")
            when (val result = engine.generate(userInfo, claim)) {
                is RebuttalResult.Generated -> return result.text
                is RebuttalResult.Unavailable ->
                    declined += "${engine.label}: ${result.reason.message}"
            }
        }

        // Every engine declined. Report why rather than returning an empty
        // draft the user cannot act on.
        return buildString {
            appendLine("No rebuttal engine was able to run on this device.")
            appendLine()
            declined.forEach { appendLine("- $it") }
        }
    }
}
