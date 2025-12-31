package com.denialshield.utils

object RebuttalGenerator {

    fun generateEmail(
        userInfo: com.denialshield.data.model.UserInfo,
        claim: com.denialshield.data.model.DenialClaim
    ): String {
        return """
            Subject: Appeal for Denial of Claim ${claim.claimId} - ${userInfo.firstName} ${userInfo.lastName}

            Dear Appeals Department,

            I am writing to formally appeal the denial of claim number ${claim.claimId} for services provided by ${claim.providerName} on ${formatDate(claim.dateReceived)}.

            Reason for denial provided: ${claim.denialReasonDescription} (${claim.denialReasonCode})

            According to my policy language:
            "${claim.policyLanguageCited.ifBlank { "See attached policy documents." }}"

            I believe this denial is in error because the services provided are medically necessary and covered under the terms of my policy as stated above.

            [Optional: Add personal narrative/facts here]

            Please reconsider this claim and provide a detailed explanation if the denial is upheld.

            Thank you,
            
            ${userInfo.firstName} ${userInfo.lastName}
            ${userInfo.address}
            ${userInfo.city}, ${userInfo.state} ${userInfo.zipCode}
            Policy: ${userInfo.policyNumber}
        """.trimIndent()
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.US)
        return sdf.format(java.util.Date(timestamp))
    }
}
