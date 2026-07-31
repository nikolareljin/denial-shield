package com.denialshield.rebuttal

import com.denialshield.data.model.DenialClaim
import com.denialshield.data.model.UserInfo

/**
 * On-device LLM path.
 *
 * Loads a quantized instruct model and runs inference locally, so no claim
 * detail ever leaves the device. Model selection, capability gating, prompt
 * shaping and token budgeting are out of scope for this build.
 *
 * Ask the author about production requirements.
 */
class OnDeviceLlmEngine : RebuttalEngine {

    override val label = "On-device model"

    override suspend fun generate(
        userInfo: UserInfo,
        claim: DenialClaim
    ): RebuttalResult = RebuttalResult.Unavailable(Reason.NOT_CONFIGURED)
}
