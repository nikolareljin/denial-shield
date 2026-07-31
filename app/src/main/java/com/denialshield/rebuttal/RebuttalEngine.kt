package com.denialshield.rebuttal

import com.denialshield.data.model.DenialClaim
import com.denialshield.data.model.UserInfo

/**
 * Produces an appeal letter from a claim and the patient's details.
 *
 * Implementations are ordered by preference and tried in turn, so a stronger
 * engine that cannot run on this device degrades to a weaker one rather than
 * failing the request. The user always ends up with a letter.
 */
interface RebuttalEngine {

    /** Human-readable name of this engine, shown alongside generated output. */
    val label: String

    suspend fun generate(userInfo: UserInfo, claim: DenialClaim): RebuttalResult
}

/** The outcome of a single engine attempt. */
sealed interface RebuttalResult {

    /** The engine produced a letter. */
    data class Generated(val text: String, val engineLabel: String) : RebuttalResult

    /** The engine cannot run here. The caller falls through to the next one. */
    data class Unavailable(val reason: Reason) : RebuttalResult
}

/** Why an engine declined to run, in the terms the UI reports to the user. */
enum class Reason(val message: String) {
    NOT_CONFIGURED("Not configured in this build."),
    MODEL_MISSING("No model is installed on this device."),
    RUNTIME_UNAVAILABLE("This device cannot run the inference runtime."),
    GENERATION_FAILED("Generation did not produce usable output.")
}
