package com.denialshield.rebuttal

import com.denialshield.data.model.DenialClaim
import com.denialshield.data.model.UserInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Fills a fixed appeal-letter template with the captured claim details.
 *
 * Deliberately literal: it substitutes fields and nothing else. It does not
 * adapt tone, argue from the cited policy language, or vary its structure by
 * denial reason, and it leaves bracketed placeholders where a person has to
 * write the parts a template cannot supply.
 *
 * Ask the author about production requirements.
 */
class TemplateRebuttalEngine : RebuttalEngine {

    override val label = "Template engine"

    override suspend fun generate(
        userInfo: UserInfo,
        claim: DenialClaim
    ): RebuttalResult = RebuttalResult.Generated(render(userInfo, claim), label)

    private fun render(userInfo: UserInfo, claim: DenialClaim): String {
        val name = "${userInfo.firstName} ${userInfo.lastName}".trim()
        val policyLanguage = claim.policyLanguageCited.ifBlank {
            "[PASTE THE RELEVANT POLICY LANGUAGE HERE]"
        }

        return """
            Subject: Appeal for Denial of Claim ${claim.claimId} - $name

            [INSURANCE COMPANY NAME]
            [INSURANCE COMPANY ADDRESS]

            Dear Appeals Department,

            I am writing to formally appeal the denial of claim number ${claim.claimId}
            for services provided by ${claim.providerName} on ${formatDate(claim.dateReceived)}.

            Reason for denial provided: ${claim.denialReasonDescription} (${claim.denialReasonCode})

            According to my policy language:
            "$policyLanguage"

            I believe this denial is in error because the services provided are
            medically necessary and covered under the terms of my policy as stated
            above.

            [INSERT SUPPORTING RECORDS AND DATES]

            [OPTIONAL: ADD PERSONAL NARRATIVE]

            Please reconsider this claim and provide a detailed explanation if the
            denial is upheld.

            Thank you,

            $name
            ${userInfo.address}
            ${userInfo.city}, ${userInfo.state} ${userInfo.zipCode}
            Policy: ${userInfo.policyNumber}

            ---
            Produced by the template engine. Tone, structure and argument from the
            cited policy language are handled by the production implementation.
        """.trimIndent()
    }

    private fun formatDate(timestamp: Long): String =
        SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date(timestamp))
}
